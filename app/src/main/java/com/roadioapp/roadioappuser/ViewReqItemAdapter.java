package com.roadioapp.roadioappuser;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roadioapp.roadioappuser.mInterfaces.ObjectInterfaces;
import com.roadioapp.roadioappuser.mModels.UserActiveRequest;
import com.roadioapp.roadioappuser.mObjects.ButtonEffects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ViewReqItemAdapter extends RecyclerView.Adapter<ViewReqItemAdapter.ViewHolder> {

    private LinkedHashMap<String, HashMap> saveData;
    private Activity activity;

    private String reqId;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView orgTV, desTV, timeTV;
        /*public TextView title, vehicle_type, bidAmount;
        public LinearLayout list_item;*/

        public ViewHolder(View v) {
            super(v);
            orgTV = (TextView) v.findViewById(R.id.orgTV);
            desTV = (TextView) v.findViewById(R.id.desTV);
            timeTV = (TextView) v.findViewById(R.id.timeTV);
            /*list_item = (LinearLayout) v.findViewById(R.id.list_item);
            title = (TextView) v.findViewById(R.id.title);
            vehicle_type = (TextView) v.findViewById(R.id.vehicle_type);
            bidAmount = (TextView) v.findViewById(R.id.bidAmount);*/
        }
    }

    public ViewReqItemAdapter(Activity mAct, LinkedHashMap<String, HashMap> dataSet) {
        this.saveData = dataSet;
        this.activity = mAct;
    }

    @Override
    public ViewReqItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item_view_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewReqItemAdapter.ViewHolder holder, final int position) {
        final LinkedHashMap data = (new ArrayList<LinkedHashMap>(saveData.values())).get(position);
        holder.orgTV.setText(data.get("origin")+"");
        holder.desTV.setText(data.get("destination")+"");
        holder.timeTV.setText(data.get("time")+"");

        /*final HashMap data = (new ArrayList<HashMap>(saveData.values())).get(position);
        holder.title.setText(""+data.get("username"));
        holder.vehicle_type.setText("Vehicle: " + data.get("user_vehicle"));
        holder.bidAmount.setText("PKR " + data.get("amount"));

        holder.list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Check", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return saveData.size();
    }


    public void setReqId(String reqId){
        this.reqId = reqId;
    }
}
