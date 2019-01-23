package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.DevicesModule;
import cloud.antelope.lingmou.mvp.ui.fragment.DevicesFragment;
import dagger.Component;

@FragmentScope
@Component(modules = DevicesModule.class, dependencies = AppComponent.class)
public interface DevicesComponent {
    void inject(DevicesFragment fragment);
}