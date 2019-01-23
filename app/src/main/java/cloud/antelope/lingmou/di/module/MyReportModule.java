package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.contract.MyReportContract;
import cloud.antelope.lingmou.mvp.model.MyReportModel;
import cloud.antelope.lingmou.mvp.ui.adapter.ClueAdapter;
import dagger.Module;
import dagger.Provides;


@Module
public class MyReportModule {
    private MyReportContract.View view;

    /**
     * 构建MyReportModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public MyReportModule(MyReportContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    MyReportContract.View provideMyReportView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    MyReportContract.Model provideMyReportModel(MyReportModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    ClueAdapter provideClueAdapter() {
        return new ClueAdapter(null);
    }

}
