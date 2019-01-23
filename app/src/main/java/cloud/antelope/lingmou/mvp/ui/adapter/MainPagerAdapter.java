package cloud.antelope.lingmou.mvp.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lingdanet.safeguard.common.utils.Utils;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.fragment.ApplicationFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.CloudEyeFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.CloudMapFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.HomeFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.MyFragment;

/**
 * Created by liucheng on 16/5/10.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE = 4;

    private final int[] mTabTitleResId = new int[]{R.string.home_item_video, R.string.home_item_map, R.string.home_item_app, R.string.home_item_personal};

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = CloudEyeFragment.newInstance();
                break;
            case 1:
                fragment = CloudMapFragment.newInstance();
                break;
            case 2:
                fragment = ApplicationFragment.newInstance();
                break;
            case 3:
                 fragment = MyFragment.newInstance();
//                fragment = new Fragment();
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
        return PAGE;
    }

}
