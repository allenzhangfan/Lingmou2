package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.RecyclerView;

import com.jess.arms.di.scope.ActivityScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import cloud.antelope.lingmou.mvp.contract.NewsDetailContract;
import cloud.antelope.lingmou.mvp.model.NewsDetailModel;
import cloud.antelope.lingmou.mvp.ui.adapter.CommentAdapter;
import dagger.Module;
import dagger.Provides;


@Module
public class NewsDetailModule {
    private NewsDetailContract.View view;

    /**
     * 构建NewsDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public NewsDetailModule(NewsDetailContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    NewsDetailContract.View provideNewsDetailView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    NewsDetailContract.Model provideNewsDetailModel(NewsDetailModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    RecyclerView provideRecyclerView() {
        return new RecyclerView(view.getActivity());
    }

    @ActivityScope
    @Provides
    @Named("mobIdCache")
    Map<String, String> provideMap() {
        return new HashMap<>();
    }

    @ActivityScope
    @Provides
    List<String> provideImageList() {
        return new ArrayList();
    }
}
