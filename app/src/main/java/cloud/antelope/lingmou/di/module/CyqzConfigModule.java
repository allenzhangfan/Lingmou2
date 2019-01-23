package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.contract.CyqzConfigContract;
import cloud.antelope.lingmou.mvp.model.CyqzConfigModel;
import dagger.Module;
import dagger.Provides;


@Module
public class CyqzConfigModule {

    private CyqzConfigContract.View view;

    /**
     * 构建ReportEditModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CyqzConfigModule(CyqzConfigContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    CyqzConfigContract.View provideCyqzConfigView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    CyqzConfigContract.Model provideCyqzConfigModel(CyqzConfigModel model) {
        return model;
    }

}
