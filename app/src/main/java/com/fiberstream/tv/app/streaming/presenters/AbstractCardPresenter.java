package com.fiberstream.tv.app.streaming.presenters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.leanback.widget.BaseCardView;
import androidx.leanback.widget.Presenter;

import com.fiberstream.tv.app.streaming.model.KategoriModel;

public abstract class AbstractCardPresenter<T extends BaseCardView> extends Presenter {

    private static final String TAG = "AbstractCardPresenter";
    private final Context mContext;

    public AbstractCardPresenter(Context context) {
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
        KategoriModel card = (KategoriModel) item;
        onBindViewHolder(card, (T) viewHolder.view);
    }

    @Override public final void onUnbindViewHolder(ViewHolder viewHolder) {
        onUnbindViewHolder((T) viewHolder.view);
    }

    public void onUnbindViewHolder(T cardView) {
        // Nothing to clean up. Override if necessary.
    }
    protected abstract T onCreateView();

    public abstract void onBindViewHolder(KategoriModel card, T cardView);

}
