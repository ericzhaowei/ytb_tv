package com.ider.ytb_tv.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.widget.TextView;

import com.ider.ytb_tv.R;


/**
 * Created by ider-eric on 2016/8/26.
 */
public class DescriptionActivity extends Activity {
    public static String SHARED_ELEMENT = "element";
    public static String DESCRIPTION = "description";
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        text = (TextView) findViewById(R.id.description_text);

        Intent intent = getIntent();
        String description = intent.getStringExtra(DESCRIPTION);
        text.setText(description);

        ViewCompat.setTransitionName(text, SHARED_ELEMENT);


    }



}
