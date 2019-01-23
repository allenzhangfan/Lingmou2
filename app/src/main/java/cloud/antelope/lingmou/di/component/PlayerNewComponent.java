package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.PlayerNewModule;

import cloud.antelope.lingmou.mvp.ui.activity.PlayerNewActivity;

@ActivityScope
@Component(modules = PlayerNewModule.class, dependencies = AppComponent.class)
public interface PlayerNewComponent {
    void inject(PlayerNewActivity activity);
}
