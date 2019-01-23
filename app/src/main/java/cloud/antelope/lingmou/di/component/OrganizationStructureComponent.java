package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.OrganizationStructureModule;

import cloud.antelope.lingmou.mvp.ui.activity.OrganizationStructureActivity;

@ActivityScope
@Component(modules = OrganizationStructureModule.class, dependencies = AppComponent.class)
public interface OrganizationStructureComponent {
    void inject(OrganizationStructureActivity activity);
}