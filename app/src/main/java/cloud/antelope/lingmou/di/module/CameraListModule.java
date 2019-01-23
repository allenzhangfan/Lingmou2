package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.mvp.ui.adapter.CameraAdapter;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.CameraListContract;
import cloud.antelope.lingmou.mvp.model.CameraListModel;


@Module
public class CameraListModule {
    private CameraListContract.View view;
    private String orgName;

    /**
     * 构建CameraListModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CameraListModule(CameraListContract.View view, String orgName) {
        this.view = view;
        this.orgName = orgName;
    }

    @FragmentScope
    @Provides
    CameraListContract.View provideCameraListView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    CameraListContract.Model provideCameraListModel(CameraListModel model) {
        return model;
    }


    @FragmentScope
    @Provides
    CameraAdapter provideCameraAdapter() {
        return new CameraAdapter(null, orgName);
    }

    @FragmentScope
    @Provides
    RecyclerView.ItemAnimator provideItemAnimator() {
        return new DefaultItemAnimator();
    }

    @FragmentScope
    @Provides
    RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(view.getFragmentActivity());
    }

    @FragmentScope
    @Provides
    RecyclerView.ItemDecoration provideItemDecoration() {
        return new DividerItemDecoration(view.getFragmentActivity(), DividerItemDecoration.VERTICAL);
    }
}