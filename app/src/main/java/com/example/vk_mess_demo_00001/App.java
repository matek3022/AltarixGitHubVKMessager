package com.example.vk_mess_demo_00001;

import android.app.Application;

import com.example.vk_mess_demo_00001.SQLite.DBHelper;
import com.example.vk_mess_demo_00001.Utils.VKService;
import com.facebook.drawee.backends.pipeline.Fresco;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by matek on 26.12.2016.
 */

public class App extends Application {
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.vk.com/method/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public static final VKService service = retrofit.create(VKService.class);
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(getApplicationContext());
        DBHelper.init(getApplicationContext());
    }
}
