package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.adapter.VictoryAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.VictoryShowContract;
import cloud.antelope.lingmou.mvp.model.VictoryShowModel;


@Module
public class VictoryShowModule {
    private VictoryShowContract.View view;

    /**
     * 构建VictoryShowModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public VictoryShowModule(VictoryShowContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    VictoryShowContract.View provideVictoryShowView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    VictoryShowContract.Model provideVictoryShowModel(VictoryShowModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    VictoryAdapter provideVitoryAdapter() {
        return new VictoryAdapter();
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
    LoadMoreView provideLoadMoreView() {
        return new CustomerLoadMoreView();
    }
}
