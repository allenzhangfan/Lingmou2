package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.OrganizationNewModule;
import cloud.antelope.lingmou.mvp.ui.fragment.OrganizationNewFragment;
import dagger.Component;

@FragmentScope
@Component(modules = OrganizationNewModule.class, dependencies = AppComponent.class)
public interface OrganizationNewComponent {
    void inject(OrganizationNewFragment fragment);
}
