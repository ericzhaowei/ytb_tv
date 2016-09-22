package com.ider.ytb_tv.ui.Adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.view.ViewGroup;

import com.ider.ytb_tv.data.AppEntry;
import com.ider.ytb_tv.data.Application;
import com.ider.ytb_tv.data.LoadState;
import com.ider.ytb_tv.data.Movie;
import com.ider.ytb_tv.data.PlayList;
import com.ider.ytb_tv.data.SettingItem;
import com.ider.ytb_tv.data.Time;
import com.ider.ytb_tv.ui.presenter.AppCardPresenter;
import com.ider.ytb_tv.ui.presenter.CardPresenter;
import com.ider.ytb_tv.ui.presenter.ProgressPresenter;
import com.ider.ytb_tv.ui.presenter.SettingItemPresenter;
import com.ider.ytb_tv.ui.presenter.TextPresenter;
import com.ider.ytb_tv.ui.presenter.TimePresenter;
import com.ider.ytb_tv.ui.views.MyImageCardView;
import com.ider.ytb_tv.ui.views.ProgressCardView;
import com.ider.ytb_tv.utils.Utils;

import java.util.Set;

/**
 * Created by ider-eric on 2016/8/15.
 */
public class PaginationAdapter extends ArrayObjectAdapter {

    Context mContext;
    Presenter mPresenter;
    ProgressPresenter mProgressPresenter;
    CardPresenter mCardPresenter;
    TextPresenter mTextPresenter;
    TimePresenter mTimePresenter;
    AppCardPresenter mAppPresenter;
    CardPresenter mSearchAppPresenter;
    SettingItemPresenter mSettingItemPresenter;
    boolean shouldLoadMore = false;
    private int MAX_SIZE = 90;
    private String pageToken;

    public PaginationAdapter(Context context) {
        init(context);
    }

    public PaginationAdapter(Context context, int maxSize) {
        init(context);
        this.MAX_SIZE = maxSize;
    }

    public PaginationAdapter(Context context, boolean shouldLoadMore) {
        init(context);
        this.shouldLoadMore = shouldLoadMore;
    }

    public PaginationAdapter(Context context, int maxSize, boolean shouldLoadMore) {
        init(context);
        this.MAX_SIZE = maxSize;
        this.shouldLoadMore = shouldLoadMore;
    }

    public void setPageToken(String pageToken) {
        this.pageToken = pageToken;
    }

    public String getPageToken() {
        return pageToken;
    }


    public void removePageToken() {
        pageToken = null;
    }

    public void setMaxsize(int size) {
        this.MAX_SIZE = size;
    }

    private void init(Context context) {
        mContext = context;
        mProgressPresenter = new ProgressPresenter();
        mCardPresenter = new CardPresenter();
        mTextPresenter = new TextPresenter();
        mAppPresenter = new AppCardPresenter();
        mSettingItemPresenter = new SettingItemPresenter();
        mSearchAppPresenter = new CardPresenter();
        mTimePresenter = new TimePresenter();
        mPresenter = new ProgressPresenter();
        requestPresenter();
    }
    private void requestPresenter() {
        setPresenterSelector(new PresenterSelector() {
            @Override
            public Presenter getPresenter(Object item) {
                if(item instanceof LoadState)
                    return mProgressPresenter;
                else if(item instanceof Movie)
                    return mCardPresenter;
                else if(item instanceof PlayList || item instanceof String)
                    return mTextPresenter;
                else if(item instanceof Application)
                    return mAppPresenter;
                else if(item instanceof SettingItem)
                    return mSettingItemPresenter;
                else if(item instanceof AppEntry)
                    return mSearchAppPresenter;
                else if(item instanceof Time) {
                    return mTimePresenter;
                } else {
                    return mPresenter;
                }

            }
        });

    }

    public boolean shouldLoadMore() {
        return shouldLoadMore && size() < MAX_SIZE;
    }

    public void showLoadState(int loadState) {
        if(size() == 1) {
            replace(0, new LoadState(loadState));
        }
    }


}
