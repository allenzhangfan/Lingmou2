package cloud.antelope.lingmou.di.module;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.contract.DeployListContract;
import cloud.antelope.lingmou.mvp.model.DeployListModel;
import cloud.antelope.lingmou.mvp.ui.adapter.DeployControlAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import dagger.Module;
import dagger.Provides;


@Module
public class DeployListModule {
    private DeployListContract.View view;
    public DeployListModule(DeployListContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    DeployListContract.View provideDeployControlView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    DeployListContract.Model provideDeployControlModel(DeployListModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    DeployControlAdapter provideDeployControlAdapter(){
        return new DeployControlAdapter(view.getActivity(),new ArrayList<>());
    }

    @FragmentScope
    @Provides
    LoadMoreView provideLoadMoreView() {
        return new CustomerLoadMoreView();
    }
}