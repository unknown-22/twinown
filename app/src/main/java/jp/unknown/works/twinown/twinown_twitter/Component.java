package jp.unknown.works.twinown.twinown_twitter;


import android.support.annotation.NonNull;

import java.util.Date;

import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.ExtendedMediaEntity;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserMentionEntity;


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

    public static class UserTimeLineEvent {
        public final ResponseList<Status> statuses;
        public final UserPreference userPreference;
        public final long userId;
        public UserTimeLineEvent(ResponseList<Status> statuses, UserPreference userPreference, long userId) {
            this.statuses = statuses;
            this.userPreference = userPreference;
            this.userId = userId;
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

    public static class DeleteEvent {
        public final long statusId;
        public DeleteEvent(long statusId) {
            this.statusId = statusId;
        }
    }

    public static class FavoritedEvent {
        public final User source;
        public final User target;
        public final Status status;
        public final UserPreference userPreference;
        public FavoritedEvent(User source, User target, Status status, UserPreference userPreference) {
            this.source = source;
            this.target = target;
            this.status = status;
            this.userPreference = userPreference;
        }
    }

    public static class FavoriteEvent {
        public final User source;
        public final User target;
        public final Status status;
        public final UserPreference userPreference;
        public FavoriteEvent(User source, User target, Status status, UserPreference userPreference) {
            this.source = source;
            this.target = target;
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
        public final long listId;
        public UserListStatusesEvent(ResponseList<Status> statuses, UserPreference userPreference, long listId) {
            this.statuses = statuses;
            this.userPreference = userPreference;
            this.listId = listId;
        }
    }

    public static class DummyStatus implements Status {
        private long id;

        public DummyStatus (long statusId) {
            this.id = statusId;
        }

        @Override
        public Date getCreatedAt() {
            return null;
        }

        @Override
        public long getId() {
            return id;
        }

        @Override
        public String getText() {
            return null;
        }

        @Override
        public String getSource() {
            return null;
        }

        @Override
        public boolean isTruncated() {
            return false;
        }

        @Override
        public long getInReplyToStatusId() {
            return 0;
        }

        @Override
        public long getInReplyToUserId() {
            return 0;
        }

        @Override
        public String getInReplyToScreenName() {
            return null;
        }

        @Override
        public GeoLocation getGeoLocation() {
            return null;
        }

        @Override
        public Place getPlace() {
            return null;
        }

        @Override
        public boolean isFavorited() {
            return false;
        }

        @Override
        public boolean isRetweeted() {
            return false;
        }

        @Override
        public int getFavoriteCount() {
            return 0;
        }

        @Override
        public User getUser() {
            return null;
        }

        @Override
        public boolean isRetweet() {
            return false;
        }

        @Override
        public Status getRetweetedStatus() {
            return null;
        }

        @Override
        public long[] getContributors() {
            return new long[0];
        }

        @Override
        public int getRetweetCount() {
            return 0;
        }

        @Override
        public boolean isRetweetedByMe() {
            return false;
        }

        @Override
        public long getCurrentUserRetweetId() {
            return 0;
        }

        @Override
        public boolean isPossiblySensitive() {
            return false;
        }

        @Override
        public String getLang() {
            return null;
        }

        @Override
        public Scopes getScopes() {
            return null;
        }

        @Override
        public String[] getWithheldInCountries() {
            return new String[0];
        }

        @Override
        public long getQuotedStatusId() {
            return 0;
        }

        @Override
        public Status getQuotedStatus() {
            return null;
        }

        @Override
        public int compareTo(@NonNull Status that) {
            long delta = this.id - that.getId();
            if (delta < Integer.MIN_VALUE) {
                return Integer.MIN_VALUE;
            } else if (delta > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            return (int) delta;
        }

        @Override
        public UserMentionEntity[] getUserMentionEntities() {
            return new UserMentionEntity[0];
        }

        @Override
        public URLEntity[] getURLEntities() {
            return new URLEntity[0];
        }

        @Override
        public HashtagEntity[] getHashtagEntities() {
            return new HashtagEntity[0];
        }

        @Override
        public MediaEntity[] getMediaEntities() {
            return new MediaEntity[0];
        }

        @Override
        public ExtendedMediaEntity[] getExtendedMediaEntities() {
            return new ExtendedMediaEntity[0];
        }

        @Override
        public SymbolEntity[] getSymbolEntities() {
            return new SymbolEntity[0];
        }

        @Override
        public RateLimitStatus getRateLimitStatus() {
            return null;
        }

        @Override
        public int getAccessLevel() {
            return 0;
        }
    }
}
