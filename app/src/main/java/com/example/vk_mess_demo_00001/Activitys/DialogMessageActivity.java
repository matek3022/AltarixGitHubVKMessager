package com.example.vk_mess_demo_00001.Activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vk_mess_demo_00001.Transformation.CircularTransformation;
import com.example.vk_mess_demo_00001.VKObjects.Attachment;
import com.example.vk_mess_demo_00001.VKObjects.Dialogs;
import com.example.vk_mess_demo_00001.VKObjects.ItemMess;
import com.example.vk_mess_demo_00001.R;
import com.example.vk_mess_demo_00001.VKObjects.ServerResponse;
import com.example.vk_mess_demo_00001.VKObjects.User;
import com.example.vk_mess_demo_00001.Utils.Util;
import com.example.vk_mess_demo_00001.Utils.VKService;
import com.example.vk_mess_demo_00001.VKObjects.video_iformation;
import com.google.gson.Gson;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.vk_mess_demo_00001.App.service;

public class DialogMessageActivity extends AppCompatActivity {

    private int user_id;
    private int chat_id;
    private String title;
    private boolean frwd;
    Adapter adapter;
    Button qwe;
    private RecyclerView recyclerView;
    SwipyRefreshLayout refreshLayout;
    int off;
    ArrayList<Dialogs> items;
    ArrayList<User> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        frwd=false;
        user_id = getIntent().getIntExtra("userID", 0);
        chat_id = getIntent().getIntExtra("ChatID", 0);
        if (chat_id != 0) {
            user_id = 2000000000 + chat_id;
            title = getIntent().getStringExtra("Title");
        } else {
            if (user_id < 0) {
                title = "Сообщество";
            } else {
                title = getIntent().getStringExtra("userName");
            }
        }


        items = new ArrayList<>();
        names = new ArrayList<>();

        adapter = new Adapter();
        recyclerView = (RecyclerView) findViewById(R.id.list);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        setTitle(title);
        refreshLayout = (SwipyRefreshLayout) findViewById(R.id.refresh);
        refreshLayout.setColorSchemeResources(R.color.accent);
        off = 0;
        refresh(off);
        refreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    if (off != 0) {
                        off -= 100;
                    }
                    refresh(off);
                } else {
                    off += 100;
                    refresh(off);
                }
            }
        });
        qwe = (Button) findViewById(R.id.button);
        qwe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!frwd) {
                    refreshLayout.setRefreshing(true);
                    final EditText mess = (EditText) findViewById(R.id.editText);
                    if (!mess.getText().toString().equals("")) {
                        String message = mess.getText().toString();
                        mess.setText("");
                        int kek = user_id;
                        if (chat_id != 0) {
                            kek = 0;
                        }
                        final SharedPreferences Token = getSharedPreferences("token", Context.MODE_PRIVATE);
                        String TOKEN = Token.getString("token_string", "");
                        Call<ServerResponse> call = service.sendMessage(TOKEN, kek, message, chat_id, 2000000000 + chat_id);

                        call.enqueue(new Callback<ServerResponse>() {
                            @Override
                            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                Log.wtf("motya", response.raw().toString());
                                off = 0;
                                refresh(off);
//                            listView.setSelection(listView.getCount() - 1);
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
                                toast.show();
                            }
                        });
                    } else {
                        refreshLayout.setRefreshing(false);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Void message", Toast.LENGTH_SHORT);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }else {
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
//        Log.wtf("back","adapter.fwd_mess.size()= "+adapter.fwd_mess.size());
        if (adapter.fwd_mess.size() > 1) {
            adapter.fwd_mess.remove(adapter.fwd_mess.size() - 1);
            items = adapter.fwd_mess.get(adapter.fwd_mess.size() - 1);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(adapter.pos.get(adapter.pos.size() - 1));
            adapter.pos.remove(adapter.pos.size() - 1);
//            listView.setSelection(listView.getCount() - 1);
        } else {
            if (adapter.fwd_mess.size() == 1) {
                items.clear();
                items.addAll(adapter.reserv);
                frwd=false;
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(adapter.pos.get(adapter.pos.size() - 1));
                adapter.fwd_mess.clear();
                adapter.pos.remove(adapter.pos.size() - 1);
            } else {
                super.onBackPressed();
            }
        }
    }


    public String name_rec(Dialogs contain_mess) {
        String str = "";
        str = "," + contain_mess.getUser_id();
        for (int i = 0; i < contain_mess.getFwd_messages().size(); i++) {
            str += name_rec(contain_mess.getFwd_messages().get(i));
        }
        return str;
    }

    public void refresh(final int offset) {
        //if (offset==0) {frwd=false;}
        if (!frwd) {
            refreshLayout.setRefreshing(true);
//        adapter = new Adapter(this);
            names = new ArrayList<User>();

            final SharedPreferences Token = getSharedPreferences("token", Context.MODE_PRIVATE);
            String TOKEN = Token.getString("token_string", "");
            Call<ServerResponse<ItemMess<ArrayList<Dialogs>>>> call = service.getHistory(TOKEN, 100, offset, user_id);

            call.enqueue(new Callback<ServerResponse<ItemMess<ArrayList<Dialogs>>>>() {
                @Override
                public void onResponse(Call<ServerResponse<ItemMess<ArrayList<Dialogs>>>> call, Response<ServerResponse<ItemMess<ArrayList<Dialogs>>>> response) {
                    Log.wtf("motya", response.raw().toString());
                    ArrayList<Dialogs> l = response.body().getResponse().getitem();
                    String people_id = "" + l.get(0).getUser_id();
                    if (offset == 0) {
                        items.clear();
                        for (int i = l.size() - 1; i >= 0; i--) {
                            people_id += name_rec(l.get(i));
                            items.add(l.get(i));
                        }
                    } else {
                        ArrayList<Dialogs> trash = new ArrayList<Dialogs>();
                        trash.clear();
                        trash.addAll(items);
                        items.clear();
                        for (int i = l.size() - 1; i >= 0; i--) {
                            people_id += name_rec(l.get(i));
                            items.add(l.get(i));
                        }
                        items.addAll(trash);

                        //recyclerView.scrollToPosition(items.size()-trash.size()-1);
                    }
                    final SharedPreferences Uid = getSharedPreferences("uid", Context.MODE_PRIVATE);
                    people_id += ", " + Uid.getInt("uid_int", 0);
//                adapter.fwd_mess.add(adapter.items);
                    Log.wtf("names", people_id);
//                listView = (ListView) findViewById(R.id.listView);
//                listView.setAdapter(adapter);
//                if (chat_id == 0) {
//                    adapter.notifyDataSetChanged();
//                }
                    refreshLayout.setRefreshing(false);

                    final SharedPreferences Token = getSharedPreferences("token", Context.MODE_PRIVATE);
                    String TOKEN = Token.getString("token_string", "");
                    Call<ServerResponse<ArrayList<User>>> call1 = service.getUser(TOKEN, people_id, "photo_100,photo_400_orig,photo_max_orig, online,city,country,education, universities, schools,bdate,contacts");

                    call1.enqueue(new Callback<ServerResponse<ArrayList<User>>>() {
                        @Override
                        public void onResponse(Call<ServerResponse<ArrayList<User>>> call1, Response<ServerResponse<ArrayList<User>>> response) {
                            Log.wtf("motya", response.raw().toString());
                            ArrayList<User> l = response.body().getResponse();
                            if (offset == 0) {
                                names.clear();
                            }
                            for (int i = 0; i < l.size(); i++) {
                                names.add(l.get(i));
                            }
                            refreshLayout.setRefreshing(false);

                            recyclerView.scrollToPosition(items.size() - offset);
                            adapter.fwd_mess.clear();
                            adapter.reserv.clear();
                            adapter.reserv.addAll(items);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<ServerResponse<ArrayList<User>>> call1, Throwable t) {
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

                @Override
                public void onFailure(Call<ServerResponse<ItemMess<ArrayList<Dialogs>>>> call, Throwable t) {
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
        }else {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.titleee:
                off = 0;
                refresh(off);
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static String convertMonth(int num) {
        String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[num];
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        ImageView online;
        AutoLinkTextView body;
        TextView time;
        RelativeLayout background;
        LinearLayout line;
        RelativeLayout foo;

        public ViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.imageView);
            online = (ImageView) itemView.findViewById(R.id.imageView6);
            body = (AutoLinkTextView) itemView.findViewById(R.id.textView2);
            time = (TextView) itemView.findViewById(R.id.textView);
            background = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            line = (LinearLayout) itemView.findViewById(R.id.line);
            foo = (RelativeLayout) itemView.findViewById(R.id.foo);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private final SharedPreferences setting = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MM");
        SimpleDateFormat day = new SimpleDateFormat("dd");
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat min = new SimpleDateFormat("mm");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        ArrayList<ArrayList<Dialogs>> fwd_mess;
        ArrayList<Dialogs> reserv;
        ArrayList<Integer> pos;

        public Adapter() {
            fwd_mess = new ArrayList<>();
            reserv = new ArrayList<>();
            pos = new ArrayList<>();
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position).getOut();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new ViewHolder(View.inflate(DialogMessageActivity.this, R.layout.messin, null));
            } else {
                return new ViewHolder(View.inflate(DialogMessageActivity.this, R.layout.messout, null));
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Dialogs dialog = items.get(position);
            final int finalPos = position;
            User user = new User();
            for (int i = 0; i < names.size(); i++) {
                if (dialog.getOut()==0) {
                    if (dialog.getUser_id() == names.get(i).getId()) {
                        user = names.get(i);
                        break;
                    }
                }else {
                    if (dialog.getFrom_id() == names.get(i).getId()) {
                        user = names.get(i);
                        break;
                    }
                }
            }
            if (dialog.getRead_state() == 0) {
                holder.foo.setBackgroundColor(ContextCompat.getColor(DialogMessageActivity.this, R.color.accent));
            }else {
                holder.foo.setBackgroundColor(Color.WHITE);
            }
            holder.line.removeAllViews();
            if (user.getOnline() != 0) {
                holder.online.setVisibility(View.VISIBLE);
            } else {
                holder.online.setVisibility(View.INVISIBLE);
            }
            if (setting.getBoolean("photouserOn", true)) {
                Picasso.with(DialogMessageActivity.this)
                        .load(user.getPhoto_100())
                        .transform(new CircularTransformation())
                        .into(holder.photo);
            } else {
                Picasso.with(DialogMessageActivity.this)
                        .load("https://vk.com/images/soviet_100.png")
                        .transform(new CircularTransformation())
                        .into(holder.photo);
            }
            final User userFinal = user;
            holder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog.getChat_id() == 0) {
                        Intent intent = new Intent(DialogMessageActivity.this, UserActivity.class);
                        intent.putExtra("userID", dialog.getUser_id());
                        intent.putExtra("userJson", new Gson().toJson(userFinal));
                        startActivity(intent);
                    }
                }
            });
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
            if (chat_id != 0) {

                if (year.format(dateTs).equals(year.format(dateCurr))) {
                    if ((day.format(dateTs).equals(day.format(dateCurr))) && (month.format(dateTs).equals(month.format(dateCurr)))) {
                        holder.time.setText(user.getFirst_name() + " " + user.getLast_name() + ", " + time_time);
                    } else {
                        holder.time.setText(user.getFirst_name() + " " + user.getLast_name() + ", " + time_day + " "
                                + convertMonth(Integer.parseInt(month.format(dateTs))));
                    }
                } else {
                    holder.time.setText(user.getFirst_name() + " " + user.getLast_name() + ", " + time_year);
                }
            } else {

                if (year.format(dateTs).equals(year.format(dateCurr))) {
                    if ((day.format(dateTs).equals(day.format(dateCurr))) && (month.format(dateTs).equals(month.format(dateCurr)))) {
                        holder.time.setText("" + time_time);
                    } else {
                        holder.time.setText("" + time_day + " "
                                + convertMonth(Integer.parseInt(month.format(dateTs))));
                    }
                } else {
                    holder.time.setText("" + time_year);
                }
            }
            if (user.getOnline() == 0) {
                holder.online.setVisibility(View.INVISIBLE);
            } else {
                holder.online.setVisibility(View.VISIBLE);
            }
            holder.body.addAutoLinkMode(AutoLinkMode.MODE_URL);
            holder.body.setUrlModeColor(Color.rgb(0, 200, 250));
            holder.body.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    if (AutoLinkMode.MODE_URL.equals(autoLinkMode)) {
                        Log.wtf("motya", matchedText);
                        while (matchedText.contains(" ")) {
                            matchedText = matchedText.replace(" ", "");
                        }
                        while (matchedText.contains("\n")) {
                            matchedText = matchedText.replace("\n", "");
                        }
                        Log.wtf("motya", "fix_matchetText=" + matchedText);
                        Util.goToUrl(DialogMessageActivity.this, matchedText);
                    }
                }
            });
            String bodyContainer = dialog.getBody();
            Log.wtf ("ooo",bodyContainer);

            if (dialog.getFwd_messages().size() != 0) {
                LayoutInflater inflater = getLayoutInflater();
                View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic, null);
                TextView text = (TextView) cont.findViewById(R.id.textView3);
                text.setTextColor(Color.BLUE);
                text.setText("Пересланые сообщения");
                holder.line.addView(cont);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        frwd=true;
                        ArrayList<Dialogs> fwrd = new ArrayList<>();
                        for (int i = 0; i < dialog.getFwd_messages().size(); i++)
                            fwrd.add(dialog.getFwd_messages().get(i));
                        fwd_mess.add(fwrd);
                        pos.add(finalPos);
                        items = fwd_mess.get(fwd_mess.size() - 1);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            for (int i=0;i<dialog.getAttachments().size();i++){
                switch (dialog.getAttachments().get(i).getType()){
                    case "photo":{
                        if (setting.getBoolean("photochatOn", true)) {
                            String photo = "";
                            String photomess = "";
                            if (dialog.getAttachments().get(i).getPhoto().getPhoto_1280() != null) {
                                photo = dialog.getAttachments().get(i).getPhoto().getPhoto_1280();
                                photomess = dialog.getAttachments().get(i).getPhoto().getPhoto_604();
                            } else {
                                if (dialog.getAttachments().get(i).getPhoto().getPhoto_807() != null) {
                                    photo = dialog.getAttachments().get(i).getPhoto().getPhoto_807();
                                    photomess = dialog.getAttachments().get(i).getPhoto().getPhoto_604();
                                } else {
                                    if (dialog.getAttachments().get(i).getPhoto().getPhoto_604() != null) {
                                        photomess = photo = dialog.getAttachments().get(i).getPhoto().getPhoto_604();
                                    } else {
                                        if (dialog.getAttachments().get(i).getPhoto().getPhoto_130() != null) {
                                            photomess = photo = dialog.getAttachments().get(i).getPhoto().getPhoto_130();
                                        } else {
                                            if (dialog.getAttachments().get(i).getPhoto().getPhoto_75() != null) {
                                                photomess = photo = dialog.getAttachments().get(i).getPhoto().getPhoto_75();
                                            }
                                        }
                                    }
                                }
                            }
                            LayoutInflater inflater = getLayoutInflater();
                            View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic, null);
                            ImageView photochka = (ImageView) cont.findViewById(R.id.imageView);
                            TextView text = (TextView) cont.findViewById(R.id.textView3);
                            text.setText("Photo");
                            final String finalphoto = photo;
                            Picasso.with(DialogMessageActivity.this)
                                    .load(photomess)
                                    .placeholder(R.drawable.loadshort)
                                    .error(R.drawable.errorshort)
                                    .into(photochka);
                            holder.line.addView(cont);
                            photochka.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new ImageViewer.Builder(DialogMessageActivity.this, new String[]{finalphoto} )
                                            .show();
                                }
                            });
                        }
                        break;
                    }
                    case "sticker":{
                        LayoutInflater inflater = getLayoutInflater();
                        View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic, null);
                        ImageView photochka = (ImageView) cont.findViewById(R.id.imageView);
                        TextView text = (TextView) cont.findViewById(R.id.textView3);
                        text.setText("Stiker");
                        Picasso.with(DialogMessageActivity.this)
                                .load(dialog.getAttachments().get(i).getSticker().getPhoto_256())
                                .placeholder(R.drawable.loadshort)
                                .error(R.drawable.errorshort)
                                .resize(200, 200)
                                .centerCrop()
                                .into(photochka);
                        holder.line.addView(cont);
                        break;
                    }
                    case "link":{
                        bodyContainer +="\n" + dialog.getAttachments().get(i).getLink().getUrl();
                        break;
                    }
                    case "video":{
                        LayoutInflater inflater = getLayoutInflater();
                        View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic, null);
                        ImageView photochka = (ImageView) cont.findViewById(R.id.imageView);
                        TextView text = (TextView) cont.findViewById(R.id.textView3);
                        text.setText(dialog.getAttachments().get(i).getVideo().getTitle());
                        Picasso.with(DialogMessageActivity.this)
                                .load(dialog.getAttachments().get(i).getVideo().getPhoto_320())
                                .placeholder(R.drawable.loadshort)
                                .error(R.drawable.errorshort)
                                .resize(400, 300)
                                .centerCrop()
                                .into(photochka);
                        holder.line.addView(cont);
                        final String video = dialog.getAttachments().get(i).getVideo().getOwner_id() + "_" + dialog.getAttachments().get(i).getVideo().getId() + "_" + dialog.getAttachments().get(i).getVideo().getAccess_key();
                        photochka.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Loading...", Toast.LENGTH_LONG);
                                toast.show();
                                final SharedPreferences Token = getSharedPreferences("token", Context.MODE_PRIVATE);
                                String TOKEN = Token.getString("token_string", "");
                                Call<ServerResponse<ItemMess<ArrayList<video_iformation>>>> call = service.getVideos(TOKEN, video);

                                call.enqueue(new Callback<ServerResponse<ItemMess<ArrayList<video_iformation>>>>() {
                                    @Override
                                    public void onResponse(Call<ServerResponse<ItemMess<ArrayList<video_iformation>>>> call, Response<ServerResponse<ItemMess<ArrayList<video_iformation>>>> response) {
                                        Log.wtf("motya", response.raw().toString());
                                        String res = response.body().getResponse().getitem().get(0).getPlayer();
                                        Uri address = Uri.parse(res);
                                        Intent openlink = new Intent(Intent.ACTION_VIEW, address);
                                        startActivity(openlink);
                                    }

                                    @Override
                                    public void onFailure(Call<ServerResponse<ItemMess<ArrayList<video_iformation>>>> call, Throwable t) {
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
                        });
                        break;
                    }
                    case "doc":{
                        LayoutInflater inflater = getLayoutInflater();
                        View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic, null);
                        ImageView photochka = (ImageView) cont.findViewById(R.id.imageView);
                        TextView text = (TextView) cont.findViewById(R.id.textView3);
                        text.setText(dialog.getAttachments().get(i).getDoc().getTitle());
                        if (dialog.getAttachments().get(i).getDoc().getType() == 1) {
                            Picasso.with(DialogMessageActivity.this)
                                    .load(R.drawable.doc)
                                    .resize(150, 150)
                                    .centerCrop()
                                    .into(photochka);
                        } else {
                            Picasso.with(DialogMessageActivity.this)
                                    .load(R.drawable.zip)
                                    .resize(150, 150)
                                    .centerCrop()
                                    .into(photochka);
                        }
                        holder.line.addView(cont);
                        final Attachment att = dialog.getAttachments().get(i);
                        photochka.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String res = att.getDoc().getUrl();
                                Uri address = Uri.parse(res);
                                Intent openlink = new Intent(Intent.ACTION_VIEW, address);
                                startActivity(openlink);
                            }
                        });
                        break;
                    }
                    case "audio":{
                        LayoutInflater inflater = getLayoutInflater();
                        View cont = inflater.inflate(R.layout.attachment_conteiner_audio_dinamic, null);
                        TextView text = (TextView) cont.findViewById(R.id.textView);
                        text.setText(dialog.getAttachments().get(i).getAudio().getArtist() + " - " + dialog.getAttachments().get(i).getAudio().getTitle());
                        Button button = (Button) cont.findViewById(R.id.button);
                        Button button1 = (Button) cont.findViewById(R.id.button1);
                        Button button2 = (Button) cont.findViewById(R.id.button2);
                        Button button3 = (Button) cont.findViewById(R.id.button3);
                        Button button4 = (Button) cont.findViewById(R.id.button4);
                        holder.line.addView(cont);
                        Log.wtf("audio", dialog.getAttachments().get(i).getAudio().getUrl());
                        final String url = dialog.getAttachments().get(i).getAudio().getUrl();
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (DialogsActivity.mediaPlayer != null) {
                                    DialogsActivity.mediaPlayer.seekTo(DialogsActivity.mediaPlayer.getCurrentPosition() - 5000);
                                }
                            }
                        });
                        button1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (DialogsActivity.mediaPlayer != null) {
                                    Log.wtf("audio", "stop seek= " + DialogsActivity.mediaPlayer.getCurrentPosition());
                                    if (DialogsActivity.mediaPlayer.getCurrentPosition() == 0) {
                                        try {
                                            DialogsActivity.mediaPlayer.release();
                                            DialogsActivity.mediaPlayer = null;
                                            DialogsActivity.mediaPlayer = new MediaPlayer();
                                            DialogsActivity.mediaPlayer.setDataSource(url);
                                            Log.wtf("audio", "url start= " + url);
                                            DialogsActivity.mediaPlayer.prepare();
                                            DialogsActivity.mediaPlayer.start();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        DialogsActivity.mediaPlayer.start();
                                    }
                                } else {
                                    try {
                                        DialogsActivity.mediaPlayer = new MediaPlayer();
                                        DialogsActivity.mediaPlayer.setDataSource(url);
                                        Log.wtf("audio", "url start= " + url);
                                        DialogsActivity.mediaPlayer.prepare();
                                        DialogsActivity.mediaPlayer.start();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                        button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (DialogsActivity.mediaPlayer != null) {
                                    DialogsActivity.mediaPlayer.pause();
                                }
                            }
                        });
                        button3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (DialogsActivity.mediaPlayer != null) {
                                    DialogsActivity.mediaPlayer.seekTo(DialogsActivity.mediaPlayer.getCurrentPosition() + 5000);
                                }
                            }
                        });
                        button4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (DialogsActivity.mediaPlayer != null) {
                                    DialogsActivity.mediaPlayer.pause();
                                    DialogsActivity.mediaPlayer.seekTo(0);
                                    Log.wtf("audio", "stop seek= " + DialogsActivity.mediaPlayer.getCurrentPosition());
                                }
                            }
                        });
                        break;
                    }
                    case "wall":{
                        bodyContainer += "\n'" + dialog.getAttachments().get(i).getType() + "'";
                        break;
                    }
                    case "gift":{
                        LayoutInflater inflater = getLayoutInflater();
                        View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic, null);
                        ImageView photochka = (ImageView) cont.findViewById(R.id.imageView);
                        TextView text = (TextView) cont.findViewById(R.id.textView3);
                        text.setText("Gift");
                        Picasso.with(DialogMessageActivity.this)
                                .load(dialog.getAttachments().get(i).getGift().getThumb_256())
                                .placeholder(R.drawable.loadshort)
                                .error(R.drawable.errorshort)
                                .resize(200, 200)
                                .centerCrop()
                                .into(photochka);
                        holder.line.addView(cont);
                        break;
                    }
                }

            }
            holder.body.setAutoLinkText(bodyContainer);

        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

}
