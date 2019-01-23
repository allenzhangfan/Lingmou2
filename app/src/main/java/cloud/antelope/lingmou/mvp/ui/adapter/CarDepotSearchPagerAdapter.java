package cloud.antelope.lingmou.mvp.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.lingdanet.safeguard.common.utils.Utils;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.fragment.CarDepotSearchDeviceFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.CarDepotSearchPlateFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.DeployListFragment;

/**
 * Created by liucheng on 16/5/10.
 */
public class CarDepotSearchPagerAdapter extends FragmentPagerAdapter {

    private final int[] mTabTitleResId = new int[]{R.string.car_plate, R.string.device};

    public CarDepotSearchPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new CarDepotSearchPlateFragment();
                break;
            case 1:
                fragment = new CarDepotSearchDeviceFragment();
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
