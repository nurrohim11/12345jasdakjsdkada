/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.fiberstream.tv.utils;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

/**
 * A collection of utility methods, all static.
 */
public class Utils {

    static DhcpInfo d;
    static String str_temp;
    /*
     * Making sure public utility methods remain static
     */
    private Utils() {
    }

    public static  final int BACKGROUND_UPDATE_DELAY =100;
    /**
     * Returns the screen/display size
     */
    public static Point getDisplaySize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     * Shows a (long) toast
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows a (long) toast.
     */
    public static void showToast(Context context, int resourceId) {
        Toast.makeText(context, context.getString(resourceId), Toast.LENGTH_LONG).show();
    }

    public static int convertDpToPixel(Context ctx, int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    /**
     * Formats time in milliseconds to hh:mm:ss string format.
     */
    public static String formatMillis(int millis) {
        String result = "";
        int hr = millis / 3600000;
        millis %= 3600000;
        int min = millis / 60000;
        millis %= 60000;
        int sec = millis / 1000;
        if (hr > 0) {
            result += hr + ":";
        }
        if (min >= 0) {
            if (min > 9) {
                result += min + ":";
            } else {
                result += "0" + min + ":";
            }
        }
        if (sec > 9) {
            result += sec;
        } else {
            result += "0" + sec;
        }
        return result;
    }

    public static long getDuration(String videoUrl) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mmr.setDataSource(videoUrl, new HashMap<String, String>());
        } else {
            mmr.setDataSource(videoUrl);
        }
        return Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    }

    public static boolean hasPermission(final Context context, final String permission) {
        return PackageManager.PERMISSION_GRANTED == context.getPackageManager().checkPermission(
                permission, context.getPackageName());
    }

    public static String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            String json = new String(bytes);
            return json;
        } catch (IOException e) {
            return null;
        }
    }

    public static Uri getResourceUri(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID));
    }

    public static List<String> getInstalledAppsPackageNameList(Context context){
        // Initialize a new intent
        Intent intent = new Intent(Intent.ACTION_MAIN,null);

        // Set intent category
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // Set intent flags
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        // Initialize a new list of resolve info
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent,0);

        // Initialize a new list of package name
        List<String> packageNameList = new ArrayList<>();

        for(ResolveInfo resolveInfo: resolveInfoList){

            // Get the activity info from resolve info
            ActivityInfo activityInfo = resolveInfo.activityInfo;

            // Get the package name from activity info's application info
            // Add the package name to the list
            packageNameList.add(activityInfo.applicationInfo.packageName);
        }

        // Return the package name list
        return packageNameList;
    }

    public static String deviceName(Context context){
        return String.valueOf(Settings.Global.getString(context.getContentResolver(), "device_name"));
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':')<0;
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }

    public static byte byteOfInt(int value, int which) {
        int shift = which * 8;
        return (byte)(value >> shift);
    }

    public static InetAddress intToInet(int value) {
        byte[] bytes = new byte[4];
        for(int i = 0; i<4; i++) {
            bytes[i] = byteOfInt(value, i);
        }
        try {
            return InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) {
            // This only happens if the byte array has a bad length
            return null;
        }
    }

    public static String runSystemCommand(String command) {

        String s = "";

        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader inputStream = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));

            for (int i=0; i<2; i++){
                s = inputStream.readLine();
                Log.d("NetworkFragment",s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  s;
    }

}
