package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.NoPermissionContract;
import cloud.antelope.lingmou.mvp.model.NoPermissionModel;


@Module
public class NoPermissionModule {
    private NoPermissionContract.View view;

    /**
     * 构建NoPermissionModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public NoPermissionModule(NoPermissionContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    NoPermissionContract.View provideNoPermissionView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    NoPermissionContract.Model provideNoPermissionModel(NoPermissionModel model) {
        return model;
    }
}
