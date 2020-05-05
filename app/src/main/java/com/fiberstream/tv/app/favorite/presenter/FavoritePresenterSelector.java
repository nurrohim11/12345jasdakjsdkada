package com.fiberstream.tv.app.favorite.presenter;

import android.content.Context;

import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;

import com.fiberstream.tv.app.favorite.model.FavoriteModel;
import com.fiberstream.tv.app.models.Card;
import com.fiberstream.tv.app.streaming.model.KategoriModel;
import com.fiberstream.tv.app.streaming.presenters.ImageCardViewPresenter;

import java.util.HashMap;

public class FavoritePresenterSelector extends PresenterSelector {

    private final Context mContext;
    private final HashMap<Card.Type, Presenter> presenters = new HashMap<Card.Type, Presenter>();

    public FavoritePresenterSelector(Context context) {
        mContext = context;
    }

    @Override
    public Presenter getPresenter(Object item) {
        if (!(item instanceof FavoriteModel)) throw new RuntimeException(
                String.format("The PresenterSelector only supports data items of type '%s'",
                        Card.class.getName()));
        FavoriteModel card = (FavoriteModel) item;
        Presenter presenter = presenters.get(card);
        if (presenter == null) {
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
                    presenter = new ImageFavoriteViewPresenter(mContext);
//                    break;
//            }
        }
//        presenters.put(card.getType(), presenter);
        return presenter;
    }

}
