package com.ider.ytb_tv.data;

/**
 * Created by ider-eric on 2016/8/29.
 */
public class AppEntry extends ResourceEntry{

    public static final int SPECIAL_NORMAL = 0;
    public static final int SPECIAL_NO_RESULT = 1;
    public static final int SPECIAL_MORE_RESULT = 2;

    public int special;
    public int id;
    public String vername;
    public int vercode;
    public String name;
    public String pkgname;
    public String iconurl;
    public String graphics;

    public boolean isNone() {
        return special == SPECIAL_NO_RESULT;
    }

    public boolean isMore() {
        return special == SPECIAL_MORE_RESULT;
    }

    public boolean isNormal() {
        return special == SPECIAL_NORMAL;
    }



}
