package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.FocusedDevicesModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.FocusedDevicesActivity;

@ActivityScope
@Component(modules = FocusedDevicesModule.class, dependencies = AppComponent.class)
public interface FocusedDevicesComponent {
    void inject(FocusedDevicesActivity activity);
}