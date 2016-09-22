package com.ider.ytb_tv.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ider.ytb_tv.R;
import com.ider.ytb_tv.data.LoadState;

/**
 * Created by ider-eric on 2016/8/13.
 */
public class ProgressCardView extends RelativeLayout {

    private ImageView imageView;
    private ProgressBar progressBar;

    public ProgressCardView(Context context) {
        super(context);
        initViews();
    }

    public ProgressCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }


    public void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.progress_card, this);
        imageView = (ImageView) findViewById(R.id.image_reload);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void setState(LoadState state) {
        switch (state.mState) {
            case 0:
                showProgressbar();
                break;
            case 1:
                hideProgressbar(R.drawable.ic_reload);
                break;
            case 2:
                hideProgressbar(R.drawable.ic_no_result);
                break;
            case 3:
                hideProgressbar(R.drawable.ic_no_network);
        }
    }

    public void showProgressbar() {
        imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressbar(int resourceId) {
        imageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        imageView.setImageResource(resourceId);
    }
}
