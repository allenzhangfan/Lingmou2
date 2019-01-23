package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.SystemBarTintManager;

import javax.inject.Named;

import cloud.antelope.lingmou.mvp.ui.adapter.DepotAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.FaceAlarmAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import cloud.antelope.lingmou.mvp.ui.widget.FullyGridLayoutManager;
import cloud.antelope.lingmou.mvp.ui.widget.GridSpacingItemDecoration;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.ClothControlAlarmContract;
import cloud.antelope.lingmou.mvp.model.ClothControlAlarmModel;


@Module
public class ClothControlAlarmModule {
    private ClothControlAlarmContract.View view;

    /**
     * 构建ClothControlAlarmModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public ClothControlAlarmModule(ClothControlAlarmContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ClothControlAlarmContract.View provideClothControlAlarmView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    ClothControlAlarmContract.Model provideClothControlAlarmModel(ClothControlAlarmModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    FaceAlarmAdapter provideFaceAlarmAdapter() {
        return new FaceAlarmAdapter(view.getActivity(), null);
    }

    @ActivityScope
    @Provides
    @Named("LinearManager")
    RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(view.getActivity());
    }

    @ActivityScope
    @Provides
    @Named("GridManager")
    RecyclerView.LayoutManager provideGridLayoutManager() {
        return new GridLayoutManager(view.getActivity(), 2);
    }

    @ActivityScope
    @Provides
    RecyclerView.ItemDecoration provideGridItemDecoration() {
        return new GridSpacingItemDecoration(2, SizeUtils.dp2px(10), true);
    }

    @ActivityScope
    @Provides
    RecyclerView.ItemAnimator provideItemAnimator() {
        return new DefaultItemAnimator();
    }

    @ActivityScope
    @Provides
    LoadMoreView provideLoadMoreView() {
        return new CustomerLoadMoreView();
    }

    @ActivityScope
    @Provides
    DepotAdapter provideDepotAdapter() {
        return new DepotAdapter(null);
    }

}
