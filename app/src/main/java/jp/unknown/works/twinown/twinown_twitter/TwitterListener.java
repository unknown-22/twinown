package jp.unknown.works.twinown.twinown_twitter;


import de.greenrobot.event.EventBus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;

public class TwitterListener extends TwitterAdapter{
    @Override
    public void gotHomeTimeline(ResponseList<Status> statuses) {
        super.gotHomeTimeline(statuses);
        EventBus.getDefault().post(new Component.StatusListEvent(statuses));
    }
}
