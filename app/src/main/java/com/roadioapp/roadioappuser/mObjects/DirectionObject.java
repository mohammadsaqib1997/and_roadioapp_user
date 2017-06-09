package com.roadioapp.roadioappuser.mObjects;


import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.roadioapp.roadioappuser.R;
import com.roadioapp.roadioappuser.mInterfaces.DBCallbacks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DirectionObject {

    private Activity activity;
    private RequestQueue directionAPIReq;
    private String key;

    public DirectionObject(Activity activity) {
        this.activity = activity;
        directionAPIReq = Volley.newRequestQueue(activity);
        key = activity.getResources().getString(R.string.google_url_key);
    }

    public void retDirection(final LatLng LL1, final LatLng LL2, final DBCallbacks.CompleteDirectionCall callback){
        if(LL1 != null && LL2 != null){
            String origin = LL1.latitude+","+LL1.longitude;
            String destination = LL2.latitude+","+LL2.longitude;
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + destination + "&key=" + key;
            StringRequest stringReqData = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String userDataResponse) {
                    try {
                        JSONObject rData = new JSONObject(userDataResponse);
                        String status = rData.getString("status");

                        if(status.equals("OK")){
                            JSONArray routes = rData.getJSONArray("routes");
                            JSONObject subRoutes = routes.getJSONObject(0);
                            //setDesTime(subRoutes);
                            JSONObject overview_polyline = subRoutes.getJSONObject("overview_polyline");
                            List<LatLng> points = decodePolyLine(overview_polyline.getString("points"));
                            PolylineOptions polylineOptions = new PolylineOptions().
                                    geodesic(true).
                                    color(Color.BLACK).
                                    width(10).zIndex(999999999.0f);

                            //LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            polylineOptions.add(LL1);
                            for (int i = 0; i < points.size(); i++){
                                //builder.include(points.get(i));
                                polylineOptions.add(points.get(i));
                            }
                            polylineOptions.add(LL2);

                            callback.onSuccess(true, "", polylineOptions);
                            //constantAssignObj.PickDesBounds = builder.build();
                            //mapObject.mapMoveCam(null, constantAssignObj.PickDesBounds, false, true);

                            //constantAssignObj.polylinePaths.add(constantAssignObj.mMap.addPolyline(polylineOptions));

                        }else{
                            callback.onSuccess(false, status, null);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onSuccess(false, e.getMessage(), null);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ReqErr", String.valueOf(error));
                    callback.onSuccess(false, error.getMessage(), null);
                }
            });
            //stringReqData.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            directionAPIReq.add(stringReqData);
        }else{
            callback.onSuccess(false, "Invalid LatLng!", null);
        }
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



}
