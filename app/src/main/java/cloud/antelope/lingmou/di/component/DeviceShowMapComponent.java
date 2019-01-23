package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.DeviceShowMapModule;

import cloud.antelope.lingmou.mvp.ui.activity.DeviceShowMapActivity;

@ActivityScope
@Component(modules = DeviceShowMapModule.class, dependencies = AppComponent.class)
public interface DeviceShowMapComponent {
    void inject(DeviceShowMapActivity activity);
}
