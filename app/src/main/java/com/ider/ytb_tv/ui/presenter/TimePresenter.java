package com.ider.ytb_tv.ui.presenter;

import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;

import com.ider.ytb_tv.data.PlayList;
import com.ider.ytb_tv.ui.views.TextCardView;
import com.ider.ytb_tv.ui.views.TimeCardView;

/**
 * Created by ider-eric on 2016/9/1.
 */
public class TimePresenter extends Presenter {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        TimeCardView cardView = new TimeCardView(parent.getContext());
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {

    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
    }
}
