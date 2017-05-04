package com.roadioapp.roadioappuser.mObjects;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roadioapp.roadioappuser.R;

public class ButtonEffects {

    private Context context;

    public ButtonEffects(Context ctx){
        this.context = ctx;
    }

    public void btnEventEffRounded(final LinearLayout view){
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TextView tv = (TextView) view.getChildAt(0);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundResource(R.drawable.bg_acc_rounded_inverse);
                    tv.setTextColor(Color.parseColor("#ffffff"));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundResource(R.drawable.bg_acc_rounded);
                    tv.setTextColor(Color.parseColor("#333333"));
                }
                return false;
            }
        });
    }

    public void btnEventEff(final LinearLayout view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TextView tv = (TextView) view.getChildAt(0);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundResource(R.color.colorPrimary);
                    tv.setTextColor(Color.parseColor("#ffffff"));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundResource(R.color.colorAccent);
                    tv.setTextColor(Color.parseColor("#333333"));
                }
                return false;
            }
        });
    }

}
