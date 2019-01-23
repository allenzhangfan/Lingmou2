package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lzy.imagepicker.bean.MediaItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import cloud.antelope.lingmou.mvp.contract.ReportEditContract;
import cloud.antelope.lingmou.mvp.model.ReportEditModel;
import dagger.Module;
import dagger.Provides;


@Module
public class ReportEditModule {

    private static final int ITEM_SPAN_COUNT = 3;

    private ReportEditContract.View view;

    /**
     * 构建ReportEditModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public ReportEditModule(ReportEditContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ReportEditContract.View provideReportEditView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    ReportEditContract.Model provideReportEditModel(ReportEditModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    @Named("SelectMediaList")
    List<MediaItem> provideMediaList() {
        return new ArrayList<>();
    }

    @ActivityScope
    @Provides
    @Named("SelectVideoList")
    List<MediaItem> provideVideoList() {
        return new ArrayList<>();
    }

    @ActivityScope
    @Provides
    RecyclerView.LayoutManager provideLayoutManager() {
        return new GridLayoutManager(view.getActivity(), ITEM_SPAN_COUNT);
    }

    @ActivityScope
    @Provides
    LoadingDialog provideLoadingDialog() {
        return new LoadingDialog(view.getActivity());
    }
}
