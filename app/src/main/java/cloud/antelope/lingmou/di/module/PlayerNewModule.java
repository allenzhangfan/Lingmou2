package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.PlayerNewContract;
import cloud.antelope.lingmou.mvp.model.PlayerNewModel;


@Module
public class PlayerNewModule {
    private PlayerNewContract.View view;

    /**
     * 构建PlayerNewModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public PlayerNewModule(PlayerNewContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    PlayerNewContract.View providePlayerNewView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    PlayerNewContract.Model providePlayerNewModel(PlayerNewModel model) {
        return model;
    }
}
