package com.fiberstream.tv.app.main.presenter;

import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ObjectAdapter;

import com.fiberstream.tv.app.settings.model.SettingRowModel;

import co.id.gmedia.coremodul.CustomModel;

public class TextRow extends ListRow {

    private CustomModel mCardRow;

    public TextRow(HeaderItem header, ObjectAdapter adapter, CustomModel cardRow) {
        super(header, adapter);
        setCardRow(cardRow);
    }

    public CustomModel getCardRow() {
        return mCardRow;
    }

    public void setCardRow(CustomModel cardRow) {
        this.mCardRow = cardRow;
    }
}
