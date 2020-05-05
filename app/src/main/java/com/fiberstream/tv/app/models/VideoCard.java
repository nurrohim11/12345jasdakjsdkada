package com.fiberstream.tv.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoCard extends Card {

    @SerializedName("sources") private List<String> mVideoSources = null;
    @SerializedName("background") private String mBackgroundUrl = "";
    @SerializedName("studio") private String mStudio = "";

    public VideoCard() {
        super();
        setType(Type.VIDEO_GRID);
    }

    public List<String> getVideoSources() {
        return mVideoSources;
    }

    public void setVideoSources(List<String> sources) {
        mVideoSources = sources;
    }

    public String getBackground() {
        return mBackgroundUrl;
    }

    public void setBackground(String background) {
        mBackgroundUrl = background;
    }

    public String getStudio() {
        return mStudio;
    }

    public void setStudio(String studio) {
        mStudio = studio;
    }
}
