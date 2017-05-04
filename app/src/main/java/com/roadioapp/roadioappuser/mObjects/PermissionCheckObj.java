package com.roadioapp.roadioappuser.mObjects;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import static android.os.Build.VERSION_CODES.M;

public class PermissionCheckObj {

    private Activity activity;

    public PermissionCheckObj(Activity act){
        this.activity = act;
    }

    public boolean permissionCheck() {
        if (verCheck()) {
            int hasFineLocationPermission = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void setPermission() {
        if (android.os.Build.VERSION.SDK_INT >= M) {
            activity.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    ConstantAssign.PERM_REQUEST_LOCATION);
        }
    }

    public void showPermissionErr() {
        Toast.makeText(activity, "Your mobile not allowed this Permission!", Toast.LENGTH_LONG).show();
    }

    private boolean verCheck() {
        return android.os.Build.VERSION.SDK_INT >= M;
    }

}
