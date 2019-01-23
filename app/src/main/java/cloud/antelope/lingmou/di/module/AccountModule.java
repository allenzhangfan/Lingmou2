package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.modle.SweetDialog;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.AccountContract;
import cloud.antelope.lingmou.mvp.model.AccountModel;


@Module
public class AccountModule {
    private AccountContract.View view;


    /**
     * 构建AccountModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public AccountModule(AccountContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    AccountContract.View provideAccountView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    AccountContract.Model provideAccountModel(AccountModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    LoadingDialog provideLoadingDialog() {
        return new LoadingDialog(view.getActivity());
    }

    @ActivityScope
    @Provides
    SweetDialog provideSweetDialog() {
        return new SweetDialog(view.getActivity());
    }
}