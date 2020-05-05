package com.fiberstream.tv.app.tv;

import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ObjectAdapter;

import com.fiberstream.tv.app.models.CardRow;
import com.fiberstream.tv.app.tv.model.ChannelRowModel;

public class TvListRows extends ListRow {

    private ChannelRowModel mCardRow;

    public TvListRows(HeaderItem header, ObjectAdapter adapter, ChannelRowModel cardRow) {
        super(header, adapter);
        setCardRow(cardRow);
    }

    public ChannelRowModel getCardRow() {
        return mCardRow;
    }

    public void setCardRow(ChannelRowModel cardRow) {
        this.mCardRow = cardRow;
    }
}
