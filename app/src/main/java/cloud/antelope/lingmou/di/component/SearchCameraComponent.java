package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.SearchCameraModule;

import cloud.antelope.lingmou.mvp.ui.activity.SearchCameraActivity;

@ActivityScope
@Component(modules = SearchCameraModule.class, dependencies = AppComponent.class)
public interface SearchCameraComponent {
    void inject(SearchCameraActivity activity);
}
