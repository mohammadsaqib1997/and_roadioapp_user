package com.roadioapp.roadioappuser;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.roadioapp.roadioappuser.mObjects.ButtonEffects;

public class WelcomeActivity extends AppCompatActivity {

    private Activity activity;

    private ButtonEffects buttonEffects;

    private LinearLayout loginBtn, registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        activity = this;

        buttonEffects = new ButtonEffects(activity);

        loginBtn = (LinearLayout) findViewById(R.id.loginBtn);
        registerBtn = (LinearLayout) findViewById(R.id.registerBtn);
        buttonEffects.btnEventEffRounded(loginBtn);
        buttonEffects.btnEventEffRounded(registerBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchAct(LoginActivity.class);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchAct(SignupActivity.class);
            }
        });
    }

    private void switchAct(Class classname){
        startActivity(new Intent(activity, classname));
        finish();
    }
}
