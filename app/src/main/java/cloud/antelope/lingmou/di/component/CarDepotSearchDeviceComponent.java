package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.CarDepotSearchDeviceModule;

import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.mvp.ui.fragment.CarDepotSearchDeviceFragment;

@FragmentScope
@Component(modules = CarDepotSearchDeviceModule.class, dependencies = AppComponent.class)
public interface CarDepotSearchDeviceComponent {
    void inject(CarDepotSearchDeviceFragment fragment);
}