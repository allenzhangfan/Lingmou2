package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.CarDepotSearchModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.CarDepotSearchActivity;

@ActivityScope
@Component(modules = CarDepotSearchModule.class, dependencies = AppComponent.class)
public interface CarDepotSearchComponent {
    void inject(CarDepotSearchActivity activity);
}