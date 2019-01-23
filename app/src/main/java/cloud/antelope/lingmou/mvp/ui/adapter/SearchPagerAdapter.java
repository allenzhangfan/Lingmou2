package cloud.antelope.lingmou.mvp.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.lingdanet.safeguard.common.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.fragment.DeployListFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.SearchListFragment;
import timber.log.Timber;

/**
 * Created by liucheng on 16/5/10.
 */
public class SearchPagerAdapter extends FragmentPagerAdapter {

    private final int[] mTabTitleResId = new int[]{R.string.face_depot_title, R.string.body_depot_title, R.string.jurisdiction_alert,R.string.jurisdiction_vidio};
    private List<Fragment> fragmentList;
    public SearchPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return Utils.getContext().getResources().getString(mTabTitleResId[position]);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //如果注释这行，那么不管怎么切换，page都不会被销毁
        //super.destroyItem(container, position, object);
    }
}
