package com.fiberstream.tv.app.tv.model;

import com.fiberstream.tv.app.models.Card;
import com.google.gson.annotations.SerializedName;

public class ChannelModel {

    @SerializedName("id") private String id = "";
    @SerializedName("fcm_id") private String fcm_id = "";
    @SerializedName("paket") private String paket = "";
    @SerializedName("icon") private String icon;
    @SerializedName("nama") private String nama = null;
    @SerializedName("link") private String link = null;
    @SerializedName("id_kategori") private int id_kategori;
    @SerializedName("kategori") private String kategori = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFcm_id() {
        return fcm_id;
    }

    public void setFcm_id(String fcm_id) {
        this.fcm_id = fcm_id;
    }

    public String getPaket() {
        return paket;
    }

    public void setPaket(String paket) {
        this.paket = paket;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public int getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(int id_kategori) {
        this.id_kategori = id_kategori;
    }
}
