package jp.unknown.works.twinown;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.models.Tab;
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Base.initDataBase(getApplicationContext());
        if (Tab.getCount() < 1) {
            Intent intent = new Intent(Globals.ACTION_KEYWORD_AUTHORIZATION);
            intent.setClass(this, AuthActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setTheme(R.style.AppThemeDark);  // TODO テーマの設定
        setContentView(R.layout.activity_main);
    }

    public Drawable getDrawableResource(int id){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return getDrawable(id);
        }
        else{
            //noinspection deprecation
            return getResources().getDrawable(id);
        }
    }

    public static class MainFragment extends Fragment {
        List<Tab> tabList;
        List<UserPreference> userPreferenceList;
        int currentUserIndex = 0;
        TimelinePagerAdapter timelinePagerAdapter;
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {}
            @Override
            public void onServiceDisconnected(ComponentName name) {}
        };
        @Bind(R.id.mainDrawerLayout) DrawerLayout mainDrawerLayout;
        @Bind(R.id.mainNavigation) NavigationView navigationView;
        @Bind(R.id.timelinePager) ViewPager timelineViewPager;
        @Bind(R.id.tweetEditText) EditText tweetEditText;

        Status toReplyStatus;

        @SuppressWarnings("unused")
        @OnClick(R.id.tweetButton)
        public void statusUpdate() {
            if (tweetEditText.length() != 0) {
                TwinownHelper.updateStatus(userPreferenceList.get(currentUserIndex), tweetEditText.getText().toString(), toReplyStatus);
                toReplyStatus = null;
                Drawable[] drawables = tweetEditText.getCompoundDrawables();
                tweetEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawables[2], null);
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
                Drawable[] drawables = tweetEditText.getCompoundDrawables();
                tweetEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawables[2], null);
            }
        }

        @SuppressWarnings("unused")
        @OnClick(R.id.fab_setting)
        public void onClickFabSetting() {
            startActivity(new Intent(getActivity(), SettingActivity.class));
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            tabList = Tab.getALL();
            userPreferenceList = UserPreference.getAll();
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
            timelinePagerAdapter = new TimelinePagerAdapter(fragmentManager, tabList);
            timelineViewPager.setAdapter(timelinePagerAdapter);
            TwinownHelper.getUser(userPreferenceList.get(currentUserIndex));
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.app_name));
            toolbar.setNavigationIcon(android.R.drawable.ic_menu_info_details);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    if (menuItem.getGroupId() == R.id.menu_tab) {
                        timelineViewPager.setCurrentItem(menuItem.getItemId());
                        mainDrawerLayout.closeDrawers();
                        return true;
                    } else if (menuItem.getGroupId() == R.id.menu_other) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_settings:
                                startActivity(new Intent(getActivity(), SettingActivity.class));
                                return true;
                        }
                    }
                    return false;
                }
            });
            Menu menu = navigationView.getMenu();
            for(int i = 0; i < tabList.size(); i++) {
                menu.add(R.id.menu_tab, i, i, tabList.get(i).name);
            }

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
            Drawable replyIconDrawable = ((MainActivity) getActivity()).getDrawableResource(R.drawable.ic_reply_white);
            Drawable[] drawables = tweetEditText.getCompoundDrawables();
            tweetEditText.setCompoundDrawablesWithIntrinsicBounds(replyIconDrawable, null, drawables[2], null);
        }

        @SuppressWarnings("unused")
        public void onEventMainThread(Component.UserEvent userEvent) {
            Picasso.with(getActivity()).load(userEvent.user.getBiggerProfileImageURL()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                    bitmapDrawable.setAlpha(128);
                    tweetEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, bitmapDrawable, null);
                }

                @Override
                public void onBitmapFailed(final Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(final Drawable placeHolderDrawable) {
                }
            });
        }
    }
}
