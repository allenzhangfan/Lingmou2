package cloud.antelope.lingmou.di.module;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.ActivityScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.ui.adapter.CollectedFaceAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.CollectedFaceContract;
import cloud.antelope.lingmou.mvp.model.CollectedFaceModel;


@Module
public class CollectedFaceModule {
    private CollectedFaceContract.View view;

    /**
     * 构建CollectedFaceModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CollectedFaceModule(CollectedFaceContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    CollectedFaceContract.View provideCollectedFaceView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    CollectedFaceContract.Model provideCollectedFaceModel(CollectedFaceModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    CollectedFaceAdapter provideCollectionAdapter(){
        return new CollectedFaceAdapter(view.getActivity(),new ArrayList<>());
    }

    @ActivityScope
    @Provides
    LoadMoreView provideLoadMoreView() {
        return new CustomerLoadMoreView();
    }
}