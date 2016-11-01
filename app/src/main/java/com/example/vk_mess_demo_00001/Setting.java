package com.example.vk_mess_demo_00001;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class Setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Settings");
        setContentView(R.layout.activity_setting);
        Switch photouser = (Switch) findViewById(R.id.switch1);
        Switch photochat = (Switch) findViewById(R.id.switch3);
        Switch online = (Switch) findViewById(R.id.switch2);
        final SharedPreferences setting = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        photouser.setChecked(setting.getBoolean("photouserOn",true));
        photochat.setChecked(setting.getBoolean("photochatOn",true));
        online.setChecked(setting.getBoolean("onlineOn",true));
        photouser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("photouserOn",isChecked);
                    editor.apply();
                }else{
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("photouserOn",isChecked);
                    editor.apply();
                }
            }
        });
        photochat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("photochatOn",isChecked);
                    editor.apply();
                }else{
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("photochatOn",isChecked);
                    editor.apply();
                }
            }
        });
        online.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("onlineOn",isChecked);
                    editor.apply();
                }else {
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("onlineOn",isChecked);
                    editor.apply();
                }
            }
        });
    }
}
