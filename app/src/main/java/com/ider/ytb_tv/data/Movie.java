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

package com.ider.ytb_tv.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

/*
 * Movie class represents video entity with title, description, image thumbs and video url.
 *
 */
public class Movie extends ResourceEntry implements Parcelable {
    private String video_id;
    private String playlist_id;
    private String title;
    private String description;
    private String cardImageUrl;
    private String bgUrl;
    private String duration;
    private String publishAt;

    public Movie() {
    }


    public String getId() {
        return video_id;
    }

    public void setId(String id) {
        this.video_id = id;
    }

    public void setPlaylist_id(String playlist_id) {
        this.playlist_id = playlist_id;
    }

    public String getPlaylist_id() {
        return playlist_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    public void setPublishAt(String publishAt) {
        this.publishAt = publishAt;
    }

    public String getPublishAt() {
        return publishAt;
    }

    public URI getCardImageURI() {
        try {
            return new URI(getCardImageUrl());
        } catch (Exception e) {
            return null;
        }
    }

    public URI getBgURI() {
        try {
            return new URI(getBgUrl());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + video_id +
                ", title='" + title + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{video_id, title, cardImageUrl, playlist_id});
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            Movie movie = new Movie();
            String[] params = new String[4];
            parcel.readStringArray(params);
            movie.setId(params[0]);
            movie.setTitle(params[1]);
            movie.setCardImageUrl(params[2]);
            movie.setPlaylist_id(params[3]);

            return movie;
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

    @Override
    public boolean equals(Object o) {
        return ((Movie)o).getId().equals(video_id);
    }
}
