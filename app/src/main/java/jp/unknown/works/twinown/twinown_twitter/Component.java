package jp.unknown.works.twinown.twinown_twitter;


import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.User;
import twitter4j.UserList;


public class Component {
    public static class HomeStatusListEvent {
        public final ResponseList<Status> statuses;
        public final UserPreference userPreference;
        public HomeStatusListEvent(ResponseList<Status> statuses, UserPreference userPreference) {
            this.statuses = statuses;
            this.userPreference = userPreference;
        }
    }

    public static class MentionStatusListEvent {
        public final ResponseList<Status> statuses;
        public final UserPreference userPreference;
        public MentionStatusListEvent(ResponseList<Status> statuses, UserPreference userPreference) {
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

    public static class UserListsEvent {
        public final ResponseList<UserList> userLists;
        public UserListsEvent(ResponseList<UserList> userLists) {
            this.userLists = userLists;
        }
    }

    public static class UserListStatusesEvent {
        public final ResponseList<Status> statuses;
        public final UserPreference userPreference;
        public UserListStatusesEvent(ResponseList<Status> statuses, UserPreference userPreference) {
            this.statuses = statuses;
            this.userPreference = userPreference;
        }
    }
}
