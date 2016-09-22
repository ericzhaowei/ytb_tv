package com.ider.ytb_tv.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.ider.ytb_tv.R;

/**
 * Created by ider-eric on 2016/9/1.
 */
public class TimeCardView extends RelativeLayout {

    public TimeCardView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.time_card, this);
    }

    public TimeCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.time_card, this);
    }

    public TimeCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.time_card, this);
    }



}
