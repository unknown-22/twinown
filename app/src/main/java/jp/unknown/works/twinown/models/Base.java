package jp.unknown.works.twinown.models;

import android.content.Context;

import jp.unknown.works.twinown.Globals;
import ollie.Ollie;
import twitter4j.Status;

public class Base {
    public static void initDataBase(Context context) {
        Ollie.with(context)
                .setName(Globals.DATABASE_NAME)
                .setVersion(1)
                .setLogLevel(Ollie.LogLevel.FULL)
                .init();
    }

    public static class StatusEvent {
        public Status status;

        public StatusEvent(Status status) {
            this.status = status;
        }
    }
}
