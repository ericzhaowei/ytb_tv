package com.ider.ytb_tv.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ider.ytb_tv.R;
import com.ider.ytb_tv.data.Application;

/**
 * Created by ider-eric on 2016/8/22.
 */
public class AppCardView extends RelativeLayout {

    ImageView icon;
    TextView label;

    public AppCardView(Context context) {
        super(context);
        initViews(context);
    }

    public AppCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public AppCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    public void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.application_card, this);
        icon = (ImageView) findViewById(R.id.app_item_icon);
        label = (TextView) findViewById(R.id.app_item_label);

    }

    public void setApplication(Application app) {
        icon.setImageDrawable(app.getIcon());
        label.setText(app.getLabel());
    }

    public void removeAllDrawable() {
        icon.setImageDrawable(null);
    }

    public ImageView getMainImage() {
        return icon;
    }

}
