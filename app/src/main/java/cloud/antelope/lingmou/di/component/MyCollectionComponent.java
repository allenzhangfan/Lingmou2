package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.MyCollectionModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.MyCollectionActivity;

@ActivityScope
@Component(modules = MyCollectionModule.class, dependencies = AppComponent.class)
public interface MyCollectionComponent {
    void inject(MyCollectionActivity activity);
}