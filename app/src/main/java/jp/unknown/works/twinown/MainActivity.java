package jp.unknown.works.twinown;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.unknown.works.twinown.Twitter.TwinownHelper;
import jp.unknown.works.twinown.Twitter.TwinownService;
import jp.unknown.works.twinown.Views.TimelinePagerAdapter;
import jp.unknown.works.twinown.models.Base;
import jp.unknown.works.twinown.models.UserPreference;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO DBのinitを毎回やる必要はないはず。画面回転時の最適化を検討。
        Base.initDataBase(getApplicationContext());
        if (UserPreference.getCount() == 0) {
            Intent intent = new Intent(Globals.ACTION_KEYWORD_AUTHORIZATION);
            intent.setClass(this, AuthActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setTheme(R.style.AppThemeDark);  // TODO テーマの設定
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class MainFragment extends Fragment {
        TimelinePagerAdapter timelinePagerAdapter;
        @Bind(R.id.timelinePager) ViewPager timelineViewPager;
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {}
            @Override
            public void onServiceDisconnected(ComponentName name) {}
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            Context context = getActivity().getApplicationContext();
            context.bindService(new Intent(context, TwinownService.class), serviceConnection, BIND_AUTO_CREATE);
            context.startService(new Intent(context, TwinownService.class));
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_main, container, false);
            ButterKnife.bind(this, view);
            FragmentManager fragmentManager = this.getFragmentManager();
            timelinePagerAdapter = new TimelinePagerAdapter(fragmentManager);
            timelineViewPager.setAdapter(timelinePagerAdapter);
            return view;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(Globals.USER_STREAM_NOTIFICATION_TAG, Globals.USER_STREAM_NOTIFICATION_ID);
            Context context = getActivity().getApplicationContext();
            context.unbindService(serviceConnection);
            context.stopService(new Intent(context, TwinownService.class));
            TwinownHelper.StreamSingleton.getInstance().stopAllUserStream();
        }
    }
}
