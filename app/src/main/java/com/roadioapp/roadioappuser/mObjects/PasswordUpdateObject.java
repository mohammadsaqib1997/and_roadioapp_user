package com.roadioapp.roadioappuser.mObjects;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.roadioapp.roadioappuser.R;

public class PasswordUpdateObject {

    private Activity activity;
    private ButtonEffects btnEffects;

    public PasswordUpdateObject(Activity activity) {
        this.activity = activity;
        btnEffects = new ButtonEffects(activity);
    }

    private String passValid(EditText curr_pass, EditText pass1, EditText pass2){
        String curr_passStr = curr_pass.getText().toString();
        String pass1Str = pass1.getText().toString();
        String pass2Str = pass2.getText().toString();
        if(curr_passStr.isEmpty()){
            return "Current Password Empty!";
        }else if(pass1Str.isEmpty()){
            return "New Password Empty!";
        }else if(pass2Str.isEmpty()){
            return "Re-Type Password Empty!";
        }else if(!pass1Str.equals(pass2Str)){
            return "Password Not Match!";
        }else{
            return "";
        }
    }

    public void showUpdatePassDialog(){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.upd_pass_dialog);
        LinearLayout subBtn = (LinearLayout) dialog.findViewById(R.id.subBtn);
        btnEffects.btnEventEffRounded(subBtn);

        final EditText curr_pass = (EditText) dialog.findViewById(R.id.curr_pass);
        final EditText pass1 = (EditText) dialog.findViewById(R.id.new_pass);
        final EditText pass2 = (EditText) dialog.findViewById(R.id.re_new_pass);

        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkPass = passValid(curr_pass, pass1, pass2);
                if(checkPass.isEmpty()){
                    Toast.makeText(activity, "Password Match!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity, checkPass, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

}
