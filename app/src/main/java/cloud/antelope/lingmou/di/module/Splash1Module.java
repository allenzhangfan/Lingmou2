package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.Splash1Contract;
import cloud.antelope.lingmou.mvp.model.Splash1Model;


@Module
public class Splash1Module {
    private Splash1Contract.View view;

    /**
     * 构建Splash1Module时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public Splash1Module(Splash1Contract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    Splash1Contract.View provideSplash1View() {
        return this.view;
    }

    @ActivityScope
    @Provides
    Splash1Contract.Model provideSplash1Model(Splash1Model model) {
        return model;
    }
}