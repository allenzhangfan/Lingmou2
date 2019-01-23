package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.LmCropContract;
import cloud.antelope.lingmou.mvp.model.LmCropModel;


@Module
public class LmCropModule {
    private LmCropContract.View view;

    /**
     * 构建LmCropModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public LmCropModule(LmCropContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    LmCropContract.View provideLmCropView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    LmCropContract.Model provideLmCropModel(LmCropModel model) {
        return model;
    }
}
