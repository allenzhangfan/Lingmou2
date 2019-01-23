package cloud.antelope.lingmou.di.module;

import android.support.v4.app.FragmentManager;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.adapter.CollectedAlertPagerAdapter;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.CollectedAlertContract;
import cloud.antelope.lingmou.mvp.model.CollectedAlertModel;


@Module
public class CollectedAlertModule {
    private CollectedAlertContract.View view;
    private FragmentManager fragmentManager;
    /**
     * 构建CollectedAlertModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CollectedAlertModule(CollectedAlertContract.View view, FragmentManager fragmentManager) {
        this.view = view;
        this.fragmentManager = fragmentManager;
    }

    @ActivityScope
    @Provides
    CollectedAlertContract.View provideCollectedAlertView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    CollectedAlertContract.Model provideCollectedAlertModel(CollectedAlertModel model) {
        return model;
    }
    @ActivityScope
    @Provides
    CollectedAlertPagerAdapter provideCollectionAlertPagerAdapter() {
        return new CollectedAlertPagerAdapter(fragmentManager);
    }
}