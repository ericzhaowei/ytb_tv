package com.ider.ytb_tv.ui.presenter;

import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;
import com.ider.ytb_tv.data.PlayList;
import com.ider.ytb_tv.ui.views.TextCardView;


public class TextPresenter extends Presenter {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        TextCardView cardView = new TextCardView(parent.getContext());
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        TextCardView textCardView = (TextCardView) viewHolder.view;
        if (item instanceof PlayList) {
            PlayList playlist = (PlayList) item;
            textCardView.setText(playlist.title);
        } else if(item instanceof String) {
            textCardView.setText((String) item);
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
    }
}
