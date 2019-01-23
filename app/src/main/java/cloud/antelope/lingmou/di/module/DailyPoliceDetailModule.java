package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.DailyPoliceDetailContract;
import cloud.antelope.lingmou.mvp.model.DailyPoliceDetailModel;


@Module
public class DailyPoliceDetailModule {
    private DailyPoliceDetailContract.View view;

    /**
     * 构建DailyPoliceDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public DailyPoliceDetailModule(DailyPoliceDetailContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    DailyPoliceDetailContract.View provideDailyPoliceDetailView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    DailyPoliceDetailContract.Model provideDailyPoliceDetailModel(DailyPoliceDetailModel model) {
        return model;
    }
}
