package com.fiberstream.tv.app.streaming.presenters;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.widget.BaseCardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fiberstream.tv.R;
import com.fiberstream.tv.app.streaming.model.KategoriModel;


public class SideInfoCardPresenter extends AbstractCardPresenter<BaseCardView> {

    public SideInfoCardPresenter(Context context) {
        super(context);
    }

    @Override
    protected BaseCardView onCreateView() {
        final BaseCardView cardView = new BaseCardView(getContext(), null,
                R.style.SideInfoCardStyle);
        cardView.setFocusable(true);
        cardView.addView(LayoutInflater.from(getContext()).inflate(R.layout.side_info_card, null));
        return cardView;
    }

    @Override
    public void onBindViewHolder(KategoriModel card, BaseCardView cardView) {
        ImageView imageView = (ImageView) cardView.findViewById(R.id.main_image);
        if (card.getImageUrl() != null) {
            int width = (int) getContext().getResources()
                    .getDimension(R.dimen.sidetext_image_card_width);
            int height = (int) getContext().getResources()
                    .getDimension(R.dimen.sidetext_image_card_height);
            int resourceId = getContext().getResources()
                    .getIdentifier(card.getImageUrl(),
                            "drawable", getContext().getPackageName());
            RequestOptions myOptions = new RequestOptions()
                    .override(width, height);
            Glide.with(getContext())
                    .asBitmap()
                    .load(resourceId)
                    .apply(myOptions)
                    .into(imageView);
        }

        TextView primaryText = (TextView) cardView.findViewById(R.id.primary_text);
        primaryText.setText(card.getNama());

        TextView secondaryText = (TextView) cardView.findViewById(R.id.secondary_text);
        secondaryText.setText("secondary");

        TextView extraText = (TextView) cardView.findViewById(R.id.extra_text);
        extraText.setText("extra");
    }

}
