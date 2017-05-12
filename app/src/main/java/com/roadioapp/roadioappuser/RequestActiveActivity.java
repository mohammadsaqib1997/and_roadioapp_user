package com.roadioapp.roadioappuser;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.database.DataSnapshot;
import com.roadioapp.roadioappuser.mModels.UserActiveRequest;
import com.roadioapp.roadioappuser.mObjects.ButtonEffects;

public class RequestActiveActivity extends AppCompatActivity implements OnMapReadyCallback {

    MapFragment mapFragment;
    GoogleMap mMap;
    ButtonEffects btnEffects;

    LinearLayout stsPendingCon, stsActiveCon, stsCompleteCon, contactDBtn, ratingCon;

    UserActiveRequest userActiveRequestModel;

    String[] statusArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_active);

        setProperties();

    }

    private void setProperties(){
        statusArr = getResources().getStringArray(R.array.req_status);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        stsPendingCon = (LinearLayout) findViewById(R.id.sts_pending_con);
        stsActiveCon = (LinearLayout) findViewById(R.id.sts_active_con);
        stsCompleteCon = (LinearLayout) findViewById(R.id.sts_complete_con);

        ratingCon = (LinearLayout) findViewById(R.id.ratingCon);
        contactDBtn = (LinearLayout) findViewById(R.id.contactDBtn);

        userActiveRequestModel = new UserActiveRequest(this);
        userActiveRequestModel.userActReqStatusCall(new UserActiveRequest.UserActReqStatusCallback() {
            @Override
            public void onSuccess(boolean status, String err, DataSnapshot dataSnapshot) {
                String reqStatus = dataSnapshot.child("status").getValue().toString();
                statusChangeUI(reqStatus);
            }
        });

        btnEffects = new ButtonEffects(this);

        LinearLayout contactDBtn = (LinearLayout) findViewById(R.id.contactDBtn);
        btnEffects.btnEventEffRounded(contactDBtn);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("MapTag", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapTag", "Can't find style. Error: ", e);
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        LatLng karachi = new LatLng(24.861462, 67.009939);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(karachi, 10));
    }

    private void statusChangeUI(String reqSts){
        if(reqSts != null && reqSts.equals(statusArr[0])){
            stsUI_inAct(stsPendingCon);
            stsUI_inAct(stsActiveCon);
            stsUI_inAct(stsCompleteCon);
        }else if(reqSts != null && reqSts.equals(statusArr[1])){
            stsUI_act(stsPendingCon);
            stsUI_inAct(stsActiveCon);
            stsUI_inAct(stsCompleteCon);
        }else if(reqSts != null && reqSts.equals(statusArr[2])){
            stsUI_act(stsPendingCon);
            stsUI_act(stsActiveCon);
            stsUI_inAct(stsCompleteCon);
        }else if(reqSts != null && reqSts.equals(statusArr[3])){
            stsUI_act(stsPendingCon);
            stsUI_act(stsActiveCon);
            stsUI_act(stsCompleteCon);
        }else{
            Log.e("CheckCallBacks", "Req No Status Found!");
        }

        if(reqSts != null && reqSts.equals(statusArr[3])){
            setRatingCon_act();
        }else{
            setRatingCon_inAct();
        }
    }

    private void stsUI_act(LinearLayout layout){
        layout.setAlpha(1);
        ImageView stsPenImgView = (ImageView) layout.getChildAt(0);
        stsPenImgView.setBackground(getResources().getDrawable(R.drawable.ic_success, null));
    }

    private void stsUI_inAct(LinearLayout layout){
        layout.setAlpha(0.5f);
        ImageView stsImgView = (ImageView) layout.getChildAt(0);
        stsImgView.setBackground(getResources().getDrawable(R.drawable.ic_proccess, null));
    }

    private void setRatingCon_act(){
        ratingCon.setVisibility(View.VISIBLE);
        contactDBtn.setVisibility(View.GONE);
    }

    private void setRatingCon_inAct(){
        ratingCon.setVisibility(View.GONE);
        contactDBtn.setVisibility(View.VISIBLE);
    }
}
