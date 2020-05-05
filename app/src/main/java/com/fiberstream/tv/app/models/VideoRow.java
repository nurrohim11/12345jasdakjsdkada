package com.fiberstream.tv.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoRow {
    @SerializedName("category") private String mCategory = "";
    @SerializedName("videos") private List<VideoCard> mVideos;

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public List<VideoCard> getVideos() {
        return mVideos;
    }

    public void setVideos(List<VideoCard> videos) {
        mVideos = videos;
    }
}
