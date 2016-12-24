package com.example.vk_mess_demo_00001.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION=3;
    public static final String DATABASE_NAME = "dialogsDb";
    public static final String TABLE_DIALOGS = "dialogs";
    public static final String TABLE_USERS = "users";

    public static final String KEY_ID = "_id";
    public static final String KEY_ID_USER = "id";
    public static final String KEY_OBJ = "json";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_DIALOGS+"("+KEY_ID
        + " integer primary key,"+KEY_OBJ+" text,"+KEY_ID_USER+" integer"+")");
        db.execSQL("create table "+TABLE_USERS+"("+KEY_ID
                + " integer primary key,"+KEY_OBJ+" text,"+KEY_ID_USER+" integer"+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_DIALOGS);
        db.execSQL("drop table if exists "+TABLE_USERS);
        onCreate(db);
    }
}
