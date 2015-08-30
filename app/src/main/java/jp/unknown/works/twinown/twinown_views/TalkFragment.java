package jp.unknown.works.twinown.twinown_views;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.R;
import jp.unknown.works.twinown.Utils;
import jp.unknown.works.twinown.models.UserPreference;
import jp.unknown.works.twinown.twinown_twitter.Component;
import jp.unknown.works.twinown.twinown_twitter.TwinownHelper;
import twitter4j.Status;

public class TalkFragment extends Fragment {
    private UserPreference userPreference;
    private Status rootStatus;
    private TimelineAdapter timelineAdapter;
    @Bind(R.id.timelineView) RecyclerView timelineView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        userPreference = (UserPreference) getArguments().getSerializable(Utils.ARGUMENTS_KEYWORD_USER_PREFERENCE);
        rootStatus = (Status) getArguments().getSerializable(Utils.ARGUMENTS_KEYWORD_STATUS);
        if (rootStatus != null) {
            TwinownHelper.getInReplyToStatus(userPreference, rootStatus);
        }
        timelineAdapter = new TimelineAdapter(this, getFragmentManager(), getActivity(), userPreference);
        timelineAdapter.addStatus(rootStatus);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (timelineAdapter != null) {
            timelineAdapter.refreshActivity(this, getFragmentManager(), context);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_talk, container, false);
        ButterKnife.bind(this, view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        timelineView.setLayoutManager(linearLayoutManager);
        timelineView.setAdapter(timelineAdapter);
        timelineView.addItemDecoration(new TimelineItemDecoration(getActivity()));
        return view;
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(final Component.InReplyToEvent inReplyToEvent) {
        if (Objects.equals(inReplyToEvent.userPreference.userId, userPreference.userId)
                && inReplyToEvent.rootStatus.getId() == rootStatus.getId()) {
            rootStatus = inReplyToEvent.status;
            timelineAdapter.addStatus(rootStatus);
            if (rootStatus.getInReplyToStatusId() != -1) {
                TwinownHelper.getInReplyToStatus(userPreference, rootStatus);
            }
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(final Component.FavoriteEvent favoriteEvent) {
        if (Objects.equals(favoriteEvent.userPreference.userId, userPreference.userId)) {
            timelineAdapter.addFavorite(favoriteEvent.status);
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(final Component.UnFavoriteEvent unFavoriteEvent) {
        if (Objects.equals(unFavoriteEvent.userPreference.userId, userPreference.userId)) {
            timelineAdapter.deleteFavorite(unFavoriteEvent.status);
        }
    }
}
