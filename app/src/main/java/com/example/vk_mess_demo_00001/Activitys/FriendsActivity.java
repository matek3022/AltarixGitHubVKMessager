package com.example.vk_mess_demo_00001.Activitys;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vk_mess_demo_00001.Fragments.FriendListFragment;
import com.example.vk_mess_demo_00001.R;
import com.example.vk_mess_demo_00001.SQLite.DBHelper;
import com.example.vk_mess_demo_00001.Transformation.CircularTransformation;
import com.example.vk_mess_demo_00001.Utils.VKService;
import com.example.vk_mess_demo_00001.VKObjects.ItemMess;
import com.example.vk_mess_demo_00001.VKObjects.ServerResponse;
import com.example.vk_mess_demo_00001.VKObjects.User;
import com.example.vk_mess_demo_00001.VKObjects.item;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.vk_mess_demo_00001.App.service;

public class FriendsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ViewPager pager;
    PagerAdapter pagerAdapter;
    SwipeRefreshLayout refreshLayout;
    static final int PAGE_COUNT = 2;
    //    Retrofit retrofit;
    public static int page = 1; //на какой странице мы сейчас
    public static ArrayList<User> info;
    public static String ALL_FRIENDS = "All friends";
    public static String ONLINE_FRIENDS = "Online";
    SQLiteDatabase dataBase;
    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataBase = DBHelper.getInstance().getWritableDatabase();
        user_id = getIntent().getIntExtra("userID", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        setTitle("Friends");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final SharedPreferences SPuser = getSharedPreferences("uidgson", Context.MODE_PRIVATE);
        final String uidgson = SPuser.getString("uidgson_string", "");
        if (uidgson!="") {
            final User iuser = new Gson().fromJson(uidgson, User.class);
            Picasso.with(FriendsActivity.this)
                    .load(iuser.getPhoto_100())
                    .transform(new CircularTransformation())
                    .into((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView20));
            ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView20)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FriendsActivity.this, UserActivity.class);
                    intent.putExtra("userID", iuser.getId());
                    intent.putExtra("userJson", uidgson);
                    startActivity(intent);
                }
            });
            if (iuser.getOnline()==1){
                ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView21)).setVisibility(View.VISIBLE);
            }else {
                ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView21)).setVisibility(View.INVISIBLE);
            }
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.textView20)).setText(iuser.getFirst_name()+" "+iuser.getLast_name());
            if (iuser.getCity() != (null)) {
                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.textView21)).setText(iuser.getCity().getTitle());
            } else {
                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.textView21)).setText("");
            }
        }
        pager = (ViewPager) findViewById(R.id.pager);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(user_id);
            }
        });
        if (user_id==0) {
            Cursor cursor = dataBase.query(DBHelper.TABLE_FRIENDS, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                info = new ArrayList<>();
                Gson gson = new Gson();
                int user = cursor.getColumnIndex(DBHelper.KEY_OBJ);
                for (int i = 0; i < cursor.getCount(); i++) {
                    info.add(gson.fromJson(cursor.getString(user), User.class));
                    cursor.moveToNext();
                }
                pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
                pager.setAdapter(pagerAdapter);

                refresh(user_id);
            } else {
                refresh(user_id);
            }
            cursor.close();
        }else {
            refresh(user_id);
        }
    }

    @Override
    protected void onStop() {
        if (user_id==0) {
            dataBase.delete(DBHelper.TABLE_FRIENDS, null, null);
            ContentValues contentValues = new ContentValues();
            Gson gson = new Gson();

            for (int i = 0; i < info.size(); i++) {
                contentValues.put(DBHelper.KEY_ID_USER, info.get(i).getId());
                contentValues.put(DBHelper.KEY_OBJ, gson.toJson(info.get(i)));
                dataBase.insert(DBHelper.TABLE_FRIENDS, null, contentValues);
            }
        }
        super.onStop();
    }

    public void setAllFriendsCount(int cnt) {
        ALL_FRIENDS = "All friends" + " (" + cnt + ")";
    }

    public void setOnlineFriendsCount(int cnt) {
        ONLINE_FRIENDS = "Online" + " (" + cnt + ")";
    }

    private void refresh(int user_id) {
        refreshLayout.setRefreshing(true);
        final SharedPreferences Token = getSharedPreferences("token", Context.MODE_PRIVATE);
        String TOKEN = Token.getString("token_string", "");
        Call<ServerResponse<ItemMess<ArrayList<User>>>> call = service.getFriends(TOKEN, user_id, "online, photo_200, city");

        call.enqueue(new Callback<ServerResponse<ItemMess<ArrayList<User>>>>() {
            @Override
            public void onResponse(Call<ServerResponse<ItemMess<ArrayList<User>>>> call, Response<ServerResponse<ItemMess<ArrayList<User>>>> response) {
                Log.wtf("motya", response.raw().toString());
                ArrayList<User> l = response.body().getResponse().getitem();
                info = l;
                if (pager != null) {
                    page = pager.getCurrentItem();
                }
                pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
                pager.setAdapter(pagerAdapter);
                pager.setCurrentItem(page);
                refreshLayout.setRefreshing(false);
                Log.i("motya", info.get(0).getFirst_name());
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

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //Log.i("motya", new Gson().toJson(info));
            return FriendListFragment.newInstance(position, new Gson().toJson(info));
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return ALL_FRIENDS;
            } else {
                return ONLINE_FRIENDS;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dialogs) {
            Intent intent = new Intent();
            intent.setClass(FriendsActivity.this, DialogsActivity.class);
            startActivity(intent);
            FriendsActivity.this.finish();

        } else if (id == R.id.nav_friends) {
            Intent intent = new Intent();
            intent.setClass(FriendsActivity.this, FriendsActivity.class);
            startActivity(intent);
            FriendsActivity.this.finish();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent();
            intent.setClass(FriendsActivity.this, SettingActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
