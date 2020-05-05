package com.fiberstream.tv.app.tv.presenter;

import android.content.Context;

import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;

import com.fiberstream.tv.R;
import com.fiberstream.tv.app.models.Card;
import com.fiberstream.tv.app.streaming.model.KategoriModel;
import com.fiberstream.tv.app.streaming.presenters.CharacterCardPresenter;
import com.fiberstream.tv.app.streaming.presenters.IconCardPresenter;
import com.fiberstream.tv.app.streaming.presenters.ImageCardViewPresenter;
import com.fiberstream.tv.app.streaming.presenters.SideInfoCardPresenter;
import com.fiberstream.tv.app.streaming.presenters.SingleLineCardPresenter;
import com.fiberstream.tv.app.streaming.presenters.TextCardPresenter;
import com.fiberstream.tv.app.streaming.presenters.VideoCardViewPresenter;
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
//    @Override
//    public Presenter getPresenter(Object item) {
//        if (!(item instanceof Card)) throw new RuntimeException(
//                String.format("The PresenterSelector only supports data items of type '%s'",
//                        Card.class.getName()));
//        Card card = (Card) item;
//        Presenter presenter = presenters.get(card.getType());
//        if (presenter == null) {
//            switch (card.getType()) {
//                case SINGLE_LINE:
//                    presenter = new SingleLineCardPresenter(mContext);
//                    break;
//                case VIDEO_GRID:
//                    presenter = new VideoCardViewPresenter(mContext, R.style.VideoGridCardTheme);
//                    break;
//                case MOVIE:
//                case MOVIE_BASE:
//                case MOVIE_COMPLETE:
//                case SQUARE_BIG:
//                case GRID_SQUARE:
//                case GAME: {
//                    int themeResId = R.style.MovieCardSimpleTheme;
//                    if (card.getType() == Card.Type.MOVIE_BASE) {
//                        themeResId = R.style.MovieCardBasicTheme;
//                    } else if (card.getType() == Card.Type.MOVIE_COMPLETE) {
//                        themeResId = R.style.MovieCardCompleteTheme;
//                    } else if (card.getType() == Card.Type.SQUARE_BIG) {
//                        themeResId = R.style.SquareBigCardTheme;
//                    } else if (card.getType() == Card.Type.GRID_SQUARE) {
//                        themeResId = R.style.GridCardTheme;
//                    } else if (card.getType() == Card.Type.GAME) {
//                        themeResId = R.style.GameCardTheme;
//                    }
//                    presenter = new ImageCardViewPresenter(mContext, themeResId);
//                    break;
//                }
//                case SIDE_INFO:
//                    presenter = new SideInfoCardPresenter(mContext);
//                    break;
//                case TEXT:
//                    presenter = new TextCardPresenter(mContext);
//                    break;
//                case ICON:
//                    presenter = new IconCardPresenter(mContext);
//                    break;
//                case CHARACTER:
//                    presenter = new CharacterCardPresenter(mContext);
//                    break;
//                default:
//                    presenter = new ImageCardViewPresenter(mContext);
//                    break;
//            }
//        }
//        presenters.put(card.getType(), presenter);
//        return presenter;
//    }
}
