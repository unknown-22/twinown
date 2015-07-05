package jp.unknown.works.twinown.twinown_twitter;


import twitter4j.ResponseList;
import twitter4j.Status;


public class Component {
    public static class StatusListEvent {
        public final ResponseList<Status> statuses;
        public StatusListEvent(ResponseList<Status> statuses) {
            this.statuses = statuses;
        }
    }

    public static class StatusEvent {
        public final Status status;
        public StatusEvent(Status status) {
            this.status = status;
        }
    }
}
