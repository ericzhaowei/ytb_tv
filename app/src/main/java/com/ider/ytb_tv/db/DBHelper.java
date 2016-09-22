package com.ider.ytb_tv.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ider-eric on 2016/8/24.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static int DB_VER = 1;
    private static String DB_NAME = "ider_ytb.db";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists movies(_id Integer primary key autoincrement, title text, video_id text, image_url text, duration text)");
        db.execSQL("create table if not exists search(_id Integer primary key autoincrement, title text)");
        db.execSQL("create table if not exists application(_id Integer primary key autoincrement, package text, activity text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
