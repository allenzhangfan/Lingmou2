package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.AccountHeadModule;

import cloud.antelope.lingmou.mvp.ui.activity.AccountHeadActivity;

@ActivityScope
@Component(modules = AccountHeadModule.class, dependencies = AppComponent.class)
public interface AccountHeadComponent {
    void inject(AccountHeadActivity activity);
}
