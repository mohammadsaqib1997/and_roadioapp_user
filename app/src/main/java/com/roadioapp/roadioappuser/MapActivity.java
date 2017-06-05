package com.roadioapp.roadioappuser;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roadioapp.roadioappuser.mModels.OnlineDriver;
import com.roadioapp.roadioappuser.mObjects.AuthObj;
import com.roadioapp.roadioappuser.mObjects.ButtonEffects;
import com.roadioapp.roadioappuser.mObjects.CaptureImageObj;
import com.roadioapp.roadioappuser.mObjects.ConstantAssign;
import com.roadioapp.roadioappuser.mObjects.DirectionFuncObj;
import com.roadioapp.roadioappuser.mObjects.GPSObject;
import com.roadioapp.roadioappuser.mObjects.MapAndLocationObject;
import com.roadioapp.roadioappuser.mObjects.MapObject;
import com.roadioapp.roadioappuser.mObjects.NavigationFuncObj;
import com.roadioapp.roadioappuser.mObjects.OnlineDriverObj;
import com.roadioapp.roadioappuser.mObjects.PermissionCheckObj;
import com.roadioapp.roadioappuser.mObjects.RequestParcelObj;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private MapFragment mapFragment;
    private LatLng karachi, moveCamPos;
    private Location mLastKnownLocation;

    // properties variable
    RelativeLayout mainActCon;
    LinearLayout curLocCont, setCurLocBtn, bottomBtnCon, typeContPar;

    final private int PLACE_AUTOCOMPLETE_CURRENT_REQUEST_CODE = 5;
    final private int PLACE_AUTOCOMPLETE_DESTINATION_REQUEST_CODE = 6;

    //Storage Variables
    LatLng curLocLL;
    boolean firstCamMov = true;

    //Objects
    private PermissionCheckObj permissionCheckObj;
    private RequestParcelObj requestParcelObj;
    private ConstantAssign constantAssignObj;
    private ButtonEffects btnEffectsObj;
    private CaptureImageObj captureImageObj;
    private NavigationFuncObj navigationFuncObj;
    private AuthObj mAuthObj;
    private DirectionFuncObj directionFuncObj;
    private MapObject mMapObj;
    private MapAndLocationObject mMapLocationObj;
    private OnlineDriverObj onlineDriverObj;
    private GPSObject gpsObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setProperties();

        for (int i = 0; i < typeContPar.getChildCount(); i++) {
            LinearLayout selectedChild = (LinearLayout) typeContPar.getChildAt(i);
            final int finalI = i;
            selectedChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout innerLL = (LinearLayout) v;
                    constantAssignObj.vecTypeTag = innerLL.getTag().toString();
                    LinearLayout innerLLChildAt = (LinearLayout) innerLL.getChildAt(0);
                    innerLLChildAt.setBackgroundResource(R.drawable.circle_img_rev_border);
                    for (int i = 0; i < typeContPar.getChildCount(); i++) {
                        if (i != finalI) {
                            LinearLayout selectedChild = (LinearLayout) typeContPar.getChildAt(i);
                            LinearLayout innerLLChildAtRe = (LinearLayout) selectedChild.getChildAt(0);
                            innerLLChildAtRe.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                }
            });
        }

        btnEffectsObj.btnEventEff(constantAssignObj.sendBtn);
        constantAssignObj.sendBtn.setOnClickListener(this);

        btnEffectsObj.btnEventEff(constantAssignObj.sendReqBtn);
        constantAssignObj.sendReqBtn.setOnClickListener(this);

        setCurLocBtn.setOnClickListener(this);
        constantAssignObj.desLocClearBtn.setOnClickListener(this);
        constantAssignObj.pickLocCurTV.setOnClickListener(this);
        constantAssignObj.pickLocDesTV.setOnClickListener(this);
        constantAssignObj.pickLocDesTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(constantAssignObj.desLocHint)) {
                    constantAssignObj.desLocClearBtn.setVisibility(View.VISIBLE);
                } else {
                    constantAssignObj.desLocClearBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuthObj.isLoginUser()){
            mMapLocationObj.connectGoogleClient();
        }else{
            mAuthObj.updateUser();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapLocationObj.disconnectGoogleClient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapLocationObj.startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapLocationObj.stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("AppStatus", "App Terminated");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        constantAssignObj.mMap = googleMap;

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

        constantAssignObj.mMap.getUiSettings().setMyLocationButtonEnabled(false);

        karachi = new LatLng(24.861462, 67.009939);
        constantAssignObj.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(karachi, 10));

        constantAssignObj.mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                constantAssignObj.userCamMove = true;
            }
        });

        constantAssignObj.mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                constantAssignObj.mMap.getUiSettings().setAllGesturesEnabled(true);
                moveCamPos = constantAssignObj.mMap.getCameraPosition().target;
                if (constantAssignObj.userCamMove && !constantAssignObj.bothLocation) {
                    directionFuncObj.setTVCurLoc("Loading", moveCamPos, false);
                    directionFuncObj.showCurrentPlace(moveCamPos);
                    constantAssignObj.userCamMove = false;
                }
                if (constantAssignObj.LL2 == null && constantAssignObj.LL1 != null) {
                    directionFuncObj.resetTVCurLoc();
                    constantAssignObj.curPinMovable.setVisibility(View.VISIBLE);
                    constantAssignObj.bothLocation = false;
                }
            }
        });

        constantAssignObj.mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });

        getDeviceLocation(false, false, null, true, true);
    }

    private void getDeviceLocation(boolean anim, final boolean defLatLng, final Location curLocation, boolean move, boolean boundMove) {
        if (permissionCheckObj.permissionCheck()) {
            if (gpsObj.isGPSEnabled()) {
                if(constantAssignObj.PickDesBounds != null){
                    if(boundMove){
                        mMapObj.mapMoveCam(null, constantAssignObj.PickDesBounds, false, anim);
                    }
                }else{
                    mLastKnownLocation = (curLocation != null) ? curLocation : LocationServices.FusedLocationApi.getLastLocation(mMapLocationObj.mGoogleApiClient);
                    if (mLastKnownLocation != null) {
                        curLocLL = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        if (!constantAssignObj.mMap.isMyLocationEnabled() && constantAssignObj.LL2 == null) {
                            constantAssignObj.mMap.setMyLocationEnabled(true);
                        }
                        if (firstCamMov && curLocation != null) {
                            move = true;
                            anim = true;
                            firstCamMov = false;
                        }
                        if (move) {
                            if (anim) {
                                mMapObj.mapMoveCam(curLocLL, null, true, true);
                            } else {
                                mMapObj.mapMoveCam(curLocLL, null, true, false);
                            }
                        }
                    } else {
                        if (defLatLng) {
                            mMapObj.mapMoveCam(karachi, null, true, anim);
                        }
                    }
                }
            } else {
                gpsObj.enableGPS();
            }
        } else {
            permissionCheckObj.showPermissionErr();
        }
    }

    @Override
    public void onClick(View v) {
        int getId = v.getId();
        switch (getId) {
            case R.id.setCurLocBtn:
                if (constantAssignObj.LL2 == null) {
                    getDeviceLocation(true, true, null, true, false);
                }else{
                    if(constantAssignObj.PickDesBounds != null){
                        mMapObj.mapMoveCam(null, constantAssignObj.PickDesBounds, false, true);
                    }
                }
                break;
            case R.id.sendBtn:
                if(constantAssignObj.hasRequest){
                    startActivity(new Intent(this, ViewBidsActivity.class));
                }else{
                    captureImageObj.dispatchTakePictureIntent();
                }
                break;
            case R.id.sendReqBtn:
                requestParcelObj.sendReqConfirmDialog();
                break;
            case R.id.pickLocCurTV:
                if(!constantAssignObj.reqSendParcel){
                    placeAutoCompleteFrag(PLACE_AUTOCOMPLETE_CURRENT_REQUEST_CODE);
                }
                break;
            case R.id.pickLocDesTV:
                if(!constantAssignObj.reqSendParcel){
                    placeAutoCompleteFrag(PLACE_AUTOCOMPLETE_DESTINATION_REQUEST_CODE);
                }
                break;
            case R.id.desLocClearBtn:
                directionFuncObj.resetTVDesLoc();
                break;
            default:
                break;
        }
    }

    private void placeAutoCompleteFrag(int requestCode) {
        Intent des_intent = null;
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("PK")
                    .build();
            des_intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(typeFilter).build(this);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        startActivityForResult(des_intent, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ConstantAssign.PERM_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    mMapLocationObj.mGoogleApiClient.connect();
                    Toast.makeText(MapActivity.this, "Location Permission Access", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    permissionCheckObj.setPermission();
                    Toast.makeText(MapActivity.this, "Location Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }

                break;
            case ConstantAssign.PERM_REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    captureImageObj.openGallery();
                    Toast.makeText(MapActivity.this, "Storage Permission Access", Toast.LENGTH_SHORT)
                            .show();
                }else{
                    permissionCheckObj.setStoragePermission();
                    Toast.makeText(MapActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_CURRENT_REQUEST_CODE || requestCode == PLACE_AUTOCOMPLETE_DESTINATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                if (requestCode == PLACE_AUTOCOMPLETE_CURRENT_REQUEST_CODE) {
                    directionFuncObj.setTVCurLoc(place.getName().toString(), place.getLatLng(), true);
                } else if (requestCode == PLACE_AUTOCOMPLETE_DESTINATION_REQUEST_CODE) {
                    directionFuncObj.setTVDesLoc(place.getName().toString(), place.getLatLng());
                }

            }
        } else if (requestCode == captureImageObj.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            requestParcelObj.setReqSendParcel();
            captureImageObj.imageToBytes();
        } else if (requestCode == captureImageObj.REQUEST_IMAGE_PICK && resultCode == RESULT_OK){
            requestParcelObj.setReqSendParcel();
            captureImageObj.selImageToBytes(data.getData());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (!constantAssignObj.mRequestingLocationUpdates) {
            mMapLocationObj.startLocationUpdates();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        getDeviceLocation(false, false, location, false, false);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && constantAssignObj.reqSendParcel) {
            requestParcelObj.resetReqSendParcel();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setProperties(){

        mAuthObj = new AuthObj(this);
        permissionCheckObj = new PermissionCheckObj(this);
        constantAssignObj = new ConstantAssign(this);
        btnEffectsObj = new ButtonEffects(this);
        gpsObj = new GPSObject(this);

        mMapLocationObj = new MapAndLocationObject(this, constantAssignObj, permissionCheckObj);

        mMapObj = new MapObject(constantAssignObj, this);
        requestParcelObj = new RequestParcelObj(this, constantAssignObj);
        captureImageObj = new CaptureImageObj(this, this, constantAssignObj);
        navigationFuncObj = new NavigationFuncObj(this, constantAssignObj, requestParcelObj, mAuthObj);
        directionFuncObj = new DirectionFuncObj(constantAssignObj, this, mMapObj);
        onlineDriverObj = new OnlineDriverObj(constantAssignObj, this);

        navigationFuncObj.assignNavigationFunc();

        requestParcelObj.checkUserLiveRequest();
        requestParcelObj.checkUserActiveRequest();

        mainActCon = (RelativeLayout) findViewById(R.id.mainActCon);

        typeContPar = (LinearLayout) findViewById(R.id.typeContPar);

        curLocCont = (LinearLayout) findViewById(R.id.curLocCont);
        setCurLocBtn = (LinearLayout) findViewById(R.id.setCurLocBtn);

        bottomBtnCon = (LinearLayout) findViewById(R.id.bottomBtnCon);
    }
}
