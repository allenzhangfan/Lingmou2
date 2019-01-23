package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.FaceFilterContract;
import cloud.antelope.lingmou.mvp.model.FaceFilterModel;


@Module
public class FaceFilterModule {
    private FaceFilterContract.View view;

    /**
     * 构建FaceFilterModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public FaceFilterModule(FaceFilterContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    FaceFilterContract.View provideFaceFilterView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    FaceFilterContract.Model provideFaceFilterModel(FaceFilterModel model) {
        return model;
    }
}
