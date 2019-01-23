package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.CarDepotSearchPlateModule;

import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.mvp.ui.fragment.CarDepotSearchPlateFragment;

@FragmentScope
@Component(modules = CarDepotSearchPlateModule.class, dependencies = AppComponent.class)
public interface CarDepotSearchPlateComponent {
    void inject(CarDepotSearchPlateFragment fragment);
}