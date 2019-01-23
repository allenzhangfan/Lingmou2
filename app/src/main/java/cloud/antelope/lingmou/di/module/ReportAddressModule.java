package cloud.antelope.lingmou.di.module;

import com.amap.api.services.geocoder.GeocodeSearch;
import com.jess.arms.di.scope.ActivityScope;

import cloud.antelope.lingmou.app.utils.GisUtils;
import cloud.antelope.lingmou.mvp.contract.ReportAddressContract;
import cloud.antelope.lingmou.mvp.model.ReportAddressModel;
import dagger.Module;
import dagger.Provides;


@Module
public class ReportAddressModule {

    private static final int LOCATE_INTERVAL = 3000;

    private ReportAddressContract.View view;

    /**
     * 构建ReportAddressModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public ReportAddressModule(ReportAddressContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ReportAddressContract.View provideReportAddressView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    ReportAddressContract.Model provideReportAddressModel(ReportAddressModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    GisUtils provideGisUtils() {
        return new GisUtils(view.getActivity(), LOCATE_INTERVAL);
    }

    @ActivityScope
    @Provides
    GeocodeSearch provideGeocodeSearch() {
        return new GeocodeSearch(view.getActivity());
    }
}
