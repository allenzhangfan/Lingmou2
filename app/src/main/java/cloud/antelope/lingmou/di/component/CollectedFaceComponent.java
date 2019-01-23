package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.CollectedFaceModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.CollectedFaceActivity;

@ActivityScope
@Component(modules = CollectedFaceModule.class, dependencies = AppComponent.class)
public interface CollectedFaceComponent {
    void inject(CollectedFaceActivity activity);
}