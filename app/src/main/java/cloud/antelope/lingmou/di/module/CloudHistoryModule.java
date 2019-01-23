package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.mvp.ui.adapter.CloudHistoryAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.CollectCameraNewAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.CloudHistoryContract;
import cloud.antelope.lingmou.mvp.model.CloudHistoryModel;


@Module
public class CloudHistoryModule {
    private CloudHistoryContract.View view;

    /**
     * 构建CloudHistoryModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CloudHistoryModule(CloudHistoryContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    CloudHistoryContract.View provideCloudHistoryView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    CloudHistoryContract.Model provideCloudHistoryModel(CloudHistoryModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(view.getFragmentActivity());
    }

    @FragmentScope
    @Provides
    CloudHistoryAdapter provideCloudHistoryAdapter() {
        return new CloudHistoryAdapter(view.getFragmentActivity());
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
