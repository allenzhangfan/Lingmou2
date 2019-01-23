package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.modle.LoadingDialog;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.VideoPlayContract;
import cloud.antelope.lingmou.mvp.model.VideoPlayModel;


@Module
public class VideoPlayModule {
    private VideoPlayContract.View view;

    /**
     * 构建VideoPlayModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public VideoPlayModule(VideoPlayContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    VideoPlayContract.View provideVideoPlayView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    VideoPlayContract.Model provideVideoPlayModel(VideoPlayModel model) {
        return model;
    }

}
