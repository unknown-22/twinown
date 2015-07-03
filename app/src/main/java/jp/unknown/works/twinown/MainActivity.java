package jp.unknown.works.twinown;

import android.content.Intent;
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
        // setTheme(R.style.AppThemeLight);  // TODO
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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_main, container, false);
            ButterKnife.bind(this, view);
            FragmentManager fragmentManager = this.getFragmentManager();
            timelinePagerAdapter = new TimelinePagerAdapter(fragmentManager);
            timelineViewPager.setAdapter(timelinePagerAdapter);
            return view;
        }
    }
}
