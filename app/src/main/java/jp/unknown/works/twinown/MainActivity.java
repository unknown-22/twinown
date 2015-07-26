package jp.unknown.works.twinown;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

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
import jp.unknown.works.twinown.twinown_views.TimelineFragment;
import jp.unknown.works.twinown.twinown_views.TimelinePagerAdapter;
import jp.unknown.works.twinown.models.Base;
import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.Status;

public class MainActivity extends AppCompatActivity {
    MainFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setExitTransition(new Fade());
            getWindow().setEnterTransition(new Fade());
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        Base.initDataBase(getApplicationContext());
        if (Tab.getCount() < 1) {
            Intent intent = new Intent(Utils.ACTION_KEYWORD_AUTHORIZATION);
            intent.setClass(this, AuthActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        String theme = Utils.getPreferenceString(this, getString(R.string.preference_key_theme), "AppThemeDark");
        switch (theme) {
            case "AppThemeDark":
                setTheme(R.style.AppThemeDark);
                break;
            case "AppThemeLight":
                setTheme(R.style.AppThemeLight);
                break;
        }
        setContentView(R.layout.activity_main);
        fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    @Override
    public void onBackPressed() {
        if (!fragment.mainDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            fragment.mainDrawerLayout.openDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
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
        Animation inAnimation;
        Animation outAnimation;
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {}
            @Override
            public void onServiceDisconnected(ComponentName name) {}
        };
        @Bind(R.id.mainDrawerLayout) DrawerLayout mainDrawerLayout;
        @Bind(R.id.mainNavigation) NavigationView navigationView;
        @Bind(R.id.timelinePager) ViewPager timelineViewPager;
        @Bind(R.id.quick_post_view) RelativeLayout quickPostView;
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

        public void onChangeAccount() {
            final String[] userScreenNameList = new String[userPreferenceList.size()];
            for(int i = 0; i < userPreferenceList.size(); i++) {
                userScreenNameList[i] = String.format("@%s", userPreferenceList.get(i).screenName);
            }
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.action_change_account))
                    .setItems(userScreenNameList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            currentUserIndex = which;
                            TwinownHelper.getUser(userPreferenceList.get(currentUserIndex));
                            Utils.showToastLong(
                                    getActivity(),
                                    String.format(getString(R.string.notice_change_account), userScreenNameList[which])
                            );
                        }
                    })
                    .show();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            tabList = Tab.getAll();
            userPreferenceList = UserPreference.getAll();
            Context context = getActivity().getApplicationContext();
            context.bindService(new Intent(context, TwinownService.class), serviceConnection, BIND_AUTO_CREATE);
            context.startService(new Intent(context, TwinownService.class));
        }

        @Override
        public void onResume() {
            super.onResume();
            EventBus.getDefault().register(this);
            TwinownHelper.getUser(userPreferenceList.get(currentUserIndex));
        }

        @Override
        public void onPause() {
            super.onPause();
            EventBus.getDefault().unregister(this);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_main, container, false);
            ButterKnife.bind(this, view);
            FragmentManager fragmentManager = this.getFragmentManager();
            timelinePagerAdapter = new TimelinePagerAdapter(fragmentManager, tabList);
            timelineViewPager.setAdapter(timelinePagerAdapter);
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            if (Utils.getPreferenceBoolean(getActivity(), getString(R.string.preference_key_title_bar), true)) {
                toolbar.setTitle(getString(R.string.app_name));
                toolbar.setNavigationIcon(android.R.drawable.ic_menu_info_details);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mainDrawerLayout.openDrawer(GravityCompat.START);
                    }
                });
            } else {
                toolbar.setVisibility(Toolbar.GONE);
            }
            inAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.in_animation);
            outAnimation= AnimationUtils.loadAnimation(getActivity(), R.anim.out_animation);
            if (Utils.getPreferenceBoolean(getActivity(), getString(R.string.preference_key_quick_post), false)) {
                togglePostView();
            }
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    if (menuItem.getGroupId() == R.id.menu_tab) {
                        if (timelineViewPager.getCurrentItem() == menuItem.getItemId()) {
                            TimelineFragment timelineFragment = timelinePagerAdapter.findFragmentByPosition(timelineViewPager, menuItem.getItemId());
                            timelineFragment.moveOnTop();
                        } else {
                            timelineViewPager.setCurrentItem(menuItem.getItemId());
                        }
                        mainDrawerLayout.closeDrawers();
                        return true;
                    } else {
                        switch (menuItem.getItemId()) {
                            case R.id.action_tweet:
                                startActivity(new Intent(getActivity(), TweetActivity.class));
                                return true;
                            case R.id.action_toggle_quick_post:
                                togglePostView();
                                mainDrawerLayout.closeDrawers();
                                return true;
                            case R.id.action_change_account:
                                onChangeAccount();
                                mainDrawerLayout.closeDrawers();
                                return true;
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
                menu.add(R.id.menu_tab, i, 50+i, tabList.get(i).name);
            }

            return view;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(Utils.USER_STREAM_NOTIFICATION_TAG, Utils.USER_STREAM_NOTIFICATION_ID);
            Context context = getActivity().getApplicationContext();
            context.unbindService(serviceConnection);
            context.stopService(new Intent(context, TwinownService.class));
            TwinownHelper.StreamSingleton.getInstance().stopAllUserStream();
        }

        private void togglePostView() {
            if(quickPostView.getVisibility() == View.GONE){
                quickPostView.startAnimation(inAnimation);
                quickPostView.setVisibility(View.VISIBLE);
            } else{
                quickPostView.startAnimation(outAnimation);
                quickPostView.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(quickPostView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

        @SuppressWarnings("unused")
        public void onEvent(final Component.MenuActionReply menuActionReply) {
            toReplyStatus = menuActionReply.toReplyStatus;
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
