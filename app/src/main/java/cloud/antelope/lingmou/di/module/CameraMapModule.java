package cloud.antelope.lingmou.di.module;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.di.scope.FragmentScope;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.contract.CameraMapContract;
import cloud.antelope.lingmou.mvp.model.CameraMapModel;
import cloud.antelope.lingmou.mvp.ui.adapter.CameraAddressAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.CameraResourceAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.HorizontalItemDecoration;
import dagger.Module;
import dagger.Provides;


@Module
public class CameraMapModule {
    private CameraMapContract.View view;

    /**
     * 构建CameraMapModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CameraMapModule(CameraMapContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    CameraMapContract.View provideCameraMapView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    CameraMapContract.Model provideCameraMapModel(CameraMapModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    CameraAddressAdapter provideCameraAddressAdapter() {
        return new CameraAddressAdapter(null);
    }

    @ActivityScope
    @Provides
    RecyclerView.ItemDecoration provideItemDecoration() {
        return new HorizontalItemDecoration.Builder(view.getActivity())
                .color(Utils.getContext().getResources().getColor(R.color.home_divider_lint))
                .sizeResId(R.dimen.a_half_dp)
                .marginResId(R.dimen.dp9, R.dimen.dp9)
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
    Map<Integer, Drawable> provideMap() {
        return new HashMap<>();
    }

    @ActivityScope
    @Provides
    LoadingDialog provideLoadingDialog() {
        return new LoadingDialog(view.getActivity());
    }

    @ActivityScope
    @Provides
    CameraResourceAdapter provideCameraResourceAdapter() {
        return new CameraResourceAdapter(view.getActivity(), new ArrayList<>());
    }
}
