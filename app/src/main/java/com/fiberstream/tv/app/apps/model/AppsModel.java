package com.fiberstream.tv.app.apps.model;

import android.graphics.drawable.Drawable;

public class AppsModel {
    String nama, paket;
    Drawable icon;

    public AppsModel(String nama, String paket, Drawable icon){
        this.nama = nama;
        this.paket = paket;
        this.icon = icon;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPaket() {
        return paket;
    }

    public void setPaket(String paket) {
        this.paket = paket;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
