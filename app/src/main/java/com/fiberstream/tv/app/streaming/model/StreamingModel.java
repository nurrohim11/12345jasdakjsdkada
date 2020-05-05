package com.fiberstream.tv.app.streaming.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StreamingModel {
    String id, title, icon, urlImage, kategori, jsonPackage, urlPlaystore, urlWeb;

    public StreamingModel (String id,String title,String icon,String urlImage,String kategori,String jsonPackage,String urlPlaystore,String urlWeb){
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.urlImage = urlImage;
        this.kategori = kategori;
        this.jsonPackage = jsonPackage;
        this.urlPlaystore = urlPlaystore;
        this.urlWeb = urlWeb;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getJsonPackage() {
        return jsonPackage;
    }

    public void setJsonPackage(String jsonPackage) {
        this.jsonPackage = jsonPackage;
    }

    public String getUrlPlaystore() {
        return urlPlaystore;
    }

    public void setUrlPlaystore(String urlPlaystore) {
        this.urlPlaystore = urlPlaystore;
    }

    public String getUrlWeb() {
        return urlWeb;
    }

    public void setUrlWeb(String urlWeb) {
        this.urlWeb = urlWeb;
    }
}
