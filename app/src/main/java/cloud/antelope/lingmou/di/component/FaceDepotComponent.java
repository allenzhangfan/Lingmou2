package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.FaceDepotModule;

import cloud.antelope.lingmou.mvp.ui.activity.FaceDepotActivity;

@ActivityScope
@Component(modules = FaceDepotModule.class, dependencies = AppComponent.class)
public interface FaceDepotComponent {
    void inject(FaceDepotActivity activity);
}