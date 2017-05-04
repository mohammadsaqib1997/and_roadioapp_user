package com.roadioapp.roadioappuser.mObjects;

import android.app.ProgressDialog;
import android.content.Context;

public class mProgressBar {

    //private Context context;
    private ProgressDialog progressDialog;

    public mProgressBar(Context ctx){
        //this.context = ctx;
        progressDialog = new ProgressDialog(ctx);
    }

    public void showProgressDialog() {
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

}
