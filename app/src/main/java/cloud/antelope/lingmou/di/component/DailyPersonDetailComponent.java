package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.DailyPersonDetailModule;

import cloud.antelope.lingmou.mvp.ui.activity.DailyPersonDetailActivity;

@ActivityScope
@Component(modules = DailyPersonDetailModule.class, dependencies = AppComponent.class)
public interface DailyPersonDetailComponent {
    void inject(DailyPersonDetailActivity activity);
}
