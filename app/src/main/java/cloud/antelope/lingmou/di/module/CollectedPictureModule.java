package cloud.antelope.lingmou.di.module;

import android.support.v4.app.FragmentManager;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.adapter.CollectionPagerAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchPagerAdapter;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.CollectedPictureContract;
import cloud.antelope.lingmou.mvp.model.CollectedPictureModel;


@Module
public class CollectedPictureModule {
    private CollectedPictureContract.View view;
    private FragmentManager fragmentManager;
    /**
     * 构建CollectedPictureModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CollectedPictureModule(CollectedPictureContract.View view, FragmentManager fragmentManager) {
        this.view = view;
        this.fragmentManager = fragmentManager;
    }

    @ActivityScope
    @Provides
    CollectedPictureContract.View provideCollectedPictureView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    CollectedPictureContract.Model provideCollectedPictureModel(CollectedPictureModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    CollectionPagerAdapter provideCollectionPagerAdapter() {
        return new CollectionPagerAdapter(fragmentManager);
    }
}