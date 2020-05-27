package com.fiberstream.tv.utils;

/**
 * Created by Shin on 02/08/2017.
 */

public class ServerURL {
    private static String base_url ="https://admin.fiberstream.id/api/";
    private static String home_url ="https://admin.fiberstream.id/";
    public static String get_dashbord = base_url+"main/dashboard";
    public static String get_dashbord_apps = base_url+"main/dashboard_apps";
    public static String get_apk_nomaden = home_url+"apk/nomaden04131.apk";
    public static String post_device = base_url+"Authentication/process_device";
    public static String get_kategori = base_url+"master/ms_kategori";
    public static String get_konten_streaming = base_url+"master/konten_streaming";
    public static String get_slider =base_url+"master/iklan";
    public static String get_menu = base_url+"authentication/menu";
    public static String post_fcmid = base_url+"authentication/auth";
    public static String get_kategori_streaming = base_url+"streaming/kategori_streaming";
    public static String get_streaming_by_kategori = base_url+"streaming/item_streaming";
    public static String get_logo = base_url+"master/logo_apps";
    public static String get_channel = base_url+"live/list_live_streaming";
    public static String get_channel_with_kategori = base_url+"live/live_konten_tv";
    public static String get_kategori_channel = base_url+"live/kategori_channel";
    public static String get_id_kategori_first = base_url+"live/id_kategori";
    public static String get_timertv = base_url+"master/timer_tv";
    public static String get_advertisement = base_url+"master/advertisement";
    public static String get_appear_text = base_url+"master/appear_text";
    public static String get_favorite_streaming = base_url+"streaming/streaming_favorit";
    public static String get_all_konten = base_url+"main/all_konten";
    public static String get_background = home_url+"assets/uploads/bg/bg_fiberstream.jpg";
    public static String post_service_client = base_url+"Authentication/service_client";

    public static final  String getKategori= base_url+"api/item/kategori_item/";
    public static final String url_profile_device = base_url+"authentication/profile_device";
    public static final  String getItemTV= base_url+"api/item/item_tv/";
    public static final  String base_url_fcm= "https://fcm.googleapis.com/fcm/send";

}
