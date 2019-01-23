package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.contract.DevicesContract;
import cloud.antelope.lingmou.mvp.model.DevicesModel;
import cloud.antelope.lingmou.mvp.ui.adapter.DeviceCameraAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.DeviceParentAdapter;
import dagger.Module;
import dagger.Provides;


@Module
public class DevicesModule {
    private DevicesContract.View view;

    /**
     * 构建DevicesModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public DevicesModule(DevicesContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    DevicesContract.View provideDevicesView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    DevicesContract.Model provideDevicesModel(DevicesModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    DeviceParentAdapter provideDeviceParentAdapter(){
        return new DeviceParentAdapter(new ArrayList());
    }

    @FragmentScope
    @Provides
    DeviceCameraAdapter provideDeviceCameraAdapter(){
        return new DeviceCameraAdapter(new ArrayList());
    }
}