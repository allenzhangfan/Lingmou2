package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.SoloModule;

import cloud.antelope.lingmou.mvp.ui.activity.SoloActivity;

@ActivityScope
@Component(modules = SoloModule.class, dependencies = AppComponent.class)
public interface SoloComponent {
    void inject(SoloActivity activity);
}
