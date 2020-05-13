package com.fiberstream.tv.app.main.presenter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;

import com.bumptech.glide.Glide;
import com.fiberstream.tv.R;
import com.fiberstream.tv.app.MainFragment;
import com.fiberstream.tv.app.main.model.SliderModel;
import com.fiberstream.tv.app.models.Card;
import com.fiberstream.tv.app.settings.presenter.ImageSettingsViewPresenter;
import com.fiberstream.tv.app.tv.model.ChannelModel;
import com.fiberstream.tv.app.tv.presenter.ChannelPresenter;

import org.w3c.dom.Text;

import java.util.HashMap;

import co.id.gmedia.coremodul.CustomModel;

//
//public class TextPresenter extends TextViewPresenter {
//
//    public TextPresenter(Context context) {
//        super(context, R.style.IconCardTheme);
//    }
//
//    @Override
//    protected ImageCardView onCreateView() {
//        final ImageCardView imageCardView = super.onCreateView();
//        imageCardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                setImageBackground(imageCardView, R.color.transparent);
//            }
//        });
//        setImageBackground(imageCardView, R.color.transparent);
//        return imageCardView;
//    }
//
//    private void setImageBackground(ImageCardView imageCardView, int colorId) {
//        imageCardView.setBackgroundColor(getContext().getResources().getColor(colorId));
//    }
//}

//public class TextPresenter extends Presenter {
//    private static final String TAG = "TitleText";
//
////    private static final int IMG_WIDTH = MainFragment.mMetrics.widthPixels * 80 / 100;
////    private static final int IMG_HEIGHT = MainFragment.mMetrics.heightPixels * 80 / 100;
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent) {
//
//        Log.d(TAG, "onCreateViewHolder");
//
//        TextView textView = new TextView(parent.getContext());
//
//        textView.setFocusable(false);
//        textView.setFocusableInTouchMode(false);
//        return new ViewHolder(textView);
//    }
//
//    @Override
//    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
//
//        CustomModel selected = (CustomModel) item;
//
//        TextView textView = (TextView) viewHolder.view;
//        Log.d(TAG, "onBindViewHolder");
//
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,32);
////        textView.setBackgroundResource(Color.BLUE);
//        textView.setText(selected.getItem2());
//    }
//
//    @Override
//    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
//
//        Log.d(TAG, "onUnbindViewHolder");
//        TextView textView = (TextView) viewHolder.view;
//        textView.setText("");
//    }
//}


public class TextPresenter extends PresenterSelector {

    private final Context mContext;
    private final HashMap<Card.Type, Presenter> presenters = new HashMap<Card.Type, Presenter>();

    public TextPresenter(Context context) {
        mContext = context;
    }
    @Override
    public Presenter getPresenter(Object item) {
        CustomModel card = (CustomModel) item;
        Presenter presenter = presenters.get(card);
        if (presenter == null) {
            presenter = new TextViewPresenter(mContext);
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
