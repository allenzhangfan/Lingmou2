package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.LocationSelectActivityModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.LocationSelectActivity;

@ActivityScope
@Component(modules = LocationSelectActivityModule.class, dependencies = AppComponent.class)
public interface LocationSelectActivityComponent {
    void inject(LocationSelectActivity activity);
}