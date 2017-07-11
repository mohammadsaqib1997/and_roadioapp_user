package com.roadioapp.roadioappuser;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roadioapp.roadioappuser.mModels.DriverBid;
import com.roadioapp.roadioappuser.mModels.UserInfo;
import com.roadioapp.roadioappuser.mModels.UserRequest;
import com.roadioapp.roadioappuser.mObjects.AuthObj;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

public class MyDeliveriesActivity extends AppCompatActivity {

    private Activity activity;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ViewReqItemAdapter viewReqItemAdapter;

    private LinkedHashMap<String, HashMap> dataLoad;
    private LinkedHashMap<String, String> itemData;

    private ProgressBar progressBar;

    private AuthObj mAuthObj;
    private UserRequest userRequestsModel;
    //Firebase variables
    DatabaseReference userRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_deliveries);

        activity = this;

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) findViewById(R.id.listRequestItems);

        dataLoad = new LinkedHashMap<String, HashMap>();
        mLayoutManager = new LinearLayoutManager(activity);
        viewReqItemAdapter = new ViewReqItemAdapter(activity, dataLoad);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(viewReqItemAdapter);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        userRequests = mDatabase.child("user_requests");

        mAuthObj = new AuthObj(activity);

        getRequestList();
    }

    private void getRequestList(){
        if(mAuthObj.isLoginUser()){
            userRequests.child(mAuthObj.authUid).orderByKey().limitToFirst(15).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for (DataSnapshot child: dataSnapshot.getChildren()){
                            reqItemUpdate(child);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("DBError", databaseError.getMessage());
                }
            });
        }else{
            Toast.makeText(activity, "Auth not found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void reqItemUpdate(final DataSnapshot snapshot){
        userRequestsModel = snapshot.getValue(UserRequest.class);
        itemData = new LinkedHashMap<String, String>();
        itemData.put("id", snapshot.getKey());
        itemData.put("origin", userRequestsModel.orgText);
        itemData.put("destination", userRequestsModel.desText);
        itemData.put("time", userRequestsModel.getFormatDate());
        dataLoad.put(snapshot.getKey(), itemData);
        viewReqItemAdapter.notifyDataSetChanged();
        if(mRecyclerView.getVisibility() != View.VISIBLE){
            progressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }


        /*userInfo2.getUserInfo(snapshot.getKey(), new UserInfo.UserCallback() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot, String errMsg) {
                if(dataSnapshot.exists()){
                    driverBid = snapshot.getValue(DriverBid.class);

                    itemData = new HashMap<String, String>();
                    itemData.put("id", snapshot.getKey());
                    itemData.put("amount", driverBid.amount);
                    itemData.put("username", userInfo2.fullName());
                    itemData.put("user_vehicle", userInfo2.getVehicle());
                    mList.put(dataSnapshot.getKey(), itemData);
                    vbAdapter.notifyDataSetChanged();
                    if(mRecyclerView.getVisibility() != View.VISIBLE){
                        progressBar.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });*/
    }
}
