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

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener, View.OnTouchListener {

    private String DOMAIN;

    private ProgressDialog progressDialog;

    private EditText mEmailField;
    private EditText mPasswordField;

    private LinearLayout loginBtn;
    private TextView loginBtnText, forgotPassword;
    private ImageView back_to_signin;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);

        DOMAIN = getString(R.string.app_api_domain);

        back_to_signin = (ImageView) findViewById(R.id.back_to_signin);
        back_to_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchAct(WelcomeActivity.class);
            }
        });

        // Views
        mEmailField = (EditText) findViewById(R.id.email);
        mPasswordField = (EditText) findViewById(R.id.password);

        // Buttons
        loginBtn = (LinearLayout) findViewById(R.id.loginBtn);
        loginBtnText = (TextView) loginBtn.getChildAt(0);
        loginBtn.setOnClickListener(this);
        loginBtn.setOnTouchListener(this);

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

        findViewById(R.id.signUpLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onStart() {
        super.onStart();
        SessionManager checkSession = new SessionManager(this);
        if(checkSession.isValidate()){
            finish();
            startActivity(new Intent(this, BasicInfo.class));
        }else if(mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this, MapActivity.class));
        }
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            finish();
            Intent moveMapAct = new Intent(LoginActivity.this, MapActivity.class);
            moveMapAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            moveMapAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(moveMapAct);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.loginBtn) {
            startSignIn();
        }else if(i == R.id.forgotPassword){
            finish();
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        }
    }

    private void startSignIn() {

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Email & password is required.", Toast.LENGTH_LONG).show();
        } else {
            keyboardHide();
            showProgressDialog();

            JSONObject params = new JSONObject();
            try{

                params.put("email", email);
                params.put("password", password);
                params.put("type", "client");

            } catch (JSONException e){
                e.printStackTrace();
            }
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            String reqUrl = DOMAIN + "/login";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, reqUrl, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        if(response.getString("status").equals("ok")){
                            //Log.e("message", response + "");
                            mAuth.signInWithCustomToken(response.getString("token")).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    hideProgressDialog();
                                    if (!task.isSuccessful()) {
                                        Log.e("LoginError", task.getException().getMessage());
                                        Toast.makeText(LoginActivity.this, task.getException().getMessage()+"",
                                                Toast.LENGTH_SHORT).show();
                                    }else{
                                        updateUI(mAuth.getCurrentUser());
                                    }
                                }
                            });
                        } else if(response.getString("status").equals("failed")) {
                            hideProgressDialog();
                            Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e){
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    Log.e("ErrorResponse", error.toString());
                    Toast.makeText(LoginActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        }

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
            case R.id.loginBtn:
                btnEventEffRounded(event, loginBtn, loginBtnText);
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