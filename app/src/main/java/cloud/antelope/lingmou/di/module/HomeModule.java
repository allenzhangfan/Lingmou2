package cloud.antelope.lingmou.di.module;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.mvp.contract.HomeContract;
import cloud.antelope.lingmou.mvp.model.HomeModel;
import cloud.antelope.lingmou.mvp.ui.adapter.HomeFragmentAdapter;
import cloud.antelope.lingmou.mvp.ui.fragment.CollectionFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.OrgMainFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.OrganizationParentFragment;
import dagger.Module;
import dagger.Provides;


@Module
public class HomeModule {
    private HomeContract.View view;
    private FragmentManager fragmentManager;

    /**
     * 构建HomeModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public HomeModule(HomeContract.View view, FragmentManager fragmentManager) {
        this.view = view;
        this.fragmentManager = fragmentManager;
    }

    @FragmentScope
    @Provides
    HomeContract.View provideHomeView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    HomeContract.Model provideHomeModel(HomeModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    List<Fragment> provideFragmentList() {
        return new ArrayList<>();
    }

    @FragmentScope
    @Provides
    HomeFragmentAdapter provideHomeFragmentAdapter(List<Fragment> list) {
        return new HomeFragmentAdapter(this.fragmentManager, list);
    }

    // @FragmentScope
    // @Provides
    // OrganizationParentFragment provideOrganizationParentFragment() {
    //     return OrganizationParentFragment.newInstance();
    // }
    @FragmentScope
    @Provides
    OrgMainFragment provideOrgMainFragment() {
        return OrgMainFragment.newInstance();
    }

    @FragmentScope
    @Provides
    CollectionFragment provideCollectionFragment() {
        return CollectionFragment.newInstance();
    }
}
