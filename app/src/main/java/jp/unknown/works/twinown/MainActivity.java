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
import butterknife.OnTextChanged;
import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.twinown_twitter.Component;
import jp.unknown.works.twinown.twinown_twitter.TwinownHelper;
import jp.unknown.works.twinown.twinown_twitter.TwinownService;
import jp.unknown.works.twinown.twinown_views.TimelinePagerAdapter;
import jp.unknown.works.twinown.models.Base;
import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.Status;

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
        UserPreference userPreference; // TODO 全部持つようにするはず
        TimelinePagerAdapter timelinePagerAdapter;
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {}
            @Override
            public void onServiceDisconnected(ComponentName name) {}
        };
        @Bind(R.id.timelinePager) ViewPager timelineViewPager;
        @Bind(R.id.tweetEditText) EditText tweetEditText;

        Status toReplyStatus;

        @SuppressWarnings("unused")
        @OnClick(R.id.tweetButton)
        public void statusUpdate() {
            if (tweetEditText.length() != 0) {
                TwinownHelper.updateStatus(userPreference, tweetEditText.getText().toString(), toReplyStatus);
                toReplyStatus = null;
                tweetEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
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

        @SuppressWarnings("unused")
        @OnTextChanged(R.id.tweetEditText)
        public void updateTweetEditText(CharSequence changedText) {
            if (changedText.length() == 0) {
                toReplyStatus = null;
                tweetEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            userPreference = UserPreference.get();
            TwinownHelper.getHomeTimeline(userPreference);
            Context context = getActivity().getApplicationContext();
            context.bindService(new Intent(context, TwinownService.class), serviceConnection, BIND_AUTO_CREATE);
            context.startService(new Intent(context, TwinownService.class));
            EventBus.getDefault().register(this);
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
            EventBus.getDefault().unregister(this);
        }

        @SuppressWarnings("unused")
        public void onEvent(final Component.MenuActionReply menuActionReply) {
            toReplyStatus = menuActionReply.toReplyStatus;
            Long inReplyToId = toReplyStatus.getId();
            final String userScreenName = toReplyStatus.getUser().getScreenName();
            tweetEditText.setText(String.format("@%s %s", userScreenName, tweetEditText.getText().toString()));
            tweetEditText.setSelection(tweetEditText.getText().toString().length());
            // TODO TextInputLayoutでうまいこと表現したかったけど無理だった（ライブラリのバグが治ったら挑戦する）
            tweetEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.abc_ic_menu_copy_mtrl_am_alpha, 0, 0, 0);
        }
    }
}
