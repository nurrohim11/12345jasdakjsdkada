package com.fiberstream.tv.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseFragment;
import androidx.leanback.app.RowsFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.PageRow;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.VerticalGridPresenter;

import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fiberstream.tv.R;
import com.fiberstream.tv.app.apps.AppsActivity;
import com.fiberstream.tv.app.favorite.model.FavoriteModel;
import com.fiberstream.tv.app.favorite.presenter.FavoritePresenterSelector;
import com.fiberstream.tv.app.main.MainListRows;
import com.fiberstream.tv.app.main.model.DataModel;
import com.fiberstream.tv.app.main.model.DataRowModel;
import com.fiberstream.tv.app.main.model.SliderModel;
import com.fiberstream.tv.app.main.presenter.MainPresenterSelector;
import com.fiberstream.tv.app.main.presenter.SliderPresenter;
import com.fiberstream.tv.app.main.presenter.TextPresenter;
import com.fiberstream.tv.app.register.RegisterActivity;
import com.fiberstream.tv.app.search.SearchActivity;
import com.fiberstream.tv.app.settings.SettingsListRow;
import com.fiberstream.tv.app.settings.model.SettingModel;
import com.fiberstream.tv.app.settings.model.SettingRowModel;
import com.fiberstream.tv.app.settings.presenter.SettingsIconPresenter;
import com.fiberstream.tv.app.streaming.detail.DetailKategoriActivity;
import com.fiberstream.tv.utils.ShadowRowPresenterSelector;
import com.fiberstream.tv.app.streaming.model.KategoriModel;
import com.fiberstream.tv.app.page.GridFragment;
import com.fiberstream.tv.app.streaming.presenters.CardPresenterSelector;
import com.fiberstream.tv.app.tv.TvListRows;
import com.fiberstream.tv.app.tv.model.ChannelModel;
import com.fiberstream.tv.app.tv.model.ChannelRowModel;
import com.fiberstream.tv.app.tv.presenter.TvPresenterSelector;
import com.fiberstream.tv.utils.ServerURL;
import com.fiberstream.tv.utils.Utils;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.AppRequestCallback;
import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.SessionManager;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.TELEPHONY_SERVICE;
import static com.fiberstream.tv.utils.Utils.BACKGROUND_UPDATE_DELAY;

public class MainFragment extends BrowseFragment {
    private static final String TAG = "MainFragment";
    public static DisplayMetrics mMetrics;

    private static final long MENU_ID_1 = 1;
    private static final String MENU_NAME_1 = "Home";
    private static final long MENU_ID_2 = 2;
    private static final String MENU_NAME_2 = "Categories";
    private static final long MENU_ID_3 = 3;
    private static final String MENU_NAME_3 = "NOMADEN IP TV";
    private static final long MENU_ID_4 = 4;
    private static final String MENU_NAME_4 = "Favorites";
    private static final long MENU_ID_5 = 5;
    private static final String MENU_NAME_5 = "Billing";
    private static final long MENU_ID_6 = 6;
    private static final String MENU_NAME_6 = "Info";
    private static final long MENU_ID_7 = 7;
    private static final String MENU_NAME_7 = "Settings";
    private static final long MENU_ID_8 = 8;
    private static final String MENU_NAME_8 = "Register";
    private static final long MENU_ID_9 = 9;
    private static final String MENU_NAME_9 = "Network";
    String device_token="";
    SessionManager sessionManager;

    private final Handler mHandler = new Handler();
    private Drawable mDefaultBackground;
    private Uri mBackgroundURI;
    private Runnable mBackgroundTask;
    private BackgroundManager mBackgroundManager;

    private ArrayObjectAdapter mRowsAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareBackgroundManager();
        mBackgroundURI = Uri.parse(ServerURL.get_background);
        startBackgroundTimer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getActivity());
        setupUi();
        loadData();
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        getMainFragmentRegistry().registerFragment(PageRow.class,
                new MainFragmentFactory(mBackgroundManager));
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundTimer();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                device_token = instanceIdResult.getToken();
                sessionManager.saveFcmId(device_token);
                Log.d(TAG,"firebase "+device_token);
                try {
                    saveFcmId();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveFcmId() throws JSONException {
        JSONObject jBody = new JSONObject();
        jBody.put("fcm_id",device_token);
        jBody.put("device",Utils.deviceName(getActivity()));
        new ApiVolley(getActivity(), jBody, "post", ServerURL.post_fcmid,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                    }
                    @Override
                    public void onEmpty(String message) {
                    }
                    @Override
                    public void onFail(String message) {
                    }
                })
        );

    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mBackgroundTask);
        mBackgroundManager = null;
        super.onDestroy();
    }

    @Override
    public void onStop() {
        mBackgroundManager.release();
        super.onStop();
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background, null);
        mBackgroundTask = new UpdateBackgroundTask();
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private class UpdateBackgroundTask implements Runnable {

        @Override
        public void run() {
            if (mBackgroundURI != null) {
                updateBackground(mBackgroundURI.toString());
            }
        }
    }

    private void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(mDefaultBackground);

        Glide.with(getActivity())
                .asBitmap()
                .load(uri)
                .apply(options)
                .into(new SimpleTarget<Bitmap>(width, height) {
                    @Override
                    public void onResourceReady(
                            Bitmap resource,
                            Transition<? super Bitmap> transition) {
                        mBackgroundManager.setBitmap(resource);
                    }
                });
    }

    private void startBackgroundTimer() {
        mHandler.removeCallbacks(mBackgroundTask);
        mHandler.postDelayed(mBackgroundTask, BACKGROUND_UPDATE_DELAY);
    }

    private void setupUi() {
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        setBadgeDrawable(getResources().getDrawable(R.drawable.logo_fiber, null));
        // Set search icon color.
        setSearchAffordanceColor(ContextCompat.getColor(getActivity(), R.color.search_opaque));
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        prepareEntranceTransition();
    }

    private void loadData() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowsAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createRows();
                startEntranceTransition();
            }
        }, 2000);
    }

    private void createRows() {
        HeaderItem headerItem1 = new HeaderItem(MENU_ID_1, MENU_NAME_1);
        PageRow pageRow1 = new PageRow(headerItem1);
        mRowsAdapter.add(pageRow1);

        HeaderItem headerItem3 = new HeaderItem(MENU_ID_3, MENU_NAME_3);
        PageRow pageRow3 = new PageRow(headerItem3);
        mRowsAdapter.add(pageRow3);

        HeaderItem headerItem2 = new HeaderItem(MENU_ID_2, MENU_NAME_2);
        PageRow pageRow2 = new PageRow(headerItem2);
        mRowsAdapter.add(pageRow2);

        HeaderItem headerItem4 = new HeaderItem(MENU_ID_4, MENU_NAME_4);
        PageRow pageRow4 = new PageRow(headerItem4);
        mRowsAdapter.add(pageRow4);

        HeaderItem headerItem5 = new HeaderItem(MENU_ID_5, MENU_NAME_5);
        PageRow pageRow5 = new PageRow(headerItem5);
        mRowsAdapter.add(pageRow5);

        HeaderItem headerItem6 = new HeaderItem(MENU_ID_6, MENU_NAME_6);
        PageRow pageRow6 = new PageRow(headerItem6);
        mRowsAdapter.add(pageRow6);

        HeaderItem headerItem7 = new HeaderItem(MENU_ID_7, MENU_NAME_7);
        PageRow pageRow7 = new PageRow(headerItem7);
        mRowsAdapter.add(pageRow7);

//        HeaderItem headerItem8 = new HeaderItem(MENU_ID_8, MENU_NAME_8);
//        PageRow pageRow8 = new PageRow(headerItem8);
//        mRowsAdapter.add(pageRow8);

        HeaderItem headerItem9 = new HeaderItem(MENU_ID_9, MENU_NAME_9);
        PageRow pageRow9 = new PageRow(headerItem9);
        mRowsAdapter.add(pageRow9);
    }

    private static class MainFragmentFactory extends BrowseFragment.FragmentFactory {
        private final BackgroundManager mBackgroundManager;

        MainFragmentFactory(BackgroundManager backgroundManager) {
            this.mBackgroundManager = backgroundManager;
        }

        @Override
        public Fragment createFragment(Object rowObj) {
            Row row = (Row)rowObj;
            mBackgroundManager.setDrawable(null);
            if (row.getHeaderItem().getId() == MENU_ID_1) {
                return new HomeFragment();
            } else if (row.getHeaderItem().getId() == MENU_ID_2) {
                return new StreamingFragment();
            } else if (row.getHeaderItem().getId() == MENU_ID_3) {
                return new TvStreamingFragment();
            }
            else if (row.getHeaderItem().getId() == MENU_ID_4) {
                return new FavoriteFragment();
            }
            else if(row.getHeaderItem().getId() == MENU_ID_5){
                return new BillingFragment();
            }
            else if(row.getHeaderItem().getId() == MENU_ID_6){
                return new InboxFragment();
            }
            else if(row.getHeaderItem().getId() == MENU_ID_7){
                return new SettingsFragment();
            }
            else if(row.getHeaderItem().getId() == MENU_ID_8){
                return new RegisterFragment();
            }
            else if(row.getHeaderItem().getId() == MENU_ID_9){
                return new NetworkFragment();
            }


            throw new IllegalArgumentException(String.format("Invalid row %s", rowObj));
        }
    }

    // TODO HOME FRAGMENT
    public static class HomeFragment extends RowsFragment {
        private final ArrayObjectAdapter mRowsAdapter;
        private static final String TAG_HOME_FRAGMENT = "HomeFragment";
        private static final int PERMISSION_REQUEST_CODE = 200;
        SessionManager sessionManager;

        public HomeFragment() {
            mRowsAdapter = new ArrayObjectAdapter(new ShadowRowPresenterSelector());
            setAdapter(mRowsAdapter);

            setOnItemViewClickedListener(new OnItemViewClickedListener() {
                @Override
                public void onItemClicked(
                        Presenter.ViewHolder itemViewHolder,
                        Object item,
                        RowPresenter.ViewHolder rowViewHolder,
                        Row row) {
                    if(item instanceof DataModel){
                        if(((DataModel) item).getFlag().equals("1")){
                            DataModel model = (DataModel) item;
                            final List<String> installedPackages = Utils.getInstalledAppsPackageNameList(getActivity());

                            if(installedPackages.contains(model.getJsonMemberPackage())){
//                                Toast.makeText(getActivity(), "Installed", Toast.LENGTH_SHORT).show();
                                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(model.getJsonMemberPackage());
                                getActivity().startActivity( launchIntent );
                            }else {
                                if(model.getJsonMemberPackage().isEmpty()){
                                    if(model.getUrlPlaystore().isEmpty()){
                                        if(model.getUrlWeb().isEmpty()){
                                            Toast.makeText(getActivity(), "Paket tidak ditemukan !!..", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                                            httpIntent.setData(Uri.parse(model.getUrlWeb()));
                                            getActivity().startActivity(httpIntent);
                                        }
                                    }else{
                                        try {
                                            getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+(model.getJsonMemberPackage()))));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+model.getJsonMemberPackage())));
                                        }
                                    }
                                }else{
                                    try {
                                        getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+model.getJsonMemberPackage())));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+model.getJsonMemberPackage())));
                                    }
                                }
                            }
                        }else{
                            DataModel model = (DataModel) item;

                            final List<String> installedPackages = Utils.getInstalledAppsPackageNameList(getActivity());
                            if(installedPackages.contains(model.getLink())){
                                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(model.getLink());
                                getActivity().startActivity( launchIntent );
                            }else{
                                UpdateApp atualizaApp = new UpdateApp();
                                atualizaApp.setContext(getActivity());
                                atualizaApp.execute("https://admin.fiberstream.id/apk/nomaden04131.apk");
                            }
                        }
                    }
//                    else if(item instanceof SliderModel){
//                        getActivity().startActivity(new Intent(getActivity(), AppsActivity.class));
//                    }
                }
            });
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            sessionManager = new SessionManager(getActivity());
            loadDetailDevice();
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());

        }

        @Override
        public void onResume() {
            super.onResume();
        }

        private void loadDetailDevice(){
            JSONObject jBody = new JSONObject();
            try {
                jBody.put("fcm_id",sessionManager.getFcmid());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new ApiVolley(getActivity(), jBody, "post", ServerURL.url_profile_device,
                    new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                        @Override
                        public void onSuccess(String response, String message) {

                            TextPresenter textPresenter = new TextPresenter(getActivity());
                            final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(textPresenter);
                            try {
                                Log.d(TAG,">> "+response);
                                JSONObject object = new JSONObject(response);
                                String nama = object.getString("nama");
                                String result_nama = nama.substring(0, 1).toUpperCase() + nama.substring(1).toLowerCase();
                                listRowAdapter.add(new CustomModel("0",result_nama));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            HeaderItem header = new HeaderItem(0, "");
                            mRowsAdapter.add(new ListRow(header, listRowAdapter));
                            loadSlider();
                        }
                        @Override
                        public void onEmpty(String message) {
                            loadSlider();
                        }
                        @Override
                        public void onFail(String message) {
                            loadSlider();
                        }
                    })
            );
        }

        private void loadSlider(){

            new ApiVolley(getActivity(), new JSONObject(), "GET", ServerURL.get_slider,
                    new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                        @Override
                        public void onSuccess(String response, String message) {

                            Log.d(TAG_HOME_FRAGMENT, "onSuccess: "+response);
                            SliderPresenter headerSlider = new SliderPresenter();
                            try{
                                JSONArray jData = new JSONArray(response);

                                final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(headerSlider);
                                for(int i = 0; i < jData.length(); i++){
                                    JSONObject jo = jData.getJSONObject(i);
                                    listRowAdapter.add(new SliderModel(jo.getString("id"), jo.getString("image"), jo.getString("url")));
                                }
                                Log.d(TAG_HOME_FRAGMENT,String.valueOf(jData.length()));
                                if(jData.length() > 0){

                                    HeaderItem header = new HeaderItem(0, "");
                                    mRowsAdapter.add(new ListRow(header, listRowAdapter));
                                }
                            }
                            catch (JSONException e){
                                e.printStackTrace();
                            }

                            loadDataDashboard();
                        }

                        @Override
                        public void onEmpty(String message) {
                            Log.d(TAG_HOME_FRAGMENT, "onEmpty: " +message);
                            loadDataDashboard();
                        }

                        @Override
                        public void onFail(String message) {
                            Log.d(TAG_HOME_FRAGMENT, "onFail: "+ message);
                        }
                    })
            );
        }

        private void loadDataDashboard() {

            JSONObject obj = new JSONObject();
            try {
                obj.put("device",Utils.deviceName(getActivity()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new ApiVolley(getActivity(), obj, "POST", ServerURL.get_dashbord_apps,
                    new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                        @Override
                        public void onSuccess(String response, String message) {
                            Log.d(TAG_HOME_FRAGMENT,"onSuccess" + response);
                            String json = response;
                            DataRowModel[] rows = new Gson().fromJson(json, DataRowModel[].class);
                            for (DataRowModel row : rows) {
                                mRowsAdapter.add(createDashboardRow(row));
                            }
                        }

                        @Override
                        public void onEmpty(String message) {
                            Log.d(TAG_HOME_FRAGMENT,">>"+message);
                        }

                        @Override
                        public void onFail(String message) {
                            Log.d(TAG_HOME_FRAGMENT,">>"+message);
                        }
                    })
            );
        }

        private Row createDashboardRow(DataRowModel cardRow) {
            PresenterSelector presenterSelector = new MainPresenterSelector(getActivity());
            ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenterSelector);
            for (DataModel card : cardRow.getData()) {
                adapter.add(card);
            }

            HeaderItem headerItem = new HeaderItem(cardRow.getKategori());
            return new MainListRows(headerItem, adapter, cardRow);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted && cameraAccepted) {
                        UpdateApp updateApp = new UpdateApp();
                        updateApp.setContext(getActivity());
                        updateApp.execute("https://admin.fiberstream.id/apk/nomaden04131.apk");
                    }
                }
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        private boolean checkPermission() {
            int result = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);

            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        private void requestPermission() {
            ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        public class UpdateApp extends AsyncTask<String, Integer, String> {
            private ProgressDialog mPDialog;
            private Context mContext;

            void setContext(Activity context) {
                mContext = context;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPDialog = new ProgressDialog(mContext);
                        mPDialog.setMessage("Downloading NOMADEN IP TV...");
                        mPDialog.setIndeterminate(true);
                        mPDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mPDialog.setCancelable(false);
                        mPDialog.show();
                    }
                });
            }

            @Override
            protected String doInBackground(String... arg0) {
                try {

                    URL url = new URL(arg0[0]);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.setDoOutput(true);
                    c.connect();
                    int lenghtOfFile = c.getContentLength();

                    String PATH = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getAbsolutePath();
                    File file = new File(PATH);
                    boolean isCreate = file.mkdirs();
                    File outputFile = new File(file, "nomaden.apk");
                    if (outputFile.exists()) {
                        boolean isDelete = outputFile.delete();
                    }
                    FileOutputStream fos = new FileOutputStream(outputFile);

                    InputStream is = c.getInputStream();

                    byte[] buffer = new byte[1024];
                    int len1;
                    long total = 0;
                    while ((len1 = is.read(buffer)) != -1) {
                        total += len1;
                        fos.write(buffer, 0, len1);
                        publishProgress((int) ((total * 100) / lenghtOfFile));
                    }
                    fos.close();
                    is.close();
                    if (mPDialog != null)
                        mPDialog.dismiss();
                    installApk();
                } catch (Exception e) {
                    Log.e("UpdateAPP", "Update error! " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mPDialog != null)
                    mPDialog.show();

            }


            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                if (mPDialog != null) {
                    mPDialog.setIndeterminate(false);
                    mPDialog.setMax(100);
                    mPDialog.setProgress(values[0]);
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (mPDialog != null)
                    mPDialog.dismiss();
                if (result != null)
                    Toast.makeText(mContext, "Download error: " + result, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(mContext, "File Downloaded", Toast.LENGTH_SHORT).show();
            }


            private void installApk() {
                try {
                    String PATH = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getAbsolutePath();
                    File file = new File(PATH + "/nomaden.apk");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    if (Build.VERSION.SDK_INT >= 24) {
                        Uri downloaded_apk = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                        intent.setDataAndType(downloaded_apk, "application/vnd.android.package-archive");
                        List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            mContext.grantUriPermission(mContext.getApplicationContext().getPackageName() + ".provider", downloaded_apk, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    } else {
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // TODO STREAMING FRAGMENT
    public static class StreamingFragment extends GridFragment {
        private static final int COLUMNS = 4;
        private final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL;
        private ArrayObjectAdapter mAdapter;
        private List<KategoriModel> kategoriModels = new ArrayList<>();
        private static final String TAG_STREAMING_FRAGMENT = "StreamingFragment";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setupAdapter();
            loadData();
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
        }

        private void setupAdapter() {
            VerticalGridPresenter presenter = new VerticalGridPresenter(ZOOM_FACTOR);
            presenter.setNumberOfColumns(COLUMNS);
            setGridPresenter(presenter);

            CardPresenterSelector cardPresenter = null;
            cardPresenter = new CardPresenterSelector(getActivity());
            mAdapter = new ArrayObjectAdapter(cardPresenter);
            setAdapter(mAdapter);

            setOnItemViewClickedListener(new OnItemViewClickedListener() {
                @Override
                public void onItemClicked(
                        Presenter.ViewHolder itemViewHolder,
                        Object item,
                        RowPresenter.ViewHolder rowViewHolder,
                        Row row) {
                    if(item instanceof KategoriModel){
                        KategoriModel card = (KategoriModel) item;
                        Gson gson = new Gson();
                        Intent intent = new Intent(getActivity(), DetailKategoriActivity.class);
                        intent.putExtra("obj", gson.toJson(card));
                        startActivity(intent);
                    }
                }
            });
        }

        private void loadData() {
            JSONObject obj = new JSONObject();
            new ApiVolley(getActivity(), obj, "GET", ServerURL.get_kategori_streaming,
                    new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                        @Override
                        public void onSuccess(String response, String message) {
                            try{
                                JSONArray obj = new JSONArray(response);
                                Log.d(TAG_STREAMING_FRAGMENT,">>>>"+obj);
                                int i;
                                for(i = 0; i < obj.length(); i++){
                                    JSONObject o = obj.getJSONObject(i);
                                    KategoriModel k = new KategoriModel(
                                            o.getString("id"),
                                            o.getString("kategori"),
                                            o.getString("image"),
                                            o.getString("bg_image")
                                    );
                                    kategoriModels.add(k);
                                }
                                mAdapter.addAll(0,kategoriModels);
                            }
                            catch (JSONException e){
                                Log.d(TAG_STREAMING_FRAGMENT,">>"+e.getMessage());
                            }
                        }

                        @Override
                        public void onEmpty(String message) {
                            Log.d(TAG_STREAMING_FRAGMENT,">>"+message);
                        }

                        @Override
                        public void onFail(String message) {
                            Log.d(TAG_STREAMING_FRAGMENT,">>"+message);
                        }
                    })
            );
        }
    }

    // TODO TVSTREAMING FRAGMENT
    public static class TvStreamingFragment extends RowsFragment {
        private final ArrayObjectAdapter mRowsAdapter;
        private static final String TAG_TV_FRAGMENT = "TVStreamingFragment";
        private SessionManager sessionManager;
        private static final int PERMISSION_REQUEST_CODE = 200;

        public TvStreamingFragment() {
            mRowsAdapter = new ArrayObjectAdapter(new ShadowRowPresenterSelector());
            setAdapter(mRowsAdapter);

            setOnItemViewClickedListener(new OnItemViewClickedListener() {
                @Override
                public void onItemClicked(
                        Presenter.ViewHolder itemViewHolder,
                        Object item,
                        RowPresenter.ViewHolder rowViewHolder,
                        Row row) {
                    if(item instanceof ChannelModel){
                        ChannelModel model = (ChannelModel) item;

                        final List<String> installedPackages = Utils.getInstalledAppsPackageNameList(getActivity());
                        if(installedPackages.contains(model.getLink())){
                            Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(model.getLink());
                            getActivity().startActivity( launchIntent );
                        }else{
//                            if (checkPermission()) {
                                UpdateApp atualizaApp = new UpdateApp();
                                atualizaApp.setContext(getActivity());
                                atualizaApp.execute(ServerURL.get_apk_nomaden);
//                            } else {
//                                requestPermission();
//                            }
//                            Toast.makeText(getActivity(), "Maaf, Nomaden tidak tersedia di perangkat anda", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            sessionManager = new SessionManager(getActivity());
            loadKontenTv();
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
        }

        private void loadKontenTv() {

            JSONObject obj = new JSONObject();
            try {
                obj.put("type","all");
                obj.put("fcm_id",sessionManager.getFcmid());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG_TV_FRAGMENT,String.valueOf(obj));
            new ApiVolley(getActivity(), obj, "POST", ServerURL.get_channel_with_kategori,
                    new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                        @Override
                        public void onSuccess(String response, String message) {
                            Log.d(TAG_TV_FRAGMENT,response);
                            String json = response;
                            ChannelRowModel[] rows = new Gson().fromJson(json, ChannelRowModel[].class);
                            for (ChannelRowModel row : rows) {
                                mRowsAdapter.add(createCardRow(row));
                            }
                        }

                        @Override
                        public void onEmpty(String message) {
                            Log.d(TAG_TV_FRAGMENT,">>"+message);
                        }

                        @Override
                        public void onFail(String message) {
                            Log.d(TAG_TV_FRAGMENT,">>"+message);
                        }
                    })
            );
        }

        private Row createCardRow(ChannelRowModel cardRow) {
            PresenterSelector presenterSelector = new TvPresenterSelector(getActivity());
            ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenterSelector);
            for (ChannelModel card : cardRow.getChannel()) {
                adapter.add(card);
            }

            HeaderItem headerItem = new HeaderItem(cardRow.getKategori());
            return new TvListRows(headerItem, adapter, cardRow);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted && cameraAccepted) {
                        UpdateApp updateApp = new UpdateApp();
                        updateApp.setContext(getActivity());
                        updateApp.execute(ServerURL.get_apk_nomaden);
                    }
                }
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        private boolean checkPermission() {
            int result = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);

            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        private void requestPermission() {
            ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        public class UpdateApp extends AsyncTask<String, Integer, String> {
            private ProgressDialog mPDialog;
            private Context mContext;

            void setContext(Activity context) {
                mContext = context;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPDialog = new ProgressDialog(mContext);
                        mPDialog.setMessage("Downloading NOMADEN IP TV...");
                        mPDialog.setIndeterminate(true);
                        mPDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mPDialog.setCancelable(false);
                        mPDialog.show();
                    }
                });
            }

            @Override
            protected String doInBackground(String... arg0) {
                try {

                    URL url = new URL(arg0[0]);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.setDoOutput(true);
                    c.connect();
                    int lenghtOfFile = c.getContentLength();

                    String PATH = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getAbsolutePath();
                    File file = new File(PATH);
                    boolean isCreate = file.mkdirs();
                    File outputFile = new File(file, "nomaden.apk");
                    if (outputFile.exists()) {
                        boolean isDelete = outputFile.delete();
                    }
                    FileOutputStream fos = new FileOutputStream(outputFile);

                    InputStream is = c.getInputStream();

                    byte[] buffer = new byte[1024];
                    int len1;
                    long total = 0;
                    while ((len1 = is.read(buffer)) != -1) {
                        total += len1;
                        fos.write(buffer, 0, len1);
                        publishProgress((int) ((total * 100) / lenghtOfFile));
                    }
                    fos.close();
                    is.close();
                    if (mPDialog != null)
                        mPDialog.dismiss();
                    installApk();
                } catch (Exception e) {
                    Log.e("UpdateAPP", "Update error! " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mPDialog != null)
                    mPDialog.show();

            }


            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                if (mPDialog != null) {
                    mPDialog.setIndeterminate(false);
                    mPDialog.setMax(100);
                    mPDialog.setProgress(values[0]);
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (mPDialog != null)
                    mPDialog.dismiss();
                if (result != null)
                    Toast.makeText(mContext, "Download error: " + result, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(mContext, "File Downloaded", Toast.LENGTH_SHORT).show();
            }


            private void installApk() {
                try {
                    String PATH = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getAbsolutePath();
                    File file = new File(PATH + "/nomaden.apk");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    if (Build.VERSION.SDK_INT >= 24) {
                        Uri downloaded_apk = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                        intent.setDataAndType(downloaded_apk, "application/vnd.android.package-archive");
                        List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            mContext.grantUriPermission(mContext.getApplicationContext().getPackageName() + ".provider", downloaded_apk, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    } else {
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // TODO FAVORITE FRAGMENT
    public static class FavoriteFragment extends GridFragment {
        private static final int COLUMNS = 4;
        private final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL;
        private ArrayObjectAdapter mAdapter;
        private List<FavoriteModel> favoriteModels = new ArrayList<>();
        private static final String TAG_FAVORITE_FRAGMENT = "FavoriteFragment";
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setupAdapter();
            loadData();
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
        }

        private void setupAdapter() {
            VerticalGridPresenter presenter = new VerticalGridPresenter(ZOOM_FACTOR);
            presenter.setNumberOfColumns(COLUMNS);
            setGridPresenter(presenter);

            FavoritePresenterSelector cardPresenter = null;
            cardPresenter = new FavoritePresenterSelector(getActivity());
            mAdapter = new ArrayObjectAdapter(cardPresenter);
            setAdapter(mAdapter);

            setOnItemViewClickedListener(new OnItemViewClickedListener() {
                @Override
                public void onItemClicked(
                        Presenter.ViewHolder itemViewHolder,
                        Object item,
                        RowPresenter.ViewHolder rowViewHolder,
                        Row row) {
                    if(item instanceof FavoriteModel) {
                        final List<String> installedPackages = Utils.getInstalledAppsPackageNameList(getActivity());
                        if (((FavoriteModel) item).getUrlWeb().equals("0")) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCI5F5g_NNcKAQUn7umV9zxA")));
                        }else{
                            if (installedPackages.contains(((FavoriteModel) item).getJsonPackage())) {
                                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(((FavoriteModel) item).getJsonPackage());
                                getActivity().startActivity(launchIntent);
                            } else {
                                if (((FavoriteModel) item).getJsonPackage().isEmpty()) {
                                    if (((FavoriteModel) item).getUrlPlaystore().isEmpty()) {
                                        if (((FavoriteModel) item).getUrlWeb().isEmpty()) {
                                            Toast.makeText(getActivity(), "Paket tidak ditemukan !!..", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                                            httpIntent.setData(Uri.parse(((FavoriteModel) item).getUrlWeb()));
                                            getActivity().startActivity(httpIntent);
                                        }
                                    } else {
                                        try {
                                            getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ((FavoriteModel) item).getJsonPackage())));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + ((FavoriteModel) item).getJsonPackage())));
                                        }
                                    }
                                } else {
                                    try {
                                        getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ((FavoriteModel) item).getJsonPackage())));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + ((FavoriteModel) item).getJsonPackage())));
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }

        private void loadData() {
            JSONObject obj = new JSONObject();
            try {
                obj.put("device",Utils.deviceName(getActivity()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new ApiVolley(getActivity(), obj, "POST", ServerURL.get_favorite_streaming,
                    new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                        @Override
                        public void onSuccess(String response, String message) {
                            try{
                                JSONArray obj = new JSONArray(response);
                                Log.d(TAG_FAVORITE_FRAGMENT,">>>>"+obj);
                                int i;
                                for(i = 0; i < obj.length(); i++){
                                    JSONObject row = obj.getJSONObject(i);
                                    FavoriteModel k = new FavoriteModel(
                                            row.getString("id"),
                                            row.getString("title"),
                                            row.getString("icon"),
                                            row.getString("url"),
                                            row.getString("kategori"),
                                            row.getString("package"),
                                            row.getString("url_playstore"),
                                            row.getString("url_web")
                                    );
                                    favoriteModels.add(k);
                                }
                                mAdapter.addAll(0,favoriteModels);
                            }
                            catch (JSONException e){
                                Log.d(TAG_FAVORITE_FRAGMENT,">>"+e.getMessage());
                            }
                        }

                        @Override
                        public void onEmpty(String message) {
                            Log.d(TAG_FAVORITE_FRAGMENT,">>"+message);
                        }

                        @Override
                        public void onFail(String message) {
                            Log.d(TAG_FAVORITE_FRAGMENT,">>"+message);
                        }
                    })
            );
        }
    }

    // TODO BILLING FRAGMENT
    public static class BillingFragment extends Fragment implements MainFragmentAdapterProvider {
        private MainFragmentAdapter mMainFragmentAdapter = new MainFragmentAdapter(this);
        private WebView mWebview;

        @Override
        public MainFragmentAdapter getMainFragmentAdapter() {
            return mMainFragmentAdapter;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getMainFragmentAdapter().getFragmentHost().showTitleView(false);
        }

        @Override
        public View onCreateView(
                LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            FrameLayout root = new FrameLayout(getActivity());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            lp.setMarginStart(32);
            mWebview = new WebView(getActivity());
            mWebview.setWebViewClient(new WebViewClient());
            mWebview.getSettings().setJavaScriptEnabled(true);
            root.addView(mWebview, lp);
            return root;
        }

        @Override
        public void onResume() {
            super.onResume();
            mWebview.loadUrl("https://fiberstream.id/invoice.php");
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
        }
    }


    // TODO INBOX FRAGMENT
    public static class InboxFragment extends Fragment implements MainFragmentAdapterProvider {
        private MainFragmentAdapter mMainFragmentAdapter = new MainFragmentAdapter(this);
        private WebView mWebview;

        @Override
        public MainFragmentAdapter getMainFragmentAdapter() {
            return mMainFragmentAdapter;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getMainFragmentAdapter().getFragmentHost().showTitleView(false);
        }

        @Override
        public View onCreateView(
                LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            FrameLayout root = new FrameLayout(getActivity());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            lp.setMarginStart(32);
            mWebview = new WebView(getActivity());
            mWebview.setWebViewClient(new WebViewClient());
            mWebview.getSettings().setJavaScriptEnabled(true);
            root.addView(mWebview, lp);
            return root;
        }

        @Override
        public void onResume() {
            super.onResume();
            mWebview.loadUrl("https://fiberstream.id/stb/info");
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
        }
    }

    // TODO SETTINGS FRAGMENT
    public static class SettingsFragment extends RowsFragment {
        private final ArrayObjectAdapter mRowsAdapter;

        public SettingsFragment() {
            ListRowPresenter selector = new ListRowPresenter();
            selector.setNumRows(1);
            mRowsAdapter = new ArrayObjectAdapter(selector);
            setAdapter(mRowsAdapter);
            setOnItemViewClickedListener(new OnItemViewClickedListener() {
                @Override
                public void onItemClicked(Presenter.ViewHolder itemViewHolder,
                                          Object item,
                                          RowPresenter.ViewHolder rowViewHolder,
                                          Row row) {
                    if(item instanceof SettingModel){
                        SettingModel model = (SettingModel) item;
                        if(model.getKode().equals("settings")){
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else if(model.getKode().equals("wifi")){
                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else if(model.getKode().equals("display")){
                            Intent intentDisplaySetting = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
                            ResolveInfo resolveInfo = getActivity().getPackageManager().resolveActivity(intentDisplaySetting,0);

                            if(resolveInfo == null){
                                Toast.makeText(getActivity(),
                                        "Not Support!",
                                        Toast.LENGTH_LONG).show();
                            }else{
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            }, 200);
        }

        private void loadData() {
            if (isAdded()) {
                String json = Utils.inputStreamToString(getResources().openRawResource(
                        R.raw.icon_settings));
                SettingRowModel cardRow = new Gson().fromJson(json, SettingRowModel.class);
                mRowsAdapter.add(createCardRow(cardRow));
                getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
            }
        }

        private ListRow createCardRow(SettingRowModel cardRow) {
            SettingsIconPresenter iconCardPresenter = new SettingsIconPresenter(getActivity());
            ArrayObjectAdapter adapter = new ArrayObjectAdapter(iconCardPresenter);
            for(SettingModel card : cardRow.getCards()) {
                adapter.add(card);
            }

            HeaderItem headerItem = new HeaderItem(cardRow.getTitle());
            return new SettingsListRow(headerItem, adapter, cardRow);
        }
    }

    // TODO REGISTER FRAGMENT
    public static class RegisterFragment extends Fragment implements MainFragmentAdapterProvider {
        private MainFragmentAdapter mMainFragmentAdapter = new MainFragmentAdapter(this);
        private WebView mWebview;
        SessionManager sessionManager;
        private String TAG = "RegisterFragment";

        @Override
        public MainFragmentAdapter getMainFragmentAdapter() {
            return mMainFragmentAdapter;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getMainFragmentAdapter().getFragmentHost().showTitleView(false);
        }

        @Override
        public View onCreateView(
                LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            sessionManager = new SessionManager(getActivity());
            FrameLayout root = new FrameLayout(getActivity());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            lp.setMarginStart(32);
            mWebview = new WebView(getActivity());
            mWebview.setWebViewClient(new WebViewClient());
            mWebview.getSettings().setJavaScriptEnabled(true);
            root.addView(mWebview, lp);
            return root;
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.d(TAG,sessionManager.getFcmid());
            mWebview.loadUrl("https://fiberstream.net.id/stb/register?fcm_id="+sessionManager.getFcmid());
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
        }
    }

    // TODO NETWORK FRAGMENT
    public static class NetworkFragment extends Fragment implements MainFragmentAdapterProvider {
        private MainFragmentAdapter mMainFragmentAdapter = new MainFragmentAdapter(this);
        private WebView mWebview;
        SessionManager sessionManager;
        private String TAG = "NetworkFragment";

        @Override
        public MainFragmentAdapter getMainFragmentAdapter() {
            return mMainFragmentAdapter;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getMainFragmentAdapter().getFragmentHost().showTitleView(false);
        }

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public View onCreateView(
                LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            sessionManager = new SessionManager(getActivity());
            FrameLayout root = new FrameLayout(getActivity());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            lp.setMarginStart(32);
            mWebview = new WebView(getActivity());
            mWebview.setWebViewClient(new WebViewClient());
            mWebview.getSettings().setJavaScriptEnabled(true);
            root.addView(mWebview, lp);
            return root;
        }

        @Override
        public void onResume() {
            super.onResume();

            // get ip address
            String ip_address = Utils.getIPAddress(true);

            // get strength wifi
            String wifi_signal ="";
            ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if(activeNetwork != null) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    @SuppressLint("WifiManagerLeak")
                    WifiManager wifiManager = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    int level = wifiInfo.getRssi();
                    if (level <= 0 && level >= -50) {
                        //Best signal
                        Log.d(TAG,"Best signal");
                        wifi_signal = "Excellent ("+level+" dBm)";
                    } else if (level < -50 && level >= -65) {
                        //Good signal
                        Log.d(TAG,"Good signal");
                        wifi_signal = "Good ("+level+" dBm)";
                    } else if (level < -65) {
                        //Low signal
                        Log.d(TAG, "Weak");
                        wifi_signal = "Weak ("+level+" dBm)";
                    }
                } else {
                    wifi_signal = "Not Available";
                }
            }

            // ping ke google.com
            String ping_google = Utils.runSystemCommand("ping google.com");
            String[] log_ping = ping_google.split("=");
            String status_ping_google="";
            if(log_ping[1].equals("1 ttl")){
                status_ping_google ="Normal ("+log_ping[3]+")";
            }else{
                status_ping_google = "Fail";
            }

            // ping ke ip gateway
            String ping_gateway = Utils.runSystemCommand("ping "+getIpGateway());
            String[] log_ping_gateway = ping_gateway.split("=");
            String status_ping_gateway="";
            if(log_ping_gateway[1].equals("1 ttl")){
                status_ping_gateway ="Normal ("+log_ping_gateway[3]+")";
            }else{
                status_ping_gateway = "Fail";
            }

            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
            mWebview.loadUrl(ServerURL.home_url+"api/main/network?ip="+ip_address+"&wifi_signal="+wifi_signal+"&ping_google="+status_ping_google+"&ping_gateway="+status_ping_gateway);
        }

        private String getIpGateway(){
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Service.CONNECTIVITY_SERVICE);

            Log.i("myNetworkType: ", connectivityManager.getActiveNetworkInfo().getTypeName());
            @SuppressLint("WifiManagerLeak")
            WifiManager wifiManager= (WifiManager) getActivity().getSystemService(getActivity().WIFI_SERVICE);
            String ip = "";
            if(connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI) {
                Log.i(TAG, "myType wifi");
                Log.i(TAG, "info" +ip);
                DhcpInfo d =wifiManager.getDhcpInfo();
                ip = String.valueOf(Utils.intToInet(d.gateway)).replace("/","");
            }else if(connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_ETHERNET){
                Log.i(TAG, "ethernet");
                ip = connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getRoutes().toString();
            }else{
                Log.i(TAG, "other");
                String[] ip_gateway = connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getRoutes().toString().split(" ");
                ip = ip_gateway[2];
            }
            return ip;
        }
    }
}
