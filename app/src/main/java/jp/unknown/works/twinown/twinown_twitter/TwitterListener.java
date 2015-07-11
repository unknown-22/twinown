package jp.unknown.works.twinown.twinown_twitter;


import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.AccountSettings;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.User;
import twitter4j.UserList;

public class TwitterListener extends TwitterAdapter{
    private final UserPreference userPreference;

    public TwitterListener(UserPreference userPreference) {
        this.userPreference = userPreference;
    }

    @Override
    public void gotHomeTimeline(ResponseList<Status> statuses) {
        super.gotHomeTimeline(statuses);
        EventBus.getDefault().post(new Component.StatusListEvent(statuses, userPreference));
    }

    @Override
    public void gotUserDetail(User user) {
        super.gotUserDetail(user);
        EventBus.getDefault().post(new Component.UserEvent(user));
    }
}
