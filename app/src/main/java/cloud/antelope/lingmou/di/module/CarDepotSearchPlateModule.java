package cloud.antelope.lingmou.di.module;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.ui.adapter.CarDepotSearchPlateListAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.CarDepotSearchPlateContract;
import cloud.antelope.lingmou.mvp.model.CarDepotSearchPlateModel;


@Module
public class CarDepotSearchPlateModule {
    private CarDepotSearchPlateContract.View view;

    /**
     * 构建CarDepotSearchPlateModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CarDepotSearchPlateModule(CarDepotSearchPlateContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    CarDepotSearchPlateContract.View provideCarDepotSearchPlateView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    CarDepotSearchPlateContract.Model provideCarDepotSearchPlateModel(CarDepotSearchPlateModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    CarDepotSearchPlateListAdapter provideCarDepotSearchPlateListAdapter() {
        return new CarDepotSearchPlateListAdapter(new ArrayList<>());
    }

    @FragmentScope
    @Provides
    LoadMoreView provideLoadMoreView() {
        return new CustomerLoadMoreView();
    }
}