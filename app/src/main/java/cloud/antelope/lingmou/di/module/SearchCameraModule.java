package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.Utils;

import javax.inject.Named;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.contract.SearchCameraContract;
import cloud.antelope.lingmou.mvp.model.SearchCameraModel;
import cloud.antelope.lingmou.mvp.ui.adapter.OrgCameraAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchCameraAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchHistoryAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.HorizontalItemDecoration;
import dagger.Module;
import dagger.Provides;


@Module
public class SearchCameraModule {
    private SearchCameraContract.View view;

    /**
     * 构建SearchCameraModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public SearchCameraModule(SearchCameraContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    SearchCameraContract.View provideSearchCameraView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    SearchCameraContract.Model provideSearchCameraModel(SearchCameraModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    OrgCameraAdapter provideCameraAdapter() {
        return new OrgCameraAdapter(null);
    }

    @ActivityScope
    @Provides
    SearchHistoryAdapter provideSearchHistoryAdapter() {
        return new SearchHistoryAdapter(null);
    }

    @ActivityScope
    @Provides
    @Named("HistoryItemDecoration")
    RecyclerView.ItemDecoration provideHistoryItemDecoration() {
        return new HorizontalItemDecoration.Builder(view.getActivity())
                .color(Utils.getContext().getResources().getColor(R.color.home_divider_lint))
                .sizeResId(R.dimen.a_half_dp)
                .marginResId(R.dimen.dp18, R.dimen.dp18)
                .build();
    }

    @ActivityScope
    @Provides
    @Named("CameraItemDecoration")
    RecyclerView.ItemDecoration provideCameraItemDecoration() {
        return new HorizontalItemDecoration.Builder(view.getActivity())
                .color(Utils.getContext().getResources().getColor(R.color.home_divider_lint))
                .sizeResId(R.dimen.a_half_dp)
                .build();
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
    SearchCameraAdapter provideSearchCameraAdapter() {
        return new SearchCameraAdapter(null);
    }
}
