package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.DeployControlModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.DeployControlActivity;

@ActivityScope
@Component(modules = DeployControlModule.class, dependencies = AppComponent.class)
public interface DeployControlComponent {
    void inject(DeployControlActivity activity);
}