package com.roadioapp.roadioappuser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity implements
        View.OnClickListener, View.OnTouchListener {

    private String DOMAIN;

    EditText mMobileNum, mPassword, mRePassword;
    String strMobileNum, strPassword, strRePassword;


    private ProgressDialog progressDialog;

    private LinearLayout signUpBtn;
    private TextView signUpBtnText;
    private ImageView back_to_signin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressDialog = new ProgressDialog(this);

        DOMAIN = getString(R.string.app_api_domain);

        back_to_signin = (ImageView) findViewById(R.id.back_to_signin);
        back_to_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchAct(WelcomeActivity.class);
            }
        });

        mMobileNum = (EditText) findViewById(R.id.mobNumber);
        mPassword = (EditText) findViewById(R.id.password);
        mRePassword = (EditText) findViewById(R.id.conPassword);

        // Buttons
        signUpBtn = (LinearLayout) findViewById(R.id.signUpBtn);
        signUpBtnText = (TextView) signUpBtn.getChildAt(0);
        signUpBtn.setOnClickListener(this);
        signUpBtn.setOnTouchListener(this);

        findViewById(R.id.loginLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchAct(LoginActivity.class);
            }
        });

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signUpBtn) {
            codeSend();
        }
    }

    private void codeSend(){
        strMobileNum = "92"+mMobileNum.getText().toString();
        strPassword = mPassword.getText().toString();
        strRePassword = mRePassword.getText().toString();

        String err = "";
        if(strMobileNum.length() < 10){
            err = "Invalid Mobile Number!";
        }else if(strPassword.isEmpty()){
            err = "Required Password";
        }else if(strPassword.length() < 5){
            err = "Password is too short";
        }else if(strRePassword.isEmpty()){
            err = "Required Confirm Password";
        }else if(!strPassword.equals(strRePassword)){
            err = "Confirm Password is not match!";
        }

        if(!err.isEmpty()){
            Toast.makeText(SignupActivity.this, err, Toast.LENGTH_SHORT).show();
        }else{
            keyboardHide();
            reqSend(strMobileNum, strPassword);
        }
    }

    private void reqSend(final String mobNumber, final String pass){
        showProgressDialog();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String reqURL = DOMAIN+"/validate";

        JSONObject params = new JSONObject();
        try {
            params.put("phone_num", mobNumber);
            params.put("type", "client");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, reqURL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                //Log.e("Response", response.toString());
                try {
                    if(response.getString("status").equals("ok")){
                        String token = response.getString("token");
                        SessionManager saveUserInfo = new SessionManager(SignupActivity.this);
                        saveUserInfo.setRegPref(token, mobNumber);
                        saveUserInfo.saveUserTamp(pass, mobNumber);
                        switchAct(ConfirmCode.class);
                    }else{
                        Toast.makeText(SignupActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        Log.e("Response", response.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Log.e("ErrorResponse", error.toString());
                Toast.makeText(SignupActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);

    }



    private void keyboardHide(){
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view != null ? view.getWindowToken() : null, 0);
    }

    private void showProgressDialog(){
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog(){
        progressDialog.dismiss();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int getId = v.getId();
        switch (getId) {
            case R.id.signUpBtn:
                btnEventEffRounded(event, signUpBtn, signUpBtnText);
                break;
            default:
                break;
        }
        return false;
    }

    public void btnEventEffRounded(MotionEvent event, View v, TextView tv) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setBackgroundResource(R.drawable.bg_acc_rounded_inverse);
            tv.setTextColor(Color.parseColor("#ffffff"));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.setBackgroundResource(R.drawable.bg_acc_rounded);
            tv.setTextColor(Color.parseColor("#333333"));
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switchAct(WelcomeActivity.class);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void switchAct(Class classname){
        startActivity(new Intent(this, classname));
        finish();
    }
}