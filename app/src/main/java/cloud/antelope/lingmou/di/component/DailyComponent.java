package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.DailyModule;
import cloud.antelope.lingmou.mvp.ui.fragment.DailyFragment;
import dagger.Component;

@FragmentScope
@Component(modules = DailyModule.class, dependencies = AppComponent.class)
public interface DailyComponent {
    void inject(DailyFragment fragment);
}
