package cloud.antelope.lingmou.mvp.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.lingdanet.safeguard.common.utils.Utils;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.fragment.DeployListFragment;

/**
 * Created by liucheng on 16/5/10.
 */
public class DeployPagerAdapter extends FragmentPagerAdapter {

    private final int[] mTabTitleResId = new int[]{R.string.all, R.string.underway, R.string.not_start,R.string.paused, R.string.expired};

    public DeployPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new DeployListFragment();
                Bundle bundle0 = new Bundle();
                bundle0.putInt("statusType",DeployListFragment.ALL);
                fragment.setArguments(bundle0);
                break;
            case 1:
                fragment = new DeployListFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putInt("statusType",DeployListFragment.UNDERWAY);
                fragment.setArguments(bundle1);
                break;
            case 2:
                fragment = new DeployListFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putInt("statusType",DeployListFragment.NOT_RUN);
                fragment.setArguments(bundle2);
                break;
            case 3:
                fragment = new DeployListFragment();
                Bundle bundle3 = new Bundle();
                bundle3.putInt("statusType",DeployListFragment.PAUSED);
                fragment.setArguments(bundle3);
                break;
            case 4:
                fragment = new DeployListFragment();
                Bundle bundle4 = new Bundle();
                bundle4.putInt("statusType",DeployListFragment.EXPIRED);
                fragment.setArguments(bundle4);
                break;
            case 5:
                fragment = new DeployListFragment();
                Bundle bundle5 = new Bundle();
                bundle5.putInt("statusType",DeployListFragment.EXPIRED);
                fragment.setArguments(bundle5);
                break;
        }
        return fragment;
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
