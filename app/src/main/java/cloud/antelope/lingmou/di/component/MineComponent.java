package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.di.module.DailyModule;
import cloud.antelope.lingmou.di.module.MineModule;
import cloud.antelope.lingmou.mvp.ui.fragment.DailyFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.MineFragment;
import dagger.Component;

@ActivityScope
@Component(modules = MineModule.class, dependencies = AppComponent.class)
public interface MineComponent {
    void inject(MineFragment fragment);
}
