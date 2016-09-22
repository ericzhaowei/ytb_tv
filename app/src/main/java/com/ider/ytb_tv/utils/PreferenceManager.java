package com.ider.ytb_tv.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ider-eric on 2016/8/18.
 */
public class PreferenceManager {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private static PreferenceManager manager;

    private PreferenceManager(Context context) {
        preferences = context.getSharedPreferences("share_prefers", Context.MODE_PRIVATE);
    }

    public static PreferenceManager getInstance(Context context) {
        if(manager == null) {
            manager = new PreferenceManager(context);
        }
        return manager;
    }

    public void updateRecommandPosition() {
        int current = getRecommandPosition();
        editor = preferences.edit();
        editor.putInt("recommand_position", current + 1);
        editor.commit();
    }

    public int getRecommandPosition() {
        return preferences.getInt("recommand_position", 0);
    }



}
