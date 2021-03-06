package jp.unknown.works.twinown.models;

import android.content.Context;

import jp.unknown.works.twinown.Utils;
import ollie.Ollie;

public class Base {
    public static void initDataBase(Context context) {
        Ollie.with(context)
                .setName(Utils.DATABASE_NAME)
                .setVersion(1)
                .setLogLevel(Ollie.LogLevel.NONE)
                .init();
    }
}
