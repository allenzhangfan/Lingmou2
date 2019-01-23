package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.NewMainContract;
import cloud.antelope.lingmou.mvp.model.NewMainModel;


@Module
public class NewMainModule {
    private NewMainContract.View view;

    /**
     * 构建NewMainModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public NewMainModule(NewMainContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    NewMainContract.View provideNewMainView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    NewMainContract.Model provideNewMainModel(NewMainModel model) {
        return model;
    }
}