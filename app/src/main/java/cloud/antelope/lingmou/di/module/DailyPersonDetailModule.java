package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.DailyPersonDetailContract;
import cloud.antelope.lingmou.mvp.model.DailyPersonDetailModel;


@Module
public class DailyPersonDetailModule {
    private DailyPersonDetailContract.View view;

    /**
     * 构建DailyPersonDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public DailyPersonDetailModule(DailyPersonDetailContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    DailyPersonDetailContract.View provideDailyPersonDetailView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    DailyPersonDetailContract.Model provideDailyPersonDetailModel(DailyPersonDetailModel model) {
        return model;
    }
}
