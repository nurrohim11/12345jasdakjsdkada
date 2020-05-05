package com.fiberstream.tv.app.main;

import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ObjectAdapter;

import com.fiberstream.tv.app.main.model.DataRowModel;
import com.fiberstream.tv.app.tv.model.ChannelRowModel;

public class MainListRows extends ListRow {

    private DataRowModel mDataRow;

    public MainListRows(HeaderItem header, ObjectAdapter adapter, DataRowModel cardRow) {
        super(header, adapter);
        setDataRow(cardRow);
    }

    public DataRowModel getCardRow() {
        return mDataRow;
    }

    public void setDataRow(DataRowModel dataRow) {
        this.mDataRow = dataRow;
    }
}
