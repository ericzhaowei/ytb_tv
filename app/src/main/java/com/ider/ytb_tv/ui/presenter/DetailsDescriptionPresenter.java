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

package com.ider.ytb_tv.ui.presenter;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.ider.ytb_tv.data.Application;
import com.ider.ytb_tv.data.Movie;
import com.ider.ytb_tv.ui.fragment.VideoDetailsFragment;
import com.ider.ytb_tv.utils.ApplicationUtil;
import com.ider.ytb_tv.utils.Utils;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    public ViewHolder holder;


    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        this.holder = viewHolder;

        if(item instanceof Movie) {
            Movie movie = (Movie) item;
            if (movie != null) {
                viewHolder.getTitle().setText(movie.getTitle());
                viewHolder.getBody().setText(movie.getDescription());
                viewHolder.getSubtitle().setText(movie.getPublishAt());

            }
        } else if(item instanceof Application) {
            Application app = (Application) item;
            if(app != null) {
                viewHolder.getTitle().setText(app.getLabel());
                viewHolder.getSubtitle().setText("Verison:"+ApplicationUtil.getInstance(viewHolder.view.getContext()).getPackageVersion(app.getPkgName()));
            }
        }

    }

    public void setDescription(String description) {
        holder.getBody().setText(description);
    }

    public String getDescription() {
        return holder.getBody().getText().toString();
    }



}
