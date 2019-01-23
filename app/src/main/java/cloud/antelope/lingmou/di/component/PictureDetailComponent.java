package cloud.antelope.lingmou.di.component;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.di.module.PictureDetailModule;
import cloud.antelope.lingmou.mvp.ui.activity.PictureDetailActivity;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

@ActivityScope
@Component(modules = PictureDetailModule.class, dependencies = AppComponent.class)
public interface PictureDetailComponent {
    void inject(PictureDetailActivity activity);
}