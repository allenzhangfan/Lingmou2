package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.FaceDetailContract;
import cloud.antelope.lingmou.mvp.model.FaceDetailModel;


@Module
public class FaceDetailModule {
    private FaceDetailContract.View view;

    /**
     * 构建FaceDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public FaceDetailModule(FaceDetailContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    FaceDetailContract.View provideFaceDetailView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    FaceDetailContract.Model provideFaceDetailModel(FaceDetailModel model) {
        return model;
    }
}
