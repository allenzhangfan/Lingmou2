package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.VideoDetailModule;

import cloud.antelope.lingmou.mvp.ui.activity.VideoDetailActivity;

@ActivityScope
@Component(modules = VideoDetailModule.class, dependencies = AppComponent.class)
public interface VideoDetailComponent {
    void inject(VideoDetailActivity activity);
}
