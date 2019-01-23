package cloud.antelope.lingmou.di.module;

import android.support.v4.app.Fragment;

import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.mvp.contract.DailyContract;
import cloud.antelope.lingmou.mvp.model.DailyModel;
import dagger.Module;
import dagger.Provides;


@Module
public class DailyModule {
    private DailyContract.View view;

    /**
     * 构建AccountModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public DailyModule(DailyContract.View view) {
        this.view = view;
    }
    @FragmentScope
    @Provides
    List<Fragment> provideFragmentList() {
        return new ArrayList<>();
    }
    @FragmentScope
    @Provides
    DailyContract.View provideDailyContract() {
        return this.view;
    }

    @FragmentScope
    @Provides
    DailyContract.Model provideDailyModel(DailyModel model) {
        return model;
    }


}