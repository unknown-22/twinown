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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import jp.unknown.works.twinown.twinown_twitter.TwinownHelper;
import jp.unknown.works.twinown.twinown_twitter.TwinownService;
import jp.unknown.works.twinown.twinown_views.TimelinePagerAdapter;
import jp.unknown.works.twinown.models.Base;
import jp.unknown.works.twinown.models.UserPreference;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Base.initDataBase(getApplicationContext());
        if (UserPreference.getCount() == 0) {
            Intent intent = new Intent(Globals.ACTION_KEYWORD_AUTHORIZATION);
            intent.setClass(this, AuthActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {}
            @Override
            public void onServiceDisconnected(ComponentName name) {}
        };
        @Bind(R.id.timelinePager) ViewPager timelineViewPager;
        @Bind(R.id.tweetEditText) EditText tweetEditText;

        @SuppressWarnings("unused")
        @OnClick(R.id.tweetButton)
        public void statusUpdate() {
            if (tweetEditText.length() != 0) {
                UserPreference userPreference = UserPreference.get();
                TwinownHelper.statusUpdate(userPreference, tweetEditText.getText().toString());
                tweetEditText.setText("");
            }
        }

        @SuppressWarnings("unused")
        @OnEditorAction(R.id.tweetEditText)
        public boolean statusQuickUpdate(KeyEvent keyEvent) {
            if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                statusUpdate();
                return true;
            }
            return false;
        }

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
