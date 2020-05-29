package com.fiberstream.tv.app.apps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
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
import androidx.leanback.widget.PresenterSelector;
import androidx.leanback.widget.VerticalGridPresenter;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fiberstream.tv.R;
import com.fiberstream.tv.app.MainFragment;
import com.fiberstream.tv.app.apps.model.AppsModel;
import com.fiberstream.tv.app.apps.presenter.AppsPresenter;
import com.fiberstream.tv.app.streaming.detail.presenter.DetailKategoriPresenter;
import com.fiberstream.tv.app.streaming.model.KategoriModel;
import com.fiberstream.tv.app.streaming.model.StreamingModel;
import com.fiberstream.tv.utils.ServerURL;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.fiberstream.tv.utils.Utils.BACKGROUND_UPDATE_DELAY;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppsFragment extends VerticalGridFragment {

    private static final int COLUMNS = 4;
    private static final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM;

    private ArrayObjectAdapter mAdapter;
    private final static  String TAG = "AppsFragment";
    private List<AppsModel> appsModels = new ArrayList<>();
    private final Handler mHandler = new Handler();
    private Drawable mDefaultBackground;
    private Uri mBackgroundURI;
    private DisplayMetrics mMetrics;
    private Runnable mBackgroundTask;
    private BackgroundManager mBackgroundManager;

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
        setTitle("Apps Drawer");
        setupRowAdapter();
    }


    private void setupRowAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(ZOOM_FACTOR);
        gridPresenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(gridPresenter);

        PresenterSelector cardPresenterSelector = new AppsPresenter(getActivity());
        mAdapter = new ArrayObjectAdapter(cardPresenterSelector);
        setAdapter(mAdapter);

        prepareEntranceTransition();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadInstalledApps();
                startEntranceTransition();
            }
        }, 100);
    }

    private void loadInstalledApps() {
        List<PackageInfo> packs = getActivity().getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!isSystemPackage(p))) {
                String appName = p.applicationInfo.loadLabel(getActivity().getPackageManager()).toString();
                String packages = p.applicationInfo.packageName;
                Drawable icon = p.applicationInfo.loadIcon(getActivity().getPackageManager());
                Log.d(TAG, String.valueOf(icon));
                appsModels.add(new AppsModel(appName, packages, icon));
            }
        }
        mAdapter.addAll(0, appsModels);
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
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

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundTimer();
    }

}
