package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.DeployListModule;
import cloud.antelope.lingmou.di.module.SearchListModule;
import cloud.antelope.lingmou.mvp.ui.fragment.DeployListFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.SearchListFragment;
import dagger.Component;

@FragmentScope
@Component(modules = SearchListModule.class, dependencies = AppComponent.class)
public interface SearchListComponent {
    void inject(SearchListFragment fragment);
}