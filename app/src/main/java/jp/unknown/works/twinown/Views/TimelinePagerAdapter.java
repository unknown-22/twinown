package jp.unknown.works.twinown.Views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import jp.unknown.works.twinown.Globals;
import jp.unknown.works.twinown.models.UserPreference;


public class TimelinePagerAdapter extends FragmentPagerAdapter {
    public TimelinePagerAdapter(FragmentManager fm) {
        super(fm);
        // TODO タブ情報を取って各FragmentにBundleで渡す。
        // TODO タブ情報もモデル化した方がよさそう。形式は検討。
    }

    @Override
    public Fragment getItem(int position) {
        TimelineFragment timelineFragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putSerializable(Globals.ARGUMENTS_KEYWORD_USER_PREFERENCE, UserPreference.get());
        timelineFragment.setArguments(args);
        return timelineFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
