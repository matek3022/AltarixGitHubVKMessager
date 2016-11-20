package com.example.vk_mess_demo_00001.Activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vk_mess_demo_00001.R;
import com.example.vk_mess_demo_00001.VKObjects.ServerResponse;
import com.example.vk_mess_demo_00001.VKObjects.User;
import com.example.vk_mess_demo_00001.Utils.VKService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserActivity extends AppCompatActivity {
    String inf="photo_400_orig,photo_max_orig, online,city,country,education, universities, schools,bdate,contacts";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        final int user_id = getIntent().getIntExtra("userID", 0);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.vk.com/method/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        VKService service = retrofit.create(VKService.class);


        LinearLayout lineRight = (LinearLayout) findViewById(R.id.lineRight);
        LinearLayout lineBottom = (LinearLayout) findViewById(R.id.lineBottom);
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final SharedPreferences Token = getSharedPreferences("token", Context.MODE_PRIVATE);
        String TOKEN = Token.getString("token_string","");
        Call<ServerResponse<ArrayList<User>>> call = service.getUser(TOKEN, ""+ user_id ,inf);
        call.enqueue(new Callback<ServerResponse<ArrayList<User>>>() {
            @Override
            public void onResponse(Call<ServerResponse<ArrayList<User>>> call, Response<ServerResponse<ArrayList<User>>> response) {
                Log.i("motya", response.raw().toString());
                LinearLayout lineRight = (LinearLayout) findViewById(R.id.lineRight);
                LinearLayout lineBottom = (LinearLayout) findViewById(R.id.lineBottom);
                TextView tv = new TextView(UserActivity.this);
                TextView tv1 = new TextView(UserActivity.this);
                TextView tv2 = new TextView(UserActivity.this);
                TextView tv3 = new TextView(UserActivity.this);
                TextView tv4 = new TextView(UserActivity.this);
                TextView tv5 = new TextView(UserActivity.this);
                TextView tv6 = new TextView(UserActivity.this);
                TextView tv7 = new TextView(UserActivity.this);
                TextView tv8 = new TextView(UserActivity.this);
                TextView tv9 = new TextView(UserActivity.this);
                lineRight.addView(tv);
                ArrayList<User> l = response.body().getResponse();
                for(int i=0; i<l.size(); i++){
                    //right
                    if (l.get(i).getOnline()==1) {tv.setText(l.get(i).getFirst_name()+" "+l.get(i).getLast_name()+" 'Online'");}
                    else {tv.setText(l.get(i).getFirst_name()+" "+l.get(i).getLast_name()+" 'Offline'");}
                    if (l.get(i).getCity()!=null) {
                        tv1.setText(l.get(i).getCity().getTitle());
                        lineRight.addView(tv1);
                    }
                    if (l.get(i).getCountry()!=null) {
                        tv2.setText(l.get(i).getCountry().getTitle());
                        lineRight.addView(tv2);
                    }
                    if ((l.get(i).getBdate()!="")&&(l.get(i).getBdate()!=null)) {
                        tv3.setText("Дата рождения: " + l.get(i).getBdate());
                        lineRight.addView(tv3);
                    }
                    //bottom
                    if ((l.get(i).getUniversity_name()!="")&&(l.get(i).getUniversity_name()!=null)) {
                        tv4.setText("Университет: " + l.get(i).getUniversity_name());
                        lineBottom.addView(tv4);
                    }
                    if ((l.get(i).getFaculty_name()!="")&&(l.get(i).getFaculty_name()!=null)) {
                        tv5.setText(l.get(i).getFaculty_name());
                        lineBottom.addView(tv5);
                    }
                    if ((l.get(i).getEducation_form()!="")&&(l.get(i).getEducation_form()!=null)) {
                        tv6.setText(l.get(i).getEducation_form());
                        lineBottom.addView(tv6);
                    }
                    if ((l.get(i).getEducation_status()!="")&&(l.get(i).getEducation_status()!=null)){
                        tv7.setText(l.get(i).getEducation_status());
                        lineBottom.addView(tv7);
                    }
                    if ((l.get(i).getMobile_phone()!="")&&(l.get(i).getMobile_phone()!=null)) {
                        tv8.setText("Мобильный: " + l.get(i).getMobile_phone());
                        lineBottom.addView(tv8);
                    }
                    if ((l.get(i).getHome_phone()!="")&&(l.get(i).getHome_phone()!=null)) {
                        tv9.setText("Домашний: " + l.get(i).getHome_phone());
                        lineBottom.addView(tv9);
                    }
                    Picasso.with(UserActivity.this)
                            .load(l.get(i).getPhoto_400_orig())
                            .into((ImageView)findViewById(R.id.imageView2));
                    final String finalphoto = l.get(i).getPhoto_max_orig();
                    ImageView imageView = (ImageView) findViewById(R.id.imageView2);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(UserActivity.this, PhotoActivity.class);
                            intent.putExtra("photo", finalphoto);
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<ArrayList<User>>> call, Throwable t) {
                Log.wtf("motya", t.getMessage());
                Toast toast = Toast.makeText(getApplicationContext(),
                        "              Internet connection is lost              ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout toastContainer = (LinearLayout) toast.getView();
                ImageView catImageView = new ImageView(getApplicationContext());
                catImageView.setImageResource(R.drawable.catsad);
                toastContainer.addView(catImageView, 0);
                toast.show();
            }
        });

        Button button = (Button) findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, DialogMessageActivity.class);
                intent.putExtra("userID", user_id);
                startActivity(intent);
            }
        });
    }

}
