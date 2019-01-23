package cloud.antelope.lingmou.mvp.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.lingdanet.safeguard.common.utils.Utils;

import java.util.ArrayList;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.fragment.DeployListFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.EventRemindListFragment;

/**
 * Created by liucheng on 16/5/10.
 */
public class EventRemindPagerAdapter extends FragmentPagerAdapter {

    private final int[] mTabTitleResId = new int[]{R.string.all, R.string.unsettled, R.string.valid_event, R.string.invalid_event};

    public EventRemindPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ArrayList<EventRemindListFragment> fragments=new ArrayList<>();
    @Override
    public Fragment getItem(int position) {
        if(fragments.isEmpty()){
            EventRemindListFragment  fragment0 = new EventRemindListFragment();
            Bundle bundle0 = new Bundle();
            bundle0.putInt("statusType", EventRemindListFragment.ALL);
            fragment0.setArguments(bundle0);
            fragments.add(fragment0);
            EventRemindListFragment  fragment1 = new EventRemindListFragment();
            Bundle bundle1 = new Bundle();
            bundle1.putInt("statusType", EventRemindListFragment.UNSETTLED);
            fragment1.setArguments(bundle1);
            fragments.add(fragment1);
            EventRemindListFragment  fragment2 = new EventRemindListFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putInt("statusType", EventRemindListFragment.VALID_EVENT);
            fragment2.setArguments(bundle2);
            fragments.add(fragment2);
            EventRemindListFragment  fragment3 = new EventRemindListFragment();
            Bundle bundle3 = new Bundle();
            bundle3.putInt("statusType", EventRemindListFragment.INVALID_EVENT);
            fragment3.setArguments(bundle3);
            fragments.add(fragment3);
        }

        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Utils.getContext().getResources().getString(mTabTitleResId[position]);
    }

    @Override
    public int getCount() {
        return mTabTitleResId.length;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //如果注释这行，那么不管怎么切换，page都不会被销毁
        //super.destroyItem(container, position, object);
    }
}
