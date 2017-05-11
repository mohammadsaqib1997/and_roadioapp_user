package com.roadioapp.roadioappuser;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roadioapp.roadioappuser.mModels.UserActiveRequest;
import com.roadioapp.roadioappuser.mObjects.ButtonEffects;
import com.roadioapp.roadioappuser.mObjects.mProgressBar;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewBidAdapter extends RecyclerView.Adapter<ViewBidAdapter.ViewHolder> {

    private HashMap<String, HashMap> saveData;
    private Activity activity;

    private String reqId;
    private mProgressBar mProgressBar;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, vehicle_type, bidAmount;
        public LinearLayout list_item;

        public ViewHolder(View v) {
            super(v);
            list_item = (LinearLayout) v.findViewById(R.id.list_item);
            title = (TextView) v.findViewById(R.id.title);
            vehicle_type = (TextView) v.findViewById(R.id.vehicle_type);
            bidAmount = (TextView) v.findViewById(R.id.bidAmount);
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
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewBidAdapter.ViewHolder holder, final int position) {

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
                userActiveRequest.userReqAct(reqId, bidUid, new UserActiveRequest.UserActReqCallback() {
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
