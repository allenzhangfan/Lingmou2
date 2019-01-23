package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.CommentDetailModule;

import cloud.antelope.lingmou.mvp.ui.activity.CommentDetailActivity;

@ActivityScope
@Component(modules = CommentDetailModule.class, dependencies = AppComponent.class)
public interface CommentDetailComponent {
    void inject(CommentDetailActivity activity);
}
