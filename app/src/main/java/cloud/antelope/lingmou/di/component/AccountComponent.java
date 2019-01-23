package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.AccountModule;

import cloud.antelope.lingmou.mvp.ui.activity.AccountActivity;

@ActivityScope
@Component(modules = AccountModule.class, dependencies = AppComponent.class)
public interface AccountComponent {
    void inject(AccountActivity activity);
}