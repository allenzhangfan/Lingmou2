package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.DeviceShowMapContract;
import cloud.antelope.lingmou.mvp.model.DeviceShowMapModel;


@Module
public class DeviceShowMapModule {
    private DeviceShowMapContract.View view;

    /**
     * 构建DeviceShowMapModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public DeviceShowMapModule(DeviceShowMapContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    DeviceShowMapContract.View provideDeviceShowMapView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    DeviceShowMapContract.Model provideDeviceShowMapModel(DeviceShowMapModel model) {
        return model;
    }
}
