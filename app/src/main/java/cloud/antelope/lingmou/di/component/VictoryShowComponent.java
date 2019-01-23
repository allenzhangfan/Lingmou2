package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.VictoryShowModule;

import cloud.antelope.lingmou.mvp.ui.activity.VictoryShowActivity;

@ActivityScope
@Component(modules = VictoryShowModule.class, dependencies = AppComponent.class)
public interface VictoryShowComponent {
    void inject(VictoryShowActivity activity);
}
