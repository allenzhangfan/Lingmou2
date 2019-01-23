package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.CameraMapModule;

import cloud.antelope.lingmou.mvp.ui.activity.CameraMapActivity;

@ActivityScope
@Component(modules = CameraMapModule.class, dependencies = AppComponent.class)
public interface CameraMapComponent {
    void inject(CameraMapActivity activity);
}
