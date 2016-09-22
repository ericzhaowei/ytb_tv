package com.ider.ytb_tv.ui.activity;

import android.os.Bundle;

import com.ider.ytb_tv.R;

/**
 * Created by ider-eric on 2016/8/17.
 */
public class SearchActivity extends ServiceActivity {
    @Override
    void serviceConnected() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }
}
