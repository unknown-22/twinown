package jp.unknown.works.twinown;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import jp.unknown.works.twinown.models.Base;
import jp.unknown.works.twinown.models.Tab;
import jp.unknown.works.twinown.models.UserPreference;
import jp.unknown.works.twinown.twinown_views.TweetFragment;
import jp.unknown.works.twinown.twinown_views.UserFragment;

public class TweetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Base.initDataBase(getApplicationContext());
        if (UserPreference.getCount() < 1) {
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.fragment) == null) {
            Fragment tweetFragment = new TweetFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment, tweetFragment).commit();
        }
        setContentView(R.layout.activity_tweet);
    }
}
