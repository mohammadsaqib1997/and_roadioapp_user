package com.roadioapp.roadioappuser.mModels;


import android.app.Activity;
import android.widget.Toast;

import com.roadioapp.roadioappuser.mObjects.AuthObj;

public class DriverBid {

    public String amount;

    private Activity activity;

    public DriverBid(){

    }

    public DriverBid(Activity activity){
        this.activity = activity;
    }

    public void bidAccept(){
        AuthObj mAuth = new AuthObj(activity);
        mAuth.updateUser();
        if(mAuth.isLoginUser()){

        }else{
            Toast.makeText(activity, "No User Found!", Toast.LENGTH_SHORT).show();
        }
    }
}
