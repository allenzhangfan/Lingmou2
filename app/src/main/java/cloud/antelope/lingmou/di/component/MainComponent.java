package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.di.module.AppUpdateModule;
import cloud.antelope.lingmou.di.module.CyqzConfigModule;
import cloud.antelope.lingmou.di.module.MainModule;
import cloud.antelope.lingmou.mvp.ui.activity.MainActivity;
import dagger.Component;

@ActivityScope
@Component(modules = {MainModule.class, CyqzConfigModule.class, AppUpdateModule.class},
        dependencies = AppComponent.class)
public interface MainComponent {
    void inject(MainActivity activity);
}
