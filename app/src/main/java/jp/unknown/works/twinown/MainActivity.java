package jp.unknown.works.twinown;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Window;
import android.view.WindowManager;

import jp.unknown.works.twinown.models.Tab;
import jp.unknown.works.twinown.models.Base;

public class MainActivity extends AppCompatActivity {
    MainFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setExitTransition(new Fade());
            getWindow().setEnterTransition(new Explode());
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
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, new MainFragment(), "main_fragment")
                    .commit();
        }
        fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("main_fragment");
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
}
