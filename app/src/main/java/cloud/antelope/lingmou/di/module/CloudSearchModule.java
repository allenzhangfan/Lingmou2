package cloud.antelope.lingmou.di.module;

import android.support.v4.app.FragmentManager;

import com.amap.api.services.cloud.CloudSearch;
import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.CloudSearchActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.DeployPagerAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchPagerAdapter;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.CloudSearchContract;
import cloud.antelope.lingmou.mvp.model.CloudSearchModel;


@Module
public class CloudSearchModule {
    private CloudSearchContract.View view;
    private FragmentManager fragmentManager;

    /**
     * 构建CloudSearchModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CloudSearchModule(CloudSearchContract.View view, FragmentManager fragmentManager) {
        this.view = view;
        this.fragmentManager = fragmentManager;
    }

    @ActivityScope
    @Provides
    CloudSearchContract.View provideCloudSearchView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    CloudSearchContract.Model provideCloudSearchModel(CloudSearchModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    SearchPagerAdapter provideSearchPagerAdapter() {
        return new SearchPagerAdapter(fragmentManager);
    }
}