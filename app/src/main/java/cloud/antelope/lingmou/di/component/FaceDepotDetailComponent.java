package cloud.antelope.lingmou.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import cloud.antelope.lingmou.di.module.FaceDepotDetailModule;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.activity.FaceDepotDetailActivity;

@ActivityScope
@Component(modules = FaceDepotDetailModule.class, dependencies = AppComponent.class)
public interface FaceDepotDetailComponent {
    void inject(FaceDepotDetailActivity activity);
}