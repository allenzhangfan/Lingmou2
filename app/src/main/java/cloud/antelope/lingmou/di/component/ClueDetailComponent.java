package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.ClueDetailModule;

import cloud.antelope.lingmou.mvp.ui.activity.ClueDetailActivity;

@ActivityScope
@Component(modules = ClueDetailModule.class, dependencies = AppComponent.class)
public interface ClueDetailComponent {
    void inject(ClueDetailActivity activity);
}
