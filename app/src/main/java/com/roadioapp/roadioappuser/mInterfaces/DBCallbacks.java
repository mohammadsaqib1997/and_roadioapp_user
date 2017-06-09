package com.roadioapp.roadioappuser.mInterfaces;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class DBCallbacks {
    public interface CompleteListener {
        void onSuccess(boolean status, String msg);
    }

    public interface CompleteDSListener {
        void onSuccess(boolean status, String msg, DataSnapshot dataSnapshot);
    }

    public interface  CompleteDirectionCall{
        void onSuccess(boolean status, String msg, PolylineOptions polylineOptions);
    }
}
