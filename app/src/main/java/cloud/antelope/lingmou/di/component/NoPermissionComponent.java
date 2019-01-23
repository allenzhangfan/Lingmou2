package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.NoPermissionModule;

import cloud.antelope.lingmou.mvp.ui.activity.NoPermissionActivity;

@ActivityScope
@Component(modules = NoPermissionModule.class, dependencies = AppComponent.class)
public interface NoPermissionComponent {
    void inject(NoPermissionActivity activity);
}
