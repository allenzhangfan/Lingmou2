package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.contract.AppUpdateContract;
import cloud.antelope.lingmou.mvp.model.AppUpdateModel;
import dagger.Module;
import dagger.Provides;


@Module
public class AppUpdateModule {
    private AppUpdateContract.View view;

    /**
     * 构建AboutModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public AppUpdateModule(AppUpdateContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    AppUpdateContract.View provideAppUpdateView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    AppUpdateContract.Model provideAppUpdateModel(AppUpdateModel model) {
        return model;
    }
}
