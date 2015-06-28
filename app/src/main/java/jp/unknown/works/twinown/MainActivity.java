package jp.unknown.works.twinown;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import jp.unknown.works.twinown.models.Base;
import jp.unknown.works.twinown.models.UserPreference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // DB設定
        Base.initDataBase(getApplicationContext());
        if (UserPreference.getCount() == 0) {
            // 初回起動処理
            Intent intent=new Intent(Globals.ACTION_KEYWORD_AUTHORIZATION);
            intent.setClass(this, AuthActivity.class);
            Resources resources = getResources();
            intent.putExtra(Globals.EXTRA_KEYWORD_CONSUMER_KEY, resources.getText(R.string.default_consumer_key));
            intent.putExtra(Globals.EXTRA_KEYWORD_CONSUMER_SECRET, resources.getText(R.string.default_consumer_secret));
            startActivity(intent);
            finish();
            // 初回起動処理
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
}
