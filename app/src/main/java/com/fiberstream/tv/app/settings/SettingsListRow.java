package com.fiberstream.tv.app.settings;

import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ObjectAdapter;

import com.fiberstream.tv.app.settings.model.SettingRowModel;

public class SettingsListRow extends ListRow {

    private SettingRowModel mCardRow;

    public SettingsListRow(HeaderItem header, ObjectAdapter adapter, SettingRowModel cardRow) {
        super(header, adapter);
        setCardRow(cardRow);
    }

    public SettingRowModel getCardRow() {
        return mCardRow;
    }

    public void setCardRow(SettingRowModel cardRow) {
        this.mCardRow = cardRow;
    }
}
