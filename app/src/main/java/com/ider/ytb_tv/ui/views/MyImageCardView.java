package com.ider.ytb_tv.ui.views;

import android.content.Context;
import android.support.v17.leanback.widget.ImageCardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ider-eric on 2016/8/12.
 */
public class MyImageCardView extends ImageCardView {


    public MyImageCardView(Context context) {
        super(context);
        initViews();
    }

    public MyImageCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public void initViews() {

        RelativeLayout info_field = (RelativeLayout) findViewById(android.support.v17.leanback.R.id.info_field);
        TextView title = (TextView) info_field.getChildAt(0);
        title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        title.setMarqueeRepeatLimit(-1);


    }

}
