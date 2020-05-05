package com.fiberstream.tv.app.details;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.leanback.app.DetailsFragment;
import androidx.leanback.app.DetailsFragmentBackgroundController;
import androidx.leanback.media.MediaPlayerAdapter;
import androidx.leanback.media.MediaPlayerGlue;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.fiberstream.tv.R;
import com.fiberstream.tv.app.streaming.presenters.CardPresenterSelector;
import com.fiberstream.tv.app.media.PlaybackSeekDiskDataProvider;
import com.fiberstream.tv.app.media.VideoMediaPlayerGlue;
import com.fiberstream.tv.app.models.Card;
import com.fiberstream.tv.app.models.DetailedCard;
import com.fiberstream.tv.utils.CardListRow;
import com.fiberstream.tv.utils.Utils;
import com.google.gson.Gson;

public class DetailViewExampleWithVideoBackgroundFragment extends DetailsFragment implements OnItemViewClickedListener,
        OnItemViewSelectedListener {

    public static final String TRANSITION_NAME = "t_for_transition";
    public static final String EXTRA_CARD = "card";

    private static final long ACTION_PLAY = 1;
    private static final long ACTION_RENT = 2;
    private static final long ACTION_WISHLIST = 3;
    private static final long ACTION_RELATED = 4;

    private Action mActionPlay;
    private Action mActionRent;
    private Action mActionWishList;
    private Action mActionRelated;
    private ArrayObjectAdapter mRowsAdapter;
    private DetailedCard data;
    private final DetailsFragmentBackgroundController mDetailsBackground =
            new DetailsFragmentBackgroundController(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUi();
        setupEventListeners();
    }

    private void setupUi() {
        // Load the card we want to display from a JSON resource. This JSON data could come from
        // anywhere in a real world app, e.g. a server.
        String json = Utils
                .inputStreamToString(getResources().openRawResource(R.raw.detail_example));
        data = new Gson().fromJson(json, DetailedCard.class);

        // Setup fragment
        setTitle(getString(R.string.detail_view_title));

        FullWidthDetailsOverviewRowPresenter rowPresenter = new FullWidthDetailsOverviewRowPresenter(
                new DetailsDescriptionPresenter(getActivity())) {

            @Override
            protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
                // Customize Actionbar and Content by using custom colors.
                RowPresenter.ViewHolder viewHolder = super.createRowViewHolder(parent);

                View actionsView = viewHolder.view.
                        findViewById(R.id.details_overview_actions_background);
                actionsView.setBackgroundColor(getActivity().getResources().
                        getColor(R.color.detail_view_actionbar_background));

                View detailsView = viewHolder.view.findViewById(R.id.details_frame);
                detailsView.setBackgroundColor(
                        getResources().getColor(R.color.detail_view_background));
                return viewHolder;
            }
        };

        FullWidthDetailsOverviewSharedElementHelper mHelper = new FullWidthDetailsOverviewSharedElementHelper();
        mHelper.setSharedElementEnterTransition(getActivity(), TRANSITION_NAME);
        rowPresenter.setListener(mHelper);
        rowPresenter.setParticipatingEntranceTransition(false);
        prepareEntranceTransition();

        ListRowPresenter shadowDisabledRowPresenter = new ListRowPresenter();
        shadowDisabledRowPresenter.setShadowEnabled(false);

        // Setup PresenterSelector to distinguish between the different rows.
        ClassPresenterSelector rowPresenterSelector = new ClassPresenterSelector();
        rowPresenterSelector.addClassPresenter(DetailsOverviewRow.class, rowPresenter);
        rowPresenterSelector.addClassPresenter(CardListRow.class, shadowDisabledRowPresenter);
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(rowPresenterSelector);

        // Setup action and detail row.
        DetailsOverviewRow detailsOverview = new DetailsOverviewRow(data);
        int imageResId = data.getLocalImageResourceId(getActivity());

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_CARD)) {
            imageResId = extras.getInt(EXTRA_CARD, imageResId);
        }
        detailsOverview.setImageDrawable(getResources().getDrawable(imageResId, null));
        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();

        mActionPlay = new Action(ACTION_PLAY, getString(R.string.action_play));
        mActionRent = new Action(ACTION_RENT, getString(R.string.action_rent));
        mActionWishList = new Action(ACTION_WISHLIST, getString(R.string.action_wishlist));
        mActionRelated = new Action(ACTION_RELATED, getString(R.string.action_related));

        actionAdapter.add(mActionRent);
        actionAdapter.add(mActionWishList);
        actionAdapter.add(mActionRelated);
        detailsOverview.setActionsAdapter(actionAdapter);
        mRowsAdapter.add(detailsOverview);

        // Setup related row.
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(
                new CardPresenterSelector(getActivity()));
        for (Card characterCard : data.getCharacters()) listRowAdapter.add(characterCard);
        HeaderItem header = new HeaderItem(0, getString(R.string.header_related));
        mRowsAdapter.add(new CardListRow(header, listRowAdapter, null));

        // Setup recommended row.
        listRowAdapter = new ArrayObjectAdapter(new CardPresenterSelector(getActivity()));
        for (Card card : data.getRecommended()) listRowAdapter.add(card);
        header = new HeaderItem(1, getString(R.string.header_recommended));
        mRowsAdapter.add(new ListRow(header, listRowAdapter));

        setAdapter(mRowsAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startEntranceTransition();
            }
        }, 500);
        initializeBackground();
    }

    @SuppressLint("RestrictedApi")
    private void initializeBackground() {
        mDetailsBackground.enableParallax();

        MediaPlayerGlue playerGlue = new MediaPlayerGlue(getActivity());
        mDetailsBackground.setupVideoPlayback(playerGlue);

        playerGlue.setTitle(data.getTitle().concat(" (Trailer)"));
        playerGlue.setArtist(data.getDescription());
        playerGlue.setVideoUrl(data.getTrailerUrl());
    }

    private void playMainVideoOnBackground() {
        VideoMediaPlayerGlue<MediaPlayerAdapter> playerGlue = new VideoMediaPlayerGlue(
                getActivity(), new MediaPlayerAdapter(getActivity()));

        mDetailsBackground.setupVideoPlayback(playerGlue);
        playerGlue.setTitle(data.getTitle() + " (Main Video)");
        playerGlue.setSubtitle(data.getDescription());
        playerGlue.getPlayerAdapter().setDataSource(Uri.parse(data.getVideoUrl()));
        PlaybackSeekDiskDataProvider.setDemoSeekProvider(playerGlue);

        mDetailsBackground.switchToVideo();
    }

    private void setupEventListeners() {
        setOnItemViewSelectedListener(this);
        setOnItemViewClickedListener(this);
    }

    private void startWizardActivityForPayment() {
//        Intent intent = new Intent(getActivity(),
//                WizardExampleActivity.class);
//
//        // Prepare extras which contains the Movie and will be passed to the Activity
//        // which is started through the Intent.
//        Bundle extras = new Bundle();
//        String json = Utils.inputStreamToString(
//                getResources().openRawResource(R.raw.wizard_example));
//        Movie movie = new Gson().fromJson(json, Movie.class);
//        extras.putSerializable("movie", movie);
//        intent.putExtras(extras);
//
//
//        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
//                .toBundle();
//        startActivityForResult(intent,
//                DetailViewExampleWithVideoBackgroundActivity.BUY_MOVIE_REQUEST, bundle);
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                              RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (!(item instanceof Action)) return;
        Action action = (Action) item;
        long id = action.getId();

        if (id == ACTION_RENT) {
            startWizardActivityForPayment();
        } else if (action.getId() == ACTION_PLAY) {
            playMainVideoOnBackground();
        } else if (action.getId() == ACTION_RELATED) {
            setSelectedPosition(1);
        } else {
            Toast.makeText(getActivity(), getString(R.string.action_cicked), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                               RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (mRowsAdapter.indexOf(row) > 0) {
            int backgroundColor = getResources().getColor(R.color.detail_view_related_background);
            getView().setBackgroundColor(backgroundColor);
        } else {
            getView().setBackground(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
        if (requestCode == DetailViewExampleWithVideoBackgroundActivity.BUY_MOVIE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayObjectAdapter actionAdapter = (ArrayObjectAdapter)
                        ((DetailsOverviewRow) getAdapter().get(0)).getActionsAdapter();

                actionAdapter.add(0, mActionPlay);
                actionAdapter.remove(mActionRent);
                setTitle(getTitle() + " (Owned)");

//                boolean watchNow = returnIntent
//                        .getBooleanExtra(WizardExampleActivity.WATCH_NOW,
//                                false);
//
//                if (watchNow) {
//                    // Leave a delay for playing the video in order to focus on the video fragment
//                    // after coming back from Wizard activity
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            playMainVideoOnBackground();
//                        }
//                    }, 500);
//                }
            }
        }
    }
}

