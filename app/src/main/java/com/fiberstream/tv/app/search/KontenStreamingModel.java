package com.fiberstream.tv.app.search;

public class KontenStreamingModel {
    String id, title, icon, urlImage, kategori, jsonPackage, urlPlaystore, urlWeb, flag, link;

    public KontenStreamingModel(String id, String title, String icon, String kategori,String link, String jsonPackage, String urlPlaystore, String urlWeb, String flag){
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.link = link;
        this.kategori = kategori;
        this.jsonPackage = jsonPackage;
        this.urlPlaystore = urlPlaystore;
        this.urlWeb = urlWeb;
        this.flag = flag;
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

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
