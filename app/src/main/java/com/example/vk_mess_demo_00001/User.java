package com.example.vk_mess_demo_00001;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Каракатица on 07.10.2016.
 */

public class User  implements Parcelable {


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(photo_200);
        dest.writeString(photo_50);
        dest.writeInt(online);
        dest.writeInt(id);
    }

    public User(Parcel in) {
        first_name = in.readString();
        last_name = in.readString();
        photo_200 = in.readString();
        photo_50 = in.readString();
        online = in.readInt();
        id = in.readInt();
    }
    public User (String fn,String ln, String ph, int on) {
        first_name=fn;
        last_name=ln;
        photo_200=ph;
        online=on;
    }
    public User () {
        first_name="";
        last_name="";
        photo_200="";
        photo_50="";
        body="";
        online=10;
        id=0;
    }
    private String first_name;
    private String last_name;
    private String photo_200;
    private String photo_100;
    private String photo_50;
    private String body;
    private int online;
    private int id;
    public  int getOnline(){
        return online;
    }
    public int getId() {

        return id;
    }
    public String getFirst_name() {
        return first_name;
    }

    public String getPhoto_100() {
        return photo_100;
    }

    public String getPhoto_200(){
        return photo_200;
    }
    public String getPhoto_50(){
        return photo_50;
    }

    public String getLast_name() {
        return last_name;
    }
    public void setId (int i) {id=i;}
    public void setFirst_name(String fn) {
        first_name=fn;
    }
    public void setLast_name(String fn){
        last_name=fn;
    }
    public void setPhoto_200(String fn){
        photo_200=fn;
    }
    public void setPhoto_50(String fn){
        photo_50=fn;
    }
    public void setOnline (int i) {
        online=i;
    }

    public static Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

}
