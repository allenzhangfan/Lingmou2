package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.CarBrandModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.CarBrandActivity;

@ActivityScope
@Component(modules = CarBrandModule.class, dependencies = AppComponent.class)
public interface CarBrandComponent {
    void inject(CarBrandActivity activity);
}