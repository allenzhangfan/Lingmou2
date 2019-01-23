package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.CloudMapModule;
import cloud.antelope.lingmou.mvp.ui.fragment.CloudMapFragment;
import dagger.Component;

@FragmentScope
@Component(modules = CloudMapModule.class, dependencies = AppComponent.class)
public interface CloudMapComponent {
    void inject(CloudMapFragment fragment);
}
