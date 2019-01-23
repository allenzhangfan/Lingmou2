package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.TrackingPersonModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.TrackingPersonActivity;

@ActivityScope
@Component(modules = TrackingPersonModule.class, dependencies = AppComponent.class)
public interface TrackingPersonComponent {
    void inject(TrackingPersonActivity activity);
}