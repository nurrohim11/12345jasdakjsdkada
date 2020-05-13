package com.fiberstream.tv.app.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.leanback.widget.BaseCardView;

import com.fiberstream.tv.R;
import com.fiberstream.tv.app.streaming.model.KategoriModel;

import co.id.gmedia.coremodul.CustomModel;

public class TextView extends BaseCardView {

    public TextView(Context context) {
        super(context, null, R.style.TextCardStyle);
        LayoutInflater.from(getContext()).inflate(R.layout.text_intro, this);
        setFocusable(true);
    }

    public void updateUi(CustomModel card) {
        android.widget.TextView extraText =findViewById(R.id.primary_text);

        extraText.setText(card.getItem2());
    }

}
