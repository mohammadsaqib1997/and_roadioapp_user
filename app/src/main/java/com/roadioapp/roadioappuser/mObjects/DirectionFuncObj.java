package com.roadioapp.roadioappuser.mObjects;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.roadioapp.roadioappuser.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DirectionFuncObj {

    private ConstantAssign constantAssignObj;
    private Activity activity;

    private mProgressBar progressBarObj;
    private RequestQueue geocodingAPIReq, directionAPIReq;

    private String key;

    private MapObject mapObject;

    public DirectionFuncObj(ConstantAssign constantAssign, Activity act, MapObject mapObject){
        this.activity = act;
        this.constantAssignObj = constantAssign;

        this.mapObject = mapObject;

        progressBarObj = new mProgressBar(activity);

        geocodingAPIReq = Volley.newRequestQueue(activity);
        directionAPIReq = Volley.newRequestQueue(activity);

        key = activity.getResources().getString(R.string.google_url_key);
    }

    public void showCurrentPlace(final LatLng latLng) {
        if(!constantAssignObj.pickLocation){
            constantAssignObj.pickLocation = true;
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=false&key=" + key;
            StringRequest stringReqData = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String userDataResponse) {
                    constantAssignObj.pickLocation = false;
                    try {
                        JSONObject rData = new JSONObject(userDataResponse);
                        String status = rData.getString("status");
                        JSONArray results = rData.getJSONArray("results");
                        if (status.equals("OK")) {
                            JSONObject getSingleData = results.getJSONObject(0);
                            JSONArray addressComponents = getSingleData.getJSONArray("address_components");
                            //JSONObject firstAddName = addressComponents.getJSONObject(0);
                            //String newLoc = firstAddName.getString("long_name");
                            boolean route = false;
                            String newLoc = "Unnamed Road";

                            for(int i=0; i<addressComponents.length(); i++){
                                JSONObject singleObj = addressComponents.getJSONObject(i);
                                JSONArray addTypes = singleObj.getJSONArray("types");
                                for(int i2 = 0; i2<addTypes.length(); i2++){
                                    String type = addTypes.getString(i2);
                                    if(type.equals("route") || type.equals("establishment")) {
                                        route = true;
                                        break;
                                    }
                                }
                                if(route){
                                    newLoc = singleObj.getString("long_name");
                                    break;
                                }
                            }

                            setTVCurLoc(newLoc, null, false);
                        } else {
                            Toast.makeText(activity, status, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    constantAssignObj.pickLocation = false;
                    Log.e("ReqErr", String.valueOf(error));
                }
            });
            stringReqData.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            geocodingAPIReq.add(stringReqData);
        }
    }

    public void showDirection(){
        if(constantAssignObj.pickLocMarker != null && constantAssignObj.desLocMarker != null){
            resetDirection();
            progressBarObj.showProgressDialog();
            String origin = (constantAssignObj.pickLocMarker.getPosition()).latitude+","+(constantAssignObj.pickLocMarker.getPosition()).longitude;
            String destination = (constantAssignObj.desLocMarker.getPosition()).latitude+","+(constantAssignObj.desLocMarker.getPosition()).longitude;
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + destination + "&key=" + key;
            StringRequest stringReqData = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String userDataResponse) {
                    progressBarObj.hideProgressDialog();
                    try {
                        JSONObject rData = new JSONObject(userDataResponse);
                        String status = rData.getString("status");

                        if(status.equals("OK")){
                            JSONArray routes = rData.getJSONArray("routes");
                            JSONObject subRoutes = routes.getJSONObject(0);
                            setDesTime(subRoutes);
                            JSONObject overview_polyline = subRoutes.getJSONObject("overview_polyline");
                            List<LatLng> points = decodePolyLine(overview_polyline.getString("points"));
                            PolylineOptions polylineOptions = new PolylineOptions().
                                    geodesic(true).
                                    color(Color.BLACK).
                                    width(10).zIndex(999999999.0f);

                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            polylineOptions.add(constantAssignObj.pickLocMarker.getPosition());
                            for (int i = 0; i < points.size(); i++){
                                builder.include(points.get(i));
                                polylineOptions.add(points.get(i));
                            }
                            polylineOptions.add(constantAssignObj.desLocMarker.getPosition());
                            constantAssignObj.PickDesBounds = builder.build();
                            mapObject.mapMoveCam(null, constantAssignObj.PickDesBounds, false, true);

                            constantAssignObj.polylinePaths.add(constantAssignObj.mMap.addPolyline(polylineOptions));

                        }else{
                            Toast.makeText(activity, status, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBarObj.hideProgressDialog();
                    Log.e("ReqErr", String.valueOf(error));
                }
            });
            stringReqData.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            directionAPIReq.add(stringReqData);
        }
    }

    public void resetDirection(){
        if(constantAssignObj.polylinePaths != null){
            for (Polyline polyline:constantAssignObj.polylinePaths ) {
                polyline.remove();
            }
            constantAssignObj.PickDesBounds = null;
        }
    }

    public void setTVCurLoc(String placeName, LatLng placeLL, boolean move) {
        constantAssignObj.pickLocCurTV.setText(placeName);
        constantAssignObj.pickLocCurTV.setTextColor(Color.parseColor("#333333"));
        if (placeLL != null) {
            constantAssignObj.LL1 = placeLL;
            if (constantAssignObj.pickLocMarker != null) {
                constantAssignObj.pickLocMarker.setPosition(placeLL);
            }
            if (move) {
                if (!mapObject.setMapCameraBound()) {
                    mapObject.mapMoveCam(placeLL, null, false, true);
                }
            }
        }

        showDirection();
    }

    public void setTVDesLoc(String placeName, LatLng placeLL) {
        constantAssignObj.pickLocDesTV.setText(placeName);
        constantAssignObj.pickLocDesTV.setTextColor(Color.parseColor("#333333"));
        constantAssignObj.LL2 = placeLL;
        if (constantAssignObj.LL1 != null) {
            if (constantAssignObj.pickLocMarker != null) {
                constantAssignObj.pickLocMarker.setPosition(constantAssignObj.LL1);
            } else {
                constantAssignObj.pickLocMarker = constantAssignObj.mMap.addMarker(new MarkerOptions().position(constantAssignObj.LL1).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_move_pin)));
                constantAssignObj.pickLocMarker.setZIndex(99.0f);
            }
        }
        if (constantAssignObj.desLocMarker != null) {
            constantAssignObj.desLocMarker.setPosition(constantAssignObj.LL2);
        } else {
            constantAssignObj.desLocMarker = constantAssignObj.mMap.addMarker(new MarkerOptions().position(constantAssignObj.LL2).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cur_loc_dark)));
            constantAssignObj.desLocMarker.setZIndex(99.0f);
        }
        constantAssignObj.curPinMovable.setVisibility(View.GONE);
        constantAssignObj.bothLocation = true;
        if (!mapObject.setMapCameraBound()) {
            mapObject.mapMoveCam(placeLL, null, false, true);
        }
        showDirection();
    }

    public void resetTVCurLoc() {
        if (constantAssignObj.pickLocMarker != null) {
            constantAssignObj.pickLocMarker.remove();
            constantAssignObj.pickLocMarker = null;
        }
        if (!constantAssignObj.mMap.isMyLocationEnabled()) {
            constantAssignObj.mMap.setMyLocationEnabled(true);
        }
        //resetMapCameraBound();
    }

    public void resetTVDesLoc() {
        constantAssignObj.LL2 = null;
        if (constantAssignObj.LL1 != null) {
            mapObject.mapMoveCam(constantAssignObj.LL1, null, false, true);
        }
        constantAssignObj.pickLocDesTV.setText(constantAssignObj.desLocHint);
        constantAssignObj.pickLocDesTV.setTextColor(Color.parseColor("#66000000"));
        constantAssignObj.pickDesLocation = "";
        if (constantAssignObj.desLocMarker != null) {
            constantAssignObj.desLocMarker.remove();
            constantAssignObj.desLocMarker = null;
        }
        resetDirection();
        //resetMapCameraBound();
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

    private void setDesTime(JSONObject object){
        if(object != null){
            try {
                JSONArray legs = object.getJSONArray("legs");
                JSONObject subLegs = legs.getJSONObject(0);
                JSONObject disObj = subLegs.getJSONObject("distance");
                JSONObject durObj = subLegs.getJSONObject("duration");
                constantAssignObj.durText = durObj.getString("text");
                constantAssignObj.disText = disObj.getString("text");
                constantAssignObj.estDistance.setText("Est. Distance "+constantAssignObj.disText);
                constantAssignObj.estTime.setText("Est. Time "+constantAssignObj.durText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            constantAssignObj.estDistance.setText("Loading...");
            constantAssignObj.estTime.setText("Loading...");
        }
    }
}
