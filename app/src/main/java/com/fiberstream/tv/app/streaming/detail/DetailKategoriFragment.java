package com.fiberstream.tv.app.streaming.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.VerticalGridFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.VerticalGridPresenter;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fiberstream.tv.R;
import com.fiberstream.tv.app.favorite.model.FavoriteModel;
import com.fiberstream.tv.app.streaming.detail.presenter.CardDetailKategoriPresenter;
import com.fiberstream.tv.app.streaming.detail.presenter.DetailKategoriPresenter;
import com.fiberstream.tv.app.streaming.model.KategoriModel;
import com.fiberstream.tv.app.streaming.model.StreamingModel;
import com.fiberstream.tv.app.streaming.presenters.CardPresenterSelector;
import com.fiberstream.tv.app.tv.model.ChannelRowModel;
import com.fiberstream.tv.utils.PicassoBackgroundManager;
import com.fiberstream.tv.utils.ServerURL;
import com.fiberstream.tv.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.AppRequestCallback;

import static com.fiberstream.tv.utils.Utils.BACKGROUND_UPDATE_DELAY;

public class DetailKategoriFragment extends VerticalGridFragment {

    private static final int COLUMNS = 4;
    private static final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM;

    private ArrayObjectAdapter mAdapter;
    private final static  String TAG_DETAIL_KATEGORI = "DetailKategori";
    private List<StreamingModel> streamingModels = new ArrayList<>();
    private KategoriModel kategoriModel;

    private final Handler mHandler = new Handler();
    private Drawable mDefaultBackground;
    private Uri mBackgroundURI;
    private DisplayMetrics mMetrics;
    private Runnable mBackgroundTask;
    private BackgroundManager mBackgroundManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Gson gson = new Gson();
        String strObj = getActivity().getIntent().getStringExtra("obj");
        kategoriModel = gson.fromJson(strObj, KategoriModel.class);
        prepareBackgroundManager();
        mBackgroundURI = Uri.parse(kategoriModel.getBgImageUrl());
        startBackgroundTimer();
                setTitle(kategoriModel.getNama()+" Category");
        setupRowAdapter();
        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                if(item instanceof StreamingModel){
                    final List<String> installedPackages = Utils.getInstalledAppsPackageNameList(getContext());

                    if(installedPackages.contains(((StreamingModel) item).getJsonPackage())){
                        Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage(((StreamingModel) item).getJsonPackage());
                        getContext().startActivity( launchIntent );
                    }else {
                        if(((StreamingModel) item).getJsonPackage().isEmpty()){
                            if(((StreamingModel) item).getUrlPlaystore().isEmpty()){
                                if(((StreamingModel) item).getUrlWeb().isEmpty()){
                                    Toast.makeText(getContext(), "Paket tidak ditemukan !!..", Toast.LENGTH_SHORT).show();
                                }else{
                                    Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                                    httpIntent.setData(Uri.parse(((FavoriteModel) item).getUrlWeb()));
                                    getContext().startActivity(httpIntent);
                                }
                            }else{
                                try {
                                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+((StreamingModel) item).getJsonPackage())));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+((StreamingModel) item).getJsonPackage())));
                                }
                            }
                        }else{
                            try {
                                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+((StreamingModel) item).getJsonPackage())));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+((StreamingModel) item).getJsonPackage())));
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundTimer();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setupRowAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(ZOOM_FACTOR);
        gridPresenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(gridPresenter);

        PresenterSelector cardPresenterSelector = new DetailKategoriPresenter(getActivity());
        mAdapter = new ArrayObjectAdapter(cardPresenterSelector);
        setAdapter(mAdapter);

        prepareEntranceTransition();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadKontenStreaming();
                startEntranceTransition();
            }
        }, 1000);
    }

    private void loadKontenStreaming(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("device","tv");
            obj.put("type","");
            obj.put("kategori",kategoriModel.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ApiVolley(getActivity(), obj, "POST", ServerURL.get_streaming_by_kategori,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        Log.d(TAG_DETAIL_KATEGORI,response);
                        try {
                            JSONArray res = new JSONArray(response);
                            for (int i =0; i<res.length(); i++){
                                JSONObject row = res.getJSONObject(i);
                                StreamingModel s = new StreamingModel(
                                        row.getString("id"),
                                        row.getString("title"),
                                        row.getString("icon"),
                                        row.getString("url"),
                                        row.getString("kategori"),
                                        row.getString("package"),
                                        row.getString("url_playstore"),
                                        row.getString("url_web")
                                );
                                streamingModels.add(s);
                            }

                            mAdapter.addAll(0, streamingModels);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onEmpty(String message) {
                        Log.d(TAG_DETAIL_KATEGORI,">>"+message);
                    }

                    @Override
                    public void onFail(String message) {
                        Log.d(TAG_DETAIL_KATEGORI,">>"+message);
                    }
                })
        );
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
}
