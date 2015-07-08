package jp.unknown.works.twinown.twinown_twitter;

import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.Status;
import twitter4j.UserStreamAdapter;

class TwinownUserStreamListener extends UserStreamAdapter {
    UserPreference userPreference;

    public TwinownUserStreamListener(UserPreference userPreference) {
        this.userPreference = userPreference;
    }

    @Override
    public void onStatus(Status status) {
        EventBus.getDefault().post(new Component.StatusEvent(status, userPreference));
    }
}
