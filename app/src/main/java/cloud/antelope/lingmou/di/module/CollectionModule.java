package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.mvp.contract.CollectionContract;
import cloud.antelope.lingmou.mvp.model.CollectionModel;
import cloud.antelope.lingmou.mvp.ui.adapter.CameraAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.CollectCameraAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.CollectCameraNewAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import dagger.Module;
import dagger.Provides;


@Module
public class CollectionModule {
    private CollectionContract.View view;

    /**
     * 构建CollectionModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CollectionModule(CollectionContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    CollectionContract.View provideCollectionView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    CollectionContract.Model provideCollectionModel(CollectionModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    RecyclerView.LayoutManager provideLayoutManager() {
        return new GridLayoutManager(view.getFragmentActivity(), 2);
        // return new LinearLayoutManager(view.getFragmentActivity());
    }

    @FragmentScope
    @Provides
    CollectCameraNewAdapter provideCameraAdapter() {
        return new CollectCameraNewAdapter(view.getFragmentActivity());
        // return new CollectCameraAdapter(null, null);
    }

    @FragmentScope
    @Provides
    RecyclerView.ItemAnimator provideItemAnimator() {
        return new DefaultItemAnimator();
    }

    @FragmentScope
    @Provides
    RecyclerView.ItemDecoration provideItemDecoration() {
        return new DividerItemDecoration(view.getFragmentActivity(), DividerItemDecoration.VERTICAL);
    }

    @FragmentScope
    @Provides
    LoadMoreView provideLoadMoreView() {
        return new CustomerLoadMoreView();
    }
}
