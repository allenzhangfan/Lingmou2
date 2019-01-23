package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.ClothControlAlarmModule;

import cloud.antelope.lingmou.mvp.ui.activity.ClothControlAlarmActivity;

@ActivityScope
@Component(modules = ClothControlAlarmModule.class, dependencies = AppComponent.class)
public interface ClothControlAlarmComponent {
    void inject(ClothControlAlarmActivity activity);
}