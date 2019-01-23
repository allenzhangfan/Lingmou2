package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.PlayerModule;

import cloud.antelope.lingmou.mvp.ui.activity.PlayerActivity;

@ActivityScope
@Component(modules = PlayerModule.class, dependencies = AppComponent.class)
public interface PlayerComponent {
    void inject(PlayerActivity activity);
}
