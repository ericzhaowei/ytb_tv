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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ider.ytb_tv.R;
import com.ider.ytb_tv.data.Application;
import com.ider.ytb_tv.data.CustService;
import com.ider.ytb_tv.data.LoadState;
import com.ider.ytb_tv.data.Movie;
import com.ider.ytb_tv.data.MovieList;
import com.ider.ytb_tv.data.SettingItem;
import com.ider.ytb_tv.data.Time;
import com.ider.ytb_tv.db.DBManager;
import com.ider.ytb_tv.ui.Adapter.PaginationAdapter;
import com.ider.ytb_tv.ui.activity.AppDetailsActivity;
import com.ider.ytb_tv.ui.activity.DetailsActivity;
import com.ider.ytb_tv.ui.activity.MainActivity;
import com.ider.ytb_tv.ui.activity.SearchActivity;
import com.ider.ytb_tv.ui.presenter.HeaderItemPresenter;
import com.ider.ytb_tv.ui.views.AppCardView;
import com.ider.ytb_tv.utils.ApplicationUtil;
import com.ider.ytb_tv.utils.BitmapUtil;
import com.ider.ytb_tv.utils.Constant;
import com.ider.ytb_tv.utils.FastBlur;
import com.ider.ytb_tv.utils.JsonParser;
import com.ider.ytb_tv.utils.NetworkUtil;
import com.ider.ytb_tv.utils.PreferenceManager;
import com.ider.ytb_tv.utils.Utils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainFragment extends BrowseFragment {


    public static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;

    private static String RECOMMAND_ID;
    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mRowsAdapter;
    private Drawable mDefaultBackground;
    private Timer mBackgroundTimer;
    private URI mBackgroundURI;
    private BackgroundManager mBackgroundManager;
    private DBManager dbManager;
    private PackageRemoveRunnable pkgRemove;

    private PaginationAdapter popularAdapter, editorAdapter, favMovieAdapters, favAppAdapter, appAdapter, settingAdapter;
    private ListRow popularRow, editorRow, favMovieRow, favAppRow, appsRow;

    MainActivity home;
    CustService custService;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        home = (MainActivity) getActivity();

        MovieList.LIST_CATEGORY = getResources().getStringArray(R.array.header_category_array);
        MovieList.LIST_ICON = getResources().getStringArray(R.array.header_category_icon_array);

        prepareManagers();

        registReceivers();

        setupUIElements();

        setupEventListeners();

        initRows();


    }

    public void registReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");
        home.registerReceiver(packageReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(DetailsActivity.ACTION_FAVORITE_ADDED);
        filter.addAction(DetailsActivity.ACTION_FAVORITE_REMOVED);
        home.registerReceiver(favActionReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(AppDetailsActivity.ACTION_FAVORITE_APP_ADDED);
        filter.addAction(AppDetailsActivity.ACTION_FAVORITE_APP_REMOVED);
        home.registerReceiver(favAppReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(NetworkUtil.CONNECTIVITY_CHANGE);
        filter.addAction(NetworkUtil.WIFI_STATE_CHANGE);
        home.registerReceiver(networkReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }


    /**
     * request for the category data
     *
     * @param category 0 is Popular, 1 is Editor's Choice
     * @param pageId   PAGER_TOKEN
     */
    public void requestUpdate(final int category, final String pageId) {
        if (custService == null) {
            custService = home.custService;
        }
        Observable
                .create(new Observable.OnSubscribe<String[]>() {
                    @Override
                    public void call(Subscriber<? super String[]> subscriber) {
                        String result = category == Constant.CATEGORY_POPULAR ? custService.getPopular(pageId) : custService.getEditChoice(pageId);
                        String recommand = category == Constant.CATEGORY_POPULAR && pageId == null && RECOMMAND_ID != null ? custService.getRelateVideos(null, RECOMMAND_ID) : null;
                        String[] results;
                        if(recommand == null) {
                            results = new String[] {result};
                        } else {
                            results = new String[] {result, recommand};
                        }

                        subscriber.onNext(results);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Func1<String[], List<Movie>>() {
                    @Override
                    public List<Movie> call(String[] s) {
                        String nextPageToken = JsonParser.getNextPageToken(s[0]);
                        int totalResults = JsonParser.getTotalResult(s[0]);
                        PaginationAdapter adapter = category == Constant.CATEGORY_POPULAR ? popularAdapter : editorAdapter;
                        adapter.setPageToken(nextPageToken);
                        if(totalResults != -1) {
                            adapter.setMaxsize(totalResults);
                        }
                        if (category == Constant.CATEGORY_POPULAR) {
                            ArrayList popular = JsonParser.parsePopular(s[0]);
                            if(s.length == 2) {
                                ArrayList recommand = JsonParser.parseRelateOrSearchVideos(s[1]);
                                popular.addAll(recommand);
                                Collections.shuffle(popular);
                            }

                            return popular;
                        } else {
                            ArrayList<Movie> movies = JsonParser.parseEditorChoice(s[0]);

                            return custService.setVideosDuration(movies);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Movie>>() {
                    @Override
                    public void call(List<Movie> movies) {
                        PaginationAdapter adapter = category == Constant.CATEGORY_POPULAR ? popularAdapter : editorAdapter;
                        if (movies != null) {
                            loadMoviesRows(adapter, movies);
                        } else {
                            adapter.showLoadState(LoadState.STATE_RETRY);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        PaginationAdapter adapter = category == Constant.CATEGORY_POPULAR ? popularAdapter : editorAdapter;
                        adapter.showLoadState(LoadState.STATE_RETRY);
                    }
                });

    }



    public void initRows() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        HeaderItem header;
        popularAdapter = new PaginationAdapter(home, true);
        popularAdapter.add(new LoadState(LoadState.STATE_LOADING));
        header = new HeaderItem(MovieList.LIST_CATEGORY[0]);
        popularRow = new ListRow(header, popularAdapter);
        mRowsAdapter.add(popularRow);

        editorAdapter = new PaginationAdapter(home, true);
        editorAdapter.add(new LoadState(LoadState.STATE_LOADING));
        header = new HeaderItem(MovieList.LIST_CATEGORY[1]);
        editorRow = new ListRow(header, editorAdapter);
        mRowsAdapter.add(editorRow);

        appAdapter = new PaginationAdapter(home);
        appAdapter.add(new LoadState(LoadState.STATE_LOADING));
        header = new HeaderItem(MovieList.LIST_CATEGORY[4]);
        appsRow = new ListRow(header, appAdapter);
        mRowsAdapter.add(appsRow);

        setAdapter(mRowsAdapter);

        loadAllApplications();
        loadSettingsRow();

        favMovieAdapters = new PaginationAdapter(home, false);
        header = new HeaderItem(MovieList.LIST_CATEGORY[2]);
        favMovieRow = new ListRow(header, favMovieAdapters);
        loadFavorites();

        favAppAdapter = new PaginationAdapter(home, false);
        header = new HeaderItem(MovieList.LIST_CATEGORY[3]);
        favAppRow = new ListRow(header, favAppAdapter);
        loadFavApps();
    }


    public void loadAllApplications() {

        Observable
                .create(new Observable.OnSubscribe<ArrayList<Application>>() {
                    @Override
                    public void call(Subscriber<? super ArrayList<Application>> subscriber) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ArrayList<Application> apps = ApplicationUtil.getInstance(home).loadAllApplication();
                        subscriber.onNext(apps);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<Application>>() {
                    @Override
                    public void call(ArrayList<Application> apps) {
                        addAllApps(apps);
                    }
                });

    }

    private void addAllApps(ArrayList<Application> apps) {
        appAdapter.removeItems(0, appAdapter.size());
        for (Application app : apps) {
            appAdapter.add(app);
        }
    }

    public void loadFavorites() {
        Observable
                .create(new Observable.OnSubscribe<ArrayList<Movie>>() {
                    @Override
                    public void call(Subscriber<? super ArrayList<Movie>> subscriber) {
                        ArrayList<Movie> movies = dbManager.queryMovies();
                        if(movies.size() != 0) {
                            int recommandPosition = PreferenceManager.getInstance(home).getRecommandPosition();
                            RECOMMAND_ID = movies.get(recommandPosition % movies.size()).getId();
                            PreferenceManager.getInstance(home).updateRecommandPosition();
                        }
                        subscriber.onNext(movies);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<Movie>>() {
                    @Override
                    public void call(ArrayList<Movie> movies) {
                        showFavoriteUI(movies);
                    }
                });
    }

    public void loadFavApps() {
        Observable
                .create(new Observable.OnSubscribe<ArrayList<Application>>() {
                    @Override
                    public void call(Subscriber<? super ArrayList<Application>> subscriber) {
                        ArrayList<Application> apps = dbManager.queryApplication();
                        subscriber.onNext(apps);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<ArrayList<Application>, Boolean>() {
                    @Override
                    public Boolean call(ArrayList<Application> applications) {
                        return applications.size() != 0;
                    }
                })
                .subscribe(new Action1<ArrayList<Application>>() {
                    @Override
                    public void call(ArrayList<Application> applications) {
                        showFavAppUI(applications);
                    }
                });
    }

    public void showFavoriteUI(ArrayList<Movie> movies) {
        if (movies.size() != 0) {
            for (Movie movie : movies) {
                favMovieAdapters.add(movie);
            }
            mRowsAdapter.add(2, favMovieRow);
        }
    }

    public void showFavAppUI(ArrayList<Application> apps) {
        for(Application app:apps) {
            favAppAdapter.add(app);
        }
        mRowsAdapter.add(mRowsAdapter.indexOf(appsRow), favAppRow);
    }


    public void loadSettingsRow() {
        settingAdapter = new PaginationAdapter(home);
        HeaderItem header = new HeaderItem(MovieList.LIST_CATEGORY[5]);
        mRowsAdapter.add(new ListRow(header, settingAdapter));
        settingAdapter.add(new SettingItem(SettingItem.ID_MAIN_SETTING, R.drawable.ic_settings));
        settingAdapter.add(new SettingItem(SettingItem.ID_NETWORK_SETTING, R.drawable.ic_wifi_disable));
        settingAdapter.add(new Time());

    }


    private void loadMoviesRows(ArrayObjectAdapter adapter, List<Movie> list) {
        if (adapter.get(0) instanceof LoadState) {
            adapter.removeItems(0, 1);
        }
        for (int i = 0; i < list.size(); i++) {
            adapter.add(list.get(i));
        }
    }

    private void prepareManagers() {

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);

        // 这里如果直接传home，当dbManager未被销毁时，home会一直存在，造成内存泄漏
        // 改为ApplicationContext的话，dbManager就和activity无关
        dbManager = DBManager.getInstance(home.getApplicationContext());
    }

    private void setupUIElements() {
        setBadgeDrawable(getResources().getDrawable(R.mipmap.freeclick));

        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(getResources().getColor(R.color.selected_background));
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
        setHeaderPresenterSelector(new PresenterSelector() {
            @Override
            public Presenter getPresenter(Object item) {
                return new HeaderItemPresenter();
            }
        });

    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home, SearchActivity.class);
                startActivity(intent);
            }
        });

        setOnItemViewClickedListener(mItemViewClickedListener);
        setOnItemViewSelectedListener(new ItemViewSelectedListener());

    }

    protected void updateBackground(String uri) {
        DisplayMetrics mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(final GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        Observable
                                .create(new Observable.OnSubscribe<Bitmap>() {
                                    @Override
                                    public void call(Subscriber<? super Bitmap> subscriber) {
                                        Bitmap bitmap = ((GlideBitmapDrawable) (resource.getCurrent())).getBitmap();
                                        Bitmap blurBitmap = BitmapUtil.blur(home, bitmap);
                                        subscriber.onNext(blurBitmap);
                                    }
                                })
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Bitmap>() {
                                    @Override
                                    public void call(Bitmap bitmap) {
                                        mBackgroundManager.setBitmap(bitmap);
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        Utils.log(TAG, throwable.getMessage());
                                    }
                                });

                    }
                });
        mBackgroundTimer.cancel();
    }


    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private OnItemViewClickedListener mItemViewClickedListener = new OnItemViewClickedListener() {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            Utils.log(TAG, "onclicked");
            if (item instanceof Movie) {
                Movie movie = (Movie) item;
                int index = favMovieAdapters.indexOf(movie);
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE, movie);
                intent.putExtra(DetailsActivity.COLLECTED, index != -1);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            } else if (item instanceof LoadState) {
                if (LoadState.STATE_LOADING != ((LoadState) item).mState) {
                    PaginationAdapter adapter = (PaginationAdapter) ((ListRow) row).getAdapter();
                    int category = adapter == popularAdapter ? Constant.CATEGORY_POPULAR : Constant.CATEGORY_EDITOR_CHOICE;
                    adapter.showLoadState(LoadState.STATE_LOADING);
                    requestUpdate(category, null);
                }
            } else if (item instanceof Application) {
                ApplicationUtil.getInstance(home).startApplication((Application) item);
            } else if (item instanceof SettingItem) {
                SettingItem settingItem = (SettingItem) item;
                Intent intent;
                switch (settingItem.getId()) {
                    case SettingItem.ID_MAIN_SETTING:
                        intent = home.getPackageManager().getLaunchIntentForPackage("com.android.tv.settings");
                        break;
                    case SettingItem.ID_NETWORK_SETTING:
                        intent = new Intent();
                        intent.setComponent(new ComponentName("com.android.tv.settings", "com.android.tv.settings.connectivity.NetworkActivity"));
                        break;
                    default:
                        intent = home.getPackageManager().getLaunchIntentForPackage("com.android.settings");
                        break;
                }
                startActivity(intent);
            } else if (item instanceof Time) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.tv.settings", "com.android.tv.settings.system.DateTimeActivity"));
                startActivity(intent);
            }
        }
    };

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Movie) {
                mBackgroundURI = ((Movie) item).getCardImageURI();
                startBackgroundTimer();

                int index = mRowsAdapter.indexOf(row);
                PaginationAdapter adapter = (PaginationAdapter) ((ListRow) row).getAdapter();
                if (adapter.get(adapter.size() - 1).equals(item) && adapter.shouldLoadMore()) {
                    requestUpdate(index, adapter.getPageToken());
                }
            } else if(item instanceof Application) {
                requestItemKeyListener(itemViewHolder.view, (Application)item);
            }
        }
    }



    private int itemDownRepeatCount = 0;
    private void requestItemKeyListener(View view, final Application item) {
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {

                    if(event.getAction() == KeyEvent.ACTION_DOWN) {

                        itemDownRepeatCount = event.getRepeatCount();
                        if(itemDownRepeatCount == 7) {
                            // start app details fragment here;
                            int index = favAppAdapter.indexOf(item);
                            Intent intent = new Intent(getActivity(), AppDetailsActivity.class);
                            intent.putExtra("pkgname", item.getPkgName());
                            intent.putExtra("activity", item.getActivity());
                            intent.putExtra(AppDetailsActivity.COLLECTED, index != -1);
                            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    getActivity(),
                                    ((AppCardView) view).getMainImage(),
                                    AppDetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                            getActivity().startActivity(intent, bundle);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }



    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mBackgroundURI != null) {
                        updateBackground(mBackgroundURI.toString());
                    }
                }
            });

        }
    }


    BroadcastReceiver packageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String data = intent.getDataString();
            final String pkgName = data.substring(data.indexOf(":") + 1);
            Utils.log(TAG, action + "   " + pkgName);
            if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                loadAllApplications();

            } else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {

                pkgRemove = new PackageRemoveRunnable(pkgName);
                mHandler.postDelayed(pkgRemove, 1000);

            } else if (action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
                Application app = Application.make(home, pkgName);
                appAdapter.replace(appAdapter.indexOf(app), app);
                mHandler.removeCallbacks(pkgRemove);
            }
        }
    };

    BroadcastReceiver favActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Movie movie = intent.getParcelableExtra(DetailsActivity.MOVIE);
            if(DetailsActivity.ACTION_FAVORITE_ADDED.equals(action)) {
               addMovieFavorite(movie);

            } else if (DetailsActivity.ACTION_FAVORITE_REMOVED.equals(action)) {
                removeFromMovieFavorite(movie);
            }
        }
    };


    BroadcastReceiver favAppReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String pkgname = intent.getStringExtra(AppDetailsActivity.APPLICATION_PACKAGE);
            String activity = intent.getStringExtra(AppDetailsActivity.APPLICATION_ACTIVITY);
            Application app = Application.make(getActivity(), pkgname, activity);
            if(AppDetailsActivity.ACTION_FAVORITE_APP_ADDED.equals(action)) {
                addAppFavorite(app);
            } else if(AppDetailsActivity.ACTION_FAVORITE_APP_REMOVED.equals(action)) {
                removeFromAppFavorite(app);
            }
        }
    };

    private void addMovieFavorite(Movie movie) {
        int index = mRowsAdapter.indexOf(favMovieRow);
        favMovieAdapters.add(movie);
        if(index == -1) {
            mRowsAdapter.add(mRowsAdapter.indexOf(editorRow)+1, favMovieRow);
        }
    }

    private void removeFromMovieFavorite(Movie movie) {
        favMovieAdapters.remove(movie);
        if(favMovieAdapters.size() == 0) {
            mRowsAdapter.remove(favMovieRow);
        }
    }

    private void addAppFavorite(Application app) {
        int index = mRowsAdapter.indexOf(favAppRow);
        favAppAdapter.add(app);
        if(index == -1) {
            mRowsAdapter.add(mRowsAdapter.indexOf(appsRow), favAppRow);
        }
    }

    private void removeFromAppFavorite(Application app) {
        favAppAdapter.remove(app);
        if(favAppAdapter.size() == 0) {
            mRowsAdapter.remove(favAppRow);
        }
    }

    BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Utils.log(TAG, action);
            if(NetworkUtil.isEthEnabled(context)) {
                settingAdapter.replace(1, new SettingItem(SettingItem.ID_NETWORK_SETTING, R.drawable.ic_ethernet));
            } else if(NetworkUtil.isWifiEnabled(context)) {
                settingAdapter.replace(1, new SettingItem(SettingItem.ID_NETWORK_SETTING, R.drawable.ic_wifi));
            } else {
                settingAdapter.replace(1, new SettingItem(SettingItem.ID_NETWORK_SETTING, R.drawable.ic_wifi_disable));
            }
        }
    };

    class PackageRemoveRunnable implements Runnable {
        private String pkgName;

        public PackageRemoveRunnable(String pkgName) {
            this.pkgName = pkgName;
        }

        @Override
        public void run() {
            for(int i = 0; i < appAdapter.size(); i++) {
                Application app = (Application) appAdapter.get(i);
                if(app.getPkgName().equals(pkgName)) {
                    appAdapter.remove(app);
                }
            }
        }
    }



}
