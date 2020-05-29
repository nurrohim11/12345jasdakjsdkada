package com.fiberstream.tv.app.apps.presenter;

import android.content.Context;

import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;

import com.fiberstream.tv.app.apps.model.AppsModel;
import com.fiberstream.tv.app.models.Card;
import com.fiberstream.tv.app.streaming.detail.presenter.CardDetailKategoriPresenter;
import com.fiberstream.tv.app.streaming.model.StreamingModel;

import java.util.HashMap;

public class AppsPresenter extends PresenterSelector {

    private final Context mContext;
    private final HashMap<Card.Type, Presenter> presenters = new HashMap<Card.Type, Presenter>();

    public AppsPresenter(Context context) {
        mContext = context;
    }

    @Override
    public Presenter getPresenter(Object item) {
        if (!(item instanceof AppsModel)) throw new RuntimeException(
                String.format("The PresenterSelector only supports data items of type '%s'",
                        Card.class.getName()));
        AppsModel card = (AppsModel) item;
        Presenter presenter = presenters.get(card);
        if (presenter == null) {
            presenter = new CardAppsPresenter(mContext);
        }
        return presenter;
    }
}

