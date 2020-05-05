package com.fiberstream.tv.app.streaming.presenters;

import android.content.Context;

import com.fiberstream.tv.app.streaming.TextCardView;
import com.fiberstream.tv.app.streaming.model.KategoriModel;

public class TextCardPresenter extends AbstractCardPresenter<TextCardView> {

    public TextCardPresenter(Context context) {
        super(context);
    }

    @Override
    protected TextCardView onCreateView() {
        return new TextCardView(getContext());
    }

    @Override
    public void onBindViewHolder(KategoriModel card, TextCardView cardView) {
        cardView.updateUi(card);
    }

}
