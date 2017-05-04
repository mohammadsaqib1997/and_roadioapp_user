package com.roadioapp.roadioappuser.mObjects;

import android.app.Activity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.places.Places;

public class MapAndLocationObject {

    public GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;

    private final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private Activity activity;
    private ConstantAssign constantAssignObj;
    private PermissionCheckObj permissionCheckObj;

    public MapAndLocationObject(Activity activity, ConstantAssign constantAssign, PermissionCheckObj permissionCheck){
        this.activity = activity;
        this.constantAssignObj = constantAssign;
        this.permissionCheckObj = permissionCheck;

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    public void connectGoogleClient(){
        if(permissionCheckObj.permissionCheck()){
            mGoogleApiClient.connect();
        }else {
            permissionCheckObj.setPermission();
        }
    }

    public void disconnectGoogleClient(){
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) activity)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) activity)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    public void startLocationUpdates() {
        if (permissionCheckObj.permissionCheck()) {
            if (mGoogleApiClient.isConnected() && !constantAssignObj.mRequestingLocationUpdates) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) activity);
                constantAssignObj.mRequestingLocationUpdates = true;
            }
        }
    }

    public void stopLocationUpdates() {
        if (constantAssignObj.mRequestingLocationUpdates) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) activity);
            constantAssignObj.mRequestingLocationUpdates = false;
        }
    }



}
