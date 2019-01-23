package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.SoloContract;
import cloud.antelope.lingmou.mvp.model.SoloModel;


@Module
public class SoloModule {
    private SoloContract.View view;

    /**
     * 构建SoloModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public SoloModule(SoloContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    SoloContract.View provideSoloView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    SoloContract.Model provideSoloModel(SoloModel model) {
        return model;
    }
}
