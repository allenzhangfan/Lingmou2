package cloud.antelope.lingmou.di.module;

import android.support.v4.app.FragmentManager;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.app.utils.GisUtils;
import cloud.antelope.lingmou.mvp.contract.MainContract;
import cloud.antelope.lingmou.mvp.model.MainModel;
import cloud.antelope.lingmou.mvp.ui.adapter.MainPagerAdapter;
import dagger.Module;
import dagger.Provides;


@Module
public class MainModule {
    private MainContract.View view;
    private FragmentManager fragmentManager;

    /**
     * 构建HomeModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public MainModule(MainContract.View view, FragmentManager fragmentManager) {
        this.view = view;
        this.fragmentManager = fragmentManager;
    }

    @ActivityScope
    @Provides
    MainContract.View provideHomeView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    MainContract.Model provideHomeModel(MainModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    MainPagerAdapter provideMainPagerAdapter() {
        return new MainPagerAdapter(this.fragmentManager);
    }

    // @ActivityScope
    // @Provides
    // GisUtils provideGisUtils() {
    //     return new GisUtils(view.getActivity(), 300000);
    // }
}
