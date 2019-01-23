package cloud.antelope.lingmou.di.module;

import android.support.v4.app.FragmentManager;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.adapter.CarDepotSearchPagerAdapter;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.CarDepotSearchContract;
import cloud.antelope.lingmou.mvp.model.CarDepotSearchModel;


@Module
public class CarDepotSearchModule {
    private CarDepotSearchContract.View view;
    private FragmentManager fragmentManager;

    /**
     * 构建CarDepotSearchModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CarDepotSearchModule(CarDepotSearchContract.View view,FragmentManager fragmentManager) {
        this.view = view;
        this.fragmentManager = fragmentManager;
    }

    @ActivityScope
    @Provides
    CarDepotSearchContract.View provideCarDepotSearchView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    CarDepotSearchContract.Model provideCarDepotSearchModel(CarDepotSearchModel model) {
        return model;
    }
    @ActivityScope
    @Provides
    CarDepotSearchPagerAdapter provideCarDepotSearchPagerAdapter() {
        return new CarDepotSearchPagerAdapter(fragmentManager);
    }
}