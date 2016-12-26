package com.example.vk_mess_demo_00001.Activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.vk_mess_demo_00001.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Settings");
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences Token = getSharedPreferences("token", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = Token.edit();
                editor.putString("token_string","");
                editor.apply();
                Intent intent = new Intent(SettingActivity.this,StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                SettingActivity.this.finish();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
