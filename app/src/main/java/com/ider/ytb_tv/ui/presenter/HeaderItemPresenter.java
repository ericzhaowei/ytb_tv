package com.ider.ytb_tv.ui.presenter;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowHeaderPresenter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ider.ytb_tv.R;
import com.ider.ytb_tv.data.Movie;
import com.ider.ytb_tv.data.MovieList;

/**
 * Created by ider-eric on 2016/8/11.
 */
public class HeaderItemPresenter extends RowHeaderPresenter {
    String TAG = "YTB_HeaderItemPresenter";
    private float mUnselectedAlpha;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mUnselectedAlpha = parent.getResources()
                .getFraction(R.fraction.lb_browse_header_unselect_alpha, 1, 1);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.header_item, null);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        ListRow row = (ListRow) item;
        HeaderItem headerItem = row.getHeaderItem();

        View view = viewHolder.view;
        TextView label = (TextView) view.findViewById(R.id.header_text);
        Typeface tf = Typeface.createFromAsset(view.getContext().getAssets(), "Fonts/iconfont.ttf");
        label.setTypeface(tf);
        label.setText(getLabel(headerItem.getName()));
        view.setAlpha(mUnselectedAlpha);

    }


    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        // no op
    }

    public String getLabel(String label) {
        for(int i = 0; i < MovieList.LIST_CATEGORY.length; i++) {
            if(MovieList.LIST_CATEGORY[i].equals(label)) {
                return MovieList.LIST_ICON[i] + "\b" + label;
            }
        }
        return label;
    }




    @Override
    protected void onSelectLevelChanged(ViewHolder holder) {
        // when a header lose focus, it's level comedown from 1.0~0.0
        holder.view.setAlpha((1 - mUnselectedAlpha) * holder.getSelectLevel() + mUnselectedAlpha);
    }
}
