package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.RealVideosListModule;

import cloud.antelope.lingmou.mvp.ui.activity.RealVideosListActivity;

@ActivityScope
@Component(modules = RealVideosListModule.class, dependencies = AppComponent.class)
public interface RealVideosListComponent {
    void inject(RealVideosListActivity activity);
}
