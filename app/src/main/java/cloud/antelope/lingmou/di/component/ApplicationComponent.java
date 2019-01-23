package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.ApplicationModule;
import cloud.antelope.lingmou.mvp.ui.fragment.ApplicationFragment;
import dagger.Component;

@FragmentScope
@Component(modules = ApplicationModule.class, dependencies = AppComponent.class)
public interface ApplicationComponent {
    void inject(ApplicationFragment fragment);
}
