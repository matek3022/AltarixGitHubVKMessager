package com.example.vk_mess_demo_00001;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.senab.photoview.PhotoViewAttacher;

public class Main3Activity extends AppCompatActivity {

    private int user_id;
    private int chat_id;
    private String title;
    ArrayList<namesChat> names;
    Adapter adapter;
    Button qwe;
    ListView listView;
    SwipyRefreshLayout refreshLayout;
    int off;
    Retrofit retrofit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.vk.com/method/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        user_id = getIntent().getIntExtra("userID", 0);
        chat_id=getIntent().getIntExtra("ChatID",0);
        if (chat_id!=0) user_id=2000000000 + chat_id;
        title = getIntent().getStringExtra("Title");
        setTitle(title);
        refreshLayout = (SwipyRefreshLayout) findViewById(R.id.refresh);
        refreshLayout.setColorSchemeResources(R.color.accent);
        off=0;
        refresh(off);
        refreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if(direction == SwipyRefreshLayoutDirection.BOTTOM){
                    if (off!=0) {
                        off-=20;
                    }
                    refresh(off);
                } else {
                    off+=20;
                    refresh(off);
                }
            }
        });
        qwe = (Button) findViewById(R.id.button);

        qwe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.setRefreshing(true);
                final EditText mess = (EditText) findViewById(R.id.editText);
                if (!mess.getText().toString().equals("")) {
                    String message=mess.getText().toString();
                    mess.setText("");
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://api.vk.com/method/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    VKService service = retrofit.create(VKService.class);
                    int kek = user_id;
                    if (chat_id != 0) {
                        kek = 0;
                    }
                    Call<ServerResponse> call = service.sendMessage(Main2Activity.TOKEN, kek, message, chat_id, 2000000000 + chat_id);

                    call.enqueue(new Callback<ServerResponse>() {
                        @Override
                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                            Log.wtf("motya", response.raw().toString());
                            off = 0;
                            refresh(off);
                            listView.setSelection(listView.getCount() - 1);
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
                }else {
                    refreshLayout.setRefreshing(false);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Void message", Toast.LENGTH_SHORT);
                    //toast.setGravity(Gravity.CENTER, 0, 0);

                    toast.show();
                }
            }
        });
    }
    public static String convertMonth(int num) {
        String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[num];
    }
    public void refresh(int off) {
        refreshLayout.setRefreshing(true);
        adapter = new Adapter(this);
        names = new ArrayList<namesChat>();
        VKService service = retrofit.create(VKService.class);

        Call<ServerResponse<ItemMess<ArrayList<Dialogs>>>> call = service.getHistory(Main2Activity.TOKEN, 20, off, user_id);

        call.enqueue(new Callback<ServerResponse<ItemMess<ArrayList<Dialogs>>>>() {
            @Override
            public void onResponse(Call<ServerResponse<ItemMess<ArrayList<Dialogs>>>> call, Response<ServerResponse<ItemMess<ArrayList<Dialogs>>>> response) {
                Log.wtf("motya", response.raw().toString());
                ArrayList<Dialogs> l = response.body().getResponse().getitem();
                for (int i=l.size()-1; i>=0;i--){
                    adapter.items.add(l.get(i));
                }
                listView = (ListView) findViewById(R.id.listView);
                listView.setAdapter(adapter);
                if (chat_id==0) {
                    adapter.notifyDataSetChanged();
                }
                refreshLayout.setRefreshing(false);
                if (chat_id!=0){

                    VKService service = retrofit.create(VKService.class);

                    Call<ServerResponse<ArrayList<namesChat>>> call1 = service.getChatUsers(Main2Activity.TOKEN, chat_id,"photo_100,online");

                    call1.enqueue(new Callback<ServerResponse<ArrayList<namesChat>>>() {
                        @Override
                        public void onResponse(Call<ServerResponse<ArrayList<namesChat>>> call1, Response<ServerResponse<ArrayList<namesChat>>> response) {
                            Log.wtf("motya", response.raw().toString());
                            ArrayList<namesChat> l = response.body().getResponse();

                            for (int i=0; i<l.size();i++){
                                names.add(l.get(i));
                            }
                            refreshLayout.setRefreshing(false);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<ServerResponse<ArrayList<namesChat>>> call1, Throwable t) {
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
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.titleee:
                refresh(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public class  Adapter extends BaseAdapter {

        ArrayList<Dialogs> items;//класс список
        Context context;
        public Adapter(Context con) {
            items = new ArrayList<>();
            context = con;

        }
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Dialogs getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Dialogs mess = getItem(position);
            SimpleDateFormat year = new SimpleDateFormat("yyyy");
            SimpleDateFormat month = new SimpleDateFormat("MM");
            SimpleDateFormat day = new SimpleDateFormat("dd");
            SimpleDateFormat hour = new SimpleDateFormat("HH");
            SimpleDateFormat min = new SimpleDateFormat("mm");
            SimpleDateFormat time = new SimpleDateFormat("HH:mm");
            SharedPreferences setting = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
            if (mess.getOut() == 0) {
                convertView = getLayoutInflater().inflate(R.layout.messin, null);
                convertView.setBackgroundColor(Color.rgb(233, 237, 245));
                ((TextView) convertView.findViewById(R.id.textView3)).setText("");
                if (chat_id!=0) {
                    for (int i=0;i<names.size();i++){
                        if (mess.getUser_id()==names.get(i).getUser_id()){
                            if (names.get(i).getOnline() == 1) {
                                ((TextView) convertView.findViewById(R.id.textView3)).setText(names.get(i).getFirst_name() + " " + names.get(i).getLast_name() + " (Online)");
                            }else {
                                ((TextView) convertView.findViewById(R.id.textView3)).setText(names.get(i).getFirst_name() + " " + names.get(i).getLast_name());
                            }
                            if (setting.getBoolean("photouserOn",true))
                                Picasso.with(context)
                                        .load(names.get(i).getPhoto_100())
                                        .resize(150, 150)
                                        .centerCrop()
                                        .into((ImageView) convertView.findViewById(R.id.imageView));
                            break;
                        }
                    }
                }else {

                    ((TextView) convertView.findViewById(R.id.textView3)).setText(title);
                }
            }
            else {
                convertView = getLayoutInflater().inflate(R.layout.messout,null);
                convertView.setBackgroundColor(Color.rgb(233, 237, 245));
                ((TextView) convertView.findViewById(R.id.textView3)).setText("");
            }
            LinearLayout line = (LinearLayout) convertView.findViewById(R.id.line);
            AutoLinkTextView tvBody = (AutoLinkTextView) convertView.findViewById(R.id.textView2);
            tvBody.addAutoLinkMode(AutoLinkMode.MODE_URL);
            tvBody.setUrlModeColor(Color.rgb(0,200,250));
            tvBody.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    if(AutoLinkMode.MODE_URL.equals(autoLinkMode)){
                        Log.wtf("motya",matchedText);
                        while(matchedText.contains(" ")) {
                            matchedText=matchedText.replace(" ", "");
                        }
                        Log.wtf("motya","fix_matchetText="+matchedText);
                        Util.goToUrl(context, matchedText);
                    }
                }
            });
            if (mess.getAttachments().size() == 0) {
                if (mess.getFwd_messages().size() == 0) {
                    tvBody.setAutoLinkText(mess.getBody());
                } else {
                    tvBody.setAutoLinkText(mess.getBody() + " 'Пересланые сообщения'");
                }
            } else {
                String str="";
                for (int i = 0; i < mess.getAttachments().size(); i++) {
                    if (mess.getAttachments().get(i).getType().equals("photo")) {
                        if (setting.getBoolean("photochatOn",true)) {
                            String photo = "";
                            String photomess = "";
                            if (mess.getAttachments().get(i).getPhoto().getPhoto_1280()!=null) {
                                photo = mess.getAttachments().get(i).getPhoto().getPhoto_1280();
                                photomess = mess.getAttachments().get(i).getPhoto().getPhoto_604();
                            } else {
                                if (mess.getAttachments().get(i).getPhoto().getPhoto_807()!=null) {
                                    photo = mess.getAttachments().get(i).getPhoto().getPhoto_807();
                                    photomess = mess.getAttachments().get(i).getPhoto().getPhoto_604();
                                } else {
                                    if (mess.getAttachments().get(i).getPhoto().getPhoto_604()!=null) {
                                        photomess=photo = mess.getAttachments().get(i).getPhoto().getPhoto_604();
                                    } else {
                                        if (mess.getAttachments().get(i).getPhoto().getPhoto_130()!=null) {
                                            photomess=photo = mess.getAttachments().get(i).getPhoto().getPhoto_130();
                                        } else {
                                            if (mess.getAttachments().get(i).getPhoto().getPhoto_75()!=null) {
                                                photomess=photo = mess.getAttachments().get(i).getPhoto().getPhoto_75();
                                            }
                                        }
                                    }
                                }
                            }
                            LayoutInflater inflater = getLayoutInflater();
                            View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic,null);
                            ImageView photochka = (ImageView) cont.findViewById(R.id.imageView);
                            TextView text = (TextView) cont.findViewById(R.id.textView3);
                            text.setText("Photo");
                            final String finalphoto = photo;
                            Picasso.with(context)
                                    .load(photomess)
                                    .placeholder(R.drawable.loadshort)
                                    .error(R.drawable.errorshort)
                                    .resize(400, 300)
                                    .centerCrop()
                                    .into(photochka);
                            line.addView(cont);
                            photochka.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Main3Activity.this, PhotoActivity.class);
                                    intent.putExtra("photo", finalphoto);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                    if (mess.getAttachments().get(i).getType().equals("sticker")){
                        LayoutInflater inflater = getLayoutInflater();
                        View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic,null);
                        ImageView photochka = (ImageView) cont.findViewById(R.id.imageView);
                        TextView text = (TextView) cont.findViewById(R.id.textView3);
                        text.setText("Stiker");
                        Picasso.with(context)
                                .load(mess.getAttachments().get(i).getSticker().getPhoto_128())
                                .placeholder(R.drawable.loadshort)
                                .error(R.drawable.errorshort)
                                .resize(150, 150)
                                .centerCrop()
                                .into(photochka);
                        line.addView(cont);
                    }
                    if (mess.getAttachments().get(i).getType().equals("link")) {
                        str += mess.getAttachments().get(i).getLink().getUrl();
                    }
                    if (mess.getAttachments().get(i).getType().equals("video")) {
                        LayoutInflater inflater = getLayoutInflater();
                        View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic,null);
                        ImageView photochka = (ImageView) cont.findViewById(R.id.imageView);
                        TextView text = (TextView) cont.findViewById(R.id.textView3);
                        text.setText(mess.getAttachments().get(i).getVideo().getTitle());
                        Picasso.with(context)
                                .load(mess.getAttachments().get(i).getVideo().getPhoto_320())
                                .placeholder(R.drawable.loadshort)
                                .error(R.drawable.errorshort)
                                .resize(400, 300)
                                .centerCrop()
                                .into(photochka);
                        line.addView(cont);
                        final String video = mess.getAttachments().get(i).getVideo().getOwner_id()+"_"+mess.getAttachments().get(i).getVideo().getId()+"_"+mess.getAttachments().get(i).getVideo().getAccess_key();
                        photochka.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                VKService service = retrofit.create(VKService.class);
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Loading...", Toast.LENGTH_LONG);
                                toast.show();
                                Call<ServerResponse<ItemMess<ArrayList<video_iformation>>>> call = service.getVideos(Main2Activity.TOKEN,video);

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
                    }
                    if (mess.getAttachments().get(i).getType().equals("doc")) {
                        LayoutInflater inflater = getLayoutInflater();
                        View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic,null);
                        ImageView photochka = (ImageView) cont.findViewById(R.id.imageView);
                        TextView text = (TextView) cont.findViewById(R.id.textView3);
                        text.setText(mess.getAttachments().get(i).getDoc().getTitle());
                        if (mess.getAttachments().get(i).getDoc().getType()==1) {
                            Picasso.with(context)
                                    .load(R.drawable.doc)
                                    .resize(150, 150)
                                    .centerCrop()
                                    .into(photochka);
                        }else{
                            Picasso.with(context)
                                    .load(R.drawable.zip)
                                    .resize(150, 150)
                                    .centerCrop()
                                    .into(photochka);
                        }
                        line.addView(cont);
                        final Attachment att = mess.getAttachments().get(i);
                        photochka.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String res = att.getDoc().getUrl();
                                Uri address = Uri.parse(res);
                                Intent openlink = new Intent(Intent.ACTION_VIEW, address);
                                startActivity(openlink);
                            }
                        });
                    }
                    if (mess.getAttachments().get(i).getType().equals("audio")) {
                        LayoutInflater inflater = getLayoutInflater();
                        View cont = inflater.inflate(R.layout.attachment_conteiner_audio_dinamic,null);
                        TextView text = (TextView) cont.findViewById(R.id.textView);
                        Log.wtf("audio",mess.getAttachments().get(i).getAudio().getUrl());
                        text.setText(mess.getAttachments().get(i).getAudio().getArtist()+" - "+mess.getAttachments().get(i).getAudio().getTitle());
                        Button button = (Button) cont.findViewById(R.id.button);
                        Button button1 = (Button) cont.findViewById(R.id.button1);
                        Button button2 = (Button) cont.findViewById(R.id.button2);
                        Button button3 = (Button) cont.findViewById(R.id.button3);
                        Button button4 = (Button) cont.findViewById(R.id.button4);
                        line.addView(cont);
                        Log.wtf("audio",mess.getAttachments().get(i).getAudio().getUrl());
                        final String url = mess.getAttachments().get(i).getAudio().getUrl();
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Main2Activity.mediaPlayer != null) {
                                    Main2Activity.mediaPlayer.seekTo(Main2Activity.mediaPlayer.getCurrentPosition() - 5000);
                                }
                            }
                        });
                        button1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Main2Activity.mediaPlayer != null) {
                                    Log.wtf("audio","stop seek= "+Main2Activity.mediaPlayer.getCurrentPosition());
                                    if (Main2Activity.mediaPlayer.getCurrentPosition()==0) {
                                        try {
                                            Main2Activity.mediaPlayer.release();
                                            Main2Activity.mediaPlayer = null;
                                            Main2Activity.mediaPlayer = new MediaPlayer();
                                            Main2Activity.mediaPlayer.setDataSource(url);
                                            Log.wtf("audio","url start= "+url);
                                            Main2Activity.mediaPlayer.prepare();
                                            Main2Activity.mediaPlayer.start();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }else {
                                        Main2Activity.mediaPlayer.start();
                                    }
                                }else{
                                    try {
                                        Main2Activity.mediaPlayer = new MediaPlayer();
                                        Main2Activity.mediaPlayer.setDataSource(url);
                                        Log.wtf("audio","url start= "+url);
                                        Main2Activity.mediaPlayer.prepare();
                                        Main2Activity.mediaPlayer.start();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                        button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Main2Activity.mediaPlayer != null) {
                                    Main2Activity.mediaPlayer.pause();
                                }
                            }
                        });
                        button3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Main2Activity.mediaPlayer != null) {
                                    Main2Activity.mediaPlayer.seekTo(Main2Activity.mediaPlayer.getCurrentPosition() + 5000);
                                }
                            }
                        });
                        button4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Main2Activity.mediaPlayer != null) {
                                    Main2Activity.mediaPlayer.pause();
                                    Main2Activity.mediaPlayer.seekTo(0);
                                    Log.wtf("audio","stop seek= "+Main2Activity.mediaPlayer.getCurrentPosition());
                                }
                            }
                        });
                    }
                    if (mess.getAttachments().get(i).getType().equals("wall")) {
                        str+="'"+mess.getAttachments().get(i).getType()+"'"+ "\n";
                    }
                }
                if (mess.getFwd_messages().size() == 0) {
                    tvBody.setAutoLinkText(mess.getBody()+ str );
                } else {
                    tvBody.setAutoLinkText(mess.getBody()+ str + " 'Пересланые сообщения'");//пересланые сообщения
                }
            }
            if (mess.getRead_state() == 0) {
                convertView.setBackgroundColor(ContextCompat.getColor(context,R.color.accent));
            }
            year.setTimeZone(TimeZone.getTimeZone("GMT+4"));
            month.setTimeZone(TimeZone.getTimeZone("GMT+4"));
            day.setTimeZone(TimeZone.getTimeZone("GMT+4"));
            hour.setTimeZone(TimeZone.getTimeZone("GMT+4"));
            min.setTimeZone(TimeZone.getTimeZone("GMT+4"));
            time.setTimeZone(TimeZone.getTimeZone("GMT+4"));
            Date dateCurr = new Date(System.currentTimeMillis());
            Date dateTs = new Date(mess.getDate() * 1000L);
            String time_day = day.format(dateTs);
            String time_time = time.format(dateTs);
            String time_year = year.format(dateTs);
            if (year.format(dateTs).equals(year.format(dateCurr)))
            {
                if ((day.format(dateTs).equals(day.format(dateCurr))) && (month.format(dateTs).equals(month.format(dateCurr))))
                {
                    ((TextView) convertView.findViewById(R.id.textView)).setText("" + time_time);
                }
                else
                {
                    ((TextView) convertView.findViewById(R.id.textView)).setText(""+time_day+" "
                            +convertMonth(Integer.parseInt(month.format(dateTs))));
                }
            }
            else
            {
                ((TextView) convertView.findViewById(R.id.textView)).setText(""+time_year);
            }
            return convertView;
        }
    }
}
