package com.fiberstream.tv.app.streaming.detail.presenter;

import android.content.Context;
import android.view.ContextThemeWrapper;

import androidx.leanback.widget.ImageCardView;

import com.bumptech.glide.Glide;
import com.fiberstream.tv.R;
import com.fiberstream.tv.app.streaming.model.StreamingModel;
import com.fiberstream.tv.app.streaming.presenters.AbstractCardPresenter;

public class CardDetailKategoriPresenter extends AbstractCardDetailKategoriPresenter<ImageCardView> {

    public CardDetailKategoriPresenter(Context context, int cardThemeResId) {
        super(new ContextThemeWrapper(context, cardThemeResId));
    }

    public CardDetailKategoriPresenter(Context context) {
        this(context, R.style.DefaultCardTheme);
    }

    @Override
    protected ImageCardView onCreateView() {
        ImageCardView imageCardView = new ImageCardView(getContext());
        return imageCardView;
    }

    @Override
    public void onBindViewHolder(StreamingModel card, ImageCardView cardView) {

        cardView.setTag(card);
        cardView.setTitleText(card.getTitle());
        Glide.with(getContext())
                .asBitmap()
                .load(card.getUrlImage())
                .into(cardView.getMainImageView());
    }
}
