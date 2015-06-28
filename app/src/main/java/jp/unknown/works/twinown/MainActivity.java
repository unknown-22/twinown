package jp.unknown.works.twinown;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.models.Base;
import jp.unknown.works.twinown.models.UserPreference;
import twitter4j.Status;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Base.initDataBase(getApplicationContext());
        if (UserPreference.getCount() == 0) {
            Intent intent=new Intent(Globals.ACTION_KEYWORD_AUTHORIZATION);
            intent.setClass(this, AuthActivity.class);
            Resources resources = getResources();
            intent.putExtra(Globals.EXTRA_KEYWORD_CONSUMER_KEY, resources.getText(R.string.default_consumer_key));
            intent.putExtra(Globals.EXTRA_KEYWORD_CONSUMER_SECRET, resources.getText(R.string.default_consumer_secret));
            startActivity(intent);
            finish();
            return;
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
