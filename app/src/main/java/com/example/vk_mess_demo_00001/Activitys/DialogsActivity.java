package com.example.vk_mess_demo_00001.Activitys;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.example.vk_mess_demo_00001.R;
import com.example.vk_mess_demo_00001.SQLite.DBHelper;
import com.example.vk_mess_demo_00001.VKObjects.Dialogs;
import com.example.vk_mess_demo_00001.VKObjects.ItemMess;
import com.example.vk_mess_demo_00001.VKObjects.ServerResponse;
import com.example.vk_mess_demo_00001.VKObjects.User;
import com.example.vk_mess_demo_00001.VKObjects.item;
import com.example.vk_mess_demo_00001.Utils.VKService;
import com.example.vk_mess_demo_00001.Utils.namesChat;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DialogsActivity extends AppCompatActivity {
    public static MediaPlayer mediaPlayer;
    SwipeRefreshLayout refreshLayout;
    Adapter adapter;
    String stroka = "";
    int off = 0;
    ArrayList<namesChat> names;
    boolean chek;
    Retrofit retrofit;
    public static DBHelper dbHelper;
    SQLiteDatabase dataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new DBHelper(this);
        dataBase = dbHelper.getWritableDatabase();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.vk.com/method/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);
        setTitle(getString(R.string.dialogs));
        chek=true;
        ListView listView = (ListView) findViewById(R.id.listView);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        final SharedPreferences setting = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (setting.getBoolean("onlineOn",true)) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://api.vk.com/method/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    VKService service = retrofit.create(VKService.class);
                    final SharedPreferences Token = getSharedPreferences("token", Context.MODE_PRIVATE);
                    String TOKEN = Token.getString("token_string","");
                    Call<ServerResponse> call = service.setOnline(TOKEN);

                    call.enqueue(new Callback<ServerResponse>() {
                        @Override
                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                            Log.wtf("motya", response.raw().toString());
                            off = 0;
                            refresh(off);
                        }

                        @Override
                        public void onFailure(Call<ServerResponse> call, Throwable t) {
                            refreshLayout.setRefreshing(false);
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "              Internet connection is lost              ", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            LinearLayout toastContainer = (LinearLayout) toast.getView();
                            ImageView catImageView = new ImageView(getApplicationContext());
                            catImageView.setImageResource(R.drawable.catsad);
                            toastContainer.addView(catImageView, 0);
                            chek = false;
                            toast.show();
                        }
                    });
                } else {
                    off = 0;
                    refresh(off);
                }
            }
        });
        Cursor cursor = dataBase.query(DBHelper.TABLE_DIALOGS,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            adapter=new Adapter(this);
            Gson gson = new Gson();
            names=new ArrayList<>();
            int dialog = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int name = cursor.getColumnIndex(DBHelper.KEY_CHAT);
            for (int i=0;i<20;i++){
                adapter.items.add(gson.fromJson(cursor.getString(dialog),item.class));
                names.add(gson.fromJson(cursor.getString(name),namesChat.class));
                Log.i ("motya",""+adapter.getItem(i).getMessage().getTitle());
                Log.i ("motya",""+names.get(i).getFirst_name());
                cursor.moveToNext();
            }
            adapter.items.add(new item());
            listView.setAdapter(adapter);
            chek=false;
            adapter.notifyDataSetChanged();
        }else {
            off = 0;
            refresh(off);
        }
        cursor.close();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!chek) {
                if (position == 20) {
                    off += 20;
                    refresh(off);
                } else {
                    Intent intent = new Intent(DialogsActivity.this, DialogMessageActivity.class);
                    intent.putExtra("userID", adapter.getItem(position).getMessage().getUser_id());
                    intent.putExtra("Title", adapter.getItem(position).getMessage().getTitle());
                    intent.putExtra("ChatID", adapter.getItem(position).getMessage().getChat_id());
                    startActivity(intent);
                }
            }
            }
        });
    }

    @Override
    protected void onPause() {
        if (adapter!=null) {
            dataBase.delete(DBHelper.TABLE_DIALOGS,null,null);
            ContentValues contentValues = new ContentValues();
            Gson gson = new Gson();
            for (int i = 0; i < 20; i++) {
                contentValues.put(DBHelper.KEY_NAME, gson.toJson(adapter.getItem(i)));
                contentValues.put(DBHelper.KEY_CHAT,gson.toJson(names.get(i)));
                dataBase.insert(DBHelper.TABLE_DIALOGS, null, contentValues);
            }
        }
        super.onPause();
    }

    public void refresh(int offset) {
        refreshLayout.setRefreshing(true);
        final Context con = this;
        stroka = "";
        chek = true;
        VKService service = retrofit.create(VKService.class);
        final SharedPreferences Token = getSharedPreferences("token", Context.MODE_PRIVATE);
        String TOKEN = Token.getString("token_string","");
        Call<ServerResponse<ItemMess<ArrayList<item>>>> call = service.getDialogs(TOKEN, 20, offset);

        call.enqueue(new Callback<ServerResponse<ItemMess<ArrayList<item>>>>() {
            @Override
            public void onResponse(Call<ServerResponse<ItemMess<ArrayList<item>>>> call,
                                   Response<ServerResponse<ItemMess<ArrayList<item>>>> response) {
                Log.wtf("motya", response.raw().toString());
                adapter = new Adapter(con);
                ArrayList<item> l = response.body().getResponse().getitem();
                if (l.size()!=0) stroka+="" + l.get(0).getMessage().getUser_id();
                for (int i = 0; i < l.size(); i++) {
                    adapter.items.add(l.get(i));
                    if (i != 0) {
                        stroka += "," + l.get(i).getMessage().getUser_id();
                    }
                }
                adapter.items.add(new item());
                final SharedPreferences Token = getSharedPreferences("token", Context.MODE_PRIVATE);
                String TOKEN = Token.getString("token_string","");
                VKService service = retrofit.create(VKService.class);
                Call<ServerResponse<ArrayList<User>>> call1 = service.getUser(TOKEN,
                        stroka,
                        "photo_100, online");
                call1.enqueue(new Callback<ServerResponse<ArrayList<User>>>() {
                    @Override
                    public void onResponse(Call<ServerResponse<ArrayList<User>>> call1, Response<ServerResponse<ArrayList<User>>> response) {
                        Log.wtf("motya", response.raw().toString());
                        ArrayList<User> l1 = response.body().getResponse();
                        names = new ArrayList<namesChat>();
                        for (int i = 0; i < l1.size(); i++) {
                            names.add(new namesChat(l1.get(i).getId(),l1.get(i).getOnline(),l1.get(i).getFirst_name(),l1.get(i).getLast_name(),l1.get(i).getPhoto_100()));
                        }
                        ListView listView = (ListView) findViewById(R.id.listView);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        chek = false;
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<ServerResponse<ArrayList<User>>> call1, Throwable t) {
                        Log.wtf("motya", t.getMessage());
                        refreshLayout.setRefreshing(false);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "              Internet connection is lost              ", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        LinearLayout toastContainer = (LinearLayout) toast.getView();
                        ImageView catImageView = new ImageView(getApplicationContext());
                        catImageView.setImageResource(R.drawable.catsad);
                        toastContainer.addView(catImageView, 0);
                        chek = false;
                        toast.show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ServerResponse<ItemMess<ArrayList<item>>>> call, Throwable t) {
                Log.wtf("motya", t.getMessage());
                refreshLayout.setRefreshing(false);
                Toast toast = Toast.makeText(getApplicationContext(),
                        "              Internet connection is lost              ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout toastContainer = (LinearLayout) toast.getView();
                ImageView catImageView = new ImageView(getApplicationContext());
                catImageView.setImageResource(R.drawable.catsad);
                toastContainer.addView(catImageView, 0);
                chek = false;
                toast.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dialog_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.titleee:
                //Toast.makeText(this, "sdjklhnfuise", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DialogsActivity.this, SettingActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static String convertMonth(int num) {
        String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[num];
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public class Adapter extends BaseAdapter {
        ArrayList<item> items;
        Context context;

        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MM");
        SimpleDateFormat day = new SimpleDateFormat("dd");
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat min = new SimpleDateFormat("mm");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");

        public Adapter(Context con) {
            items = new ArrayList<item>();
            context = con;
        }

        @Override
        public int getCount() {
            return items.size();

        }

        @Override
        public item getItem(int position) {
            return items.get(position);
        }


        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            item item=getItem(position);
            final Dialogs dialog = item.getMessage();
            namesChat nameme = new namesChat(0,10,"","","");
            SharedPreferences setting = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
            for (int i=0;i<names.size();i++){
                if (dialog.getUser_id()==names.get(i).getUser_id()){
                    nameme = names.get(i);
                    break;
                }
            }
            if (dialog.getRead_state()!=20) {
                if (dialog.getUser_id()>=0) {
                    convertView = getLayoutInflater().inflate(R.layout.item_list, null);
                    if (dialog.getChat_id() == 0) {
                        if (nameme.getOnline() != 0) {
                            Picasso.with(context)
                                    .load(R.drawable.tochka)
                                    .resize(30, 30)
                                    .centerCrop()
                                    .into((ImageView) convertView.findViewById(R.id.imageView5));
                        } else {
                            Picasso.with(context)
                                    .load(R.drawable.white)
                                    .resize(30, 30)
                                    .centerCrop()
                                    .into((ImageView) convertView.findViewById(R.id.imageView5));
                        }
                        if (setting.getBoolean("photouserOn",true)) {
                            Picasso.with(context)
                                    .load(nameme.getPhoto_100())
                                    .placeholder(R.drawable.whitephoto)
                                    .resize(150, 150)
                                    .centerCrop()
                                    .into((ImageView) convertView.findViewById(R.id.imageView4));
                        }
                    } else {
                        if (setting.getBoolean("photouserOn",true))
                        if (dialog.getPhoto_100() != null) {
                            Picasso.with(context)
                                    .load(dialog.getPhoto_100())
                                    .placeholder(R.drawable.whitephoto)
                                    .resize(150, 150)
                                    .centerCrop()
                                    .into((ImageView) convertView.findViewById(R.id.imageView4));
                        } else {
                            Picasso.with(context)
                                    .load("https://vk.com/images/soviet_200.png")
                                    .resize(150, 150)
                                    .centerCrop()
                                    .into((ImageView) convertView.findViewById(R.id.imageView4));
                        }
                    }
                    if (item.getUnread()!=0){

                        ((TextView) convertView.findViewById(R.id.textView9)).setText(""+item.getUnread());
                    }else {
                        ((TextView) convertView.findViewById(R.id.textView9)).setBackgroundColor(Color.WHITE);
                    }
                    if (dialog.getRead_state() == 0) {
                        ((TextView) convertView.findViewById(R.id.textView6)).setBackgroundColor(Color.rgb(236, 235, 235));
                    } else {
                        ((TextView) convertView.findViewById(R.id.textView6)).setBackgroundColor(Color.WHITE);
                    }
                    if (dialog.getBody() != "") {
                        if (dialog.getOut() == 0) {
                            ((TextView) convertView.findViewById(R.id.textView6)).setText("" + dialog.getBody());
                        } else {
                            ((TextView) convertView.findViewById(R.id.textView6)).setText("Вы: " + dialog.getBody());
                        }
                    } else {
                        if (dialog.getOut() == 0) {
                            if (dialog.getAttachments().size() > 0) {
                                ((TextView) convertView.findViewById(R.id.textView6)).setText("'" + dialog.getAttachments().get(0).getType() + "'");
                            } else {
                                ((TextView) convertView.findViewById(R.id.textView6)).setText("'Пересланые сообщения'");
                            }
                        } else {
                            if (dialog.getAttachments().size() > 0) {
                                ((TextView) convertView.findViewById(R.id.textView6)).setText("Вы: " + "'" + dialog.getAttachments().get(0).getType() + "'");
                            } else {
                                ((TextView) convertView.findViewById(R.id.textView6)).setText("Вы: " + "'Пересланые сообщения'");
                            }
                        }
                    }
                    if (dialog.getChat_id() == 0) {
                        ((TextView) convertView.findViewById(R.id.textView)).setText(nameme.getFirst_name() + " " + nameme.getLast_name());
                        dialog.setTitle(nameme.getFirst_name() + " " + nameme.getLast_name());
                        convertView.findViewById(R.id.imageView4).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(DialogsActivity.this, UserActivity.class);
                                intent.putExtra("userID", dialog.getUser_id());
                                startActivity(intent);
                            }
                        });
                    } else {
                        ((TextView) convertView.findViewById(R.id.textView)).setText(dialog.getTitle());
                    }
                    year.setTimeZone(TimeZone.getTimeZone("GMT+4"));
                    month.setTimeZone(TimeZone.getTimeZone("GMT+4"));
                    day.setTimeZone(TimeZone.getTimeZone("GMT+4"));
                    hour.setTimeZone(TimeZone.getTimeZone("GMT+4"));
                    min.setTimeZone(TimeZone.getTimeZone("GMT+4"));
                    time.setTimeZone(TimeZone.getTimeZone("GMT+4"));
                    Date dateCurr = new Date(System.currentTimeMillis());
                    Date dateTs = new Date(dialog.getDate() * 1000L);
                    String time_day = day.format(dateTs);
                    String time_time = time.format(dateTs);
                    String time_year = year.format(dateTs);
                    if (year.format(dateTs).equals(year.format(dateCurr))) {
                        if ((day.format(dateTs).equals(day.format(dateCurr))) && (month.format(dateTs).equals(month.format(dateCurr)))) {
                            ((TextView) convertView.findViewById(R.id.textView8)).setText("" + time_time);
                        } else {
                            ((TextView) convertView.findViewById(R.id.textView8)).setText("" + time_day + " "
                                    + convertMonth(Integer.parseInt(month.format(dateTs))));
                        }
                    } else {
                        ((TextView) convertView.findViewById(R.id.textView8)).setText("" + time_year);
                    }
                }else {
                    convertView = getLayoutInflater().inflate(R.layout.item_list, null);
                    if (dialog.getOut() == 0) {
                        ((TextView) convertView.findViewById(R.id.textView6)).setText("" + dialog.getBody());
                    } else {
                        ((TextView) convertView.findViewById(R.id.textView6)).setText("Вы: " + dialog.getBody());
                    }
                    ((TextView) convertView.findViewById(R.id.textView)).setText("Сообщество");
                    year.setTimeZone(TimeZone.getTimeZone("GMT+4"));
                    month.setTimeZone(TimeZone.getTimeZone("GMT+4"));
                    day.setTimeZone(TimeZone.getTimeZone("GMT+4"));
                    hour.setTimeZone(TimeZone.getTimeZone("GMT+4"));
                    min.setTimeZone(TimeZone.getTimeZone("GMT+4"));
                    time.setTimeZone(TimeZone.getTimeZone("GMT+4"));
                    Date dateCurr = new Date(System.currentTimeMillis());
                    Date dateTs = new Date(dialog.getDate() * 1000L);
                    String time_day = day.format(dateTs);
                    String time_time = time.format(dateTs);
                    String time_year = year.format(dateTs);
                    if (year.format(dateTs).equals(year.format(dateCurr))) {
                        if ((day.format(dateTs).equals(day.format(dateCurr))) && (month.format(dateTs).equals(month.format(dateCurr)))) {
                            ((TextView) convertView.findViewById(R.id.textView8)).setText("" + time_time);
                        } else {
                            ((TextView) convertView.findViewById(R.id.textView8)).setText("" + time_day + " "
                                    + convertMonth(Integer.parseInt(month.format(dateTs))));
                        }
                    } else {
                        ((TextView) convertView.findViewById(R.id.textView8)).setText("" + time_year);
                    }
                    dialog.setTitle("Сообщество");
                }
            } else {
                convertView = getLayoutInflater().inflate(R.layout.next_list, null);
                ((TextView) convertView.findViewById(R.id.textView7)).setText("View next dialogs?");
            }
            return convertView;
        }
    }
}
