package jp.unknown.works.twinown;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import jp.unknown.works.twinown.twinown_views.TweetFragment;
import jp.unknown.works.twinown.twinown_views.UserFragment;

public class TweetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.fragment) == null) {
            Fragment tweetFragment = new TweetFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment, tweetFragment).commit();
        }
        setContentView(R.layout.activity_tweet);
    }
}
