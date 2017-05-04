package com.roadioapp.roadioappuser;

import android.app.Activity;
import android.os.Bundle;
import com.roadioapp.roadioappuser.mObjects.RequestBidsObj;

public class ViewBidsActivity extends Activity {

    private RequestBidsObj requestBidsObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bids);

        setProps();
        requestBidsObj.getBids();

    }

    private void setProps(){
        requestBidsObj = new RequestBidsObj(this);
    }
}
