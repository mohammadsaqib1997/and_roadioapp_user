package com.roadioapp.roadioappuser.mObjects;

import android.app.Activity;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

public class MapObject {

    private ConstantAssign constantAssignObj;
    private Activity activity;

    public MapObject(ConstantAssign constantAssign, Activity act){
        this.constantAssignObj = constantAssign;
        this.activity = act;

    }

    public void mapMoveCam(LatLng latLng, LatLngBounds latLngBounds, boolean uMoveCam, boolean anim) {
        constantAssignObj.mMap.getUiSettings().setAllGesturesEnabled(false);
        if (latLngBounds != null) {
            if(anim){
                constantAssignObj.mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, getHeightWidth("w"), 500, 100));
            }else {
                constantAssignObj.mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, getHeightWidth("w"), 500, 100));
            }
        } else {
            if(anim){
                constantAssignObj.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            }else{
                constantAssignObj.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            }
        }
        constantAssignObj.userCamMove = uMoveCam;
    }

    public boolean setMapCameraBound() {
        if (constantAssignObj.pickLocMarker != null && constantAssignObj.desLocMarker != null) {
            constantAssignObj.mMap.setMyLocationEnabled(false);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(constantAssignObj.pickLocMarker.getPosition());
            builder.include(constantAssignObj.desLocMarker.getPosition());
            LatLngBounds bounds = builder.build();
            mapMoveCam(null, bounds, false, true);
            return true;
        }
        return false;
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

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = constantAssignObj.mMap.getProjection();
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
    }
}
