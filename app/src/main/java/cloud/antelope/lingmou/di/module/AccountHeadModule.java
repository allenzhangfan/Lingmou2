package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.AccountHeadContract;
import cloud.antelope.lingmou.mvp.model.AccountHeadModel;


@Module
public class AccountHeadModule {
    private AccountHeadContract.View view;

    /**
     * 构建AccountHeadModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public AccountHeadModule(AccountHeadContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    AccountHeadContract.View provideAccountHeadView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    AccountHeadContract.Model provideAccountHeadModel(AccountHeadModel model) {
        return model;
    }
}
