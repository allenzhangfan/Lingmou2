package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.ResetPasswordModule;

import cloud.antelope.lingmou.mvp.ui.activity.ResetPasswordActivity;

@ActivityScope
@Component(modules = ResetPasswordModule.class, dependencies = AppComponent.class)
public interface ResetPasswordComponent {
    void inject(ResetPasswordActivity activity);
}