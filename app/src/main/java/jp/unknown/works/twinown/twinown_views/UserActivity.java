package jp.unknown.works.twinown.twinown_views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.R;
import jp.unknown.works.twinown.TweetActivity;
import jp.unknown.works.twinown.Utils;
import jp.unknown.works.twinown.models.UserPreference;
import jp.unknown.works.twinown.twinown_twitter.Component;
import twitter4j.User;

public class UserActivity extends AppCompatActivity {
    UserPreference userPreference;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (userPreference == null && user == null) {
            userPreference = (UserPreference) getIntent().getSerializableExtra(Utils.ARGUMENTS_KEYWORD_USER_PREFERENCE);
            user = (User) getIntent().getSerializableExtra(Utils.ARGUMENTS_KEYWORD_USER);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.fragment) == null) {
            Fragment userFragment = new UserFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Utils.ARGUMENTS_KEYWORD_USER_PREFERENCE, userPreference);
            bundle.putSerializable(Utils.ARGUMENTS_KEYWORD_USER, user);
            userFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.fragment, userFragment).commit();
        }
        setContentView(R.layout.activity_user);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    public void onEvent(final Component.MenuActionReply menuActionReply) {
        startActivity(new Intent(this, TweetActivity.class)
                .putExtra(Utils.ARGUMENTS_KEYWORD_STATUS, menuActionReply.toReplyStatus));
    }
}
