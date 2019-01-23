package cloud.antelope.lingmou.di.component;

import cloud.antelope.lingmou.di.module.AppUpdateModule;
import cloud.antelope.lingmou.di.module.CyqzConfigModule;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.NewMainModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.NewMainActivity;

@ActivityScope
@Component(modules = {NewMainModule.class, CyqzConfigModule.class, AppUpdateModule.class},dependencies = AppComponent.class)
public interface NewMainComponent {
    void inject(NewMainActivity activity);
}