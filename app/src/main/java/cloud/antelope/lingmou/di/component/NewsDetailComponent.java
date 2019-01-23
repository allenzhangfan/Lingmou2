package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.di.module.AllCommentModule;
import cloud.antelope.lingmou.di.module.NewsDetailModule;
import cloud.antelope.lingmou.mvp.ui.activity.NewsDetailActivity;
import dagger.Component;

@ActivityScope
@Component(modules = {NewsDetailModule.class, AllCommentModule.class}, dependencies = AppComponent.class)
public interface NewsDetailComponent {
    void inject(NewsDetailActivity activity);
}
