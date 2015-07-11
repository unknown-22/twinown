package jp.unknown.works.twinown.twinown_twitter;


import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.User;


public class Component {
    public static class StatusListEvent {
        public final ResponseList<Status> statuses;
        public final UserPreference userPreference;
        public StatusListEvent(ResponseList<Status> statuses, UserPreference userPreference) {
            this.statuses = statuses;
            this.userPreference = userPreference;
        }
    }

    public static class StatusEvent {
        public final Status status;
        public final UserPreference userPreference;
        public StatusEvent(Status status, UserPreference userPreference) {
            this.status = status;
            this.userPreference = userPreference;
        }
    }

    public static class MenuActionReply {
        public final Status toReplyStatus;
        public MenuActionReply(Status toReplyStatus) {
            this.toReplyStatus = toReplyStatus;
        }
    }

    public static class UserEvent {
        public final User user;
        public UserEvent(User user) {
            this.user = user;
        }
    }
}
