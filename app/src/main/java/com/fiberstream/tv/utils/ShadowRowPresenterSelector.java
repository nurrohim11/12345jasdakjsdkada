package com.fiberstream.tv.utils;

import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;

import com.fiberstream.tv.app.models.CardRow;
import com.fiberstream.tv.app.tv.TvListRows;
import com.fiberstream.tv.utils.CardListRow;

public class ShadowRowPresenterSelector extends PresenterSelector {

    private ListRowPresenter mShadowEnabledRowPresenter = new ListRowPresenter();
    private ListRowPresenter mShadowDisabledRowPresenter = new ListRowPresenter();

    public ShadowRowPresenterSelector() {
        mShadowEnabledRowPresenter.setNumRows(1);
        mShadowDisabledRowPresenter.setShadowEnabled(false);
    }

    @Override public Presenter getPresenter(Object item) {
        if (!(item instanceof TvListRows)) return mShadowDisabledRowPresenter;
        return mShadowDisabledRowPresenter;
    }

    @Override
    public Presenter[] getPresenters() {
        return new Presenter [] {
                mShadowDisabledRowPresenter,
                mShadowEnabledRowPresenter
        };
    }
}
