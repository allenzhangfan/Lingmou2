package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.ui.adapter.DeviceParentAdapter;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.DailyDevicesContract;
import cloud.antelope.lingmou.mvp.model.DailyDevicesModel;


@Module
public class DailyDevicesModule {
    private DailyDevicesContract.View view;

    /**
     * 构建DailyDevicesModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public DailyDevicesModule(DailyDevicesContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    DailyDevicesContract.View provideDailyDevicesView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    DailyDevicesContract.Model provideDailyDevicesModel(DailyDevicesModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    DeviceParentAdapter provideDeviceParentAdapter(){
        return new DeviceParentAdapter(new ArrayList());
    }
}