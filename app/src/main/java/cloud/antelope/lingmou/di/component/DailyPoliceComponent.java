package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.DailyPoliceModule;
import cloud.antelope.lingmou.mvp.ui.fragment.DailyPoliceFragment;
import dagger.Component;

@FragmentScope
@Component(modules = DailyPoliceModule.class, dependencies = AppComponent.class)
public interface DailyPoliceComponent {
    void inject(DailyPoliceFragment fragment);
}
