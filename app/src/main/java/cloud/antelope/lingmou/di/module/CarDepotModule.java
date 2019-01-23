package cloud.antelope.lingmou.di.module;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.ui.adapter.CarDepotAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.CarDepotContract;
import cloud.antelope.lingmou.mvp.model.CarDepotModel;


@Module
public class CarDepotModule {
    private CarDepotContract.View view;

    /**
     * 构建CarDepotModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CarDepotModule(CarDepotContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    CarDepotContract.View provideCarDepotView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    CarDepotContract.Model provideCarDepotModel(CarDepotModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    CarDepotAdapter provideCarDepotAdapter() {
        return new CarDepotAdapter(view.getActivity(),new ArrayList<>());
    }
    @ActivityScope
    @Provides
    LoadMoreView provideLoadMoreView() {
        return new CustomerLoadMoreView();
    }
}