package com.example.vk_mess_demo_00001;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    String TOKEN = "cf561f6c64e197e63cff33065cdf789bd8dd6159087fceac7df5178ead82eb23d893913e42679f502f665";
    String inf="photo_200, online";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.vk.com/method/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        VKService service = retrofit.create(VKService.class);


        LinearLayout line = (LinearLayout) findViewById(R.id.activity_main);
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = getLayoutInflater();
        View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic,null);
        TextView text = (TextView) cont.findViewById(R.id.textView3);
        text.setText("kekeeeeeeeeeesi");
        line.addView(cont);
        Call<ServerResponse<ArrayList<User>>> call = service.getUser(TOKEN, "1,41857323",inf);

        call.enqueue(new Callback<ServerResponse<ArrayList<User>>>() {
            @Override
            public void onResponse(Call<ServerResponse<ArrayList<User>>> call, Response<ServerResponse<ArrayList<User>>> response) {
                Log.i("motya", response.raw().toString());
                TextView tv = (TextView) findViewById(R.id.textView3);
                TextView tv1 = (TextView) findViewById(R.id.textView4);
                TextView tv2 = (TextView) findViewById(R.id.textView5);
                ArrayList<User> l = response.body().getResponse();
                for(int i=0; i<l.size(); i++){
                    tv.setText(l.get(i).getFirst_name());
                    tv1.setText(l.get(i).getLast_name());
                    if (l.get(i).getOnline()==1) {tv2.setText("online");}
                    else {tv2.setText("offline");}
                    Picasso.with(MainActivity.this)
                            .load(l.get(i).getPhoto_200())

                            .into((ImageView)findViewById(R.id.imageView6));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<ArrayList<User>>> call, Throwable t) {
                Log.wtf("motya", t.getMessage());
            }
        });
    }

    public void click(View view) {
        Intent intent = new Intent(MainActivity.this,Main2Activity.class);
        startActivity(intent);
    }
}
