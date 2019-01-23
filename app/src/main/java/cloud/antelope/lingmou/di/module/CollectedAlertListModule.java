package cloud.antelope.lingmou.di.module;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.contract.CollectedAlertListContract;
import cloud.antelope.lingmou.mvp.contract.DeployListContract;
import cloud.antelope.lingmou.mvp.model.CollectedAlertListModel;
import cloud.antelope.lingmou.mvp.model.DeployListModel;
import cloud.antelope.lingmou.mvp.ui.adapter.DailyPoliceAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.DeployControlAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import dagger.Module;
import dagger.Provides;


@Module
public class CollectedAlertListModule {
    private CollectedAlertListContract.View view;
    public CollectedAlertListModule(CollectedAlertListContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    CollectedAlertListContract.View provideCollectedAlertListView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    CollectedAlertListContract.Model provideCollectedAlertListModel(CollectedAlertListModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    DailyPoliceAdapter provideDailyPoliceAdapter(){
        return new DailyPoliceAdapter(new ArrayList<>(),view.getActivity());
    }

    @FragmentScope
    @Provides
    LoadMoreView provideLoadMoreView() {
        return new CustomerLoadMoreView();
    }
}