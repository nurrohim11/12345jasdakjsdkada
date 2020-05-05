package com.fiberstream.tv.app.streaming.model;

public class KategoriModel {
    String id, nama, imageUrl,bgImageUrl;

    public KategoriModel(String id, String nama, String imageUrl, String bgImageUrl){
        this.id = id;
        this.nama =nama;
        this.imageUrl = imageUrl;
        this.bgImageUrl = bgImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBgImageUrl() {
        return bgImageUrl;
    }

    public void setBgImageUrl(String bgImageUrl) {
        this.bgImageUrl = bgImageUrl;
    }
}
