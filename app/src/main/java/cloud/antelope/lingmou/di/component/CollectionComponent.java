package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.CollectionModule;
import cloud.antelope.lingmou.mvp.ui.fragment.CollectionFragment;
import dagger.Component;

@FragmentScope
@Component(modules = CollectionModule.class, dependencies = AppComponent.class)
public interface CollectionComponent {
    void inject(CollectionFragment fragment);
}
