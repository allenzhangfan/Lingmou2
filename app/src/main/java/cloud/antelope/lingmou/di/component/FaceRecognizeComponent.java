package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.FaceRecognizeModule;

import cloud.antelope.lingmou.mvp.ui.activity.FaceRecognizeActivity;

@ActivityScope
@Component(modules = FaceRecognizeModule.class, dependencies = AppComponent.class)
public interface FaceRecognizeComponent {
    void inject(FaceRecognizeActivity activity);
}