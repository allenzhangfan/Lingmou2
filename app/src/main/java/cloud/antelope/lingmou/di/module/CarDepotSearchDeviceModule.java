package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.ui.adapter.SearchCameraAdapter;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.CarDepotSearchDeviceContract;
import cloud.antelope.lingmou.mvp.model.CarDepotSearchDeviceModel;


@Module
public class CarDepotSearchDeviceModule {
    private CarDepotSearchDeviceContract.View view;

    /**
     * 构建CarDepotSearchDeviceModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CarDepotSearchDeviceModule(CarDepotSearchDeviceContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    CarDepotSearchDeviceContract.View provideCarDepotSearchDeviceView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    CarDepotSearchDeviceContract.Model provideCarDepotSearchDeviceModel(CarDepotSearchDeviceModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    SearchCameraAdapter provideSearchCameraAdapter() {
        return new SearchCameraAdapter(new ArrayList<>());
    }
}