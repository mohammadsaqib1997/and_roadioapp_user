package com.roadioapp.roadioappuser.mObjects;

import android.app.Activity;
import android.graphics.Point;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.roadioapp.roadioappuser.mModels.ActiveDriver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapObjectTwo {

    private Activity activity;
    private GoogleMap mMap;

    public LatLng driverLL;
    public LatLng karachi;

    private Marker orgMarker, desMarker;

    private Map<String, Marker> ADMarker;
    private JSONObject MarkerIcons;

    private ActiveDriver activeDriverData;

    public MapObjectTwo(Activity act){
        this.activity = act;
        karachi = new LatLng(24.861462, 67.009939);
    }

    public void setMap(GoogleMap map){
        this.mMap = map;
    }

    public void mapMoveCam(LatLng latLng, LatLngBounds latLngBounds, boolean anim) {
        mMap.getUiSettings().setAllGesturesEnabled(false);
        if (latLngBounds != null) {
            if (anim) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, getHeightWidth("w"), 500, 100));
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, getHeightWidth("w"), 500, 100));
            }
        } else if(latLng != null) {
            if (anim) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            }
        }
    }

    public void setDefaultMapListner(){
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mMap.getUiSettings().setAllGesturesEnabled(true);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });
    }

    private int getHeightWidth(String arg) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if (arg.equals("h")) {
            return displayMetrics.heightPixels;
        } else if (arg.equals("w")) {
            return displayMetrics.widthPixels;
        }
        return 0;
    }

    public void setTrackingContent(){
        ADMarker = new HashMap<String, Marker>();
        MarkerIcons = new JSONObject();
        try {
            MarkerIcons.put("Bike", activity.getResources().getIdentifier("top_bike", "drawable", "com.roadioapp.roadioappuser"));
            MarkerIcons.put("Car", activity.getResources().getIdentifier("top_car", "drawable", "com.roadioapp.roadioappuser"));
            MarkerIcons.put("Pickup", activity.getResources().getIdentifier("top_van", "drawable", "com.roadioapp.roadioappuser"));
            MarkerIcons.put("Truck", activity.getResources().getIdentifier("top_truck", "drawable", "com.roadioapp.roadioappuser"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setDriverMarker(DataSnapshot dataSnapshot, String key, String driverVehicle){
        if(dataSnapshot != null){
            activeDriverData = dataSnapshot.getValue(ActiveDriver.class);
            driverLL = new LatLng(activeDriverData.lat, activeDriverData.lng);
            Marker marker;
            if(ADMarker.containsKey(key)){
                marker = ADMarker.get(key);
                marker.setPosition(driverLL);
                marker.setRotation(activeDriverData.direction);
            }else{
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(driverLL);
                markerOptions.flat(true).anchor(0.5f, 0.5f).rotation(activeDriverData.direction);
                try {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource((int) MarkerIcons.get(driverVehicle)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                marker = mMap.addMarker(markerOptions);
            }
            ADMarker.put(key, marker);
        }
    }

    public void removeDriverMarker(String UID){
        if(ADMarker.containsKey(UID)){
            Marker marker = ADMarker.get(UID);
            marker.remove();
            ADMarker.remove(UID);
        }
    }

    public void setOrgMarker(LatLng latLng){
        if (orgMarker!=null) {
            orgMarker.setPosition(latLng);
        }else{
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(activity.getResources().getIdentifier("ic_move_pin", "drawable", "com.roadioapp.roadioappuser")));
            orgMarker = mMap.addMarker(markerOptions);
        }
    }

    public void setDesMarker(LatLng latLng){
        if (desMarker!=null) {
            desMarker.setPosition(latLng);
        }else{
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(activity.getResources().getIdentifier("ic_cur_loc_dark", "drawable", "com.roadioapp.roadioappuser")));
            desMarker = mMap.addMarker(markerOptions);
        }
    }

    public LatLng getOrgLL(){
        if(orgMarker != null){
            return orgMarker.getPosition();
        }
        return null;
    }

    public LatLng getDesLL(){
        if(desMarker != null){
            return desMarker.getPosition();
        }
        return null;
    }

    public boolean isSetOD_LL(){
        return orgMarker != null && desMarker != null;
    }

    /*public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }*/


}