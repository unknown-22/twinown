package jp.unknown.works.twinown;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Globals {
    public static final String DATABASE_NAME = "twinown.db";

    public static final String ACTION_KEYWORD_AUTHORIZATION = "action_keyword_authorization";
    public static final String EXTRA_KEYWORD_CONSUMER_KEY = "extra_keyword_consumer_key";
    public static final String EXTRA_KEYWORD_CONSUMER_SECRET = "extra_keyword_consumer_secret";

    public static final String ARGUMENTS_KEYWORD_USER_PREFERENCE = "arguments_keyword_user_preference";

    @SuppressWarnings("unused")
    public static void debugLog(String message) {
        String debugLogTag = "TWINOWN_DEBUG";
        Log.d(debugLogTag, message);
    }

    public static void showSnackBar(View view, CharSequence message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public static void showToast(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
