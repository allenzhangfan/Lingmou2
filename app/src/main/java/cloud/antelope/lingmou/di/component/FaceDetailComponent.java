package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.FaceDetailModule;

import cloud.antelope.lingmou.mvp.ui.activity.FaceDetailActivity;

@ActivityScope
@Component(modules = FaceDetailModule.class, dependencies = AppComponent.class)
public interface FaceDetailComponent {
    void inject(FaceDetailActivity activity);
}
