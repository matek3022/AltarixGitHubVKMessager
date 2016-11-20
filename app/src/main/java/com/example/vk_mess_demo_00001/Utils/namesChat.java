package com.example.vk_mess_demo_00001.Utils;

/**
 * Created by Каракатица on 16.10.2016.
 */

public class namesChat {
    private int id;
    private int online;
    private String first_name;
    private String last_name;
    private String photo_100;
    private String photo_50;
    public namesChat (int us_id,int on,String fn,String ln, String ph){
        id=us_id;
        online=on;
        first_name=fn;
        last_name=ln;
        photo_100=ph;
    }
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setUser_id(int id) {
        id = id;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getPhoto_100() {
        return photo_100;
    }

    public String getPhoto_50() {
        return photo_50;
    }

    public int getUser_id() {
        return id;
    }

    public int getOnline() {
        return online;
    }
}
