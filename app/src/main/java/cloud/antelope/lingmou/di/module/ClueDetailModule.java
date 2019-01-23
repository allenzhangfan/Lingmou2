package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.di.scope.ActivityScope;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.mvp.contract.ClueDetailContract;
import cloud.antelope.lingmou.mvp.model.ClueDetailModel;
import cloud.antelope.lingmou.mvp.model.entity.AnswerItemEntity;
import cloud.antelope.lingmou.mvp.ui.adapter.ClueTalkAdapter;
import dagger.Module;
import dagger.Provides;


@Module
public class ClueDetailModule {
    private ClueDetailContract.View view;

    /**
     * 构建ClueDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public ClueDetailModule(ClueDetailContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ClueDetailContract.View provideClueDetailView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    ClueDetailContract.Model provideClueDetailModel(ClueDetailModel model) {
        return model;
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
/*
    @ActivityScope
    @Provides
    List<ClueDetailActivity.ClueBannerItem> provideList() {
        return new ArrayList<>();
    }*/

    @ActivityScope
    @Provides
    List<AnswerItemEntity> provideList() {
        return new ArrayList<>();
    }

    @ActivityScope
    @Provides
    ClueTalkAdapter provideAdapter(List<AnswerItemEntity> list) {
        return new ClueTalkAdapter(list);
    }
}
