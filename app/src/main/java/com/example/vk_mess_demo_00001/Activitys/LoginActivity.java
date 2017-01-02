package com.example.vk_mess_demo_00001.Activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.vk_mess_demo_00001.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    public static String LOGOUT = "logout";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        boolean logout = false;
        if(extras != null) {
            logout = extras.getBoolean(LOGOUT, false);
        }
        setContentView(R.layout.activity_login);

        WebView web = (WebView) findViewById(R.id.webView);
        web.getSettings().setJavaScriptEnabled(true);
        if (logout) {
            CookieSyncManager.createInstance(web.getContext()).sync();
            CookieManager man = CookieManager.getInstance();
            man.removeAllCookie();
        }
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.startsWith("https://oauth.vk.com/blank.html")) {
                    doneWithThis(url);
                }
            }
        });
        String url = "https://oauth.vk.com/authorize?" +
                "client_id=" + 5658788 + "&" +
                "scope=" + 8388607 + "&" +
                "redirect_uri=" + "https://oauth.vk.com/blank.html" + "&" +
                "display=touch&" +
                "v=" + "5.57" + "&" +
                "response_type=token";
        web.loadUrl(url);
        web.setVisibility(View.VISIBLE);

    }

    public void doneWithThis(String url) {
        String token = extract(url, "access_token=(.*?)&");
        int uid = Integer.parseInt(extract(url, "user_id=(\\d*)"));
        final SharedPreferences Token = getSharedPreferences("token", Context.MODE_PRIVATE);
        final SharedPreferences Uid = getSharedPreferences("uid", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = Token.edit();
        editor.putString("token_string",token);
        editor.apply();

        editor = Uid.edit();
        editor.putInt("uid_int",uid);
        editor.apply();

        goNext();
    }

    public String extract(String from, String patt) {
        Pattern ptrn = Pattern.compile(patt);
        Matcher mtch = ptrn.matcher(from);
        if (!mtch.find())
            return null;
        return mtch.toMatchResult().group(1);
    }

    public void goNext() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), DialogsActivity.class);
        LoginActivity.this.finish();
        startActivity(intent);
    }
}