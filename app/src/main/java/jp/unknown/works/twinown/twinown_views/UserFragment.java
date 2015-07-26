package jp.unknown.works.twinown.twinown_views;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.R;
import jp.unknown.works.twinown.Utils;
import jp.unknown.works.twinown.models.UserPreference;
import jp.unknown.works.twinown.twinown_twitter.Component;
import jp.unknown.works.twinown.twinown_twitter.TwinownHelper;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.User;

public class UserFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {
    private final Handler handler = new Handler();
    private UserPreference userPreference;
    private User user;
    private LinearLayoutManager linearLayoutManager;
    private TimelineAdapter timelineAdapter;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.appbar) AppBarLayout appBarLayout;
    @Bind(R.id.userBannerView) ImageView userBannerView;
    @Bind(R.id.userIconView) ImageView userIconView;
    @Bind(R.id.userBio) TextView userBio;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.timelineView) RecyclerView timelineView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        userPreference = (UserPreference) getArguments().getSerializable(Utils.ARGUMENTS_KEYWORD_USER_PREFERENCE);
        user = (User) getArguments().getSerializable(Utils.ARGUMENTS_KEYWORD_USER);
        if (user != null) {
            TwinownHelper.getUserTimeLine(userPreference, user.getId(), new Paging());
        }
        timelineAdapter = new TimelineAdapter(getFragmentManager(), getActivity(), userPreference);
        EventBus.getDefault().register(this);
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
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, view);
        collapsingToolbarLayout.setTitle(String.format("%s : @%s", user.getName(), user.getScreenName()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            userIconView.setTransitionName(Utils.SHARED_ELEMENT_NAME_STATUS_ICON);
        }
        RoundedTransformation transform = new RoundedTransformation((int) (getActivity().getResources().getDimension(R.dimen.icon_size) / 8));
        Picasso.with(getActivity()).load(user.getProfileBannerMobileRetinaURL()).into(userBannerView);
        Picasso.with(getActivity()).load(user.getBiggerProfileImageURL()).transform(transform).into(userIconView);
        userBio.setText(user.getDescription());
        linearLayoutManager = new LinearLayoutManager(getActivity());
        timelineView.setLayoutManager(linearLayoutManager);
        timelineView.setAdapter(timelineAdapter);
        timelineView.addItemDecoration(new TimelineItemDecoration(getActivity()));
        timelineView.addOnScrollListener(new InfiniteScrollListener());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                headUpdate(timelineAdapter.getStatus(0).getId());
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        swipeRefreshLayout.setEnabled(i == 0);
    }

    @SuppressWarnings("unused")
    public void onEvent(final Component.UserTimeLineEvent userTimeLineEvent) {
        if (Objects.equals(userTimeLineEvent.userPreference.userId, userPreference.userId)
                && userTimeLineEvent.userId == user.getId()) {
            addStatusList(userTimeLineEvent.statuses);
        }
    }

    private void addStatusList(final ResponseList<Status> statuses) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (statuses == null) {
                    Utils.showToastLong(getActivity(), getString(R.string.error));
                    getActivity().finish();
                    return;
                }
                timelineAdapter.addStatusList(statuses);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void headUpdate(long sinceId) {
        Paging paging = new Paging();
        if (sinceId > 0) {
            paging.setSinceId(sinceId);
        }
        TwinownHelper.getUserTimeLine(userPreference, user.getId(), paging);
    }

    private void tailUpdate(long maxId) {
        Paging paging = new Paging();
        paging.setMaxId(maxId);
        TwinownHelper.getUserTimeLine(userPreference, user.getId(), paging);
    }

    private class InfiniteScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!swipeRefreshLayout.isRefreshing() && timelineAdapter.getItemCount()-(1 + 3) <= linearLayoutManager.findLastVisibleItemPosition()) {
                swipeRefreshLayout.setRefreshing(true);
                tailUpdate(timelineAdapter.getStatus(timelineAdapter.getItemCount() - 1).getId());
            }
        }
    }
}
