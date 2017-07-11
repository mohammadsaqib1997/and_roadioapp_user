package com.roadioapp.roadioappuser.mObjects;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.roadioapp.roadioappuser.MyDeliveriesActivity;
import com.roadioapp.roadioappuser.R;
import com.roadioapp.roadioappuser.SettingActivity;
import com.roadioapp.roadioappuser.mInterfaces.DBCallbacks;
import com.roadioapp.roadioappuser.mModels.UserInfo;
import com.roadioapp.roadioappuser.transforms.CircleTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class NavigationFuncObj {

    private Activity activity;
    private ConstantAssign constantAssignObj;
    private RequestParcelObj requestParcelObj;
    private AuthObj mAuthObj;
    private StorageReference mProfileImageRef;
    private boolean drawerState = false;

    private UserInfo userInfoModel;

    public NavigationFuncObj(Activity activity, ConstantAssign constantAssign, RequestParcelObj requestParcelObj, AuthObj authObj){
        this.activity = activity;
        this.constantAssignObj = constantAssign;
        this.requestParcelObj = requestParcelObj;
        this.mAuthObj = authObj;
        this.userInfoModel = new UserInfo(activity);
        mProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile_images");
    }

    public void assignNavigationFunc(){
        constantAssignObj.navMenuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (constantAssignObj.reqSendParcel) {
                    requestParcelObj.resetReqSendParcel();
                } else {
                    if (drawerState) {
                        constantAssignObj.drawer_layout.closeDrawer(Gravity.START);
                    } else {
                        constantAssignObj.drawer_layout.openDrawer(Gravity.START);
                    }
                }
            }
        });
        constantAssignObj.drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerState = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerState = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                /*if (newState == DrawerLayout.STATE_SETTLING) {
                    if (drawerState) {
                        menuCloseBtn();
                    } else {
                        menuOpenBtn();
                    }
                }
                if (newState == DrawerLayout.STATE_IDLE) {
                    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                        if (!drawerState) {
                            menuOpenBtn();
                        }
                    } else {
                        if (drawerState) {
                            menuCloseBtn();
                        }
                    }
                }*/
            }
        });
        constantAssignObj.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });
        userInfoModel.getMyInfo(new DBCallbacks.CompleteListener() {
            @Override
            public void onSuccess(boolean status, String msg) {
                if(status){
                    constantAssignObj.smUsernameTV.setText(userInfoModel.first_name+" "+userInfoModel.last_name);
                }else{
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mProfileImageRef.child(mAuthObj.authUid+".jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Picasso.with(activity).load(task.getResult()).placeholder(R.drawable.circle_img).transform(new CircleTransform()).into(constantAssignObj.userProfileImg);
                }
            }
        });

        constantAssignObj.smSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, SettingActivity.class));
            }
        });

        for(int i = 0; i < constantAssignObj.smListCont.getChildCount(); i++){
            LinearLayout ListItem = (LinearLayout) constantAssignObj.smListCont.getChildAt(i);
            ListItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(view.getId() == R.id.myDeliveriesBtn){
                        activity.startActivity(new Intent(activity, MyDeliveriesActivity.class));
                    }else{
                        Toast.makeText(activity, "Coming Soon!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setMessage("Are you sure! You want to Logout!");
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //signOut();
                mAuthObj.signOut();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



}
