package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.ReportAddressModule;

import cloud.antelope.lingmou.mvp.ui.activity.ReportAddressActivity;

@ActivityScope
@Component(modules = ReportAddressModule.class, dependencies = AppComponent.class)
public interface ReportAddressComponent {
    void inject(ReportAddressActivity activity);
}
