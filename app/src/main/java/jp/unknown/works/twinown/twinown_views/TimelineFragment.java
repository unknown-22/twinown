package jp.unknown.works.twinown.twinown_views;

import android.os.Bundle;
import android.os.Handler;
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
import jp.unknown.works.twinown.Globals;
import jp.unknown.works.twinown.R;
import jp.unknown.works.twinown.models.Tab;
import jp.unknown.works.twinown.twinown_twitter.Component;
import jp.unknown.works.twinown.twinown_twitter.TwinownHelper;
import jp.unknown.works.twinown.models.UserPreference;


public class TimelineFragment extends Fragment {
    private static final Handler handler = new Handler();
    private Tab tab;
    private UserPreference userPreference;
    private LinearLayoutManager linearLayoutManager;
    private TimelineAdapter timelineAdapter;
    @Bind(R.id.timelineView) RecyclerView timelineView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        tab = (Tab) getArguments().getSerializable(Globals.ARGUMENTS_KEYWORD_TAB);
        userPreference = UserPreference.get(tab != null ? tab.userId : null);
        TwinownHelper.StreamSingleton.getInstance().getOrCreateTwitterStream(userPreference);
        TwinownHelper.StreamSingleton.getInstance().startUserStream(userPreference);
        TwinownHelper.getHomeTimeline(userPreference);
        timelineAdapter = new TimelineAdapter(getFragmentManager(), getActivity(), userPreference);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        ButterKnife.bind(this, view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        timelineView.setLayoutManager(linearLayoutManager);
        timelineView.setAdapter(timelineAdapter);
        timelineView.addItemDecoration(new TimelineItemDecoration(getActivity()));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TwinownHelper.StreamSingleton.getInstance().stopAndDeleteUserStream(userPreference);
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    public void onEvent(final Component.StatusEvent statusEvent) {
        if (tab.type == Tab.TAB_TYPE_STREAM && Objects.equals(statusEvent.userPreference.userId, userPreference.userId)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    timelineAdapter.addStatus(statusEvent.status);
                    refreshTimelineView();
                }
            });
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(final Component.StatusListEvent statusListEvent) {
        if (Objects.equals(statusListEvent.userPreference.userId, userPreference.userId)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    timelineAdapter.addStatusList(statusListEvent.statuses);
                }
            });
        }
    }

    private void refreshTimelineView() {
        if (timelineView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                && linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
            linearLayoutManager.scrollToPosition(0);
        }
    }
}
