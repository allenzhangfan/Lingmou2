package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.BodyDepotModule;

import cloud.antelope.lingmou.mvp.ui.activity.BodyDepotActivity;

@ActivityScope
@Component(modules = BodyDepotModule.class, dependencies = AppComponent.class)
public interface BodyDepotComponent {
    void inject(BodyDepotActivity activity);
}