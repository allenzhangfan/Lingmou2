package cloud.antelope.lingmou.di.module;

import android.app.Activity;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.ui.adapter.TrackingPersonAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.TrackingPersonContract;
import cloud.antelope.lingmou.mvp.model.TrackingPersonModel;


@Module
public class TrackingPersonModule {
    private TrackingPersonContract.View view;

    /**
     * 构建TrakingPersonModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public TrackingPersonModule(TrackingPersonContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    TrackingPersonContract.View provideTrackingPersonView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    TrackingPersonContract.Model provideTrackingPersonModel(TrackingPersonModel model) {
        return model;
    }
    @ActivityScope
    @Provides
    TrackingPersonAdapter provideTrackingPersonAdapter() {
        return new TrackingPersonAdapter((Activity) view,new ArrayList<>());
    }
    @ActivityScope
    @Provides
    LoadMoreView provideLoadMoreView() {
        return new CustomerLoadMoreView();
    }
}