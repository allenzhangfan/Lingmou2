package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.SizeUtils;

import javax.inject.Named;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.adapter.DailyFilterAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.FaceDepotAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import cloud.antelope.lingmou.mvp.ui.widget.SpacesItemDecoration;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.FaceDepotContract;
import cloud.antelope.lingmou.mvp.model.FaceDepotModel;


@Module
public class FaceDepotModule {
    private FaceDepotContract.View view;

    /**
     * 构建FaceDepotModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public FaceDepotModule(FaceDepotContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    FaceDepotContract.View provideFaceDepotView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    FaceDepotContract.Model provideFaceDepotModel(FaceDepotModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    @Named("gridlayout")
    RecyclerView.LayoutManager provideLayoutManager() {
        return new GridLayoutManager(view.getActivity(), 2);
    }

    @ActivityScope
    @Provides
    @Named("linearlayout")
    RecyclerView.LayoutManager provideLinearLayoutManager() {
        return new LinearLayoutManager(view.getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @ActivityScope
    @Provides
    RecyclerView.ItemDecoration provideItemDecoration() {
        return new SpacesItemDecoration(2, SizeUtils.dp2px(7), false);
    }

    @ActivityScope
    @Provides
    @Named("gridAnimator")
    RecyclerView.ItemAnimator provideItemAnimator() {
        return new DefaultItemAnimator();
    }

    @ActivityScope
    @Provides
    @Named("linearAnimator")
    RecyclerView.ItemAnimator provideLinearItemAnimator() {
        return new DefaultItemAnimator();
    }

    @ActivityScope
    @Provides
    FaceDepotAdapter provideAdapter() {
        return new FaceDepotAdapter(view.getActivity(), null);
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
