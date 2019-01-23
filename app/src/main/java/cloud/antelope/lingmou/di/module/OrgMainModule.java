package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.contract.OrgMainContract;
import cloud.antelope.lingmou.mvp.model.OrgMainModel;
import dagger.Module;
import dagger.Provides;


@Module
public class OrgMainModule {
    private OrgMainContract.View view;

    /**
     * 构建OrgMainModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public OrgMainModule(OrgMainContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    OrgMainContract.View provideOrgMainView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    OrgMainContract.Model provideOrgMainModel(OrgMainModel model) {
        return model;
    }
}
