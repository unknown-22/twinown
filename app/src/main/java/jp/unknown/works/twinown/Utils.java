package jp.unknown.works.twinown;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Utils {
    public static final String DATABASE_NAME = "twinown.db";

    public static final String ACTION_KEYWORD_AUTHORIZATION = "action_keyword_authorization";

    public static final String ARGUMENTS_KEYWORD_USER_PREFERENCE = "arguments_keyword_user_preference";
    public static final String ARGUMENTS_KEYWORD_TAB = "arguments_keyword_tab";
    public static final String ARGUMENTS_KEYWORD_STATUS = "arguments_keyword_status";
    public static final String ARGUMENTS_KEYWORD_USER = "arguments_keyword_user";
    public static final String ARGUMENTS_KEYWORD_MEDIA_URL = "arguments_keyword_url";

    public static final String SHARED_ELEMENT_NAME_STATUS_ICON = "shared_element_name_status_icon";

    public static final String USER_STREAM_NOTIFICATION_TAG = "user_stream_notification_tag";
    public static final int USER_STREAM_NOTIFICATION_ID = 0;

    @SuppressWarnings("unused")
    public static void debugLog(String message) {
        String debugLogTag = "TWINOWN_DEBUG";
        Log.d(debugLogTag, message);
    }

    public static void showSnackBar(View view, CharSequence message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public static void showToastLong(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showToastShort(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getPreferenceString(Context context, String preferenceKey, String defaultString) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(preferenceKey, defaultString);
    }

    public static boolean getPreferenceBoolean(Context context, String preferenceKey, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(preferenceKey, defaultValue);
    }
}
