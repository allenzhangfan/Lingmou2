package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.contract.PictureDepotDetailContract;
import cloud.antelope.lingmou.mvp.model.PictureDetailModel;
import cloud.antelope.lingmou.mvp.ui.adapter.PicturePagerAdapter;
import dagger.Module;
import dagger.Provides;


@Module
public class PictureDetailModule {
    private PictureDepotDetailContract.View view;

    /**
     * 构建FaceDepotDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public PictureDetailModule(PictureDepotDetailContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    PictureDepotDetailContract.View provideFaceDepotDetailView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    PictureDepotDetailContract.Model provideFaceDepotDetailModel(PictureDetailModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    PicturePagerAdapter provideFaceDepotAdapter() {
        return new PicturePagerAdapter(view.getActivity(), null);
    }

}