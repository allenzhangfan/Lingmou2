package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.EventRemindModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.EventRemindActivity;

@ActivityScope
@Component(modules = EventRemindModule.class, dependencies = AppComponent.class)
public interface EventRemindComponent {
    void inject(EventRemindActivity activity);
}