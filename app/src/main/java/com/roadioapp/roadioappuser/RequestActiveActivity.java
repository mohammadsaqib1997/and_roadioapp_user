package com.roadioapp.roadioappuser;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.roadioapp.roadioappuser.mInterfaces.DBCallbacks;
import com.roadioapp.roadioappuser.mInterfaces.ObjectInterfaces;
import com.roadioapp.roadioappuser.mModels.ActiveDriver;
import com.roadioapp.roadioappuser.mModels.UserActiveRequest;
import com.roadioapp.roadioappuser.mModels.UserInfo;
import com.roadioapp.roadioappuser.mModels.UserRequest;
import com.roadioapp.roadioappuser.mObjects.ButtonEffects;
import com.roadioapp.roadioappuser.mObjects.ConstantAssign;
import com.roadioapp.roadioappuser.mObjects.DirectionObject;
import com.roadioapp.roadioappuser.mObjects.GPSObject;
import com.roadioapp.roadioappuser.mObjects.MapObjectTwo;
import com.roadioapp.roadioappuser.mObjects.PermissionCheckObj;
import com.roadioapp.roadioappuser.mObjects.mProgressBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequestActiveActivity extends AppCompatActivity implements OnMapReadyCallback {

    Activity activity;

    MapFragment mapFragment;
    GoogleMap mMap;
    DateFormat formatter;
    LatLngBounds.Builder selectedBounds;
    LatLngBounds selBoundsLLB;

    LinearLayout
            stsPendingCon,
            stsActiveCon,
            stsCompleteCon,
            contactDBtn,
            subRatingBtn,
            ratingCon,
            star_rating_con,
            setCurLocBtn;
    TextView complete_time_TV, active_time_TV;

    UserActiveRequest userActiveRequestModel;
    UserInfo userInfoModel;
    UserRequest userRequestModel;
    ActiveDriver activeDriverModel;

    String[] statusArr;
    int saveStars = 0;
    long active_time = 0, complete_time = 0;
    boolean firstRes = true;
    String reqDriverMob = "";
    String reqDriverVehicle = "";
    String reqStatus;
    List<Polyline> polylinePaths = new ArrayList<>();

    ButtonEffects btnEffects;
    mProgressBar progressBarObj;

    PermissionCheckObj permissionCheckObj;
    GPSObject gpsObj;
    MapObjectTwo mapObj;
    DirectionObject directionObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_active);
        activity = this;

        setProperties();

    }

    @Override
    protected void onStop() {
        userActiveRequestModel.userActReqStatusCallRemove();
        super.onStop();
    }

    private void setProperties() {
        formatter = new SimpleDateFormat("dd MMM, yyyy hh:mm:ss a");

        statusArr = getResources().getStringArray(R.array.req_status);
        permissionCheckObj = new PermissionCheckObj(this);
        progressBarObj = new mProgressBar(this);
        gpsObj = new GPSObject(this);
        mapObj = new MapObjectTwo(this);
        directionObj = new DirectionObject(this);

        userInfoModel = new UserInfo(this);
        userActiveRequestModel = new UserActiveRequest(this);
        userRequestModel = new UserRequest(this);
        activeDriverModel = new ActiveDriver(this);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        stsPendingCon = (LinearLayout) findViewById(R.id.sts_pending_con);
        stsActiveCon = (LinearLayout) findViewById(R.id.sts_active_con);
        stsCompleteCon = (LinearLayout) findViewById(R.id.sts_complete_con);

        setCurLocBtn = (LinearLayout) findViewById(R.id.setCurLocBtn);
        setCurLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selBoundsLLB != null) {
                    mapObj.mapMoveCam(null, selBoundsLLB, true);
                }
            }
        });

        complete_time_TV = (TextView) findViewById(R.id.complete_time_TV);
        active_time_TV = (TextView) findViewById(R.id.active_time_TV);

        ratingCon = (LinearLayout) findViewById(R.id.ratingCon);
        contactDBtn = (LinearLayout) findViewById(R.id.contactDBtn);
        contactDBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContactDialog();
            }
        });

        subRatingBtn = (LinearLayout) findViewById(R.id.subRatingBtn);
        subRatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveStars > 0) {
                    progressBarObj.showProgressDialog();
                    userActiveRequestModel.completeJob(saveStars, new ObjectInterfaces.SimpleCallback() {
                        @Override
                        public void onSuccess(boolean status, String err) {
                            progressBarObj.hideProgressDialog();
                            if (status) {
                                finishAffinity();
                                startActivity(new Intent(RequestActiveActivity.this, MapActivity.class));
                            } else {
                                Toast.makeText(RequestActiveActivity.this, err, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RequestActiveActivity.this, "Please rate your Rider!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        star_rating_con = (LinearLayout) findViewById(R.id.star_rating_con);
        for (int i = 0; i < star_rating_con.getChildCount(); i++) {
            final int ind = i + 1;
            ImageView star = (ImageView) star_rating_con.getChildAt(i);
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveStars = ind;
                    act_stars(ind);
                }
            });
        }

        btnEffects = new ButtonEffects(this);

        btnEffects.btnEventEffRounded(contactDBtn);
        btnEffects.btnEventEffRounded(subRatingBtn);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapObj.setMap(googleMap);
        mapObj.setDefaultMapListner();
        mapObj.setTrackingContent();

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

        mapObj.mapMoveCam(mapObj.karachi, null, false);
        mapReadyContentLoad();
    }

    private void mapReadyContentLoad() {
        progressBarObj.showProgressDialog();
        userActiveRequestModel.userActReqStatusCall(new ObjectInterfaces.UserActReqStatusCallback() {
            @Override
            public void onSuccess(boolean status, String err, DataSnapshot dataSnapshot) {
                progressBarObj.hideProgressDialog();
                if (dataSnapshot.exists()) {
                    reset_stars();
                    saveStars = 0;
                    final String reqDriverUID = dataSnapshot.child("driver_uid").getValue().toString();
                    final String reqID = dataSnapshot.child("req_id").getValue().toString();
                    reqStatus = dataSnapshot.child("status").getValue().toString();
                    active_time = (Long) dataSnapshot.child("active_time").getValue();
                    complete_time = (Long) dataSnapshot.child("complete_time").getValue();
                    if (firstRes) {
                        userInfoModel.getUserInfo(reqDriverUID, new UserInfo.UserCallback() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot, String errMsg) {
                                if (errMsg != null) {
                                    Toast.makeText(RequestActiveActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                                } else {
                                    reqDriverMob = userInfoModel.getMobNo();
                                    reqDriverVehicle = userInfoModel.getVehicle();
                                    activeDriverModel.trackDriver(reqDriverUID, new DBCallbacks.CompleteDSListener() {
                                        @Override
                                        public void onSuccess(boolean status, String msg, DataSnapshot dataSnapshot) {
                                            if (status) {
                                                if (dataSnapshot.exists()) {
                                                    mapObj.setDriverMarker(dataSnapshot, reqDriverUID, reqDriverVehicle);
                                                    driverDirectionChange(reqStatus, true);
                                                } else {
                                                    if (mapObj.isSetOD_LL()) {
                                                        selectedBounds = new LatLngBounds.Builder();
                                                        selectedBounds.include(mapObj.getDesLL()).include(mapObj.getOrgLL());
                                                        selBoundsLLB = selectedBounds.build();
                                                        mapObj.mapMoveCam(null, selBoundsLLB, true);
                                                        mapObj.removeDriverMarker(reqDriverUID);
                                                    }
                                                }

                                            } else {
                                                Toast.makeText(RequestActiveActivity.this, msg, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        userRequestModel.getUserRequestByReqID(reqID, new DBCallbacks.CompleteDSListener() {
                            @Override
                            public void onSuccess(boolean status, String msg, DataSnapshot dataSnapshot) {
                                if (status) {
                                    UserRequest userRequestData = dataSnapshot.getValue(UserRequest.class);
                                    LatLng orgLL = new LatLng(Double.parseDouble(userRequestData.orgLat), Double.parseDouble(userRequestData.orgLng));
                                    LatLng desLL = new LatLng(Double.parseDouble(userRequestData.desLat), Double.parseDouble(userRequestData.desLng));
                                    selectedBounds = new LatLngBounds.Builder();
                                    selectedBounds.include(orgLL).include(desLL);
                                    mapObj.setOrgMarker(orgLL);
                                    mapObj.setDesMarker(desLL);
                                    selBoundsLLB = selectedBounds.build();
                                    mapObj.mapMoveCam(null, selBoundsLLB, false);
                                    statusChangeUI(reqStatus);
                                } else {
                                    Toast.makeText(RequestActiveActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        firstRes = false;
                    }else{
                        statusChangeUI(reqStatus);
                    }
                }
            }
        });
    }

    private void statusChangeUI(String reqSts) {
        if (reqSts != null && reqSts.equals(statusArr[0])) {
            stsUI_inAct(stsPendingCon);
            stsUI_inAct(stsActiveCon);
            stsUI_inAct(stsCompleteCon);

            setTime(active_time_TV, "00:00");
            setTime(complete_time_TV, "00:00");
        } else if (reqSts != null && reqSts.equals(statusArr[1])) {
            stsUI_act(stsPendingCon);
            stsUI_inAct(stsActiveCon);
            stsUI_inAct(stsCompleteCon);

            setTime(active_time_TV, "00:00");
            setTime(complete_time_TV, "00:00");
        } else if (reqSts != null && reqSts.equals(statusArr[2])) {
            stsUI_act(stsPendingCon);
            stsUI_act(stsActiveCon);
            stsUI_inAct(stsCompleteCon);

            setTime(active_time_TV, convertDate(active_time));
            setTime(complete_time_TV, "00:00");
        } else if (reqSts != null && reqSts.equals(statusArr[3])) {
            stsUI_act(stsPendingCon);
            stsUI_act(stsActiveCon);
            stsUI_act(stsCompleteCon);

            setTime(active_time_TV, convertDate(active_time));
            setTime(complete_time_TV, convertDate(complete_time));
        } else {
            Log.e("CheckCallBacks", "Req No Status Found!");
        }

        updateSelectedBounds(reqSts);

        if (reqSts != null && reqSts.equals(statusArr[3])) {
            setRatingCon_act();
        } else {
            setRatingCon_inAct();
        }
    }

    private void updateSelectedBounds(String reqSts) {
        if (mapObj.isSetOD_LL() && reqSts != null) {
            selectedBounds = new LatLngBounds.Builder();
            boolean chk = true;

            if (reqSts.equals(statusArr[0])) {
                selectedBounds.include(mapObj.getDesLL()).include(mapObj.getOrgLL());

            } else if (reqSts.equals(statusArr[1]) && mapObj.driverLL != null) {
                selectedBounds.include(mapObj.driverLL).include(mapObj.getOrgLL());

            } else if (reqSts.equals(statusArr[2]) && mapObj.driverLL != null) {
                selectedBounds.include(mapObj.driverLL).include(mapObj.getDesLL());

            } else if (reqSts.equals(statusArr[3])) {
                selectedBounds.include(mapObj.getDesLL()).include(mapObj.getOrgLL());

            } else {
                chk = false;

            }

            if (chk) {
                selBoundsLLB = selectedBounds.build();
                mapObj.mapMoveCam(null, selBoundsLLB, true);
            }
        }
        driverDirectionChange(reqSts, false);
    }

    private void driverDirectionChange(String reqSts, boolean track) {
        if (mapObj.isSetOD_LL() && reqSts != null) {
            directionObj.resetDirection(polylinePaths);
            LatLng selLL1 = null, selLL2 = null;

            if (reqSts.equals(statusArr[1])) {
                selLL1 = mapObj.driverLL;
                selLL2 = mapObj.getOrgLL();
            } else if (reqSts.equals(statusArr[2])) {
                selLL1 = mapObj.driverLL;
                selLL2 = mapObj.getDesLL();
            } else {
                selLL1 = mapObj.getOrgLL();
                selLL2 = mapObj.getDesLL();
            }

            if(reqSts.equals(statusArr[1]) || reqSts.equals(statusArr[2])){
                if(track){
                    directionObj.retDirection(selLL1, selLL2, new DBCallbacks.CompleteDirectionCall() {
                        @Override
                        public void onSuccess(boolean status, String msg, PolylineOptions polylineOptions) {
                            if (status) {
                                polylinePaths.add(mMap.addPolyline(polylineOptions));
                            } else {
                                Toast.makeText(RequestActiveActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }else{
                if(!track){
                    directionObj.retDirection(selLL1, selLL2, new DBCallbacks.CompleteDirectionCall() {
                        @Override
                        public void onSuccess(boolean status, String msg, PolylineOptions polylineOptions) {
                            if (status) {
                                polylinePaths.add(mMap.addPolyline(polylineOptions));
                            } else {
                                Toast.makeText(RequestActiveActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }
    }

    private void stsUI_act(LinearLayout layout) {
        layout.setAlpha(1);
        ImageView stsPenImgView = (ImageView) layout.getChildAt(0);
        stsPenImgView.setBackground(ContextCompat.getDrawable(activity, R.drawable.ic_success));
    }

    private void stsUI_inAct(LinearLayout layout) {
        layout.setAlpha(0.5f);
        ImageView stsImgView = (ImageView) layout.getChildAt(0);
        stsImgView.setBackground(ContextCompat.getDrawable(activity, R.drawable.ic_proccess));
    }

    private void setRatingCon_act() {
        ratingCon.setVisibility(View.VISIBLE);
        contactDBtn.setVisibility(View.GONE);
    }

    private void setRatingCon_inAct() {
        ratingCon.setVisibility(View.GONE);
        contactDBtn.setVisibility(View.VISIBLE);
    }

    private void act_stars(final int stars) {
        reset_stars();
        for (int i = 0; i < stars; i++) {
            ImageView star = (ImageView) star_rating_con.getChildAt(i);
            star.setBackground(ContextCompat.getDrawable(activity, R.drawable.star_active));
        }
    }

    private void reset_stars() {
        for (int i = 0; i < star_rating_con.getChildCount(); i++) {
            ImageView star = (ImageView) star_rating_con.getChildAt(i);
            star.setBackground(ContextCompat.getDrawable(activity, R.drawable.star_inactive));
        }
    }

    private void showContactDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.contact_dialog);
        LinearLayout btnsCon = (LinearLayout) dialog.findViewById(R.id.btnsCon);
        LinearLayout callBtn = (LinearLayout) dialog.findViewById(R.id.callBtn);
        LinearLayout smsBtn = (LinearLayout) dialog.findViewById(R.id.smsBtn);
        for (int i = 0; i < btnsCon.getChildCount(); i++) {
            LinearLayout selectBtn = (LinearLayout) btnsCon.getChildAt(i);
            btnEffects.btnEventEffRounded(selectBtn);
        }
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionCheckObj.callPermissionCheck()) {
                    callDriverIntent();
                } else {
                    permissionCheckObj.setCallPermission();
                }
            }
        });
        smsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsDriverIntent();
            }
        });
        dialog.show();
    }

    public void callDriverIntent() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:+" + reqDriverMob));
        startActivity(callIntent);
    }

    private void smsDriverIntent() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:+" + reqDriverMob)));
    }

    private void setTime(TextView tv, String text) {
        tv.setText(text);
    }

    private String convertDate(long timestamp) {
        if (timestamp == 0) {
            return "00:00";
        } else {
            Date date = new Date(timestamp);
            return formatter.format(date);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ConstantAssign.PERM_REQUEST_CALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callDriverIntent();
                } else {
                    Toast.makeText(RequestActiveActivity.this, "Call Permission Denied", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
