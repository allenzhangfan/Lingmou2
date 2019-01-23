package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.modle.LoadingDialog;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.ResetPasswordContract;
import cloud.antelope.lingmou.mvp.model.ResetPasswordModel;


@Module
public class ResetPasswordModule {
    private ResetPasswordContract.View view;

    /**
     * 构建ResetPasswordModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public ResetPasswordModule(ResetPasswordContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ResetPasswordContract.View provideResetPasswordView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    ResetPasswordContract.Model provideResetPasswordModel(ResetPasswordModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    LoadingDialog provideLoadingDialog() {
        return new LoadingDialog(view.getActivity());
    }
}