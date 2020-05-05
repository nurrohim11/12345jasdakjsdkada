package com.fiberstream.tv.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SongList {

    @SerializedName("songs") private List<Song> mSongs;

    public List<Song> getSongs() {
        return mSongs;
    }

}
