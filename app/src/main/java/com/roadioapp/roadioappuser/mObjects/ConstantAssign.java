package com.roadioapp.roadioappuser.mObjects;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.roadioapp.roadioappuser.R;

import java.util.ArrayList;
import java.util.List;

public class ConstantAssign {

    public static final int PERM_REQUEST_LOCATION = 100;
    public static final int PERM_REQUEST_CALL = 200;


    public TextView pickLocCurTV, pickLocDesTV, sendBtnText, estDistance, estTime;
    public LinearLayout sendBtn, estCon, sendReqBtn;
    public ImageView navMenuIcon, desLocClearBtn, logOutBtn, curPinMovable;
    public DrawerLayout drawer_layout;
    public Marker pickLocMarker, desLocMarker;

    public byte[] selImgByteArray, selImgThmbByteArray;
    public String vecTypeTag = "Bike", disText = "", durText = "", pickDesLocation = "";
    final public String pickLocHint = "Pick location?";
    final public String desLocHint = "Destination location?";
    public LatLng LL1, LL2;

    public boolean
            reqSendParcel = false,
            hasRequest = false,
            pickLocation = false,
            bothLocation = false,
            userCamMove = true,
            mRequestingLocationUpdates = false;

    public LatLngBounds PickDesBounds;
    public List<Polyline> polylinePaths = new ArrayList<>();

    public GoogleMap mMap;

    public ConstantAssign(Activity activity){
        pickLocCurTV = (TextView) activity.findViewById(R.id.pickLocCurTV);
        pickLocDesTV = (TextView) activity.findViewById(R.id.pickLocDesTV);

        sendReqBtn = (LinearLayout) activity.findViewById(R.id.sendReqBtn);

        sendBtn = (LinearLayout) activity.findViewById(R.id.sendBtn);
        sendBtnText = (TextView) sendBtn.getChildAt(0);

        estCon = (LinearLayout) activity.findViewById(R.id.estCon);

        desLocClearBtn = (ImageView) activity.findViewById(R.id.desLocClearBtn);

        navMenuIcon = (ImageView) activity.findViewById(R.id.navMenuIcon);
        drawer_layout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);

        NavigationView navView = (NavigationView) drawer_layout.findViewById(R.id.nav_view);
        logOutBtn = (ImageView) navView.findViewById(R.id.logoutBtn);

        estDistance = (TextView) activity.findViewById(R.id.estDistance);
        estTime = (TextView) activity.findViewById(R.id.estTime);

        curPinMovable = (ImageView) activity.findViewById(R.id.curPinMovable);
    }

    /*public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }*/

}
