package com.roadioapp.roadioappuser.mObjects;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class mProgressBar {

    private ProgressDialog progressDialog;

    public mProgressBar(Activity activity){
        progressDialog = new ProgressDialog(activity);
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
