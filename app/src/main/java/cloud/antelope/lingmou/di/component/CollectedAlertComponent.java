package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.CollectedAlertModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.CollectedAlertActivity;

@ActivityScope
@Component(modules = CollectedAlertModule.class, dependencies = AppComponent.class)
public interface CollectedAlertComponent {
    void inject(CollectedAlertActivity activity);
}