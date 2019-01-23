package cloud.antelope.lingmou.mvp.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.lingdanet.safeguard.common.utils.Utils;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.fragment.CollectionListFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.SearchListFragment;

/**
 * Created by liucheng on 16/5/10.
 */
public class CollectionPagerAdapter extends FragmentPagerAdapter {

    private final int[] mTabTitleResId = new int[]{ R.string.face_picture, R.string.body_picture,R.string.vidio_snapshot};

    public CollectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        switch (position) {
//            case 0:
//                fragment = new CollectionListFragment();
//                Bundle bundle0 = new Bundle();
//                bundle0.putInt("statusType",CollectionListFragment.ALL);
//                fragment.setArguments(bundle0);
//                break;
            case 0:
                fragment = new CollectionListFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putInt("statusType",CollectionListFragment.FACE);
                fragment.setArguments(bundle1);
                break;
            case 1:
                fragment = new CollectionListFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putInt("statusType",CollectionListFragment.BODY);
                fragment.setArguments(bundle2);
                break;
            case 2:
                fragment = new CollectionListFragment();
                Bundle bundle3 = new Bundle();
                bundle3.putInt("statusType",CollectionListFragment.VIDEO);
                fragment.setArguments(bundle3);
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
