package com.roadioapp.roadioappuser;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.roadioapp.roadioappuser.mObjects.ButtonEffects;
import com.roadioapp.roadioappuser.mObjects.PasswordUpdateObject;

public class SettingActivity extends AppCompatActivity {

    private Activity activity;

    ImageView close_act_btn;
    LinearLayout dialog_btn_passUpd;

    //Objects
    ButtonEffects btnEffects;
    PasswordUpdateObject passwordUpdateObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        activity = this;

        btnEffects = new ButtonEffects(activity);
        passwordUpdateObj = new PasswordUpdateObject(activity);

        close_act_btn = (ImageView) findViewById(R.id.close_act_btn);
        close_act_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog_btn_passUpd = (LinearLayout) findViewById(R.id.dialog_btn_passUpd);
        dialog_btn_passUpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordUpdateObj.showUpdatePassDialog();
            }
        });
    }

}
