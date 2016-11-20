package com.example.vk_mess_demo_00001.Activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.vk_mess_demo_00001.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        final SharedPreferences Token = getSharedPreferences("token", Context.MODE_PRIVATE);
        String TOKEN = Token.getString("token_string","");
        if (TOKEN==""){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            StartActivity.this.finish();
        }else {
            Intent intent = new Intent(this,DialogsActivity.class);
            startActivity(intent);
            StartActivity.this.finish();
        }
    }
}
