package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.FragmentScope;

import javax.inject.Named;

import cloud.antelope.lingmou.mvp.contract.DailyPoliceContract;
import cloud.antelope.lingmou.mvp.model.DailyPoliceModel;
import cloud.antelope.lingmou.mvp.ui.adapter.DailyFilterAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.DailyPoliceAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import dagger.Module;
import dagger.Provides;


@Module
public class DailyPoliceModule {
    private DailyPoliceContract.View view;

    /**
     * 构建DailyPoliceModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public DailyPoliceModule(DailyPoliceContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    DailyPoliceContract.View provideDailyPoliceView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    DailyPoliceContract.Model provideDailyPoliceModel(DailyPoliceModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    DailyPoliceAdapter provideDailyPoliceAdapter() {
        return new DailyPoliceAdapter(null, view.getFragmentContext().getContext());
    }

    @FragmentScope
    @Provides
    @Named("PoliceLayoutManager")
    RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(view.getFragmentContext().getContext());
    }

    @FragmentScope
    @Provides
    @Named("FilterLayoutManager")
    RecyclerView.LayoutManager provideFilterLayoutManager() {
        return new LinearLayoutManager(view.getFragmentContext().getContext());
    }

    @FragmentScope
    @Provides
    @Named("PoliceItemAnimator")
    RecyclerView.ItemAnimator provideItemAnimator() {
        return new DefaultItemAnimator();
    }

    @FragmentScope
    @Provides
    @Named("FilterItemAnimator")
    RecyclerView.ItemAnimator provideFilterItemAnimator() {
        return new DefaultItemAnimator();
    }

    @FragmentScope
    @Provides
    RecyclerView.ItemDecoration provideItemDecoration() {
        return new DividerItemDecoration(view.getFragmentContext().getContext(), DividerItemDecoration.VERTICAL);
    }

    @FragmentScope
    @Provides
    LoadMoreView provideLoadMoreView() {
        return new CustomerLoadMoreView();
    }


    @FragmentScope
    @Provides
    DailyFilterAdapter provideDailyFilterAdapter() {
        return new DailyFilterAdapter();
    }
}
