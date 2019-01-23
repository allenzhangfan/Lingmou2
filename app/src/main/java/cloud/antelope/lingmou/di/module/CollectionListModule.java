package cloud.antelope.lingmou.di.module;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.contract.CollectionListContract;
import cloud.antelope.lingmou.mvp.model.CollectionListModel;
import cloud.antelope.lingmou.mvp.ui.adapter.CollectionBodyAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.CollectionFaceAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.CollectionSnapshotAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import dagger.Module;
import dagger.Provides;


@Module
public class CollectionListModule {
    private CollectionListContract.View view;
    public CollectionListModule(CollectionListContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    CollectionListContract.View provideCollectionListView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    CollectionListContract.Model provideCollectionListModel(CollectionListModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    CollectionSnapshotAdapter provideCollectionAdapter(){
        return new CollectionSnapshotAdapter(view.getActivity(),new ArrayList<>());
    }
    @FragmentScope
    @Provides
    CollectionFaceAdapter provideCollectionFaceAdapter(){
        return new CollectionFaceAdapter(view.getActivity(),new ArrayList<>());
    }
    @FragmentScope
    @Provides
    CollectionBodyAdapter provideCollectionBodyAdapter(){
        return new CollectionBodyAdapter(view.getActivity(),new ArrayList<>());
    }

    @FragmentScope
    @Provides
    LoadMoreView provideLoadMoreView() {
        return new CustomerLoadMoreView();
    }
}