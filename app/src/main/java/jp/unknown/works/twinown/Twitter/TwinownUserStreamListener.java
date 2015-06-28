package jp.unknown.works.twinown.Twitter;

import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.models.Base;
import twitter4j.Status;
import twitter4j.UserStreamAdapter;

public class TwinownUserStreamListener extends UserStreamAdapter {

    public TwinownUserStreamListener() {

    }

    @Override
    public void onStatus(Status status) {
        // TODO statusをそのまま使わないようにする
        EventBus.getDefault().post(new Base.StatusEvent(status));
    }

}
