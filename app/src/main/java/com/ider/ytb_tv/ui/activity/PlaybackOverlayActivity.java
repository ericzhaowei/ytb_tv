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

package com.ider.ytb_tv.ui.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.ider.ytb_tv.data.CustService;
import com.ider.ytb_tv.data.Movie;
import com.ider.ytb_tv.R;
import com.ider.ytb_tv.ui.fragment.PlaybackOverlayFragment;
import com.ider.ytb_tv.utils.PropertyReader;
import com.ider.ytb_tv.utils.Utils;

/**
 * PlaybackOverlayActivity for video playback that loads PlaybackOverlayFragment
 */
public class PlaybackOverlayActivity extends Activity implements PlaybackOverlayFragment.OnActionEventListener {
    public static final String TAG = "PlaybackOverlayActivity";

    private YouTubePlayer youtubePlayer;
    private Movie mSelectedMovie;
    private int mPosition;

    private FragmentManager fragmentManager;
    private FragmentTransaction  fragmentTransaction;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_fragment);
        mSelectedMovie = getIntent().getParcelableExtra(DetailsActivity.MOVIE);
        mPosition = getIntent().getIntExtra(DetailsActivity.PLAYLIST_POSITION, 0);
        initFragment();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void initFragment() {
        fragmentManager = getFragmentManager();
        YouTubePlayerFragment playerFragment = new YouTubePlayerFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.player_container, playerFragment);
        fragmentTransaction.commit();

        playerFragment.initialize(CustService.ID_YTB_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                PlaybackOverlayActivity.this.youtubePlayer = youTubePlayer;
                youtubePlayer.setShowFullscreenButton(false);
                if(!Utils.isMouseMode(PlaybackOverlayActivity.this)) {
                    youtubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                }
                startVideo();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });

    }

    public void startVideo() {
        if(!isPlaylist()) {
            youtubePlayer.loadVideo(mSelectedMovie.getId());
        } else {
            youtubePlayer.loadPlaylist(mSelectedMovie.getPlaylist_id(), mPosition, 0);
        }

        youtubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {

            }

            @Override
            public void onPaused() {

            }

            @Override
            public void onStopped() {
                if (!isPlaylist()) {
                    PlaybackOverlayActivity.this.finish();
                }
            }

            @Override
            public void onBuffering(boolean b) {

            }

            @Override
            public void onSeekTo(int i) {

            }
        });

        youtubePlayer.setPlaylistEventListener(new YouTubePlayer.PlaylistEventListener() {
            @Override
            public void onPrevious() {

            }

            @Override
            public void onNext() {

            }

            @Override
            public void onPlaylistEnded() {
                PlaybackOverlayActivity.this.finish();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onFragmentPlayPause(Boolean pause) {
        if (pause && youtubePlayer.isPlaying()) {
            youtubePlayer.pause();
        } else if (!pause && !youtubePlayer.isPlaying()) {
            youtubePlayer.play();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if(youtubePlayer != null) {
                youtubePlayer.seekRelativeMillis(-10000);
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if(youtubePlayer != null) {
                youtubePlayer.seekRelativeMillis(10000);
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
            if(youtubePlayer != null) {
                if(youtubePlayer.hasNext()) {
                    youtubePlayer.next();
                } else {
                    Toast.makeText(this, "This is the last video!!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
            if(youtubePlayer != null) {
                if(youtubePlayer.hasPrevious()) {
                    youtubePlayer.previous();
                } else {
                    Toast.makeText(this, "This is the first video!!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            if(youtubePlayer != null) {
                if(youtubePlayer.isPlaying()) {
                    youtubePlayer.pause();
                } else {
                    youtubePlayer.play();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private boolean isPlaylist() {
        return mSelectedMovie.getPlaylist_id() != null;
    }

}
