package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.CollectedAlertListModule;
import cloud.antelope.lingmou.di.module.DeployListModule;
import cloud.antelope.lingmou.mvp.ui.fragment.CollectedAlertListFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.DeployListFragment;
import dagger.Component;

@FragmentScope
@Component(modules = CollectedAlertListModule.class, dependencies = AppComponent.class)
public interface CollectedAlertListComponent {
    void inject(CollectedAlertListFragment fragment);
}