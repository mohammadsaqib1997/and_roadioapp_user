package com.roadioapp.roadioappuser;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.roadioapp.roadioappuser.mInterfaces.ObjectInterfaces;
import com.roadioapp.roadioappuser.mModels.UserActiveRequest;
import com.roadioapp.roadioappuser.mObjects.ButtonEffects;
import com.roadioapp.roadioappuser.mObjects.mProgressBar;
import com.roadioapp.roadioappuser.transforms.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewBidAdapter extends RecyclerView.Adapter<ViewBidAdapter.ViewHolder> {

    private HashMap<String, HashMap> saveData;
    private Activity activity;

    private String reqId;
    private mProgressBar mProgressBar;
    private StorageReference mProfileImageRef;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, vehicle_type, bidAmount;
        public LinearLayout list_item;
        public ImageView pro_img;

        public ViewHolder(View v) {
            super(v);
            list_item = (LinearLayout) v.findViewById(R.id.list_item);
            title = (TextView) v.findViewById(R.id.title);
            vehicle_type = (TextView) v.findViewById(R.id.vehicle_type);
            bidAmount = (TextView) v.findViewById(R.id.bidAmount);
            pro_img = (ImageView) v.findViewById(R.id.pro_img);
        }
    }

    public ViewBidAdapter(Activity mAct, HashMap<String, HashMap> dataSet) {
        this.saveData = dataSet;
        this.activity = mAct;
        mProgressBar = new mProgressBar(activity);
    }

    @Override
    public ViewBidAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bid_view_layout, parent, false);
        mProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile_images");
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewBidAdapter.ViewHolder holder, final int position) {

        final HashMap data = (new ArrayList<HashMap>(saveData.values())).get(position);
        holder.title.setText(""+data.get("username"));
        holder.vehicle_type.setText("Vehicle: " + data.get("user_vehicle"));
        holder.bidAmount.setText("PKR " + data.get("amount"));

        holder.list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmBidDialog(activity, data.get("id")+"", reqId);
            }
        });
        mProfileImageRef.child(data.get("id")+".jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Picasso.with(activity).load(task.getResult()).placeholder(R.drawable.circle_img).transform(new CircleTransform()).into(holder.pro_img);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return saveData.size();
    }

    private void confirmBidDialog(Context context, final String bidUid, final String reqId) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.confirm_dialog);
        TextView msg = (TextView) dialog.findViewById(R.id.msgCon);
        msg.setText(R.string.confirm_bid_msg);
        LinearLayout btnsCon = (LinearLayout) dialog.findViewById(R.id.btnsCon);
        LinearLayout cancelBtn = (LinearLayout) dialog.findViewById(R.id.cancelBtn);
        LinearLayout yesBtn = (LinearLayout) dialog.findViewById(R.id.yesBtn);
        ButtonEffects btnEffectsObj = new ButtonEffects(activity);
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
                dialog.dismiss();
                mProgressBar.showProgressDialog();
                UserActiveRequest userActiveRequest = new UserActiveRequest(activity);
                userActiveRequest.userReqAct(reqId, bidUid, new ObjectInterfaces.SimpleCallback() {
                    @Override
                    public void onSuccess(boolean status, String err) {
                        mProgressBar.hideProgressDialog();
                        if(!status){
                            Toast.makeText(activity, err, Toast.LENGTH_SHORT).show();
                        }else{
                            activity.finishAffinity();
                            activity.startActivity(new Intent(activity, RequestActiveActivity.class));
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    public void setReqId(String reqId){
        this.reqId = reqId;
    }
}
