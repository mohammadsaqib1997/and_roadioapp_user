package com.roadioapp.roadioappuser.mObjects;

import android.app.Dialog;
import android.content.Context;
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
import com.roadioapp.roadioappuser.mModels.UserRequest;

public class RequestParcelObj {

    private Context context;
    private ButtonEffects btnEffectsObj;
    private UserRequest userRequestModel;
    private ConstantAssign constantAssignObj;

    public RequestParcelObj(Context ctx, ConstantAssign constantAssign){
        this.context = ctx;
        this.btnEffectsObj = new ButtonEffects(ctx);
        this.userRequestModel = new UserRequest(ctx, constantAssign);
        this.constantAssignObj = constantAssign;
    }

    public void sendReqConfirmDialog() {
        final Dialog dialog = new Dialog(context);
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
                userRequestModel.postReqParcel(new UserRequest.UserReqCallbacks() {
                    @Override
                    public void onSuccess(boolean res) {
                        dialog.dismiss();
                        if(res){
                            resetReqSendParcel();
                            sendReqSuccessDialog();
                            constantAssignObj.hasRequest = true;
                            constantAssignObj.sendBtnText.setText("View Bids");
                        }else {
                            Toast.makeText(context, "Bad Request!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void sendReqSuccessDialog(){
        final Dialog dialog = new Dialog(context);
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

    public void checkUserRequest(){
        userRequestModel.userRequestCheck(new UserRequest.UserReqCheckCallbacks() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    constantAssignObj.hasRequest = true;
                    constantAssignObj.sendBtnText.setText("View Bids");
                }
            }
        });
    }


}
