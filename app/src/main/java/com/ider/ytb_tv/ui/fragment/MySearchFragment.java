package com.ider.ytb_tv.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.*;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SearchBar;
import android.support.v17.leanback.widget.SearchEditText;
import android.support.v17.leanback.widget.SpeechOrbView;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ider.ytb_tv.R;
import com.ider.ytb_tv.data.AppEntry;
import com.ider.ytb_tv.data.CustService;
import com.ider.ytb_tv.data.LoadState;
import com.ider.ytb_tv.data.Movie;
import com.ider.ytb_tv.data.PlayList;
import com.ider.ytb_tv.data.ResourceEntry;
import com.ider.ytb_tv.db.DBManager;
import com.ider.ytb_tv.ui.Adapter.PaginationAdapter;
import com.ider.ytb_tv.ui.activity.DetailsActivity;
import com.ider.ytb_tv.ui.activity.SearchActivity;
import com.ider.ytb_tv.utils.JsonParser;
import com.ider.ytb_tv.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MySearchFragment extends Fragment {

    public static String TAG = "SearchFragment";
    Handler handler;
    ArrayObjectAdapter mRowsAdapter;
    PaginationAdapter mHistoryAdapter;
    PaginationAdapter mPlaylistAdapter;
    PaginationAdapter mVideosAdapter;
    PaginationAdapter mPlaylistVideosAdapter;
    PaginationAdapter mAppAdapter;
    ListRow historyRow, playlistRow, videoRow, playlistVideosRow, appRow;
    DBManager dbManager;

    CustService custService;
    QueryTextChangeRunnable playlistRunnable, videosRunnable, appsRunnable;
    RowsFragment mRowsFragment;
    SearchBar mSearchBar;
    String queryStr;
    SearchActivity activity;
    String currentPlayListId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        activity = (SearchActivity) getActivity();
        dbManager = DBManager.getInstance(activity.getApplicationContext());
        handler = new Handler();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.search_fragment, null, false);
        hideSpeechView(rootView);
        setupSearchBarListener();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSearchFragment();
        initRows();
        setupEventFragment();
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    public void initSearchFragment() {
        mRowsFragment = new RowsFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.m_search_fragment_container, mRowsFragment);
        fragmentTransaction.commit();
        mRowsFragment.setAdapter(mRowsAdapter);
    }

    public void setupEventFragment() {
        mRowsFragment.setOnItemViewSelectedListener(onItemViewSelectedListener);
        mRowsFragment.setOnItemViewClickedListener(onItemViewClickedListener);

    }


    public void initRows() {
        // line 0 : history
        loadHistory();

        // line 1 : playlists
        mPlaylistAdapter = new PaginationAdapter(activity, 60, true);

        // line 2 : videos
        mVideosAdapter = new PaginationAdapter(activity, 60, true);

        // line 3 : apps
        mAppAdapter = new PaginationAdapter(activity, false);
    }

    private void loadHistory() {

        Observable
                .create(new Observable.OnSubscribe<List<String>>() {
                    @Override
                    public void call(Subscriber<? super List<String>> subscriber) {
                        subscriber.onNext(dbManager.querySearchHistory());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> strings) {
                        showHistoryRow(strings);
                    }
                });
    }

    private void showHistoryRow(List<String> histories) {
        if (histories.size() != 0) {
            mHistoryAdapter = new PaginationAdapter(activity, false);
            historyRow = new ListRow(new HeaderItem(getResources().getString(R.string.search_history)), mHistoryAdapter);
            mHistoryAdapter.add(getResources().getString(R.string.search_history_clear));
            for (String history : histories) {
                mHistoryAdapter.add(history);
            }
            mRowsAdapter.add(0, historyRow);
        }
    }


    private void hideSpeechView(View rootView) {
        mSearchBar = (SearchBar) rootView.findViewById(R.id.m_search_bar);
        mSearchBar.setTitle(getResources().getString(R.string.search_bar_title));
        final SpeechOrbView mSpeechOrbView = (SpeechOrbView) mSearchBar.findViewById(android.support.v17.leanback.R.id.lb_search_bar_speech_orb);
        mSpeechOrbView.setAlpha(0);
        mSpeechOrbView.setFocusable(false);
        final SearchEditText mSearchEditText = (SearchEditText) mSearchBar.findViewById(android.support.v17.leanback.R.id.lb_search_text_editor);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mSearchBar.getLayoutParams();
        lp.leftMargin = -150;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSearchEditText.requestFocus();
            }
        }, 0);
    }

    public void setupSearchBarListener() {
        mSearchBar.setSearchBarListener(new SearchBar.SearchBarListener() {
            @Override
            public void onSearchQueryChange(final String query) {
                onQueryTextChange(query);
            }

            @Override
            public void onSearchQuerySubmit(String query) {
                onQueryTextSubmit(query);
            }

            @Override
            public void onKeyboardDismiss(String query) {
                if(checkQueryText(query)) {
                    dbManager.insertSearchHistory(query);
                }

            }
        });
    }


    /**
     * check the query text is available or not
     * @param query
     * @return true if available
     */
    public boolean checkQueryText(String query) {
        queryStr = query;
        if(query.equals("")) {
            return false;
        }
        return true;
    }

    public void removeLastQueryTask() {
        if (playlistRunnable != null) {
            handler.removeCallbacks(playlistRunnable);
        }
        if (videosRunnable != null) {
            handler.removeCallbacks(videosRunnable);
        }
        if(appsRunnable != null) {
            handler.removeCallbacks(appsRunnable);
        }

    }


    /**
     * set all the paginAdapter pagertoken null
     */
    public void removeAllPageToken() {
        mPlaylistAdapter.removePageToken();
        mVideosAdapter.removePageToken();
        if (mPlaylistVideosAdapter != null) {
            mPlaylistVideosAdapter.removePageToken();
        }
    }

    /**
     * Remove the playlistVIDEO row if exist.
     * Setup the playlist and video rows for the query text
     * @param query
     */
    public void setSearchResultRows(String query) {
        if(playlistVideosRow != null) {
            mRowsAdapter.remove(playlistVideosRow);
        }
        if (mRowsAdapter.indexOf(playlistRow) == -1) {
            Utils.log(TAG, "add listrow");
            playlistRow = new ListRow(mPlaylistAdapter);
            mRowsAdapter.add(playlistRow);
            videoRow = new ListRow(mVideosAdapter);
            mRowsAdapter.add(videoRow);
            appRow = new ListRow(mAppAdapter);
            mRowsAdapter.add(appRow);
        }
        HeaderItem header = new HeaderItem(String.format(getResources().getString(R.string.search_results_playlist), query));
        playlistRow.setHeaderItem(header);
        header = new HeaderItem(String.format(getResources().getString(R.string.search_results_videos), query));
        videoRow.setHeaderItem(header);
        header = new HeaderItem(String.format(getResources().getString(R.string.search_results_apps), query));
        appRow.setHeaderItem(header);
        mRowsAdapter.notifyArrayItemRangeChanged(mRowsAdapter.indexOf(playlistRow), mRowsAdapter.size());
    }

    public void onQueryTextChange(String newQuery) {
        if(!checkQueryText(newQuery)) {
            return;
        }
        removeLastQueryTask();
        removeAllPageToken();

        setSearchResultRows(newQuery);

        playlistRunnable = new QueryTextChangeRunnable(newQuery, mPlaylistAdapter);
        handler.postDelayed(playlistRunnable, 500);
        videosRunnable = new QueryTextChangeRunnable(newQuery, mVideosAdapter);
        handler.postDelayed(videosRunnable, 500);
        appsRunnable = new QueryTextChangeRunnable(newQuery, mAppAdapter);
        handler.postDelayed(appsRunnable, 500);
    }

    public void onQueryTextSubmit(String query) {
        if(!checkQueryText(query)) {
            return;
        }
        removeAllPageToken();
        removeLastQueryTask();
        setSearchResultRows(query);
        playlistRunnable = new QueryTextChangeRunnable(query, mPlaylistAdapter);
        handler.postDelayed(playlistRunnable, 0);
        videosRunnable = new QueryTextChangeRunnable(query, mVideosAdapter);
        handler.postDelayed(videosRunnable, 0);
        appsRunnable = new QueryTextChangeRunnable(query, mAppAdapter);
        handler.postDelayed(appsRunnable, 0);

        mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());

    }


    class QueryTextChangeRunnable implements Runnable {
        String query;
        PaginationAdapter adapter;

        public QueryTextChangeRunnable(String query, PaginationAdapter adapter) {
            this.query = query;
            this.adapter = adapter;
        }

        @Override
        public void run() {
            adapter.removeItems(0, adapter.size());
            adapter.add(new LoadState(LoadState.STATE_LOADING));

            startSearch(query, adapter);
        }
    }

    public void startSearch(final String query, final PaginationAdapter adapter) {
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        if (custService == null) {
                            custService = activity.custService;
                        }
                        String result = null;
                        if (adapter == mPlaylistAdapter) {
                            result = custService.getSearchPlaylist(adapter.getPageToken(), query);
                        } else if (adapter == mVideosAdapter) {
                            result = custService.getSearchVideos(adapter.getPageToken(), query);
                        } else if(adapter == mAppAdapter) {
                            result = custService.getSearchApps(query);
                            Utils.log(TAG, result);
                        }
                        result = result == null ? "null" : result;
                        subscriber.onNext(result);
                    }
                })
                .subscribeOn(Schedulers.io())
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return query.equals(queryStr);
                    }
                })
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return s != null;
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Func1<String, ArrayList<ResourceEntry>>() {
                    @Override
                    public ArrayList<ResourceEntry> call(String s) {
                        Utils.log(TAG, "do parse");
                        if (adapter == mPlaylistAdapter) {
                            adapter.setPageToken(JsonParser.getNextPageToken(s));
                            return JsonParser.parsePlaylist(s);
                        } else if (adapter == mVideosAdapter) {
                            adapter.setPageToken(JsonParser.getNextPageToken(s));
                            ArrayList<ResourceEntry> list = JsonParser.parseRelateOrSearchVideos(s);
                            return custService.setVideosDuration(list);
                        } else if (adapter == mAppAdapter) {
                            return JsonParser.parseForApp(s);
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<ArrayList<ResourceEntry>, Boolean>() {
                    @Override
                    public Boolean call(ArrayList<ResourceEntry> results) {
                        return query.equals(queryStr);
                    }
                })
                .subscribe(new Action1<ArrayList<ResourceEntry>>() {
                    @Override
                    public void call(ArrayList<ResourceEntry> results) {
                        if(adapter == mPlaylistAdapter || adapter == mVideosAdapter) {
                            loadResults(results, adapter);
                        } else if(adapter == mAppAdapter) {
                            loadAppResult(results, query);
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Utils.log(TAG, "onError:" + throwable.getMessage());
                    }
                });

    }

    public void loadResults(ArrayList<ResourceEntry> results, PaginationAdapter adapter) {

        if (adapter.get(0) instanceof LoadState) {
            adapter.removeItems(0, 1);
        }

        if(results == null || results.size() == 0) {
            adapter.add(new LoadState(LoadState.STATE_NO_RESUTL));
            return;
        }

        for (ResourceEntry result : results) {
            adapter.add(result);
        }
    }

    public void loadAppResult(ArrayList<ResourceEntry> results, String name) {

        if (mAppAdapter.get(0) instanceof LoadState) {
            mAppAdapter.removeItems(0, 1);
        }

        if(results == null || results.size() == 0) {
            AppEntry app = new AppEntry();
            app.special = AppEntry.SPECIAL_NO_RESULT;
            app.name = name;
            mAppAdapter.add(app);
            return;
        }

        for(ResourceEntry result : results) {
            mAppAdapter.add(result);
        }
        AppEntry app = new AppEntry();
        app.special = AppEntry.SPECIAL_MORE_RESULT;
        app.name = name;
        mAppAdapter.add(app);
    }


    OnItemViewSelectedListener onItemViewSelectedListener = new OnItemViewSelectedListener() {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (row == playlistVideosRow && item instanceof Movie) {
                if (mPlaylistVideosAdapter.get(mPlaylistVideosAdapter.size() - 1).equals(item) && mPlaylistVideosAdapter.shouldLoadMore()) {
                    retreiveForVideosInPlaylist(currentPlayListId);
                }
                return;
            }
            if (item instanceof PlayList || item instanceof Movie) {
                ListRow listRow = (ListRow) row;
                PaginationAdapter adapter = (PaginationAdapter) listRow.getAdapter();
                if (adapter.get(adapter.size() - 1).equals(item) && adapter.shouldLoadMore()) {
                    startSearch(queryStr, adapter);
                }
            }
        }
    };


    OnItemViewClickedListener onItemViewClickedListener = new OnItemViewClickedListener() {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Movie) {
                Movie movie = (Movie) item;
                Intent intent = new Intent(activity, DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE, movie);
                if(movie.getPlaylist_id() != null) {
                    intent.putExtra(DetailsActivity.PLAYLIST_POSITION, mPlaylistVideosAdapter.indexOf(movie));
                }
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);

            } else if (item instanceof PlayList) {
                onPlaylistClicked((PlayList) item);

            } else if (item instanceof String) {
                onHistoryClicked((String) item);
            } else if (item instanceof AppEntry) {
                AppEntry app = (AppEntry) item;
                Intent intent;
                if(app.isNormal()) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("aptoideinstall://%s&show_install_popup=true", app.id)));
                    startActivity(intent);
                } else {
                    intent= new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(String.format("market://search?q=%s", app.name)));
                    startActivity(intent);
                }
            }
        }
    };

    public void onHistoryClicked(String query) {
        if (query.equals(mHistoryAdapter.get(0))) {
            dbManager.deleteAllSearch();
            mRowsAdapter.remove(historyRow);
        } else {
            mSearchBar.setSearchQuery(query);
            onQueryTextSubmit(query);
        }
    }

    public void onPlaylistClicked(PlayList playlist) {
        this.currentPlayListId = playlist.id;
        if(mRowsAdapter.indexOf(playlistVideosRow) == -1) {
            mPlaylistVideosAdapter = new PaginationAdapter(getActivity(), true);
            HeaderItem header = new HeaderItem(String.format(getResources().getString(R.string.videos_in_playlist), playlist.title));
            playlistVideosRow = new ListRow(header, mPlaylistVideosAdapter);
            mRowsAdapter.add(mRowsAdapter.indexOf(playlistRow)+1, playlistVideosRow);
            retreiveForVideosInPlaylist(currentPlayListId);
        } else {
            mPlaylistVideosAdapter.removePageToken();
            mPlaylistVideosAdapter.removeItems(0, mPlaylistVideosAdapter.size());
            playlistVideosRow.setHeaderItem(new HeaderItem(String.format(getResources().getString(R.string.videos_in_playlist), playlist.title)));
            retreiveForVideosInPlaylist(currentPlayListId);
            mRowsAdapter.notifyArrayItemRangeChanged(mRowsAdapter.indexOf(playlistVideosRow), mRowsAdapter.indexOf(playlistVideosRow) + 1);
        }

    }

    public void retreiveForVideosInPlaylist(final String playlistId) {
        if (mPlaylistVideosAdapter.size() == 0) {
            mPlaylistVideosAdapter.add(new LoadState(LoadState.STATE_LOADING));
        }
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        String result = custService.retreiveVideosInPlaylist(playlistId, mPlaylistVideosAdapter.getPageToken());
                        subscriber.onNext(result);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, ArrayList<Movie>>() {
                    @Override
                    public ArrayList<Movie> call(String s) {
                        int totalResults = JsonParser.getTotalResult(s);
                        Utils.log(TAG, "total : " + totalResults);
                        if (totalResults != -1) {
                            mPlaylistVideosAdapter.setMaxsize(totalResults);
                        }
                        mPlaylistVideosAdapter.setPageToken(JsonParser.getNextPageToken(s));
                        ArrayList<Movie> list = JsonParser.parseEditorChoice(s);
                        for(int i = 0; i < list.size(); i++) {
                            list.get(i).setPlaylist_id(playlistId);
                        }
                        return custService.setVideosDuration(list);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<Movie>>() {
                    @Override
                    public void call(ArrayList<Movie> movies) {
                        if (mPlaylistVideosAdapter.get(0) instanceof LoadState) {
                            mPlaylistVideosAdapter.removeItems(0, mPlaylistVideosAdapter.size());
                        }
                        for (Movie movie : movies) {
                            mPlaylistVideosAdapter.add(movie);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Utils.log(TAG, throwable.getMessage());
                    }
                });
    }

}
