package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.Utils;

import cloud.antelope.lingmou.mvp.ui.adapter.FaceRecognizeAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.GridSpacingItemDecoration;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.FaceRecognizeContract;
import cloud.antelope.lingmou.mvp.model.FaceRecognizeModel;


@Module
public class FaceRecognizeModule {
    private FaceRecognizeContract.View view;

    /**
     * 构建FaceRecognizeModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public FaceRecognizeModule(FaceRecognizeContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    FaceRecognizeContract.View provideFaceRecognizeView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    FaceRecognizeContract.Model provideFaceRecognizeModel(FaceRecognizeModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    FaceRecognizeAdapter provideFaceRecogAdapter() {
        return new FaceRecognizeAdapter(Utils.getContext(), null);
    }

    @ActivityScope
    @Provides
    RecyclerView.LayoutManager provideLayoutManager() {
        return new GridLayoutManager(Utils.getContext(), 3);
    }

    @ActivityScope
    @Provides
    RecyclerView.ItemDecoration provideItemDecoration() {
        return new GridSpacingItemDecoration(3, 10, true);
    }

}
