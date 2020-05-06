package com.fiberstream.tv.app;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fiberstream.tv.R;
import com.fiberstream.tv.app.favorite.model.FavoriteModel;
import com.fiberstream.tv.app.favorite.presenter.FavoritePresenterSelector;
import com.fiberstream.tv.app.main.MainListRows;
import com.fiberstream.tv.app.main.model.DataModel;
import com.fiberstream.tv.app.main.model.DataRowModel;
import com.fiberstream.tv.app.main.model.SliderModel;
import com.fiberstream.tv.app.main.presenter.MainPresenterSelector;
import com.fiberstream.tv.app.main.presenter.SliderPresenter;
import com.fiberstream.tv.app.search.SearchActivity;
import com.fiberstream.tv.app.streaming.detail.DetailKategoriActivity;
import com.fiberstream.tv.app.streaming.detail.DetailKategoriFragment;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.AppRequestCallback;
import co.id.gmedia.coremodul.SessionManager;

import static com.fiberstream.tv.utils.Utils.BACKGROUND_UPDATE_DELAY;

public class MainFragment extends BrowseFragment {
    private static final String TAG = "MainFragment";
    public static DisplayMetrics mMetrics;

    private static final long MENU_ID_1 = 1;
    private static final String MENU_NAME_1 = "Home";
    private static final long MENU_ID_2 = 2;
    private static final String MENU_NAME_2 = "Categories";
    private static final long MENU_ID_3 = 3;
    private static final String MENU_NAME_3 = "IP TV";
    private static final long MENU_ID_4 = 4;
    private static final String MENU_NAME_4 = "Favorite";
    private static final long MENU_ID_5 = 5;
    private static final String MENU_NAME_5 = "Billing";
    private static final long MENU_ID_6 = 6;
    private static final String MENU_NAME_6 = "Inbox";
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
                Log.d(TAG,">>"+device_token);
                try {
                    saveFcmId();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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

        Glide.with(getContext())
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

    private void saveFcmId() throws JSONException {
        JSONObject jBody = new JSONObject();
        jBody.put("fcm_id",device_token);
        new ApiVolley(getContext(), jBody, "post", ServerURL.post_fcmid,
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
    private void setupUi() {
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(getResources().getColor(R.color.fastlane_background));
//        setTitle("Fiberstream");
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

        HeaderItem headerItem2 = new HeaderItem(MENU_ID_2, MENU_NAME_2);
        PageRow pageRow2 = new PageRow(headerItem2);
        mRowsAdapter.add(pageRow2);

        HeaderItem headerItem3 = new HeaderItem(MENU_ID_3, MENU_NAME_3);
        PageRow pageRow3 = new PageRow(headerItem3);
        mRowsAdapter.add(pageRow3);

        HeaderItem headerItem4 = new HeaderItem(MENU_ID_4, MENU_NAME_4);
        PageRow pageRow4 = new PageRow(headerItem4);
        mRowsAdapter.add(pageRow4);

        HeaderItem headerItem5 = new HeaderItem(MENU_ID_5, MENU_NAME_5);
        PageRow pageRow5 = new PageRow(headerItem5);
        mRowsAdapter.add(pageRow5);

        HeaderItem headerItem6 = new HeaderItem(MENU_ID_6, MENU_NAME_6);
        PageRow pageRow6 = new PageRow(headerItem6);
        mRowsAdapter.add(pageRow6);
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

            throw new IllegalArgumentException(String.format("Invalid row %s", rowObj));
        }
    }

    // TODO HOME FRAGMENT
    public static class HomeFragment extends RowsFragment {
        private final ArrayObjectAdapter mRowsAdapter;
        private static final String TAG_HOME_FRAGMENT = "HomeFragment";
        private SessionManager sessionManager;

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
                            final List<String> installedPackages = Utils.getInstalledAppsPackageNameList(getContext());

                            if(installedPackages.contains(model.getJsonMemberPackage())){
                                Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage(model.getJsonMemberPackage());
                                getContext().startActivity( launchIntent );
                            }else {
                                if(model.getJsonMemberPackage().isEmpty()){
                                    if(model.getUrlPlaystore().isEmpty()){
                                        if(model.getUrlWeb().isEmpty()){
                                            Toast.makeText(getContext(), "Paket tidak ditemukan !!..", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                                            httpIntent.setData(Uri.parse(model.getUrlWeb()));
                                            getContext().startActivity(httpIntent);
                                        }
                                    }else{
                                        try {
                                            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+(model.getJsonMemberPackage()))));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+model.getJsonMemberPackage())));
                                        }
                                    }
                                }else{
                                    try {
                                        getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+model.getJsonMemberPackage())));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+model.getJsonMemberPackage())));
                                    }
                                }
                            }
                        }else{
                            DataModel model = (DataModel) item;

                            final List<String> installedPackages = Utils.getInstalledAppsPackageNameList(getContext());
                            if(installedPackages.contains(model.getLink())){
                                Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage(model.getLink());
                                getContext().startActivity( launchIntent );
                            }else{
                                Toast.makeText(getActivity(), "Maaf, Nomaden tidak tersedia di perangkat anda", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            sessionManager = new SessionManager(getActivity());
            loadSlider();
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
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
                        Intent intent = new Intent(getContext(), DetailKategoriActivity.class);
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

                        final List<String> installedPackages = Utils.getInstalledAppsPackageNameList(getContext());
                        if(installedPackages.contains(model.getLink())){
                            Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage(model.getLink());
                            getContext().startActivity( launchIntent );
                        }else{
                            Toast.makeText(getActivity(), "Maaf, Nomaden tidak tersedia di perangkat anda", Toast.LENGTH_SHORT).show();
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
                    if(item instanceof FavoriteModel){
                        final List<String> installedPackages = Utils.getInstalledAppsPackageNameList(getContext());

                        if(installedPackages.contains(((FavoriteModel) item).getJsonPackage())){
                            Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage(((FavoriteModel) item).getJsonPackage());
                            getContext().startActivity( launchIntent );
                        }else {
                            if(((FavoriteModel) item).getJsonPackage().isEmpty()){
                                if(((FavoriteModel) item).getUrlPlaystore().isEmpty()){
                                    if(((FavoriteModel) item).getUrlWeb().isEmpty()){
                                        Toast.makeText(getContext(), "Paket tidak ditemukan !!..", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                                        httpIntent.setData(Uri.parse(((FavoriteModel) item).getUrlWeb()));
                                        getContext().startActivity(httpIntent);
                                    }
                                }else{
                                    try {
                                        getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+((FavoriteModel) item).getJsonPackage())));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+((FavoriteModel) item).getJsonPackage())));
                                    }
                                }
                            }else{
                                try {
                                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+((FavoriteModel) item).getJsonPackage())));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+((FavoriteModel) item).getJsonPackage())));
                                }
                            }
                        }
                    }
                }
            });
        }

        private void loadData() {
            JSONObject obj = new JSONObject();
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
            mWebview.loadUrl("https://fiberstream.id/inbox.php");
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
        }
    }

}
