package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.MyMessageModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.MyMessageActivity;

@ActivityScope
@Component(modules = MyMessageModule.class, dependencies = AppComponent.class)
public interface MyMessageComponent {
    void inject(MyMessageActivity activity);
}