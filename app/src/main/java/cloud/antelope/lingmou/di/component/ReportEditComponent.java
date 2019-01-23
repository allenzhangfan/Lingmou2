package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.di.module.CyqzConfigModule;
import cloud.antelope.lingmou.di.module.ReportEditModule;
import cloud.antelope.lingmou.mvp.ui.activity.ReportEditActivity;
import dagger.Component;

@ActivityScope
@Component(modules = {ReportEditModule.class, CyqzConfigModule.class}, dependencies = AppComponent.class)
public interface ReportEditComponent {
    void inject(ReportEditActivity activity);
}
