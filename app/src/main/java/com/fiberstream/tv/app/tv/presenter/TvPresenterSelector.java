package com.fiberstream.tv.app.tv.presenter;

import android.content.Context;

import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;

import com.fiberstream.tv.R;
import com.fiberstream.tv.app.models.Card;
import com.fiberstream.tv.app.tv.model.ChannelModel;

import java.util.HashMap;

public class TvPresenterSelector extends PresenterSelector {

    private final Context mContext;
    private final HashMap<Card.Type, Presenter> presenters = new HashMap<Card.Type, Presenter>();

    public TvPresenterSelector(Context context) {
        mContext = context;
    }
    @Override
    public Presenter getPresenter(Object item) {
        ChannelModel card = (ChannelModel) item;
        Presenter presenter = presenters.get(card);
        if (presenter == null) {
            presenter = new ChannelPresenter(mContext);
        }
        return presenter;
    }
}
