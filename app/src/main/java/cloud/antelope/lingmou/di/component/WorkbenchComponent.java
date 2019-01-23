package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.WorkbenchModule;
import cloud.antelope.lingmou.mvp.ui.fragment.WorkbenchFragment;
import dagger.Component;

@FragmentScope
@Component(modules = WorkbenchModule.class, dependencies = AppComponent.class)
public interface WorkbenchComponent {
    void inject(WorkbenchFragment fragment);
}
