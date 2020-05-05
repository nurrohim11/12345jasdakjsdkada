package com.fiberstream.tv.app.favorite.presenter;

import android.content.Context;
import android.view.ContextThemeWrapper;

import androidx.leanback.widget.ImageCardView;

import com.bumptech.glide.Glide;
import com.fiberstream.tv.R;
import com.fiberstream.tv.app.favorite.model.FavoriteModel;
import com.fiberstream.tv.app.streaming.model.KategoriModel;
import com.fiberstream.tv.app.streaming.presenters.AbstractCardPresenter;

public class ImageFavoriteViewPresenter extends AbstractFavoritePresenter<ImageCardView> {

    public ImageFavoriteViewPresenter(Context context, int cardThemeResId) {
        super(new ContextThemeWrapper(context, cardThemeResId));
    }

    public ImageFavoriteViewPresenter(Context context) {
        this(context, R.style.DefaultCardTheme);
    }

    @Override
    protected ImageCardView onCreateView() {
        ImageCardView imageCardView = new ImageCardView(getContext());
//        imageCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "Clicked on ImageCardView", Toast.LENGTH_SHORT).show();
//            }
//        });
        return imageCardView;
    }

    @Override
    public void onBindViewHolder(FavoriteModel card, final ImageCardView cardView) {
        cardView.setTag(card);
//        cardView.setTitleText(card.getNama());
        cardView.setTitleText(card.getTitle());
//        cardView.setContentText(card.getDescription());
//        if (card.getImageUrl() != null) {
//            int resourceId = getContext().getResources()
//                    .getIdentifier(card.getImageUrl(),
//                            "drawable", getContext().getPackageName());
            Glide.with(getContext())
                    .asBitmap()
                    .load(card.getUrlImage())
                    .into(cardView.getMainImageView());
//        }
    }

}
