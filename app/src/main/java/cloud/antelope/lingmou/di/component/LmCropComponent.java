package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.LmCropModule;

import cloud.antelope.lingmou.mvp.ui.activity.LmCropActivity;

@ActivityScope
@Component(modules = LmCropModule.class, dependencies = AppComponent.class)
public interface LmCropComponent {
    void inject(LmCropActivity activity);
}
