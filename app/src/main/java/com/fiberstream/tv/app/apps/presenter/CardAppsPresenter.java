package com.fiberstream.tv.app.apps.presenter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.ImageCardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fiberstream.tv.R;
import com.fiberstream.tv.app.apps.model.AppsModel;
import com.fiberstream.tv.app.streaming.detail.presenter.AbstractCardDetailKategoriPresenter;
import com.fiberstream.tv.app.streaming.model.StreamingModel;

public class CardAppsPresenter extends AbstractCardAppsPresenter<ImageCardView> {

    public CardAppsPresenter(Context context, int cardThemeResId) {
        super(new ContextThemeWrapper(context, cardThemeResId));
    }

    public CardAppsPresenter(Context context) {
        this(context, R.style.DefaultCardTheme);
    }

    @Override
    protected ImageCardView onCreateView() {
        ImageCardView imageCardView = new ImageCardView(getContext());
        return imageCardView;
    }

    @Override
    public void onBindViewHolder(AppsModel card, final ImageCardView cardView) {

        cardView.setTag(card);
        cardView.setTitleText(card.getNama());
//        cardView.getMainImageView().setImageDrawable(card.get(position).getIcon());
        try {
            Drawable icon = getContext().getPackageManager().getApplicationIcon(card.getPaket());
            cardView.getMainImageView().setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        Glide.with(getContext())
//                .asBitmap()
//                .load(card.getIcon())
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        cardView.getMainImageView().setImageBitmap(resource);
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                    }
//                });
//                .into(cardView.getMainImageView());
    }

}

