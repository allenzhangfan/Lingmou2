package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.DeviceMapModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.DeviceMapActivity;

@ActivityScope
@Component(modules = DeviceMapModule.class, dependencies = AppComponent.class)
public interface DeviceMapComponent {
    void inject(DeviceMapActivity activity);
}