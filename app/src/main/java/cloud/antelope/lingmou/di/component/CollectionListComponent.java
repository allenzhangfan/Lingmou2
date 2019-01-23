package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.CollectionListModule;
import cloud.antelope.lingmou.di.module.SearchListModule;
import cloud.antelope.lingmou.mvp.ui.fragment.CollectionFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.CollectionListFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.SearchListFragment;
import dagger.Component;

@FragmentScope
@Component(modules = CollectionListModule.class, dependencies = AppComponent.class)
public interface CollectionListComponent {
    void inject(CollectionListFragment fragment);
}