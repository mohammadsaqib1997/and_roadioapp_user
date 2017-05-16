package com.roadioapp.roadioappuser;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.roadioapp.roadioappuser.mInterfaces.ObjectInterfaces;
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

        ImageView close_act_btn = (ImageView) findViewById(R.id.close_act_btn);
        close_act_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView cancel_req_btn = (TextView) findViewById(R.id.cancel_req_btn);
        cancel_req_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBidsObj.removeReq(new ObjectInterfaces.SimpleCallback() {
                    @Override
                    public void onSuccess(boolean status, String err) {
                        if(status){
                            finish();
                        }else{
                            Toast.makeText(ViewBidsActivity.this, err, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
