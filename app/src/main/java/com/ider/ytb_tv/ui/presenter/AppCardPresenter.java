package com.ider.ytb_tv.ui.presenter;

import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ider.ytb_tv.R;
import com.ider.ytb_tv.data.Application;
import com.ider.ytb_tv.ui.views.AppCardView;

/**
 * Created by ider-eric on 2016/8/22.
 */
public class AppCardPresenter extends Presenter {

    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;

    private static void updateCardBackgroundColor(AppCardView view, boolean selected) {
        if(selected) {
            view.findViewById(R.id.app_item_label).setBackgroundColor(sSelectedBackgroundColor);
        } else {
            view.findViewById(R.id.app_item_label).setBackground(null);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        sDefaultBackgroundColor = parent.getResources().getColor(R.color.default_background);
        sSelectedBackgroundColor = parent.getResources().getColor(R.color.selected_background);
        AppCardView cardView = new AppCardView(parent.getContext()) {
            @Override
            public void setSelected(boolean selected) {
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);

        return new ViewHolder(cardView);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Application app = (Application) item;
        AppCardView cardView = (AppCardView) viewHolder.view;
        cardView.setApplication(app);

    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        AppCardView cardView = (AppCardView) viewHolder.view;
        cardView.removeAllDrawable();
    }

}
