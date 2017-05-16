package com.roadioapp.roadioappuser.mModels;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class UserCompleteRequest {

    public String req_id, driver_uid, client_uid, status;
    public long active_time, complete_time;

    public UserCompleteRequest(){

    }

    public void activeToCloneData(String reqID, Map dataSet, final CloneDataCallback callback){
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("complete_requests");
        dbReference.child(reqID).setValue(dataSet, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null){
                    callback.onSuccess(false, databaseError.getMessage());
                }else{
                    callback.onSuccess(true, "");
                }
            }
        });
    }


    public interface CloneDataCallback{
        void onSuccess(boolean status, String err);
    }



}
