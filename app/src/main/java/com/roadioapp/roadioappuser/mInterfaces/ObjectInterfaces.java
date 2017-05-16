package com.roadioapp.roadioappuser.mInterfaces;

import com.google.firebase.database.DataSnapshot;

public class ObjectInterfaces {
    public interface SimpleCallback {
        void onSuccess(boolean status, String err);
    }

    public interface UserActReqStatusCallback {
        void onSuccess(boolean status, String err, DataSnapshot dataSnapshot);
    }
}
