package jp.unknown.works.twinown;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import jp.unknown.works.twinown.models.UserPreference;
import jp.unknown.works.twinown.twinown_views.TalkFragment;
import twitter4j.Status;

public class TalkActivity extends AppCompatActivity {
    UserPreference userPreference;
    Status rootStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (userPreference == null && rootStatus == null) {
            userPreference = (UserPreference) getIntent().getSerializableExtra(Utils.ARGUMENTS_KEYWORD_USER_PREFERENCE);
            rootStatus = (Status) getIntent().getSerializableExtra(Utils.ARGUMENTS_KEYWORD_STATUS);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.fragment) == null) {
            Fragment talkFragment = new TalkFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Utils.ARGUMENTS_KEYWORD_USER_PREFERENCE, userPreference);
            bundle.putSerializable(Utils.ARGUMENTS_KEYWORD_STATUS, rootStatus);
            talkFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.fragment, talkFragment).commit();
        }
        setContentView(R.layout.activity_talk);
    }
}
