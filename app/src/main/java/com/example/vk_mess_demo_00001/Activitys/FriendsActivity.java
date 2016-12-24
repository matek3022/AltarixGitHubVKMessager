package com.example.vk_mess_demo_00001.Activitys;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vk_mess_demo_00001.Fragments.FriendListFragment;
import com.example.vk_mess_demo_00001.R;
import com.example.vk_mess_demo_00001.Utils.VKService;
import com.example.vk_mess_demo_00001.Utils.namesChat;
import com.example.vk_mess_demo_00001.VKObjects.ItemMess;
import com.example.vk_mess_demo_00001.VKObjects.ServerResponse;
import com.example.vk_mess_demo_00001.VKObjects.User;
import com.example.vk_mess_demo_00001.VKObjects.item;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendsActivity extends AppCompatActivity {

    ViewPager pager;
    PagerAdapter pagerAdapter;
    SwipeRefreshLayout refreshLayout;
    static final int PAGE_COUNT = 2;
    Retrofit retrofit;
    public static ArrayList<User> info;
    public static String ALL_FRIENDS = "All friends";
    public static String ONLINE_FRIENDS = "Online";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.vk.com/method/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pager = (ViewPager) findViewById(R.id.pager);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(0);
            }
        });
        refresh(0);
    }

    @Override
    protected void onDestroy() {
        /* У ТЕБЯ НЕ УДАЛЯЮТСЯ ФРАГМЕНТЫ, УЗНАЙ КАК УДАЛИТЬ И ВОЗВРАЩАЙСЯ СЮДА ОБЯЗАТЕЛЬНО*/
        /* У ТЕБЯ НЕ УДАЛЯЮТСЯ ФРАГМЕНТЫ, УЗНАЙ КАК УДАЛИТЬ И ВОЗВРАЩАЙСЯ СЮДА ОБЯЗАТЕЛЬНО*/
        /* У ТЕБЯ НЕ УДАЛЯЮТСЯ ФРАГМЕНТЫ, УЗНАЙ КАК УДАЛИТЬ И ВОЗВРАЩАЙСЯ СЮДА ОБЯЗАТЕЛЬНО*/
        /* У ТЕБЯ НЕ УДАЛЯЮТСЯ ФРАГМЕНТЫ, УЗНАЙ КАК УДАЛИТЬ И ВОЗВРАЩАЙСЯ СЮДА ОБЯЗАТЕЛЬНО*/
        /* У ТЕБЯ НЕ УДАЛЯЮТСЯ ФРАГМЕНТЫ, УЗНАЙ КАК УДАЛИТЬ И ВОЗВРАЩАЙСЯ СЮДА ОБЯЗАТЕЛЬНО*/
        super.onDestroy();
    }

    public void setAllFriendsCount (int cnt){
        ALL_FRIENDS="All friends"+" ("+cnt+")";
    }
    public void setOnlineFriendsCount (int cnt){
        ONLINE_FRIENDS="Online"+" ("+cnt+")";
    }
    private void refresh(int user_id){
        refreshLayout.setRefreshing(true);
        VKService service = retrofit.create(VKService.class);
        final SharedPreferences Token = getSharedPreferences("token", Context.MODE_PRIVATE);
        String TOKEN = Token.getString("token_string", "");
        Call<ServerResponse<ItemMess<ArrayList<User>>>> call = service.getFriends(TOKEN, user_id, "online, photo_200, city");

        call.enqueue(new Callback<ServerResponse<ItemMess<ArrayList<User>>>>() {
            @Override
            public void onResponse(Call<ServerResponse<ItemMess<ArrayList<User>>>> call,Response<ServerResponse<ItemMess<ArrayList<User>>>> response) {
                Log.wtf("motya", response.raw().toString());
                ArrayList<User> l = response.body().getResponse().getitem();
                info=l;
                pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
                pager.setAdapter(pagerAdapter);
                refreshLayout.setRefreshing(false);
                Log.i("motya",info.get(0).getFirst_name());
            }

            @Override
            public void onFailure(Call<ServerResponse<ItemMess<ArrayList<User>>>> call, Throwable t) {
                Log.wtf("motya", t.getMessage());
                refreshLayout.setRefreshing(false);
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
    }
    public ArrayList<User> getInfo(){
        return info;
    }
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.i("motya",new Gson().toJson(info));
            return FriendListFragment.newInstance(position,new Gson().toJson(info));
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position==0){
                return ALL_FRIENDS;
            }else {
                return ONLINE_FRIENDS;
            }
        }
    }
}
