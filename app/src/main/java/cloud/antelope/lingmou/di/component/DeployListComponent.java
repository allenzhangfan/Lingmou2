package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.DeployListModule;
import cloud.antelope.lingmou.mvp.ui.fragment.DeployListFragment;
import dagger.Component;

@FragmentScope
@Component(modules = DeployListModule.class, dependencies = AppComponent.class)
public interface DeployListComponent {
    void inject(DeployListFragment fragment);
}