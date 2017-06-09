package com.roadioapp.roadioappuser.mModels;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roadioapp.roadioappuser.mInterfaces.DBCallbacks;
import com.roadioapp.roadioappuser.mInterfaces.ObjectInterfaces;
import com.roadioapp.roadioappuser.mObjects.AuthObj;
import com.roadioapp.roadioappuser.mObjects.ConstantAssign;
import com.roadioapp.roadioappuser.mObjects.mProgressBar;

import java.util.HashMap;
import java.util.Map;

public class UserRequest {

    public String desLat, desLng, desText, disText, durText, id, orgLat, orgLng, orgText, parcelThmb, parcelUri, vecType;

    public long createdAt;

    private DatabaseReference requestLiveCollection, requestActiveCollection, requestCollection;
    private StorageReference parcelImagesRef, parcelImgThmbRef;

    private Activity activity;
    private AuthObj authObj;

    //objects
    private ConstantAssign constantAssign;

    public UserRequest() {

    }

    public UserRequest(Activity activity){
        this.activity = activity;
        this.authObj = new AuthObj(activity);

        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
        requestLiveCollection = dbReference.child("user_live_requests");
        requestActiveCollection = dbReference.child("user_active_requests");
        requestCollection = dbReference.child("user_requests");

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        parcelImagesRef = storageRef.child("parcel_images");
        parcelImgThmbRef = storageRef.child("parcel_thumbs");
    }

    public void setConstantAssign(ConstantAssign constantAssign){
        this.constantAssign = constantAssign;
    }

    public void getUserRequestByReqID(String ID, final DBCallbacks.CompleteDSListener callback){
        requestCollection.child(authObj.authUid).child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    callback.onSuccess(true, "", dataSnapshot);
                }else{
                    callback.onSuccess(false, "Data Not Found!", null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onSuccess(false, databaseError.getMessage(), null);
            }
        });
    }

    public void postReqParcel(final ObjectInterfaces.SimpleCallback callbacks){
        if(authObj.isLoginUser()){
            final String orgLat = String.valueOf(constantAssign.LL1.latitude);
            final String orgLng = String.valueOf(constantAssign.LL1.longitude);
            final String orgText = constantAssign.pickLocCurTV.getText().toString();

            final String desLat = String.valueOf(constantAssign.LL2.latitude);
            final String desLng = String.valueOf(constantAssign.LL2.longitude);
            final String desText = constantAssign.pickLocDesTV.getText().toString();

            final String key = requestLiveCollection.push().getKey();

            final StorageReference pracelImgName = parcelImagesRef.child(key+".jpg");
            UploadTask uploadTask = pracelImgName.putBytes(constantAssign.selImgByteArray);


            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    callbacks.onSuccess(false, e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshotImage) {
                    StorageReference pracelThmbImgName = parcelImgThmbRef.child(key+".jpg");
                    UploadTask uploadTask = pracelThmbImgName.putBytes(constantAssign.selImgThmbByteArray);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            pracelImgName.delete();
                            callbacks.onSuccess(false, e.getMessage());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshotImage.getDownloadUrl();
                            Uri thmbDownloadUrl = taskSnapshot.getDownloadUrl();

                            Map<String, Object> dataMap = new HashMap<String, Object>();
                            dataMap.put("id", key);
                            dataMap.put("orgLat", orgLat);
                            dataMap.put("orgLng", orgLng);
                            dataMap.put("orgText", orgText);
                            dataMap.put("desLat", desLat);
                            dataMap.put("desLng", desLng);
                            dataMap.put("desText", desText);
                            dataMap.put("vecType", constantAssign.vecTypeTag);
                            dataMap.put("disText", constantAssign.disText);
                            dataMap.put("durText", constantAssign.durText);
                            dataMap.put("parcelUri", downloadUrl.toString());
                            dataMap.put("parcelThmb", thmbDownloadUrl.toString());
                            dataMap.put("createdAt", ServerValue.TIMESTAMP);

                            requestCollection.child(authObj.authUid).child(key).setValue(dataMap);
                            requestLiveCollection.child(authObj.authUid).child("reqId").setValue(key);
                            callbacks.onSuccess(true, "");
                        }
                    });
                }
            });
        }else{
            callbacks.onSuccess(false, "Auth Not Found!");
        }
    }

    public void userLiveRequestCheck(final ObjectInterfaces.SimpleCallback callbacks){
        if(authObj.isLoginUser()){
            requestLiveCollection.child(authObj.authUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        callbacks.onSuccess(true, "");
                    }else{
                        callbacks.onSuccess(false, "");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callbacks.onSuccess(false, databaseError.getMessage());
                }
            });

        }else{
            callbacks.onSuccess(false, "Auth Not Found!");
        }
    }

    public void userActiveRequestCheck(final ObjectInterfaces.SimpleCallback callbacks){
        if (authObj.isLoginUser()) {
            requestActiveCollection.child(authObj.authUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        callbacks.onSuccess(true, "");
                    } else {
                        callbacks.onSuccess(false, "");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callbacks.onSuccess(false, databaseError.getMessage());
                }
            });
        }else{
            callbacks.onSuccess(false, "Auth Not Found!");
        }
    }

}
