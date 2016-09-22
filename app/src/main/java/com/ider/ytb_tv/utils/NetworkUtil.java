package com.ider.ytb_tv.utils;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by ider-eric on 2016/8/26.
 */
public class NetworkUtil {

    public static String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    public static String WIFI_STATE_CHANGE = WifiManager.WIFI_STATE_CHANGED_ACTION;

    public static boolean isNetworkAvailable(Context context) {
        return isWifiEnabled(context) || isEthEnabled(context);
    }

    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return info.isConnected() && info.isAvailable();

    }

    public static boolean isEthEnabled(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        return info.isConnected() && info.isAvailable();
    }

}
