package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.DailyDevicesModule;

import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.mvp.ui.fragment.DailyDevicesFragment;

@FragmentScope
@Component(modules = DailyDevicesModule.class, dependencies = AppComponent.class)
public interface DailyDevicesComponent {
    void inject(DailyDevicesFragment fragment);
}