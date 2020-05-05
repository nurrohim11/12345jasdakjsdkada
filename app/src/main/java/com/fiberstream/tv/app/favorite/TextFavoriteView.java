package com.fiberstream.tv.app.favorite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.leanback.widget.BaseCardView;

import com.fiberstream.tv.R;
import com.fiberstream.tv.app.streaming.model.KategoriModel;

public class TextFavoriteView extends BaseCardView {

    public TextFavoriteView(Context context) {
        super(context, null, R.style.TextCardStyle);
        LayoutInflater.from(getContext()).inflate(R.layout.text_icon_card, this);
        setFocusable(true);
    }

    public void updateUi(KategoriModel card) {
        TextView extraText = (TextView) findViewById(R.id.extra_text);
        TextView primaryText = (TextView) findViewById(R.id.primary_text);
        final ImageView imageView = (ImageView) findViewById(R.id.main_image);

        extraText.setText(card.getNama());
        primaryText.setText("Bisa");

        // Create a rounded drawable.
        Bitmap bitmap = BitmapFactory
                .decodeResource(getContext().getResources(), Integer.parseInt(card.getImageUrl()));
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), bitmap);
        drawable.setAntiAlias(true);
        drawable.setCornerRadius(
                Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
        imageView.setImageDrawable(drawable);
    }

}
