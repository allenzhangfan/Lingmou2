package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.di.module.CyqzConfigModule;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.MyReportModule;

import cloud.antelope.lingmou.mvp.ui.activity.MyReportActivity;

@ActivityScope
@Component(modules = {MyReportModule.class}, dependencies = AppComponent.class)
public interface MyReportComponent {
    void inject(MyReportActivity activity);
}
