package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.DeviceListModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.DeviceListActivity;

@ActivityScope
@Component(modules = DeviceListModule.class, dependencies = AppComponent.class)
public interface DeviceListComponent {
    void inject(DeviceListActivity activity);
}