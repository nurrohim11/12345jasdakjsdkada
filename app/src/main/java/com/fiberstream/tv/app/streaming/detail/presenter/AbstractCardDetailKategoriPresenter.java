package com.fiberstream.tv.app.streaming.detail.presenter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.leanback.widget.BaseCardView;
import androidx.leanback.widget.Presenter;

import com.fiberstream.tv.app.streaming.model.KategoriModel;
import com.fiberstream.tv.app.streaming.model.StreamingModel;

public abstract class AbstractCardDetailKategoriPresenter<T extends BaseCardView> extends Presenter {

    private static final String TAG = "AbstractCardPresenter";
    private final Context mContext;

    public AbstractCardDetailKategoriPresenter(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @Override public final ViewHolder onCreateViewHolder(ViewGroup parent) {
        T cardView = onCreateView();
        return new ViewHolder(cardView);
    }

    @Override public final void onBindViewHolder(ViewHolder viewHolder, Object item) {
        StreamingModel card = (StreamingModel) item;
        onBindViewHolder(card, (T) viewHolder.view);
    }

    @Override public final void onUnbindViewHolder(ViewHolder viewHolder) {
        onUnbindViewHolder((T) viewHolder.view);
    }

    public void onUnbindViewHolder(T cardView) {
        // Nothing to clean up. Override if necessary.
    }
    protected abstract T onCreateView();

    public abstract void onBindViewHolder(StreamingModel card, T cardView);

}
