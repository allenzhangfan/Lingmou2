package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.NewDeployMissionModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.NewDeployMissionActivity;

@ActivityScope
@Component(modules = NewDeployMissionModule.class, dependencies = AppComponent.class)
public interface NewDeployMissionComponent {
    void inject(NewDeployMissionActivity activity);
}