package com.fiberstream.tv.app.streaming.presenters;

import android.content.Context;

import androidx.leanback.widget.ImageCardView;

import com.fiberstream.tv.app.streaming.model.KategoriModel;

/**
 * Presenter for rendering video cards on the Vertical Grid fragment.
 */
public class VideoCardViewPresenter extends ImageCardViewPresenter {

    public VideoCardViewPresenter(Context context, int cardThemeResId) {
        super(context, cardThemeResId);
    }

    public VideoCardViewPresenter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(KategoriModel card, final ImageCardView cardView) {
        super.onBindViewHolder(card, cardView);
//        VideoCard videoCard = (VideoCard) card;
//        Glide.with(getContext())
//                .asBitmap()
//                .load(videoCard.getImageUrl())
//                .into(cardView.getMainImageView());

    }

}
