package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.FaceFilterModule;

import cloud.antelope.lingmou.mvp.ui.activity.FaceFilterActivity;

@ActivityScope
@Component(modules = FaceFilterModule.class, dependencies = AppComponent.class)
public interface FaceFilterComponent {
    void inject(FaceFilterActivity activity);
}
