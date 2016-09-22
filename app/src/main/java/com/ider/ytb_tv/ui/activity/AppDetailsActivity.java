package com.ider.ytb_tv.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ider.ytb_tv.R;

/**
 * Created by ider-eric on 2016/9/8.
 */
public class AppDetailsActivity extends Activity {

    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String APPLICATION_PACKAGE = "application_package";
    public static final String APPLICATION_ACTIVITY = "application_activity";
    public static final String ACTION_FAVORITE_APP_ADDED = "ider.intent.action.favorite.application.added";
    public static final String ACTION_FAVORITE_APP_REMOVED = "ider.intent.action.favorite.application.removed";
    public static final String COLLECTED = "Collected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
    }



}
