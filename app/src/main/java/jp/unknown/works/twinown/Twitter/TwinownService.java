package jp.unknown.works.twinown.Twitter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import jp.unknown.works.twinown.Globals;
import jp.unknown.works.twinown.MainActivity;
import jp.unknown.works.twinown.R;


public class TwinownService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_NO_CREATE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setTicker(getText(R.string.user_stream_notification_ticker));
        builder.setContentTitle(getText(R.string.user_stream_notification_title));
        builder.setContentText(getText(R.string.user_stream_notification_text));
        builder.setSmallIcon(android.R.drawable.ic_dialog_info);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(Globals.USER_STREAM_NOTIFICATION_TAG, Globals.USER_STREAM_NOTIFICATION_ID, notification);
        startForeground(Globals.USER_STREAM_NOTIFICATION_ID, notification);
        return null;
    }

    @Override
    public void onTaskRemoved(Intent intent) {
        super.onTaskRemoved(intent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(Globals.USER_STREAM_NOTIFICATION_TAG, Globals.USER_STREAM_NOTIFICATION_ID);
        TwinownHelper.StreamSingleton.getInstance().stopAllUserStream();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
