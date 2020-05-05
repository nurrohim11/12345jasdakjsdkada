package com.fiberstream.tv.app.tv.model;

import com.fiberstream.tv.app.models.Card;
import com.google.gson.annotations.SerializedName;

public class ChannelModel {

    @SerializedName("id") private String id = "";
    @SerializedName("id_kategori_c") private String id_kategori_c = "";
    @SerializedName("nama") private String nama = "";
    @SerializedName("link") private String link;
    @SerializedName("keterangan") private String keterangan = null;
    @SerializedName("icon") private String icon = null;
    @SerializedName("timestamp") private String timestamp = null;
    @SerializedName("status") private int status;
    @SerializedName("insert_at") private String insert_at = null;
    @SerializedName("user_insert") private String user_insert = null;
    @SerializedName("update_at") private String update_at = null;
    @SerializedName("user_update") private String user_update = null;
    @SerializedName("rekomendasi") private String rekomendasi = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId_kategori_c() {
        return id_kategori_c;
    }

    public void setId_kategori_c(String id_kategori_c) {
        this.id_kategori_c = id_kategori_c;
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

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInsert_at() {
        return insert_at;
    }

    public void setInsert_at(String insert_at) {
        this.insert_at = insert_at;
    }

    public String getUser_insert() {
        return user_insert;
    }

    public void setUser_insert(String user_insert) {
        this.user_insert = user_insert;
    }

    public String getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }

    public String getUser_update() {
        return user_update;
    }

    public void setUser_update(String user_update) {
        this.user_update = user_update;
    }

    public String getRekomendasi() {
        return rekomendasi;
    }

    public void setRekomendasi(String rekomendasi) {
        this.rekomendasi = rekomendasi;
    }
}
