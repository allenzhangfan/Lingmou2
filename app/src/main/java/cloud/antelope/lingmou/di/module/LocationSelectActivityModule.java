package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.adapter.LocationAdapter;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.LocationSelectActivityContract;
import cloud.antelope.lingmou.mvp.model.LocationSelectActivityModel;


@Module
public class LocationSelectActivityModule {
    private LocationSelectActivityContract.View view;

    /**
     * 构建LocationSelectActivityModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public LocationSelectActivityModule(LocationSelectActivityContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    LocationSelectActivityContract.View provideLocationSelectActivityView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    LocationSelectActivityContract.Model provideLocationSelectActivityModel(LocationSelectActivityModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    LocationAdapter provideLocationAdapter(){
        return new LocationAdapter(R.layout.adapter_location,new ArrayList<>());
    }
}