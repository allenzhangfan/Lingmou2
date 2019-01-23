package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.ui.adapter.CarBrandAdapter;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.CarBrandContract;
import cloud.antelope.lingmou.mvp.model.CarBrandModel;


@Module
public class CarBrandModule {
    private CarBrandContract.View view;

    /**
     * 构建CarBrandModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CarBrandModule(CarBrandContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    CarBrandContract.View provideCarBrandView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    CarBrandContract.Model provideCarBrandModel(CarBrandModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    CarBrandAdapter provideCarBrandAdapter() {
        return new CarBrandAdapter(view.getActivity(),new ArrayList<>());
    }
}