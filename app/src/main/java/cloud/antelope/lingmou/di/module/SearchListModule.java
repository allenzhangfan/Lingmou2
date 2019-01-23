package cloud.antelope.lingmou.di.module;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.contract.SearchListContract;
import cloud.antelope.lingmou.mvp.model.SearchListModel;
import cloud.antelope.lingmou.mvp.ui.adapter.DeployControlAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchAlertAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchBodyAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchFaceAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchVideoAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import dagger.Module;
import dagger.Provides;


@Module
public class SearchListModule {
    private SearchListContract.View view;
    public SearchListModule(SearchListContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    SearchListContract.View provideSearchControlView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    SearchListContract.Model provideDeploySearchModel(SearchListModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    SearchFaceAdapter provideSearchFaceAdapter(){
        return new SearchFaceAdapter(view.getActivity(),new ArrayList<>());
    }
    @FragmentScope
    @Provides
    SearchBodyAdapter provideSearchBodyAdapter(){
        return new SearchBodyAdapter(view.getActivity(),new ArrayList<>());
    }
    @FragmentScope
    @Provides
    SearchAlertAdapter provideSearchAlertAdapter(){
        return new SearchAlertAdapter(view.getActivity(),new ArrayList<>());
    }
    @FragmentScope
    @Provides
    SearchVideoAdapter provideSearchVedioAdapter(){
        return new SearchVideoAdapter(view.getActivity(),new ArrayList<>());
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