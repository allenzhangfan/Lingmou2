package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.AllCommentModule;

import cloud.antelope.lingmou.mvp.ui.activity.AllCommentActivity;

@ActivityScope
@Component(modules = AllCommentModule.class, dependencies = AppComponent.class)
public interface AllCommentComponent {
    void inject(AllCommentActivity activity);
}
