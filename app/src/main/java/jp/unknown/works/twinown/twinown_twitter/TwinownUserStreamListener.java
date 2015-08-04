package jp.unknown.works.twinown.twinown_twitter;

import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
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

    @Override
    public void onFavorite(User source, User target, Status favoritedStatus) {
        if (source.getId() == userPreference.userId) {
            EventBus.getDefault().post(new Component.FavoriteEvent(source, target, favoritedStatus, userPreference));
        } else if (target.getId() == userPreference.userId) {
            EventBus.getDefault().post(new Component.FavoritedEvent(source, target, favoritedStatus, userPreference));
        }
    }

    @Override
    public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
        super.onUnfavorite(source, target, unfavoritedStatus);
        if (source.getId() == userPreference.userId) {
            EventBus.getDefault().post(new Component.UnFavoriteEvent(source, target, unfavoritedStatus, userPreference));
        }
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        super.onDeletionNotice(statusDeletionNotice);
        EventBus.getDefault().post(new Component.DeleteEvent(statusDeletionNotice.getStatusId()));
    }
}
