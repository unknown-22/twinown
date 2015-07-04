package jp.unknown.works.twinown.twinown_twitter;

import de.greenrobot.event.EventBus;
import twitter4j.Status;
import twitter4j.UserStreamAdapter;

class TwinownUserStreamListener extends UserStreamAdapter {

    public TwinownUserStreamListener() {

    }

    @Override
    public void onStatus(Status status) {
        EventBus.getDefault().post(new Component.StatusEvent(status));
    }

}
