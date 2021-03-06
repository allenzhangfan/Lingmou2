package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.MyCollectionContract;
import cloud.antelope.lingmou.mvp.model.MyCollectionModel;


@Module
public class MyCollectionModule {
    private MyCollectionContract.View view;

    /**
     * 构建MyCollectionModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public MyCollectionModule(MyCollectionContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    MyCollectionContract.View provideMyCollectionView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    MyCollectionContract.Model provideMyCollectionModel(MyCollectionModel model) {
        return model;
    }
}