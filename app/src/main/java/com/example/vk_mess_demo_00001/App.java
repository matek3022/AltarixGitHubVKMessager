package com.example.vk_mess_demo_00001;

import android.app.Application;

import com.example.vk_mess_demo_00001.SQLite.DBHelper;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by matek on 26.12.2016.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(getApplicationContext());
        DBHelper.init(getApplicationContext());
    }
}
