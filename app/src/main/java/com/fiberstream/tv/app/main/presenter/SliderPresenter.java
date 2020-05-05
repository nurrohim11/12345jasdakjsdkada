package com.fiberstream.tv.app.main.presenter;

import android.transition.Slide;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.leanback.widget.Presenter;

import com.bumptech.glide.Glide;
import com.fiberstream.tv.app.MainFragment;
import com.fiberstream.tv.app.main.model.SliderModel;


public class SliderPresenter extends Presenter {
    private static final String TAG = "HeaderSlider";

    private static final int IMG_WIDTH = MainFragment.mMetrics.widthPixels * 60 / 100;
    private static final int IMG_HEIGHT = MainFragment.mMetrics.heightPixels * 60 / 100;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        Log.d(TAG, "onCreateViewHolder");

        ImageView imageView = new ImageView(parent.getContext());

        imageView.setFocusable(true);
        imageView.setFocusableInTouchMode(true);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {

        SliderModel selected = (SliderModel) item;

        ImageView imageView = (ImageView) viewHolder.view;
        Log.d(TAG, "onBindViewHolder");

        imageView.getLayoutParams().height = IMG_HEIGHT;
        imageView.getLayoutParams().width = IMG_WIDTH;
        imageView.requestLayout();
        Glide.with(viewHolder.view.getContext())
                .load(selected.getUrl())
                .into(imageView);
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

        Log.d(TAG, "onUnbindViewHolder");
        ImageView imageView = (ImageView) viewHolder.view;
        imageView.setImageDrawable(null);
    }
}
