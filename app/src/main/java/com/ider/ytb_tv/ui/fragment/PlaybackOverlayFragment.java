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

import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRow.FastForwardAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.PlayPauseAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RepeatAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RewindAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ShuffleAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipNextAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipPreviousAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ThumbsDownAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ThumbsUpAction;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ider.ytb_tv.ui.activity.DetailsActivity;
import com.ider.ytb_tv.data.Movie;
import com.ider.ytb_tv.data.MovieList;
import com.ider.ytb_tv.R;
import com.ider.ytb_tv.ui.presenter.CardPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * Class for video playback with media control
 */
public class PlaybackOverlayFragment extends android.support.v17.leanback.app.PlaybackOverlayFragment {
    private static final String TAG = "PlaybackControlsFragmnt";

    private static final boolean SHOW_DETAIL = true;
    private static final int PRIMARY_CONTROLS = 3;
    private static final boolean SHOW_IMAGE = PRIMARY_CONTROLS <= 5;
    private static final int BACKGROUND_TYPE = PlaybackOverlayFragment.BG_LIGHT;
    private static final int CARD_WIDTH = 200;
    private static final int CARD_HEIGHT = 240;
    private static final int DEFAULT_UPDATE_PERIOD = 1000;
    private static final int UPDATE_PERIOD = 16;
    private static final int SIMULATED_BUFFERED_TIME = 10000;

    private ArrayObjectAdapter mRowsAdapter;
    private ArrayObjectAdapter mPrimaryActionsAdapter;
    private PlayPauseAction mPlayPauseAction;
    private FastForwardAction mFastForwardAction;
    private RewindAction mRewindAction;
    private PlaybackControlsRow mPlaybackControlsRow;
    private Handler mHandler;
    private Runnable mRunnable;
    private Movie mSelectedMovie;

    private OnActionEventListener mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectedMovie = getActivity()
                .getIntent().getParcelableExtra(DetailsActivity.MOVIE);


        mHandler = new Handler();

        setBackgroundType(BACKGROUND_TYPE);
        setFadingEnabled(false);

        setupRows();

    }

//    @SuppressWarnings("deprecation")
//    @Override
//    public void onAttach(Activity context) {
//        super.onAttach(context);
//        if (context instanceof OnPlayPauseClickedListener) {
//            mCallback = (OnPlayPauseClickedListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnPlayPauseClickedListener");
//        }
//    }

    private void setupRows() {

        ClassPresenterSelector ps = new ClassPresenterSelector();

        PlaybackControlsRowPresenter playbackControlsRowPresenter;
        if (SHOW_DETAIL) {
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter(
                    new DescriptionPresenter());
        } else {
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter();
        }
        playbackControlsRowPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            public void onActionClicked(Action action) {
                if (action.getId() == mPlayPauseAction.getId()) {
                    togglePlayback(mPlayPauseAction.getIndex() == PlayPauseAction.PLAY);
                } else if (action.getId() == mFastForwardAction.getId()) {
                    Toast.makeText(getActivity(), "TODO: Fast Forward", Toast.LENGTH_SHORT).show();
                } else if (action.getId() == mRewindAction.getId()) {
                    Toast.makeText(getActivity(), "TODO: Rewind", Toast.LENGTH_SHORT).show();
                }
                if (action instanceof PlaybackControlsRow.MultiAction) {
                    ((PlaybackControlsRow.MultiAction) action).nextIndex();
                    notifyChanged(action);
                }
            }
        });

        ps.addClassPresenter(PlaybackControlsRow.class, playbackControlsRowPresenter);
        ps.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(ps);

        addPlaybackControlsRow();

        setAdapter(mRowsAdapter);
    }

    public void togglePlayback(boolean Pause) {
        if (Pause) {
            setFadingEnabled(true);
            mCallback.onFragmentPlayPause(true);
            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlayPauseAction.PLAY));
        } else {
            setFadingEnabled(false);
            mCallback.onFragmentPlayPause(false);
            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlayPauseAction.PAUSE));
        }
        notifyChanged(mPlayPauseAction);
    }


    private void addPlaybackControlsRow() {
        mPlaybackControlsRow = new PlaybackControlsRow();
        mRowsAdapter.add(mPlaybackControlsRow);

        updatePlaybackRow();

        ControlButtonPresenterSelector presenterSelector = new ControlButtonPresenterSelector();
        mPrimaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        mPlaybackControlsRow.setPrimaryActionsAdapter(mPrimaryActionsAdapter);

        mPlayPauseAction = new PlayPauseAction(getActivity());
        mFastForwardAction = new PlaybackControlsRow.FastForwardAction(getActivity());
        mRewindAction = new PlaybackControlsRow.RewindAction(getActivity());


        mPrimaryActionsAdapter.add(mRewindAction);

        mPrimaryActionsAdapter.add(mPlayPauseAction);

        mPrimaryActionsAdapter.add(mFastForwardAction);

    }

    private void notifyChanged(Action action) {
        ArrayObjectAdapter adapter = mPrimaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
    }

    private void updatePlaybackRow() {
        if (mPlaybackControlsRow.getItem() != null) {
            Movie item = (Movie) mPlaybackControlsRow.getItem();
            item.setTitle(mSelectedMovie.getTitle());
        }
        if (SHOW_IMAGE) {
            updateVideoImage(mSelectedMovie.getCardImageURI().toString());
        }
        mRowsAdapter.notifyArrayItemRangeChanged(0, 1);
        mPlaybackControlsRow.setTotalTime(60000);
        mPlaybackControlsRow.setCurrentTime(0);
        mPlaybackControlsRow.setBufferedProgress(0);
    }

//
//    private int getUpdatePeriod() {
//        if (getView() == null || mPlaybackControlsRow.getTotalTime() <= 0) {
//            return DEFAULT_UPDATE_PERIOD;
//        }
//        return Math.max(UPDATE_PERIOD, mPlaybackControlsRow.getTotalTime() / getView().getWidth());
//    }
//
//    private void startProgressAutomation() {
//        mRunnable = new Runnable() {
//            @Override
//            public void run() {
//                int updatePeriod = getUpdatePeriod();
//                int currentTime = mPlaybackControlsRow.getCurrentTime() + updatePeriod;
//                int totalTime = mPlaybackControlsRow.getTotalTime();
//                mPlaybackControlsRow.setCurrentTime(currentTime);
//                mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);
//
//                mHandler.postDelayed(this, updatePeriod);
//            }
//        };
//        mHandler.postDelayed(mRunnable, getUpdatePeriod());
//    }
//
//
//    private void stopProgressAutomation() {
//        if (mHandler != null && mRunnable != null) {
//            mHandler.removeCallbacks(mRunnable);
//        }
//    }
//
//    @Override
//    public void onStop() {
//        stopProgressAutomation();
//        super.onStop();
//    }
//
    protected void updateVideoImage(String uri) {
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .into(new SimpleTarget<GlideDrawable>(CARD_WIDTH, CARD_HEIGHT) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mPlaybackControlsRow.setImageDrawable(resource);
                        mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
                    }
                });
    }

    // Container Activity must implement this interface
    public interface OnActionEventListener {
        void onFragmentPlayPause(Boolean playPause);
    }

    static class DescriptionPresenter extends AbstractDetailsDescriptionPresenter {
        @Override
        protected void onBindDescription(ViewHolder viewHolder, Object item) {
            viewHolder.getTitle().setText(((Movie) item).getTitle());
        }
    }
}
