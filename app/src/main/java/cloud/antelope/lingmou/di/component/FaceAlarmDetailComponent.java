package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.FaceAlarmDetailModule;

import cloud.antelope.lingmou.mvp.ui.activity.FaceAlarmDetailActivity;

@ActivityScope
@Component(modules = FaceAlarmDetailModule.class, dependencies = AppComponent.class)
public interface FaceAlarmDetailComponent {
    void inject(FaceAlarmDetailActivity activity);
}
