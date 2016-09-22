package com.ider.ytb_tv.ui.views;

import android.content.Context;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.ider.ytb_tv.R;

/**
 * Created by ider-eric on 2016/8/18.
 */
public class TextCardView extends CardView {

    private TextView mainText;

    public TextCardView(Context context) {
        super(context);
        buildTextCardView(context);
    }

    public TextCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        buildTextCardView(context);
    }

    public TextCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        buildTextCardView(context);
    }

    private void buildTextCardView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.text_card, this);
        mainText = (TextView) findViewById(R.id.card_main_text);
    }

    public void setText(String text) {
        mainText.setText(text);
    }


}
