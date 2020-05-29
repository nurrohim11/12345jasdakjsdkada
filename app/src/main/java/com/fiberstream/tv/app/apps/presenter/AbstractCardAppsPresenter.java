package com.fiberstream.tv.app.apps.presenter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.leanback.widget.BaseCardView;
import androidx.leanback.widget.Presenter;

import com.fiberstream.tv.app.apps.model.AppsModel;
import com.fiberstream.tv.app.streaming.model.StreamingModel;

public abstract class AbstractCardAppsPresenter<T extends BaseCardView> extends Presenter {

    private static final String TAG = "AbstractCardPresenter";
    private final Context mContext;

    public AbstractCardAppsPresenter(Context context) {
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
        AppsModel card = (AppsModel) item;
        onBindViewHolder(card, (T) viewHolder.view);
    }

    @Override public final void onUnbindViewHolder(ViewHolder viewHolder) {
        onUnbindViewHolder((T) viewHolder.view);
    }

    public void onUnbindViewHolder(T cardView) {
        // Nothing to clean up. Override if necessary.
    }
    protected abstract T onCreateView();

    public abstract void onBindViewHolder(AppsModel card, T cardView);

}
