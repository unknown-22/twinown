package jp.unknown.works.twinown;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.Twitter.TimelineAdapter;
import jp.unknown.works.twinown.Twitter.TwinownHelper;
import jp.unknown.works.twinown.models.Base;
import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.TwitterStream;


public class TimelineFragment extends Fragment {
    private static Handler handler = new Handler();
    TwitterStream twitterStream;
    LinearLayoutManager linearLayoutManager;
    TimelineAdapter timelineAdapter;
    @InjectView(R.id.timeline_view) RecyclerView timelineView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserPreference userPreference = UserPreference.get();
        twitterStream = TwinownHelper.createUserStream(userPreference);
        TwinownHelper.startUserStream(twitterStream);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        ButterKnife.inject(this, view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        //linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        timelineAdapter = new TimelineAdapter(getActivity());
        timelineView.setLayoutManager(linearLayoutManager);
        timelineView.setAdapter(timelineAdapter);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        TwinownHelper.stopUserStream(twitterStream);
    }

    @SuppressWarnings("unused")
    public void onEvent(final Base.StatusEvent statusEvent) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                timelineAdapter.addStatus(statusEvent.status);
                timelineAdapter.notifyItemInserted(0);
                timelineView.smoothScrollToPosition(0);  // TODO Topに居る、スクロール中でない場合のみ
            }
        });
    }
}
