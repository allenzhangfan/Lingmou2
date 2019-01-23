package cloud.antelope.lingmou.di.module;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.di.scope.FragmentScope;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.contract.CloudMapContract;
import cloud.antelope.lingmou.mvp.model.CloudMapModel;
import cloud.antelope.lingmou.mvp.ui.adapter.CameraAddressAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.CameraResourceAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.HorizontalItemDecoration;
import dagger.Module;
import dagger.Provides;


@Module
public class CloudMapModule {
    private CloudMapContract.View view;

    /**
     * 构建CloudMapModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */

    public CloudMapModule(CloudMapContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    CloudMapContract.View provideCloudMapView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    CloudMapContract.Model provideCloudMapModel(CloudMapModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    CameraAddressAdapter provideCameraAddressAdapter() {
        return new CameraAddressAdapter(null);
    }

    @FragmentScope
    @Provides
    RecyclerView.ItemDecoration provideItemDecoration() {
        return new HorizontalItemDecoration.Builder(view.getFragmentActivity())
                .color(Utils.getContext().getResources().getColor(R.color.home_divider_lint))
                .sizeResId(R.dimen.a_half_dp)
                .marginResId(R.dimen.dp6, R.dimen.dp6)
                .build();
    }

    @FragmentScope
    @Provides
    RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(view.getFragmentActivity());
    }

    @FragmentScope
    @Provides
    RecyclerView.ItemAnimator provideItemAnimator() {
        return new DefaultItemAnimator();
    }

    @FragmentScope
    @Provides
    Map<Integer, Drawable> provideMap() {
        return new HashMap<>();
    }

    @FragmentScope
    @Provides
    LoadingDialog provideLoadingDialog() {
        return new LoadingDialog(view.getFragmentActivity());
    }

    @FragmentScope
    @Provides
    CameraResourceAdapter provideCameraResourceAdapter() {
        return new CameraResourceAdapter(view.getFragmentActivity(), new ArrayList<>());
    }

}
