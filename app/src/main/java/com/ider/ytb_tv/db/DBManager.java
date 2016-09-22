package com.ider.ytb_tv.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ider.ytb_tv.data.Application;
import com.ider.ytb_tv.data.Movie;
import com.ider.ytb_tv.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by ider-eric on 2016/8/24.
 */
public class DBManager {
    String TAG = "DBManager";
    DBHelper helper;
    private static DBManager manager;
    private SQLiteDatabase db;
    Context context;

    private DBManager(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
        this.context = context;
    }

    public static DBManager getInstance(Context context) {
        synchronized (DBManager.class) {
            if (manager == null) {
                manager = new DBManager(context);
            }
        }
        return manager;
    }

    public void insertMovie(Movie movie) {
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put("title", movie.getTitle());
        cv.put("video_id", movie.getId());
        cv.put("image_url", movie.getCardImageUrl());
        cv.put("duration", movie.getDuration());
        db.insert("movies", null, cv);
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    public ArrayList<Movie> queryMovies() {
        db.beginTransaction();
        Cursor cursor = db.rawQuery("select * from movies", null);
        ArrayList<Movie> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Movie movie = new Movie();
            movie.setTitle(cursor.getString(1));
            movie.setId(cursor.getString(2));
            movie.setCardImageUrl(cursor.getString(3));
            movie.setDuration(cursor.getString(4));
            list.add(movie);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        return list;
    }


    public void deleteMovie(Movie movie) {
        db.beginTransaction();
        db.delete("movies", "video_id=?", new String[]{movie.getId()});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void insertSearchHistory(String searchText) {
        List<String> list = querySearchHistory();
        if (list.contains(searchText)) {
            return;
        }
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put("title", searchText);
        db.insert("search", null, cv);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void deleteSearchHistory(String searchText) {
        db.beginTransaction();
        db.delete("search", "title=?", new String[]{searchText});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void deleteAllSearch() {
        db.beginTransaction();
        db.execSQL("delete from search");
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<String> querySearchHistory() {
        db.beginTransaction();
        Cursor cursor = db.rawQuery("select * from search", null);
        List<String> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Utils.log(TAG, "cursor.moveToNext");
            list.add(cursor.getString(1));
        }
        Collections.reverse(list);
        if (list.size() > 20) {
            list = list.subList(0, 20);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return list;
    }

    public void insertApplication(Application app) {
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put("package", app.getPkgName());
        cv.put("activity", app.getActivity());
        db.insert("application", null, cv);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public ArrayList<Application> queryApplication() {
        db.beginTransaction();
        Cursor cursor = db.rawQuery("select * from application", null);
        ArrayList<Application> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Application app = Application.make(context, cursor.getString(1), cursor.getString(2));
            list.add(app);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return list;
    }

    public void deleteApplication(Application app) {
        db.beginTransaction();
        db.delete("application", "package=? and activity=?", new String[]{app.getPkgName(), app.getActivity()});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

}
