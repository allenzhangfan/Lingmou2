package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.CloudEyeModule;
import cloud.antelope.lingmou.mvp.ui.fragment.CloudEyeFragment;
import dagger.Component;

@FragmentScope
@Component(modules = CloudEyeModule.class, dependencies = AppComponent.class)
public interface CloudEyeComponent {
    void inject(CloudEyeFragment fragment);
}
