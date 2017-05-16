package com.example.vk_mess_demo_00001.managers;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;


public class PreferencesManager {

    public static String TOKEN = "token";
    public static String SETTINGS = "mysettings";
    public static String USERID = "uid";
    public static String USERGSON = "uidgson";
    private static WeakReference<Context> context;
    private static PreferencesManager instance;
    private SharedPreferences tokenPrefs;
    private SharedPreferences settingsPref;
    private SharedPreferences userIdPref;
    private SharedPreferences userGsonPref;

    private PreferencesManager(Context context) {
        PreferencesManager.context = new WeakReference<>(context);
        initPrefs();
    }

    public static void init(Context context) {
        instance = new PreferencesManager(context);
    }

    public static PreferencesManager getInstance() {
        return instance;
    }

    private void initPrefs() {
        if (tokenPrefs == null)
            tokenPrefs = context.get().getSharedPreferences(TOKEN, Context.MODE_PRIVATE);
        if (settingsPref == null)
            settingsPref = context.get().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        if (userIdPref == null)
            userIdPref = context.get().getSharedPreferences(USERID, Context.MODE_PRIVATE);
        if (userGsonPref == null)
            userGsonPref = context.get().getSharedPreferences(USERGSON, Context.MODE_PRIVATE);
    }

    public int getUserID() {
        return userIdPref.getInt("uid_int", 0);
    }

    public void setUserID(int uid) {
        SharedPreferences.Editor editor = userIdPref.edit();
        editor.putInt("uid_int", uid);
        editor.apply();
    }

    public String getUserGson() {
        return userGsonPref.getString("uidgson_string", "");
    }

    public void setUserGson(String gson) {
        SharedPreferences.Editor editor = userGsonPref.edit();
        editor.putString("uidgson_string", gson);
        editor.apply();
    }

    public String getToken() {
        return tokenPrefs.getString("token_string", "");
    }

    public void setToken(String token) {
        SharedPreferences.Editor editor = tokenPrefs.edit();
        editor.putString("token_string", token);
        editor.apply();
    }

    public boolean getSettingPhotoUserOn() {
        return settingsPref.getBoolean("photouserOn", true);
    }

    public void setSettingPhotoUserOn(boolean isChecked) {
        SharedPreferences.Editor editor = settingsPref.edit();
        editor.putBoolean("photouserOn", isChecked);
        editor.apply();
    }

    public boolean getSettingPhotoChatOn() {
        return settingsPref.getBoolean("photochatOn", true);
    }

    public void setSettingPhotoChatOn(boolean isChecked) {
        SharedPreferences.Editor editor = settingsPref.edit();
        editor.putBoolean("photochatOn", isChecked);
        editor.apply();
    }

    public boolean getSettingOnline() {
        return settingsPref.getBoolean("onlineOn", true);
    }

    public void setSettingOnline(boolean isChecked) {
        SharedPreferences.Editor editor = settingsPref.edit();
        editor.putBoolean("onlineOn", isChecked);
        editor.apply();
    }

    public String getCryptKey() {
        return settingsPref.getString("cryptKey", "");
    }

    public void setCryptKey(String key) {
        SharedPreferences.Editor editor = settingsPref.edit();
        editor.putString("cryptKey", key);
        editor.apply();
    }

    public String getCryptString() {
        return settingsPref.getString("cryptString", "");
    }

    public void setCryptString(String string) {
        SharedPreferences.Editor editor = settingsPref.edit();
        editor.putString("cryptString", string);
        editor.apply();
    }

    public String getDecryptString() {
        return settingsPref.getString("decryptString", "");
    }

    public void setDecryptString(String string) {
        SharedPreferences.Editor editor = settingsPref.edit();
        editor.putString("decryptString", string);
        editor.apply();
    }
}
