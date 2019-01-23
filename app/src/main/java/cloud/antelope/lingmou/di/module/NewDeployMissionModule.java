package cloud.antelope.lingmou.di.module;

import android.content.Context;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.modle.LoadingDialog;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.NewDeployMissionContract;
import cloud.antelope.lingmou.mvp.model.NewDeployMissionModel;


@Module
public class NewDeployMissionModule {
    private NewDeployMissionContract.View view;

    /**
     * 构建NewDeployMissionModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public NewDeployMissionModule(NewDeployMissionContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    NewDeployMissionContract.View provideNewDeployMissionView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    NewDeployMissionContract.Model provideNewDeployMissionModel(NewDeployMissionModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    LoadingDialog provideLoadingDialog() {
        return new LoadingDialog((Context) view);
    }

}