package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.HistoryWatchModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.HistoryWatchActivity;

@ActivityScope
@Component(modules = HistoryWatchModule.class, dependencies = AppComponent.class)
public interface HistoryWatchComponent {
    void inject(HistoryWatchActivity activity);
}