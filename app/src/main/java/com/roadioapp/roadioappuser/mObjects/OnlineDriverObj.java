package com.roadioapp.roadioappuser.mObjects;

import android.app.Activity;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roadioapp.roadioappuser.mModels.OnlineDriver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineDriverObj {

    private ConstantAssign constantAssignObj;
    private OnlineDriver onlineDriverModuleObj;

    //Firebase Variables
    private DatabaseReference onlineDrivers;

    private Map<String, Marker> ODMarkers;
    private JSONObject MarkerIcons;
    private List<String> ODKeys;

    public OnlineDriverObj(ConstantAssign constantAssign, Activity act){
        this.constantAssignObj = constantAssign;

        onlineDrivers = FirebaseDatabase.getInstance().getReference().child("online_drivers");

        ODMarkers = new HashMap<String, Marker>();
        ODKeys = new ArrayList<String>();
        MarkerIcons = new JSONObject();
        try {
            MarkerIcons.put("Bike", act.getResources().getIdentifier("top_bike", "drawable", "com.roadioapp.roadioappuser"));
            MarkerIcons.put("Car", act.getResources().getIdentifier("top_car", "drawable", "com.roadioapp.roadioappuser"));
            MarkerIcons.put("Pickup", act.getResources().getIdentifier("top_van", "drawable", "com.roadioapp.roadioappuser"));
            MarkerIcons.put("Truck", act.getResources().getIdentifier("top_truck", "drawable", "com.roadioapp.roadioappuser"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        startTrackOnlineDriver();
    }

    private void startTrackOnlineDriver(){
        onlineDrivers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    updateMarkers(child);
                }
                onlineDrivers.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        updateMarkers(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        updateMarkers(dataSnapshot);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        removeMarkers(dataSnapshot);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void updateMarkers(DataSnapshot dataSnapshot){
        if (constantAssignObj.mMap != null) {
            onlineDriverModuleObj = dataSnapshot.getValue(OnlineDriver.class);
            if(onlineDriverModuleObj.vehicle != null){
                Marker marker;
                if (ODMarkers.containsKey(onlineDriverModuleObj.uid)) {
                    marker = ODMarkers.get(onlineDriverModuleObj.uid);
                    marker.setPosition(new LatLng(onlineDriverModuleObj.lat, onlineDriverModuleObj.lng));
                    marker.setRotation(onlineDriverModuleObj.direction);
                } else {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(onlineDriverModuleObj.lat, onlineDriverModuleObj.lng));
                    markerOptions.flat(true).anchor(0.5f, 0.5f).rotation(onlineDriverModuleObj.direction);
                    try {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource((int) MarkerIcons.get(onlineDriverModuleObj.vehicle)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    marker = constantAssignObj.mMap.addMarker(markerOptions);
                    ODKeys.add(onlineDriverModuleObj.uid);
                }
                ODMarkers.put(onlineDriverModuleObj.uid, marker);
            }
        }
    }

    private void removeMarkers(DataSnapshot dataSnapshot){
        if(constantAssignObj.mMap != null){
            onlineDriverModuleObj = dataSnapshot.getValue(OnlineDriver.class);
            if(ODMarkers.containsKey(onlineDriverModuleObj.uid)){
                sMarkerRemove(onlineDriverModuleObj.uid);
                ODKeys.remove(onlineDriverModuleObj.uid);
                ODMarkers.remove(onlineDriverModuleObj.uid);
            }
        }
    }

    private void sMarkerRemove(String uid){
        Marker marker = ODMarkers.get(uid);
        marker.remove();
    }



}
