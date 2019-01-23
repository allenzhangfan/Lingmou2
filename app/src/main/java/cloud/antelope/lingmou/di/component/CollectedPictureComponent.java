package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.CollectedPictureModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.CollectedPictureActivity;

@ActivityScope
@Component(modules = CollectedPictureModule.class, dependencies = AppComponent.class)
public interface CollectedPictureComponent {
    void inject(CollectedPictureActivity activity);
}