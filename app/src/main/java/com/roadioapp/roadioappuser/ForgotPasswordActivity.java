package com.roadioapp.roadioappuser;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roadioapp.roadioappuser.mObjects.ButtonEffects;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Activity activity;

    private ButtonEffects buttonEffects;

    private LinearLayout subBtn;
    private EditText mobNumber;

    private String mob_no_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        activity = this;

        buttonEffects = new ButtonEffects(activity);

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
                    Toast.makeText(activity, "valid", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
