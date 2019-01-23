package cloud.antelope.lingmou.di.component;


import dagger.Component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.di.module.CameraListModule;

import cloud.antelope.lingmou.mvp.ui.fragment.CameraListFragment;

@FragmentScope
@Component(modules = CameraListModule.class, dependencies = AppComponent.class)
public interface CameraListComponent {
    void inject(CameraListFragment fragment);
}