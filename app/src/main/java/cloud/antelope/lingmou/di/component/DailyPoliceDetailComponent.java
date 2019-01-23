package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.DailyPoliceDetailModule;

import cloud.antelope.lingmou.mvp.ui.activity.DailyPoliceDetailActivity;

@ActivityScope
@Component(modules = DailyPoliceDetailModule.class, dependencies = AppComponent.class)
public interface DailyPoliceDetailComponent {
    void inject(DailyPoliceDetailActivity activity);
}
