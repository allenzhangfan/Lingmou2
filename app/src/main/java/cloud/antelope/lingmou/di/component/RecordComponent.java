package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.RecordModule;

import cloud.antelope.lingmou.mvp.ui.activity.RecordActivity;

@ActivityScope
@Component(modules = RecordModule.class, dependencies = AppComponent.class)
public interface RecordComponent {
    void inject(RecordActivity activity);
}
