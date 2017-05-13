package com.roadioapp.roadioappuser.mModels;

import android.app.Activity;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfo {

    public String first_name, last_name, vehicle, mob_no;

    //here program variables
    private DatabaseReference userCollection;

    public UserInfo(){

    }

    public UserInfo(Activity activity){
        userCollection = FirebaseDatabase.getInstance().getReference().child("users");
    }

    public void getUserInfo(String uid, final UserCallback callback){
        userCollection.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    initValue(dataSnapshot.getValue(UserInfo.class));
                    callback.onSuccess(dataSnapshot, null);
                }else{
                    callback.onSuccess(null, "No Data Found!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onSuccess(null, databaseError.getMessage());
            }
        });
    }

    private void initValue(UserInfo userInfo){
        first_name = userInfo.first_name;
        last_name = userInfo.last_name;
        vehicle = userInfo.vehicle;
        mob_no = userInfo.mob_no;
    }

    public String fullName(){
        return first_name+" "+last_name;
    }

    public String getVehicle(){
        return vehicle;
    }

    public String getMobNo(){
        return mob_no;
    }

    public interface UserCallback{
        void onSuccess(DataSnapshot dataSnapshot, String errMsg);
    }
}
