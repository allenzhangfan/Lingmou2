package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.di.scope.FragmentScope;

import javax.inject.Named;

import cloud.antelope.lingmou.mvp.ui.adapter.CollectCameraNewAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.DailyFilterAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.RealVideosListAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.RealVideosListContract;
import cloud.antelope.lingmou.mvp.model.RealVideosListModel;


@Module
public class RealVideosListModule {
    private RealVideosListContract.View view;

    /**
     * 构建RealVideosListModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public RealVideosListModule(RealVideosListContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    RealVideosListContract.View provideRealVideosListView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    RealVideosListContract.Model provideRealVideosListModel(RealVideosListModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    @Named("PoliceLayoutManager")
    RecyclerView.LayoutManager provideLayoutManager() {
        return new GridLayoutManager(view.getFragmentActivity(), 2);
        // return new LinearLayoutManager(view.getFragmentActivity());
    }

    @ActivityScope
    @Provides
    @Named("FilterLayoutManager")
    RecyclerView.LayoutManager provideFilterLayoutManager() {
        return new LinearLayoutManager(view.getFragmentActivity());
    }

    @ActivityScope
    @Provides
    RealVideosListAdapter provideRealVideosListAdapter() {
        return new RealVideosListAdapter(view.getFragmentActivity());
        // return new CollectCameraAdapter(null, null);
    }

    @ActivityScope
    @Provides
    @Named("PoliceItemAnimator")
    RecyclerView.ItemAnimator provideItemAnimator() {
        return new DefaultItemAnimator();
    }

    @ActivityScope
    @Provides
    @Named("FilterItemAnimator")
    RecyclerView.ItemAnimator provideFilterItemAnimator() {
        return new DefaultItemAnimator();
    }


    @ActivityScope
    @Provides
    RecyclerView.ItemDecoration provideItemDecoration() {
        return new DividerItemDecoration(view.getFragmentActivity(), DividerItemDecoration.VERTICAL);
    }

    @ActivityScope
    @Provides
    LoadMoreView provideLoadMoreView() {
        return new CustomerLoadMoreView();
    }

    @ActivityScope
    @Provides
    DailyFilterAdapter provideDailyFilterAdapter() {
        return new DailyFilterAdapter();
    }
}
