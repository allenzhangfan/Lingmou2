package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.DeployMissionDetailModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.DeployMissionDetailActivity;

@ActivityScope
@Component(modules = DeployMissionDetailModule.class, dependencies = AppComponent.class)
public interface DeployMissionDetailComponent {
    void inject(DeployMissionDetailActivity activity);
}