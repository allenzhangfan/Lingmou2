package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.FaceAlarmDetailContract;
import cloud.antelope.lingmou.mvp.model.FaceAlarmDetailModel;


@Module
public class FaceAlarmDetailModule {
    private FaceAlarmDetailContract.View view;

    /**
     * 构建FaceAlarmDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public FaceAlarmDetailModule(FaceAlarmDetailContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    FaceAlarmDetailContract.View provideFaceAlarmDetailView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    FaceAlarmDetailContract.Model provideFaceAlarmDetailModel(FaceAlarmDetailModel model) {
        return model;
    }
}
