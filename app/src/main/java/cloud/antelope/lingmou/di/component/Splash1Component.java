package cloud.antelope.lingmou.di.component;

import cloud.antelope.lingmou.di.module.AppUpdateModule;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.Splash1Module;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.Splash1Activity;

@ActivityScope
@Component(modules = {Splash1Module.class, AppUpdateModule.class}, dependencies = AppComponent.class)
public interface Splash1Component {
    void inject(Splash1Activity activity);
}