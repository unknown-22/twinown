package jp.unknown.works.twinown.twinown_views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.List;

import jp.unknown.works.twinown.Utils;
import jp.unknown.works.twinown.models.Tab;


public class TimelinePagerAdapter extends FragmentPagerAdapter {
    List<Tab> tabList;

    public TimelinePagerAdapter(FragmentManager fm, List<Tab> tabList) {
        super(fm);
        this.tabList = tabList;
    }

    @Override
    public Fragment getItem(int position) {
        TimelineFragment timelineFragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putSerializable(Utils.ARGUMENTS_KEYWORD_TAB, tabList.get(position));
        timelineFragment.setArguments(args);
        return timelineFragment;
    }

    @Override
    public int getCount() {
        return tabList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabList.get(position).name;
    }

    public TimelineFragment findFragmentByPosition(ViewPager viewPager, int position) {
        return (TimelineFragment) instantiateItem(viewPager, position);
    }
}
