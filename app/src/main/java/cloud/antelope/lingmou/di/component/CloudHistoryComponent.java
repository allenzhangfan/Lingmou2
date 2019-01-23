package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.CloudHistoryModule;
import cloud.antelope.lingmou.mvp.ui.fragment.CloudHistoryFragment;
import dagger.Component;

@FragmentScope
@Component(modules = CloudHistoryModule.class, dependencies = AppComponent.class)
public interface CloudHistoryComponent {
    void inject(CloudHistoryFragment fragment);
}
