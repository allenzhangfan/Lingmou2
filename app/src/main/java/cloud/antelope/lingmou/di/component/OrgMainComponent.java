package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.OrgMainModule;

import cloud.antelope.lingmou.mvp.ui.fragment.OrgMainFragment;

@ActivityScope
@Component(modules = OrgMainModule.class, dependencies = AppComponent.class)
public interface OrgMainComponent {
    void inject(OrgMainFragment fragment);
}
