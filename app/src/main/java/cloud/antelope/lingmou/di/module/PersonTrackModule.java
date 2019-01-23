package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.contract.PersonTrackContract;
import cloud.antelope.lingmou.mvp.model.PersonTrackModel;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.ui.adapter.PersonTrackAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.PersonTrackPagerAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.HorizontalItemDecoration;
import dagger.Module;
import dagger.Provides;


@Module
public class PersonTrackModule {
    private PersonTrackContract.View view;

    /**
     * 构建PersonTrackModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public PersonTrackModule(PersonTrackContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    PersonTrackContract.View providePersonTrackView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    PersonTrackContract.Model providePersonTrackModel(PersonTrackModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    PersonTrackPagerAdapter providePagerAdapter() {
        return new PersonTrackPagerAdapter(view.getActivity());
    }

    @ActivityScope
    @Provides
    PersonTrackAdapter providePersonTrackAdapter() {
        return new PersonTrackAdapter(null);
    }

    @ActivityScope
    @Provides
    List<FaceNewEntity> provideFaceList() {
        return new ArrayList<>();
    }

    @ActivityScope
    @Provides
    List<LatLng> provideLatLngList() {
        return new ArrayList<>();
    }

    @ActivityScope
    @Provides
    List<String> provideDeviceList() {
        return new ArrayList<>();
    }

    @ActivityScope
    @Provides
    List<Marker> provideMarkerList() {
        return new ArrayList<>();
    }

    @ActivityScope
    @Provides
    RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(view.getActivity());
    }

    @ActivityScope
    @Provides
    RecyclerView.ItemAnimator provideItemAnimator() {
        return new DefaultItemAnimator();
    }

    @ActivityScope
    @Provides
    RecyclerView.ItemDecoration provideItemDecoration() {
        return new HorizontalItemDecoration.Builder(view.getActivity())
                .color(Utils.getContext().getResources().getColor(R.color.bg_app))
                .sizeResId(R.dimen.a_half_dp)
                // .marginResId(R.dimen.sixteen_dp, R.dimen.sixteen_dp)
                .build();
    }

    @ActivityScope
    @Provides
    LruCache<Integer, BitmapDescriptor> provideLruCache() {
        //默认最多会缓存80张图片作为聚合显示元素图片,根据自己显示需求和app使用内存情况,可以修改数量
        return new LruCache<Integer, BitmapDescriptor>(80) {
            protected void entryRemoved(boolean evicted, Integer key, BitmapDescriptor oldValue, BitmapDescriptor newValue) {
                oldValue.getBitmap().recycle();
            }
        };
    }

}
