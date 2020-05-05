package com.fiberstream.tv.app.favorite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.leanback.widget.BaseCardView;

import com.fiberstream.tv.R;
import com.fiberstream.tv.app.streaming.model.KategoriModel;

public class CharacterFavoriteView extends BaseCardView {

    public CharacterFavoriteView(Context context) {
        super(context, null, R.style.CharacterCardStyle);
        LayoutInflater.from(getContext()).inflate(R.layout.character_card, this);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ImageView mainImage = (ImageView) findViewById(R.id.main_image);
                View container = findViewById(R.id.container);
                if (hasFocus) {
                    container.setBackgroundResource(R.drawable.character_focused);
                    mainImage.setBackgroundResource(R.drawable.character_focused);
                } else {
                    container.setBackgroundResource(R.drawable.character_not_focused_padding);
                    mainImage.setBackgroundResource(R.drawable.character_not_focused);
                }
            }
        });
        setFocusable(true);
    }

    public void updateUi(KategoriModel card) {
        TextView primaryText = (TextView) findViewById(R.id.primary_text);
        final ImageView imageView = (ImageView) findViewById(R.id.main_image);

        primaryText.setText(card.getNama());
//        if (card.getImageUrl() != null) {
//            int resourceId = Integer.parseInt(card.getImageUrl());
            Bitmap bitmap = BitmapFactory
                    .decodeResource(getContext().getResources(), R.drawable.face_08);
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), bitmap);
            drawable.setAntiAlias(true);
            drawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
            imageView.setImageDrawable(drawable);
//        }
    }


}
