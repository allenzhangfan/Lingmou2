package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.DeviceListContract;
import cloud.antelope.lingmou.mvp.model.DeviceListModel;


@Module
public class DeviceListModule {
    private DeviceListContract.View view;

    /**
     * 构建DeviceListModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public DeviceListModule(DeviceListContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    DeviceListContract.View provideDeviceListView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    DeviceListContract.Model provideDeviceListModel(DeviceListModel model) {
        return model;
    }
}