package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.AlertDetailModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.AlertDetailActivity;

@ActivityScope
@Component(modules = AlertDetailModule.class, dependencies = AppComponent.class)
public interface AlertDetailComponent {
    void inject(AlertDetailActivity activity);
}