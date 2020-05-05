package com.fiberstream.tv.app.tv.model;

import com.fiberstream.tv.app.models.Card;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChannelRowModel {
    @SerializedName("channel") private List<ChannelModel> channel;
    @SerializedName("kategori") private String kategori;
    @SerializedName("id") private String id;

    public String getId() {
        return id;
    }

    public String getKategori() {
        return kategori;
    }

    public List<ChannelModel> getChannel() {
        return channel;
    }
}
