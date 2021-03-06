package com.roadioapp.roadioappuser;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.roadioapp.roadioappuser.mObjects.ButtonEffects;
import com.roadioapp.roadioappuser.mObjects.SaveLocalMemory;
import com.roadioapp.roadioappuser.mObjects.mProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Activity activity;

    private ButtonEffects buttonEffects;
    private mProgressBar progressBarObj;
    private String DOMAIN;
    private SaveLocalMemory saveLocalMemory;

    private LinearLayout subBtn;
    private EditText mobNumber;

    private String mob_no_str;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        activity = this;

        DOMAIN = getString(R.string.app_api_domain);
        progressBarObj = new mProgressBar(activity);
        buttonEffects = new ButtonEffects(activity);
        saveLocalMemory = new SaveLocalMemory(activity).selectPref("forgotPass");

        mobNumber = (EditText) findViewById(R.id.mobNumber);

        subBtn = (LinearLayout) findViewById(R.id.subBtn);
        buttonEffects.btnEventEffRounded(subBtn);
        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mob_no_str = mobNumber.getText().toString().trim();
                String err = "";
                if(mob_no_str.isEmpty()){
                    err = "Mobile Number is required!";
                }else if(mob_no_str.length() != 12){
                    err = "Mobile Number is invalid!";
                }

                if(!err.isEmpty()){
                    Toast.makeText(activity, err, Toast.LENGTH_SHORT).show();
                }else{
                    forgotPassReqSend();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!saveLocalMemory.getVal("mob_no").isEmpty()){
            switchAct();
        }
    }

    private void forgotPassReqSend(){
        progressBarObj.showProgressDialog();
        JSONObject params = new JSONObject();
        try{
            params.put("mob_no", mob_no_str);
            params.put("type", "client");
        } catch (JSONException e){
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        String reqUrl = DOMAIN + "/forgot_password";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, reqUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.getString("status").equals("ok")){
                        progressBarObj.hideProgressDialog();
                        saveLocalMemory.editPref().putVal("token", response.getString("token")).putVal("mob_no", mob_no_str).commitPref();
                        switchAct();
                    } else {
                        progressBarObj.hideProgressDialog();
                        Toast.makeText(activity, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    progressBarObj.hideProgressDialog();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBarObj.hideProgressDialog();
                Log.e("ErrorResponse", error.toString());
                Toast.makeText(activity, "Bad Request", Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }

    private void switchAct(){
        finish();
        startActivity(new Intent(activity, ForgotPassConfirmActivity.class));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
