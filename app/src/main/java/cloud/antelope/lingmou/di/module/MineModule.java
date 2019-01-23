package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.mvp.contract.DailyContract;
import cloud.antelope.lingmou.mvp.contract.MineContract;
import cloud.antelope.lingmou.mvp.model.DailyModel;
import cloud.antelope.lingmou.mvp.model.MineModel;
import dagger.Module;
import dagger.Provides;


@Module
public class MineModule {
    private MineContract.View view;


    /**
     * 构建AccountModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public MineModule(MineContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    MineContract.View provideMineContract() {
        return this.view;
    }

    @ActivityScope
    @Provides
    MineContract.Model provideMineModel(MineModel model) {
        return model;
    }

}