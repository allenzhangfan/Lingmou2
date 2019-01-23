package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.HomeModule;
import cloud.antelope.lingmou.mvp.ui.fragment.HomeFragment;
import dagger.Component;

@FragmentScope
@Component(modules = HomeModule.class, dependencies = AppComponent.class)
public interface HomeComponent {
    void inject(HomeFragment fragment);
}
