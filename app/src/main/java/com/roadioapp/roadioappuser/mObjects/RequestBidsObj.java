package com.roadioapp.roadioappuser.mObjects;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roadioapp.roadioappuser.R;
import com.roadioapp.roadioappuser.ViewBidAdapter;
import com.roadioapp.roadioappuser.mInterfaces.ObjectInterfaces;
import com.roadioapp.roadioappuser.mModels.DriverBid;
import com.roadioapp.roadioappuser.mModels.UserInfo;

import java.util.HashMap;

public class RequestBidsObj {

    private DatabaseReference userLiveRequestCollection, driverBidsCollection, driverBidsCollectionChildRef;
    private ChildEventListener bidChildListener;

    private AuthObj mAuthObj;
    private UserInfo userInfo2;
    private DriverBid driverBid;

    private HashMap<String, HashMap> mList;
    private HashMap<String, String> itemData;
    private ViewBidAdapter vbAdapter;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressBar progressBar;

    public RequestBidsObj(Activity activity){
        setProps(activity);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        driverBidsCollection = mDatabase.child("driver_bids");
        userLiveRequestCollection = mDatabase.child("user_live_requests");

        mAuthObj = new AuthObj(activity);
        userInfo2 = new UserInfo(activity);
    }

    public void removeReq(final ObjectInterfaces.SimpleCallback callback){
        if(mAuthObj.isLoginUser()){
            userLiveRequestCollection.child(mAuthObj.authUid).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError != null){
                        callback.onSuccess(false, databaseError.getMessage());
                    }else{
                        callback.onSuccess(true, "");
                    }
                }
            });
        }else{
            callback.onSuccess(false, "Auth Not Found!");
        }
    }

    public void getBids(){
        if(mAuthObj.authUid != null){
            userLiveRequestCollection.child(mAuthObj.authUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        final String reqID = dataSnapshot.child("reqId").getValue()+"";
                        vbAdapter.setReqId(reqID);
                        driverBidsCollectionChildRef = driverBidsCollection.child(reqID);
                        bidChildListener = new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                bidUpdate(dataSnapshot);
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                bidUpdate(dataSnapshot);
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        driverBidsCollectionChildRef.addChildEventListener(bidChildListener);
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void bidUpdate(final DataSnapshot snapshot){
        userInfo2.getUserInfo(snapshot.getKey(), new UserInfo.UserCallback() {
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
        });
    }

    private void setProps(Activity activity){
        progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) activity.findViewById(R.id.my_recycler_view);

        mLayoutManager = new LinearLayoutManager(activity);
        mList = new HashMap<String, HashMap>();
        vbAdapter = new ViewBidAdapter(activity, mList);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(vbAdapter);
    }

    public void removeBidListener(){
        driverBidsCollectionChildRef.removeEventListener(bidChildListener);
    }



}
