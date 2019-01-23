package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.di.module.CyqzConfigModule;
import cloud.antelope.lingmou.di.module.NewsListModule;
import cloud.antelope.lingmou.mvp.ui.activity.NewsListActivity;
import dagger.Component;

@ActivityScope
@Component(modules = {NewsListModule.class, CyqzConfigModule.class}, dependencies = AppComponent.class)
public interface NewsListComponent {
    void inject(NewsListActivity activity);
}
