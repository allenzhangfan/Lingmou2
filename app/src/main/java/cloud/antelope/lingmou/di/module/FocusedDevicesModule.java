package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.FocusedDevicesContract;
import cloud.antelope.lingmou.mvp.model.FocusedDevicesModel;


@Module
public class FocusedDevicesModule {
    private FocusedDevicesContract.View view;

    /**
     * 构建focused_devicesModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public FocusedDevicesModule(FocusedDevicesContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    FocusedDevicesContract.View provideFocusedDevicesView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    FocusedDevicesContract.Model provideFocusedDevicesModel(FocusedDevicesModel model) {
        return model;
    }
}