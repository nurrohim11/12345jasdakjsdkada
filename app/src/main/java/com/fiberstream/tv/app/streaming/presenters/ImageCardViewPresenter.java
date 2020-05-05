package com.fiberstream.tv.app.streaming.presenters;

import android.content.Context;
import android.util.Log;
import android.view.ContextThemeWrapper;

import androidx.leanback.widget.ImageCardView;

import com.bumptech.glide.Glide;
import com.fiberstream.tv.R;
import com.fiberstream.tv.app.streaming.model.KategoriModel;

public class ImageCardViewPresenter extends AbstractCardPresenter<ImageCardView> {

    public ImageCardViewPresenter(Context context, int cardThemeResId) {
        super(new ContextThemeWrapper(context, cardThemeResId));
    }

    public ImageCardViewPresenter(Context context) {
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
    public void onBindViewHolder(KategoriModel card, final ImageCardView cardView) {
        cardView.setTag(card);
//        cardView.setTitleText(card.getNama());
        cardView.setTitleText(card.getNama());
//        cardView.setContentText(card.getDescription());
//        if (card.getImageUrl() != null) {
//            int resourceId = getContext().getResources()
//                    .getIdentifier(card.getImageUrl(),
//                            "drawable", getContext().getPackageName());
            Glide.with(getContext())
                    .asBitmap()
                    .load(card.getImageUrl())
                    .into(cardView.getMainImageView());
//        }
    }

}
