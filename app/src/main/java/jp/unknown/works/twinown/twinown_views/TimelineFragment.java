package jp.unknown.works.twinown.twinown_views;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.Globals;
import jp.unknown.works.twinown.R;
import jp.unknown.works.twinown.models.Tab;
import jp.unknown.works.twinown.twinown_twitter.Component;
import jp.unknown.works.twinown.twinown_twitter.TwinownHelper;
import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;


public class TimelineFragment extends Fragment {
    private static final Handler handler = new Handler();
    private Tab tab;
    private UserPreference userPreference;
    private LinearLayoutManager linearLayoutManager;
    private TimelineAdapter timelineAdapter;
    private Pattern userScreenNamePattern;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.timelineView) RecyclerView timelineView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        tab = (Tab) getArguments().getSerializable(Globals.ARGUMENTS_KEYWORD_TAB);
        userPreference = UserPreference.get(tab != null ? tab.userId : null);
        initializeTwitter();
        timelineAdapter = new TimelineAdapter(getFragmentManager(), getActivity(), userPreference);
        userScreenNamePattern = Pattern.compile(Pattern.quote(String.format("@%s", userPreference.screenName)), Pattern.CASE_INSENSITIVE);
        EventBus.getDefault().register(this);
    }

    private void initializeTwitter() {
        if (tab.type == Tab.TAB_TYPE_STREAM) {
            TwinownHelper.StreamSingleton.getInstance().getOrCreateTwitterStream(userPreference);
            TwinownHelper.StreamSingleton.getInstance().startUserStream(userPreference);
        }
        headUpdate(0);
    }

    private void headUpdate(long sinceId) {
        Paging paging = new Paging();
        if (sinceId > 0) {
            paging.setSinceId(sinceId);
        }
        switch (tab.type) {
            case Tab.TAB_TYPE_STREAM:
                TwinownHelper.getHomeTimeline(userPreference, paging);
                break;
            case Tab.TAB_TYPE_MENTION:
                TwinownHelper.getMentionTimeline(userPreference, paging);
                break;
            case Tab.TAB_TYPE_LIST:
                TwinownHelper.getTabTimeline(userPreference, tab.getListId(), paging);
                break;
        }
    }

    private void tailUpdate(long maxId) {
        Paging paging = new Paging();
        paging.setMaxId(maxId);
        switch (tab.type) {
            case Tab.TAB_TYPE_STREAM:
                TwinownHelper.getHomeTimeline(userPreference, paging);
                break;
            case Tab.TAB_TYPE_MENTION:
                TwinownHelper.getMentionTimeline(userPreference, paging);
                break;
            case Tab.TAB_TYPE_LIST:
                TwinownHelper.getTabTimeline(userPreference, tab.getListId(), paging);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        ButterKnife.bind(this, view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        timelineView.setLayoutManager(linearLayoutManager);
        timelineView.setAdapter(timelineAdapter);
        timelineView.addItemDecoration(new TimelineItemDecoration(getActivity()));
        timelineView.addOnScrollListener(new InfiniteScrollListener());
        if (tab.type == Tab.TAB_TYPE_STREAM) {
            swipeRefreshLayout.setEnabled(false);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                headUpdate(timelineAdapter.getStatus(0).getId());
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (timelineAdapter != null) {
            timelineAdapter.refreshActivity(getFragmentManager(), activity);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finalizeTwitter();
        EventBus.getDefault().unregister(this);
    }

    private void finalizeTwitter() {
        switch (tab.type) {
            case Tab.TAB_TYPE_STREAM:
                TwinownHelper.StreamSingleton.getInstance().stopAndDeleteUserStream(userPreference);
                break;
            case Tab.TAB_TYPE_MENTION:
                break;
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(final Component.StatusEvent statusEvent) {
        if (isAddAdapter(statusEvent)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    timelineAdapter.addStatus(statusEvent.status);
                    followOnTop();
                }
            });
        }
    }

    private boolean isAddAdapter(final Component.StatusEvent statusEvent) {
        return (tab.type == Tab.TAB_TYPE_STREAM || (tab.type == Tab.TAB_TYPE_MENTION && userScreenNamePattern.matcher(statusEvent.status.getText()).find())
            ) && Objects.equals(statusEvent.userPreference.userId, userPreference.userId);
    }

    @SuppressWarnings("unused")
    public void onEvent(final Component.HomeStatusListEvent homeStatusListEvent) {
        if (tab.type == Tab.TAB_TYPE_STREAM && Objects.equals(homeStatusListEvent.userPreference.userId, userPreference.userId)) {
            addStatusList(homeStatusListEvent.statuses);
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(final Component.FavoriteEvent favoriteEvent) {
        if (tab.type == Tab.TAB_TYPE_STREAM && Objects.equals(favoriteEvent.userPreference.userId, userPreference.userId)) {
            String text = favoriteEvent.status.getText();
            if (text.length() > 31) {
                text = String.format("%s...", text.substring(0, 30));
            }
            Globals.showToastShort(getActivity(), String.format("お気に入りに追加しました(@%s %s)", favoriteEvent.target.getScreenName(), text));
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(final Component.FavoritedEvent favoritedEvent) {
        if (tab.type == Tab.TAB_TYPE_STREAM && Objects.equals(favoritedEvent.userPreference.userId, userPreference.userId)) {
            String text = favoritedEvent.status.getText();
            if (text.length() > 31) {
                text = String.format("%s...", text.substring(0, 30));
            }
            Globals.showToastShort(getActivity(), String.format("@%sさんがお気に入りに追加しました(%s)", favoritedEvent.source.getScreenName(), text));
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(final Component.MentionStatusListEvent mentionStatusListEvent) {
        if (tab.type == Tab.TAB_TYPE_MENTION && Objects.equals(mentionStatusListEvent.userPreference.userId, userPreference.userId)) {
            addStatusList(mentionStatusListEvent.statuses);
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(final Component.UserListStatusesEvent userListStatusesEvent) {
        if (tab.type == Tab.TAB_TYPE_LIST && Objects.equals(userListStatusesEvent.userPreference.userId, userPreference.userId)) {
            addStatusList(userListStatusesEvent.statuses);
        }
    }

    private void addStatusList(final ResponseList<Status> statuses) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                timelineAdapter.addStatusList(statuses);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void followOnTop() {
        if (timelineView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                && linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
            linearLayoutManager.scrollToPosition(0);
        }
    }

    public void moveOnTop() {
        timelineView.smoothScrollToPosition(0);
    }

    private class InfiniteScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!swipeRefreshLayout.isRefreshing() && timelineAdapter.getItemCount()-1 == linearLayoutManager.findLastVisibleItemPosition()) {
                swipeRefreshLayout.setRefreshing(true);
                tailUpdate(timelineAdapter.getStatus(timelineAdapter.getItemCount() - 1).getId());
            }
        }
    }
}
