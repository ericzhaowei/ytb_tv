package com.ider.ytb_tv.data;

import android.graphics.drawable.Drawable;

/**
 * Created by ider-eric on 2016/8/22.
 */
public class SettingItem {

    public static final int ID_MAIN_SETTING = 0x1000;
    public static final int ID_NETWORK_SETTING = 0x1001;

    public int id;
    private int iconId;

    public SettingItem(int id, int iconId) {
        this.id = id;
        this.iconId = iconId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setIcon(int icon) {
        this.iconId = icon;
    }

    public int getIcon() {
        return iconId;
    }
}
