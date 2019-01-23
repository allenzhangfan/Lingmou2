package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.AppUpdateModule;

import cloud.antelope.lingmou.mvp.ui.activity.AboutActivity;

@ActivityScope
@Component(modules = AppUpdateModule.class, dependencies = AppComponent.class)
public interface AboutComponent {
    void inject(AboutActivity activity);
}
