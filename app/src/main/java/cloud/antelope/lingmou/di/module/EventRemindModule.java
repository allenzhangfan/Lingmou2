package cloud.antelope.lingmou.di.module;

import android.support.v4.app.FragmentManager;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.ui.adapter.EventRemindPagerAdapter;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.EventRemindContract;
import cloud.antelope.lingmou.mvp.model.EventRemindModel;


@Module
public class EventRemindModule {
    private EventRemindContract.View view;
    private FragmentManager fragmentManager;
    /**
     * 构建EventRemindModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public EventRemindModule(EventRemindContract.View view, FragmentManager fragmentManager) {
        this.view = view;
        this.fragmentManager = fragmentManager;
    }

    @ActivityScope
    @Provides
    EventRemindContract.View provideEventRemindView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    EventRemindContract.Model provideEventRemindModel(EventRemindModel model) {
        return model;
    }
    @ActivityScope
    @Provides
    EventRemindPagerAdapter provideEventRemindPagerAdapter() {
        return new EventRemindPagerAdapter(fragmentManager);
    }
}