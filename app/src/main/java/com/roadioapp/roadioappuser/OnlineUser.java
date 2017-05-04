package com.roadioapp.roadioappuser;

import java.util.HashMap;
import java.util.Map;

public class OnlineUser {

    public double lat;
    public double lng;
    public String position;
    public String uid;

    public OnlineUser(){

    }

    public OnlineUser(String uid, double lat, double lng, String position){
        this.lat = lat;
        this.lng = lng;
        this.position = position;
        this.uid = uid;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("lat", lat);
        result.put("lng", lng);
        result.put("position", position);
        result.put("uid", uid);
        return result;
    }
}
