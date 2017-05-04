package com.roadioapp.roadioappuser.mModels;

import android.app.Activity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roadioapp.roadioappuser.mObjects.AuthObj;

public class UserActiveRequest {

    public String driver_uid, req_id, status;

    private DatabaseReference mDatabase, userActiveRequestCol, userLiveRequestCol;
    private AuthObj mAuthObj;

    public UserActiveRequest(){

    }

    public UserActiveRequest(Activity act){
        mAuthObj = new AuthObj();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userActiveRequestCol = mDatabase.child("user_active_requests");
        userLiveRequestCol = mDatabase.child("user_live_requests");
    }

    public void userReqAct(String req_id, String driver_uid, UserActiveRequest.UserActReqCallbacks callback){
        if(mAuthObj.isLoginUser()){

        }else{
            callback.onSuccess(false, "Auth Not Found!");
        }
    }


    public interface UserActReqCallbacks{
        void onSuccess(boolean status, String err);
    }
}
