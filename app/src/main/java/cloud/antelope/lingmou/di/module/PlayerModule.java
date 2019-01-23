package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.PlayerContract;
import cloud.antelope.lingmou.mvp.model.PlayerModel;


@Module
public class PlayerModule {
    private PlayerContract.View view;

    /**
     * 构建PlayerModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public PlayerModule(PlayerContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    PlayerContract.View providePlayerView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    PlayerContract.Model providePlayerModel(PlayerModel model) {
        return model;
    }
}
