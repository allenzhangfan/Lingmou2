package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.MessageCodeActivityModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.MessageCodeActivityActivity;

@ActivityScope
@Component(modules = MessageCodeActivityModule.class, dependencies = AppComponent.class)
public interface MessageCodeActivityComponent {
    void inject(MessageCodeActivityActivity activity);
}