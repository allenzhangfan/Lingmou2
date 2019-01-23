package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.CloudSearchModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.CloudSearchActivity;

@ActivityScope
@Component(modules = CloudSearchModule.class, dependencies = AppComponent.class)
public interface CloudSearchComponent {
    void inject(CloudSearchActivity activity);
}