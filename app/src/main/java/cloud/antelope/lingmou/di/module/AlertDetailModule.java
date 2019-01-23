package cloud.antelope.lingmou.di.module;

import android.content.Context;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.modle.LoadingDialog;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.AlertDetailContract;
import cloud.antelope.lingmou.mvp.model.AlertDetailModel;


@Module
public class AlertDetailModule {
    private AlertDetailContract.View view;

    /**
     * 构建AlertDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public AlertDetailModule(AlertDetailContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    AlertDetailContract.View provideAlertDetailView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    AlertDetailContract.Model provideAlertDetailModel(AlertDetailModel model) {
        return model;
    }
    @ActivityScope
    @Provides
    LoadingDialog provideLoadingDialog() {
        return new LoadingDialog((Context) view);
    }
}