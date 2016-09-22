package com.ider.ytb_tv.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ider.ytb_tv.R;
import com.ider.ytb_tv.data.SettingItem;

/**
 * Created by ider-eric on 2016/8/22.
 */
public class SettingItemCard extends RelativeLayout {
    ImageView icon;
    public SettingItemCard(Context context) {
        super(context);
        initViews(context);
    }

    public SettingItemCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public SettingItemCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    public void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.setting_item_card, this);
        icon = (ImageView) findViewById(R.id.setting_icon);
    }

    public void setItem(SettingItem item) {
        icon.setImageResource(item.getIcon());
    }
}
