package com.fiberstream.tv.app.main.presenter;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;

import androidx.leanback.widget.ImageCardView;

import com.bumptech.glide.Glide;
import com.fiberstream.tv.R;
import com.fiberstream.tv.app.main.TextView;
import com.fiberstream.tv.app.settings.model.SettingModel;
import com.fiberstream.tv.app.settings.presenter.AbstractSettingsPresenter;
import com.fiberstream.tv.app.streaming.TextCardView;

import co.id.gmedia.coremodul.CustomModel;

public class TextViewPresenter extends AbstrackTextPresenter<TextView> {

    public TextViewPresenter(Context context, int cardThemeResId) {
        super(new ContextThemeWrapper(context, cardThemeResId));
    }

    public TextViewPresenter(Context context) {
        this(context, R.style.DefaultCardTheme);
    }

    @Override
    protected TextView onCreateView() {
        return new TextView(getContext());
    }

    @Override
    public void onBindViewHolder(CustomModel card, TextView cardView) {
        cardView.updateUi(card);
    }

}

