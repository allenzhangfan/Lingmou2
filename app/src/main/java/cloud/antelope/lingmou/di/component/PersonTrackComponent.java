package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.PersonTrackModule;

import cloud.antelope.lingmou.mvp.ui.activity.PersonTrackActivity;

@ActivityScope
@Component(modules = PersonTrackModule.class, dependencies = AppComponent.class)
public interface PersonTrackComponent {
    void inject(PersonTrackActivity activity);
}
