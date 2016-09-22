package com.ider.ytb_tv.ui.presenter;

import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;

import com.ider.ytb_tv.data.SettingItem;
import com.ider.ytb_tv.ui.views.SettingItemCard;

/**
 * Created by ider-eric on 2016/8/22.
 */
public class SettingItemPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        SettingItemCard itemCard = new SettingItemCard(parent.getContext());
        itemCard.setFocusableInTouchMode(true);
        itemCard.setFocusable(true);
        return new ViewHolder(itemCard);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        SettingItemCard card = (SettingItemCard) viewHolder.view;
        card.setItem((SettingItem) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
