package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.EventRemindListModule;

import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.mvp.ui.fragment.EventRemindListFragment;

@FragmentScope
@Component(modules = EventRemindListModule.class, dependencies = AppComponent.class)
public interface EventRemindListComponent {
    void inject(EventRemindListFragment fragment);
}