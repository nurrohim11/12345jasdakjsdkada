package com.fiberstream.tv.app.main.presenter;

import android.content.Context;
import android.view.ContextThemeWrapper;

import androidx.leanback.widget.ImageCardView;

import com.bumptech.glide.Glide;
import com.fiberstream.tv.R;
import com.fiberstream.tv.app.main.model.DataModel;
import com.fiberstream.tv.app.tv.model.ChannelModel;

public class MainPresenter extends AbstractMainPresenter<ImageCardView> {

    public MainPresenter(Context context, int cardThemeResId) {
        super(new ContextThemeWrapper(context, cardThemeResId));
    }

    public MainPresenter(Context context) {
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
    public void onBindViewHolder(DataModel card, final ImageCardView cardView) {
        cardView.setTag(card);
        cardView.setTitleText(card.getNama());
//        cardView.setContentText(card.getDescription());
//        if (card.getLocalImageResourceName() != null) {
//            int resourceId = getContext().getResources()
//                    .getIdentifier(card.getLocalImageResourceName(),
//                            "drawable", getContext().getPackageName());
            Glide.with(getContext())
                    .asBitmap()
                    .load(card.getIcon())
                    .into(cardView.getMainImageView());
//        }
    }

}
