package com.roadioapp.roadioappuser.mModels;

import android.app.Activity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roadioapp.roadioappuser.mInterfaces.DBCallbacks;
import com.roadioapp.roadioappuser.mInterfaces.ObjectInterfaces;

import java.util.HashMap;
import java.util.Map;

public class ActiveDriver {

    public double lat, lng;
    public float direction;

    private DatabaseReference activeDriverCol;
    private ValueEventListener trackListener;

    public ActiveDriver(){

    }

    public ActiveDriver(Activity activity){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        activeDriverCol = mDatabase.child("active_driver");
    }

    public void trackDriver(String DUID, final DBCallbacks.CompleteDSListener callback){
        trackListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onSuccess(true, "", dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onSuccess(false, databaseError.getMessage(), null);
            }
        };
        activeDriverCol.child(DUID).addValueEventListener(trackListener);
    }

    public void removeTrackDriver(){
        activeDriverCol.removeEventListener(trackListener);
    }



}
