package com.fiberstream.tv.app.streaming.detail.presenter;

import android.content.Context;

import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;

import com.fiberstream.tv.app.models.Card;
import com.fiberstream.tv.app.streaming.model.KategoriModel;
import com.fiberstream.tv.app.streaming.model.StreamingModel;
import com.fiberstream.tv.app.streaming.presenters.ImageCardViewPresenter;

import java.util.HashMap;

public class DetailKategoriPresenter extends PresenterSelector {

    private final Context mContext;
    private final HashMap<Card.Type, Presenter> presenters = new HashMap<Card.Type, Presenter>();

    public DetailKategoriPresenter(Context context) {
        mContext = context;
    }

    @Override
    public Presenter getPresenter(Object item) {
        if (!(item instanceof StreamingModel)) throw new RuntimeException(
                String.format("The PresenterSelector only supports data items of type '%s'",
                        Card.class.getName()));
        StreamingModel card = (StreamingModel) item;
        Presenter presenter = presenters.get(card);
        if (presenter == null) {
            presenter = new CardDetailKategoriPresenter(mContext);
        }
        return presenter;
    }
}
