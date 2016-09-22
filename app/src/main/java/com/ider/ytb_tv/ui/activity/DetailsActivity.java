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
import android.os.Bundle;

import com.ider.ytb_tv.R;
import com.ider.ytb_tv.ui.fragment.VideoDetailsFragment;

public class DetailsActivity extends ServiceActivity {
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String MOVIE = "Movie";
    public static final String PLAYLIST_POSITION = "Playlist_Position";
    public static final String COLLECTED = "Collected";

    public static final String ACTION_FAVORITE_ADDED = "ider.intent.action.favorite_added";
    public static final String ACTION_FAVORITE_REMOVED = "ider.intent.action.favorite_removed";

    public VideoDetailsFragment detailsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        detailsFragment = (VideoDetailsFragment) getFragmentManager().findFragmentById(R.id.details_fragment);
    }

    @Override
    void serviceConnected() {
        detailsFragment.requestRelatedVideos(custService);
        detailsFragment.requestDescription(custService);
    }
}
