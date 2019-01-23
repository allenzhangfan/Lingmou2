package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.Utils;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.contract.NewsListContract;
import cloud.antelope.lingmou.mvp.model.NewsListModel;
import cloud.antelope.lingmou.mvp.ui.adapter.NewsAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import cloud.antelope.lingmou.mvp.ui.widget.HorizontalItemDecoration;
import dagger.Module;
import dagger.Provides;


@Module
public class NewsListModule {
    private NewsListContract.View view;

    /**
     * 构建NewsListModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public NewsListModule(NewsListContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    NewsListContract.View provideNewsListView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    NewsListContract.Model provideNewsListModel(NewsListModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(view.getActivity());
    }

    @ActivityScope
    @Provides
    RecyclerView.ItemAnimator provideItemAnimator() {
        return new DefaultItemAnimator();
    }

    @ActivityScope
    @Provides
    RecyclerView.ItemDecoration provideItemDecoration() {
        return new HorizontalItemDecoration.Builder(view.getActivity())
                .color(Utils.getContext().getResources().getColor(R.color.bg_app))
                .sizeResId(R.dimen.dp8)
                // .marginResId(R.dimen.sixteen_dp, R.dimen.sixteen_dp)
                .build();
    }

    @ActivityScope
    @Provides
    LoadMoreView provideLoadMoreView() {
        return new CustomerLoadMoreView();
    }

    @ActivityScope
    @Provides
    NewsAdapter provideNewsAdapter() {
        return new NewsAdapter(null);
    }

}
