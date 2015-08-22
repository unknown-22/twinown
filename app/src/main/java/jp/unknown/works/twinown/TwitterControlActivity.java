package jp.unknown.works.twinown;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TwitterControlActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_control);
        ButterKnife.bind(this);
        if(savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.settings_container, new AppearanceControlFragment())
                    .commit();
        }
        toolbar.setTitle(getString(R.string.setting_action_twitter));
    }

    public static class AppearanceControlFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.twitter_preference);
        }

        @Override
        public void onResume() {
            super.onResume();
            reloadSummary();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
        }

        private void reloadSummary(){
            ListAdapter adapter = getPreferenceScreen().getRootAdapter();
            for (int i=0;i<adapter.getCount();i++){
                Object item = adapter.getItem(i);
                if (item instanceof ListPreference){
                    ListPreference preference = (ListPreference) item;
                    preference.setSummary(preference.getEntry() == null ? "" : preference.getEntry());
                }
            }
        }

        private SharedPreferences.OnSharedPreferenceChangeListener listener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        reloadSummary();
                    }
                };
    }
}
