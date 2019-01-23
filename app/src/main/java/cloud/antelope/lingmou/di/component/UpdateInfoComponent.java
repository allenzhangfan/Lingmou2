package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.UpdateInfoModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.UpdateInfoActivity;

@ActivityScope
@Component(modules = UpdateInfoModule.class, dependencies = AppComponent.class)
public interface UpdateInfoComponent {
    void inject(UpdateInfoActivity activity);
}