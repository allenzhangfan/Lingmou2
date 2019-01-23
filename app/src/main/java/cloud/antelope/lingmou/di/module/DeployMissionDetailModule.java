package cloud.antelope.lingmou.di.module;

import android.content.Context;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.modle.LoadingDialog;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.DeployMissionDetailContract;
import cloud.antelope.lingmou.mvp.model.DeployMissionDetailModel;


@Module
public class DeployMissionDetailModule {
    private DeployMissionDetailContract.View view;

    /**
     * 构建DeployMissionDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public DeployMissionDetailModule(DeployMissionDetailContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    DeployMissionDetailContract.View provideDeployMissionDetailView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    DeployMissionDetailContract.Model provideDeployMissionDetailModel(DeployMissionDetailModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    LoadingDialog provideLoadingDialog() {
        return new LoadingDialog((Context) view);
    }
}