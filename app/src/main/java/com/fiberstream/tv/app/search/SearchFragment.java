package com.fiberstream.tv.app.search;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.leanback.app.BackgroundManager;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.SpeechRecognitionCallback;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fiberstream.tv.R;
import com.fiberstream.tv.app.search.presenter.CardSearchPresenter;
import com.fiberstream.tv.utils.ServerURL;
import com.fiberstream.tv.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.AppRequestCallback;

import static com.fiberstream.tv.utils.Utils.BACKGROUND_UPDATE_DELAY;

public class SearchFragment extends androidx.leanback.app.SearchFragment
        implements androidx.leanback.app.SearchFragment.SearchResultProvider {

    private static final String TAG = SearchFragment.class.getSimpleName();

    private static final int REQUEST_SPEECH = 0x00000010;
    private static final long SEARCH_DELAY_MS = 1000L;

    private ArrayObjectAdapter mRowsAdapter;

    private final Handler mHandler = new Handler();
    private final Runnable mDelayedLoad = new Runnable() {
        @Override
        public void run() {
            getDataStream();
        }
    };
    private String mQuery;
    private List<KontenStreaming> streaming = new ArrayList<>();


    private final Handler handler = new Handler();
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Uri mBackgroundURI;
    private Runnable mBackgroundTask;
    private BackgroundManager mBackgroundManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        prepareBackgroundManager();
        mBackgroundURI = Uri.parse(ServerURL.get_background);
        startBackgroundTimer();

        setSearchResultProvider(this);
        setOnItemViewClickedListener(new ItemViewClickedListener());
        if (!Utils.hasPermission(getActivity(), Manifest.permission.RECORD_AUDIO)) {
            Log.v(TAG, "no permission RECORD_AUDIO");
            // SpeechRecognitionCallback is not required and if not provided recognition will be handled
            // using internal speech recognizer, in which case you must have RECORD_AUDIO permission
            setSpeechRecognitionCallback(new SpeechRecognitionCallback() {
                @Override
                public void recognizeSpeech() {
                    Log.v(TAG, "recognizeSpeech");
                    try {
                        startActivityForResult(getRecognizerIntent(), REQUEST_SPEECH);
                    } catch (ActivityNotFoundException e) {
                        Log.e(TAG, "Cannot find activity for speech recognizer", e);
                    }
                }
            });
        }
    }

    public boolean hasResults() {
        return mRowsAdapter.size() > 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundTimer();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult requestCode=" + requestCode +
                " resultCode=" + resultCode +
                " data=" + data);

        switch (requestCode) {
            case REQUEST_SPEECH:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        setSearchQuery(data, true);
                        break;
                    case RecognizerIntent.RESULT_CLIENT_ERROR:
                        Log.w(TAG, Integer.toString(requestCode));
                }
        }
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        Log.d(TAG, "getResultsAdapter");
        // mRowsAdapter (Search result) has prepared in loadRows method
        return mRowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery){
        Log.i(TAG, String.format("Search Query Text Change %s", newQuery));
        loadQueryWithDelay(newQuery, SEARCH_DELAY_MS);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i(TAG, String.format("Search Query Text Submit %s", query));
        // No need to delay(wait) loadQuery, since the query typing has completed.
        loadQueryWithDelay(query, 0);
        return true;
    }

    /**
     * Starts {@link #loadRows()} method after delay.
     * It also cancels previously registered task if it has not yet executed.
     * @param query the word to be searched
     * @param delay the time to wait until loadRows will be executed (milliseconds).
     */
    private void loadQueryWithDelay(String query, long delay) {
        mHandler.removeCallbacks(mDelayedLoad);
        if (!TextUtils.isEmpty(query) && !query.equals("nil")) {
            mQuery = query;
            mHandler.postDelayed(mDelayedLoad, delay);
        }
    }

    /**
     * Searches query specified by mQuery, and sets the result to mRowsAdapter.
     */

    private void getDataStream(){

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("device",Utils.deviceName(getActivity()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ApiVolley(getActivity(), jBody, "POST", ServerURL.get_all_konten,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {

                        streaming.clear();
                        try {
                            JSONArray arr = new JSONArray(response);
                            for (int j =0; j<arr.length(); j++){
                                JSONObject o = arr.getJSONObject(j);
                                KontenStreaming k = new KontenStreaming(
                                        o.getString("id"),
                                        o.getString("id_kategori"),
                                        o.getString("nama"),
                                        o.getString("link"),
                                        o.getString("icon"),
                                        o.getString("package"),
                                        o.getString("url_playstore"),
                                        o.getString("url_web"),
                                        o.getString("flag")
                                );
                                streaming.add(k);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        loadRows();
                    }

                    @Override
                    public void onEmpty(String message) {

                        streaming.clear();
                        loadRows();
                    }

                    @Override
                    public void onFail(String message) {

                        streaming.clear();
                        loadRows();
                    }
                })
        );
    }
    private void loadRows() {
        // offload processing from the UI thread
        new AsyncTask<String, Void, ListRow>() {
            private final String query = mQuery;

            @Override
            protected void onPreExecute() {
                mRowsAdapter.clear();
            }

            @Override
            protected ListRow doInBackground(String... params) {

                final List<KontenStreaming> result = new ArrayList<>();
                for (KontenStreaming movie : streaming) {
                    // Main logic of search is here.
                    // Just check that "query" is contained in Title or Description or not. (NOTE: excluded studio information here)
                    if (movie.getNama().toLowerCase().contains(query.toLowerCase())) {
                        result.add(movie);
                    }
                }

                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardSearchPresenter());
                listRowAdapter.addAll(0, result);
                HeaderItem header = new HeaderItem("Search Results");
                return new ListRow(header, listRowAdapter);
            }

            @Override
            protected void onPostExecute(ListRow listRow) {
                mRowsAdapter.add(listRow);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof KontenStreaming) {
                KontenStreaming movie = (KontenStreaming) item;
                if(movie.getFlag().equals("0")){
                    final List<String> installedPackages = Utils.getInstalledAppsPackageNameList(getContext());
                    if(installedPackages.contains(movie.getLink())){
                        Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage(movie.getLink());
                        getContext().startActivity( launchIntent );
                    }else{
                        Toast.makeText(getActivity(), "Maaf, Nomaden tidak tersedia di perangkat anda", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    final List<String> installedPackages = Utils.getInstalledAppsPackageNameList(getContext());

                    if(installedPackages.contains(movie.getJsonMemberPackage())){
                        Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage(movie.getJsonMemberPackage());
                        getContext().startActivity( launchIntent );
                    }else {
                        if(movie.getJsonMemberPackage().isEmpty()){
                            if(movie.getUrlPlaystore().isEmpty()){
                                if(movie.getUrlWeb().isEmpty()){
                                    Toast.makeText(getContext(), "Paket tidak ditemukan !!..", Toast.LENGTH_SHORT).show();
                                }else{
                                    Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                                    httpIntent.setData(Uri.parse(movie.getUrlWeb()));
                                    getContext().startActivity(httpIntent);
                                }
                            }else{
                                try {
                                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+(movie.getJsonMemberPackage()))));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+movie.getJsonMemberPackage())));
                                }
                            }
                        }else{
                            try {
                                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+movie.getJsonMemberPackage())));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+movie.getJsonMemberPackage())));
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT)
                        .show();
            }
        }
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
        handler.removeCallbacks(mBackgroundTask);
        handler.postDelayed(mBackgroundTask, BACKGROUND_UPDATE_DELAY);
    }

}