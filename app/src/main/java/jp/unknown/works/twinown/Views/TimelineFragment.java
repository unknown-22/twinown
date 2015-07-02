package jp.unknown.works.twinown.Views;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.Globals;
import jp.unknown.works.twinown.R;
import jp.unknown.works.twinown.Twitter.Component;
import jp.unknown.works.twinown.Twitter.TwinownHelper;
import jp.unknown.works.twinown.models.UserPreference;


public class TimelineFragment extends Fragment {
    private static final Handler handler = new Handler();
    private UserPreference userPreference;
    private LinearLayoutManager linearLayoutManager;
    private TimelineAdapter timelineAdapter;
    @Bind(R.id.timelineView) RecyclerView timelineView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        userPreference = (UserPreference) getArguments().getSerializable(Globals.ARGUMENTS_KEYWORD_USER_PREFERENCE);
        TwinownHelper.StreamSingleton.getInstance().getOrCreateTwitterStream(userPreference);
        TwinownHelper.StreamSingleton.getInstance().startUserStream(userPreference);
        timelineAdapter = new TimelineAdapter(getActivity());
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        ButterKnife.bind(this, view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        timelineView.setLayoutManager(linearLayoutManager);
        timelineView.setAdapter(timelineAdapter);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        TwinownHelper.StreamSingleton.getInstance().stopAndDeleteUserStream(userPreference);
    }

    @SuppressWarnings("unused")
    public void onEvent(final Component.StatusEvent statusEvent) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                timelineAdapter.addStatus(statusEvent.status);
                refreshTimelineView();
            }
        });
    }

    private void refreshTimelineView() {
        if (timelineView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                && linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
            linearLayoutManager.scrollToPosition(0);
        }
    }
}
