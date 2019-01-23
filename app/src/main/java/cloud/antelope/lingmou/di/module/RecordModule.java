package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.RecordContract;
import cloud.antelope.lingmou.mvp.model.RecordModel;


@Module
public class RecordModule {
    private RecordContract.View view;

    /**
     * 构建RecordModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public RecordModule(RecordContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    RecordContract.View provideRecordView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    RecordContract.Model provideRecordModel(RecordModel model) {
        return model;
    }
}
