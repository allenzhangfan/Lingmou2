package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.HistoryWatchContract;
import cloud.antelope.lingmou.mvp.model.HistoryWatchModel;


@Module
public class HistoryWatchModule {
    private HistoryWatchContract.View view;

    /**
     * 构建HistoryWatchModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public HistoryWatchModule(HistoryWatchContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    HistoryWatchContract.View provideHistoryWatchView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    HistoryWatchContract.Model provideHistoryWatchModel(HistoryWatchModel model) {
        return model;
    }
}