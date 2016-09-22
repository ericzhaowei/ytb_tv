package com.ider.ytb_tv.utils;

import java.io.IOException;
import java.net.ResponseCache;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ider-eric on 2016/8/10.
 */
public class OkhttpManager {

    private OkHttpClient mOkHttpClient;
    private static OkhttpManager manager;

    private OkhttpManager() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

    }


    public static OkhttpManager getInstance() {
        if (manager == null) {
            manager = new OkhttpManager();
        }
        return manager;
    }


    public String sendGet(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = mOkHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        }
        return null;
    }

}
