package cloud.antelope.lingmou.di.module;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.ui.adapter.DailyPoliceAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.DeployControlAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.EventRemindListAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.EventRemindListContract;
import cloud.antelope.lingmou.mvp.model.EventRemindListModel;


@Module
public class EventRemindListModule {
    private EventRemindListContract.View view;

    /**
     * 构建EventRemindListModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public EventRemindListModule(EventRemindListContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    EventRemindListContract.View provideEventRemindListView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    EventRemindListContract.Model provideEventRemindListModel(EventRemindListModel model) {
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