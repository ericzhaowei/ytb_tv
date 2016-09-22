package com.ider.ytb_tv.ui.presenter;

import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.view.ViewGroup;

import com.ider.ytb_tv.R;
import com.ider.ytb_tv.data.LoadState;
import com.ider.ytb_tv.ui.views.ProgressCardView;
import com.ider.ytb_tv.utils.Constant;

/**
 * Created by ider-eric on 2016/8/13.
 */
public class ProgressPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {


        ProgressCardView cardView = new ProgressCardView(parent.getContext());


        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        LoadState mLoadState = (LoadState) item;
        ProgressCardView progressCardView = (ProgressCardView)viewHolder.view;
        progressCardView.setState(mLoadState);

    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
