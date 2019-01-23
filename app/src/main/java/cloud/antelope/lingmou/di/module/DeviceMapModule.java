package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.DeviceMapContract;
import cloud.antelope.lingmou.mvp.model.DeviceMapModel;


@Module
public class DeviceMapModule {
    private DeviceMapContract.View view;

    /**
     * 构建DeviceMapModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public DeviceMapModule(DeviceMapContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    DeviceMapContract.View provideDeviceMapView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    DeviceMapContract.Model provideDeviceMapModel(DeviceMapModel model) {
        return model;
    }
}