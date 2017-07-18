package com.roadioapp.roadioappuser.mObjects;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.roadioapp.roadioappuser.R;
import com.roadioapp.roadioappuser.RequestActiveActivity;
import com.roadioapp.roadioappuser.mInterfaces.ObjectInterfaces;
import com.roadioapp.roadioappuser.mModels.UserRequest;

public class RequestParcelObj {

    private Activity activity;
    private ButtonEffects btnEffectsObj;
    private UserRequest userRequestModel;
    private ConstantAssign constantAssignObj;
    private mProgressBar progressBarObj;

    public RequestParcelObj(Activity act, ConstantAssign constantAssign){
        this.activity = act;
        this.btnEffectsObj = new ButtonEffects(act);
        this.userRequestModel = new UserRequest(act);
        this.constantAssignObj = constantAssign;
        this.progressBarObj = new mProgressBar(act);
        userRequestModel.setConstantAssign(constantAssign);
    }

    public void sendReqConfirmDialog() {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.confirm_dialog);
        TextView msg = (TextView) dialog.findViewById(R.id.msgCon);
        msg.setText(R.string.confirm_req_msg);
        LinearLayout btnsCon = (LinearLayout) dialog.findViewById(R.id.btnsCon);
        LinearLayout cancelBtn = (LinearLayout) dialog.findViewById(R.id.cancelBtn);
        LinearLayout yesBtn = (LinearLayout) dialog.findViewById(R.id.yesBtn);
        for (int i = 0; i < btnsCon.getChildCount(); i++) {
            LinearLayout selectBtn = (LinearLayout) btnsCon.getChildAt(i);
            btnEffectsObj.btnEventEffRounded(selectBtn);
        }
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarObj.showProgressDialog();
                userRequestModel.postReqParcel(new ObjectInterfaces.SimpleCallback() {
                    @Override
                    public void onSuccess(boolean status, String res) {
                        progressBarObj.hideProgressDialog();
                        dialog.dismiss();
                        if(status){
                            resetReqSendParcel();
                            sendReqSuccessDialog();
                            constantAssignObj.hasRequest = true;
                            constantAssignObj.sendBtnText.setText("View Bids");
                        }else {
                            Toast.makeText(activity, res, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void sendReqSuccessDialog(){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.success_dialog);
        TextView msg = (TextView) dialog.findViewById(R.id.msgCon);
        msg.setText(R.string.success_req_msg);
        LinearLayout yesBtn = (LinearLayout) dialog.findViewById(R.id.yesBtn);
        btnEffectsObj.btnEventEffRounded(yesBtn);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void resetReqSendParcel() {
        constantAssignObj.reqSendParcel = false;
        constantAssignObj.desLocClearBtn.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams h_layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        h_layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        constantAssignObj.estCon.setLayoutParams(h_layoutParams);
        constantAssignObj.sendBtn.setVisibility(View.VISIBLE);
        constantAssignObj.selImgByteArray = null;
        constantAssignObj.selImgThmbByteArray = null;
        constantAssignObj.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        constantAssignObj.navMenuIcon.setBackgroundResource(R.drawable.ic_action_menu);
    }

    public void setReqSendParcel(){
        constantAssignObj.reqSendParcel = true;
        constantAssignObj.desLocClearBtn.setVisibility(View.GONE);
        constantAssignObj.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        constantAssignObj.navMenuIcon.setBackgroundResource(R.drawable.ic_action_back);
        RelativeLayout.LayoutParams h_layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        h_layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        constantAssignObj.estCon.setLayoutParams(h_layoutParams);
        constantAssignObj.sendBtn.setVisibility(View.GONE);
    }

    public void checkUserLiveRequest(){
        userRequestModel.userLiveRequestCheck(new ObjectInterfaces.SimpleCallback() {
            @Override
            public void onSuccess(boolean status, String res) {
                if(status){
                    constantAssignObj.hasRequest = true;
                    constantAssignObj.sendBtnText.setText("View Bids");
                }else{
                    constantAssignObj.hasRequest = false;
                    constantAssignObj.sendBtnText.setText("Send Parcel");
                }
            }
        });
    }

    public void remLisCheckUserLiveReq(){
        userRequestModel.remLisUserLiveReqCheck();
    }

    public void checkUserActiveRequest(){
        progressBarObj.showProgressDialog();
        userRequestModel.userActiveRequestCheck(new ObjectInterfaces.SimpleCallback() {
            @Override
            public void onSuccess(boolean status, String res) {
                progressBarObj.hideProgressDialog();
                if(status){
                    activity.finishAffinity();
                    activity.startActivity(new Intent(activity, RequestActiveActivity.class));
                }
            }
        });
    }


}
