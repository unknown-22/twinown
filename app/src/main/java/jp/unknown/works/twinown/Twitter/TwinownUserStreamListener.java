package jp.unknown.works.twinown.Twitter;

import de.greenrobot.event.EventBus;
import twitter4j.Status;
import twitter4j.UserStreamAdapter;

class TwinownUserStreamListener extends UserStreamAdapter {

    public TwinownUserStreamListener() {

    }

    @Override
    public void onStatus(Status status) {
        // TODO statusをそのまま使わないようにする
        EventBus.getDefault().post(new Component.StatusEvent(status));
    }

}
