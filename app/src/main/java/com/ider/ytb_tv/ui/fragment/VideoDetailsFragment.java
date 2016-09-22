/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ider.ytb_tv.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ider.ytb_tv.data.CustService;
import com.ider.ytb_tv.data.ResourceEntry;
import com.ider.ytb_tv.db.DBManager;
import com.ider.ytb_tv.ui.Adapter.PaginationAdapter;
import com.ider.ytb_tv.ui.activity.DescriptionActivity;
import com.ider.ytb_tv.ui.activity.DetailsActivity;
import com.ider.ytb_tv.ui.activity.MainActivity;
import com.ider.ytb_tv.data.Movie;
import com.ider.ytb_tv.ui.activity.PlaybackOverlayActivity;
import com.ider.ytb_tv.R;
import com.ider.ytb_tv.utils.BitmapUtil;
import com.ider.ytb_tv.utils.JsonParser;
import com.ider.ytb_tv.utils.OkhttpManager;
import com.ider.ytb_tv.utils.Utils;
import com.ider.ytb_tv.ui.presenter.DetailsDescriptionPresenter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 *
 */
public class VideoDetailsFragment extends DetailsFragment {
    public static final String TAG = "VideoDetailsFragment";

    private static final int ACTION_WATCH_TRAILER = 1;
    private static final int ACTION_DESCRIPTION = 2;
    private static final int ACTION_COLLECT = 3;

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private Movie mSelectedMovie;
    private int mPlaylistPosition;
    private boolean isFav;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;
    private DetailsDescriptionPresenter mDetailsDescriptionPresenter;
    private DBManager dbManager;

    private PaginationAdapter relatedAdapter;
    private BackgroundManager mBackgroundManager;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSelectedMovie = getActivity().getIntent().getParcelableExtra(DetailsActivity.MOVIE);
        mPlaylistPosition = getActivity().getIntent().getIntExtra(DetailsActivity.PLAYLIST_POSITION, -1);

        isFav = getActivity().getIntent().getBooleanExtra(DetailsActivity.COLLECTED, false);
        dbManager = DBManager.getInstance(getActivity().getApplicationContext());
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());

        if (mSelectedMovie != null) {
            Utils.log(TAG, mSelectedMovie.getId());
            setupAdapter();
            setupDetailsOverviewRow();
            setupDetailsOverviewRowPresenter();
            setupMovieListRow();
            setupMovieListRowPresenter();
            setOnItemViewClickedListener(new ItemViewClickedListener());


        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStop() {
        super.onStop();
    }






    private void setupAdapter() {
        mPresenterSelector = new ClassPresenterSelector();
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);
        setAdapter(mAdapter);
    }

    private void setupDetailsOverviewRow() {
        final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
        row.setImageDrawable(getResources().getDrawable(R.drawable.default_background));

        int width = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_HEIGHT);
        Glide.with(getActivity())
                .load(mSelectedMovie.getCardImageUrl())
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        Log.d(TAG, "details overview card image url ready: " + resource);
                        row.setImageDrawable(resource);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });

        row.addAction(new Action(ACTION_WATCH_TRAILER, getResources().getString(
                R.string.watch_1)));
//        row.addAction(new Action(ACTION_DESCRIPTION, getResources().getString(R.string.description_1)));
        row.addAction(new Action(ACTION_COLLECT, isFav ? getResources().getString(R.string.remove_favorite) : getResources().getString(R.string.favorite_1)));

        mAdapter.add(row);

    }

    private void setupDetailsOverviewRowPresenter() {
        mDetailsDescriptionPresenter = new DetailsDescriptionPresenter();
        // Set detail background and style.
        DetailsOverviewRowPresenter detailsPresenter =
                new DetailsOverviewRowPresenter(mDetailsDescriptionPresenter);
        detailsPresenter.setBackgroundColor(getResources().getColor(R.color.selected_background));
        detailsPresenter.setStyleLarge(true);

        // Hook up transition element.
        detailsPresenter.setSharedElementEnterTransition(getActivity(),
                DetailsActivity.SHARED_ELEMENT_NAME);

        detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if (action.getId() == ACTION_WATCH_TRAILER) {
                    Intent intent = new Intent(getActivity(), PlaybackOverlayActivity.class);
                    intent.putExtra(DetailsActivity.MOVIE, mSelectedMovie);
                    intent.putExtra(DetailsActivity.PLAYLIST_POSITION, mPlaylistPosition);
                    startActivity(intent);

                } else if (action.getId() == ACTION_DESCRIPTION) {
                    showFullDescription(mDetailsDescriptionPresenter.getDescription());

                } else if (action.getId() == ACTION_COLLECT) {
                    if(isFav) {
                        dbManager.deleteMovie(mSelectedMovie);
                        action.setLabel1(getResources().getString(R.string.favorite_1));
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                        sendFavactionBroadcast(DetailsActivity.ACTION_FAVORITE_REMOVED, mSelectedMovie);
                        isFav = false;
                    } else {
                        dbManager.insertMovie(mSelectedMovie);
                        action.setLabel1(getResources().getString(R.string.remove_favorite));
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                        sendFavactionBroadcast(DetailsActivity.ACTION_FAVORITE_ADDED, mSelectedMovie);
                        isFav = true;
                    }
                }
            }
        });

        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void setupMovieListRow() {
        String subcategory = getString(R.string.related_movies);
        HeaderItem header = new HeaderItem(subcategory);
        relatedAdapter = new PaginationAdapter(getActivity());
        mAdapter.add(new ListRow(header, relatedAdapter));
    }

    private void setupMovieListRow(ArrayList<Movie> movies) {
        for(Movie movie : movies) {
            relatedAdapter.add(movie);
        }
    }

    public void requestDescription(final CustService service) {
        Observable
                .create(new Observable.OnSubscribe<Movie>() {
                    @Override
                    public void call(Subscriber<? super Movie> subscriber) {
                        subscriber.onNext(service.setVideoDescription(mSelectedMovie));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Movie>() {
                    @Override
                    public void call(Movie movie) {
                        Utils.log(TAG, "setItem, " + movie.getDescription());
                        ((DetailsOverviewRow)mAdapter.get(0)).setItem(movie);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());

                    }
                });
    }

    public void requestRelatedVideos(final CustService service) {
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        String result = service.getRelateVideos(null, mSelectedMovie.getId());
                        subscriber.onNext(result);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Func1<String, ArrayList<Movie>>() {
                    @Override
                    public ArrayList call(String s) {
                        ArrayList<ResourceEntry> list = JsonParser.parseRelateOrSearchVideos(s);
                        return service.setVideosDuration(list);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<Movie>>() {
                    @Override
                    public void call(ArrayList<Movie> movies) {
                        setupMovieListRow(movies);
                    }
                });
    }

    private void setupMovieListRowPresenter() {
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Movie) {
                Movie movie = (Movie) item;
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(getResources().getString(R.string.movie), movie);
                intent.putExtra(getResources().getString(R.string.should_start), true);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            }
        }
    }


    private void showFullDescription(String description) {
        Intent intent = new Intent(getActivity(), DescriptionActivity.class);
        intent.putExtra(DescriptionActivity.DESCRIPTION, description);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                mDetailsDescriptionPresenter.holder.getBody(),
                DescriptionActivity.SHARED_ELEMENT

        ).toBundle();
        getActivity().startActivity(intent, bundle);
    }

    private void sendFavactionBroadcast(String action, Movie movie) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(DetailsActivity.MOVIE, movie);
        getActivity().sendBroadcast(intent);
    }


}
