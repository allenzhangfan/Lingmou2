package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.OrganizationParentModule;
import cloud.antelope.lingmou.mvp.ui.fragment.OrganizationParentFragment;
import dagger.Component;

@FragmentScope
@Component(modules = OrganizationParentModule.class, dependencies = AppComponent.class)
public interface OrganizationParentComponent {
    void inject(OrganizationParentFragment fragment);
}
