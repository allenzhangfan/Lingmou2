package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.di.module.PlayerNewModule;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.VideoPlayModule;

import cloud.antelope.lingmou.mvp.ui.activity.VideoPlayActivity;

@ActivityScope
@Component(modules = VideoPlayModule.class, dependencies = AppComponent.class)
public interface VideoPlayComponent {
    void inject(VideoPlayActivity activity);
}
