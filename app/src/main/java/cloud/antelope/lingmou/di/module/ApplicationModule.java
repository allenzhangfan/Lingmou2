package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.mvp.contract.ApplicationContract;
import cloud.antelope.lingmou.mvp.model.ApplicationModel;
import cloud.antelope.lingmou.mvp.model.entity.AppBean;
import cloud.antelope.lingmou.mvp.ui.adapter.AppAdapter;
import dagger.Module;
import dagger.Provides;


@Module
public class ApplicationModule {

    private static final int APP_SPAN_COUNT = 2;

    private ApplicationContract.View view;

    /**
     * 构建ApplicationModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public ApplicationModule(ApplicationContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    ApplicationContract.View provideApplicationView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    ApplicationContract.Model provideApplicationModel(ApplicationModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    List<AppBean> provideList() {
        return new ArrayList<>();
    }

    @FragmentScope
    @Provides
    AppAdapter provideAppAdapter(List<AppBean> list) {
        return new AppAdapter(list);
    }

    @FragmentScope
    @Provides
    RecyclerView.LayoutManager provideLayoutManager() {
        return new GridLayoutManager(view.getFragmentActivity(), APP_SPAN_COUNT);
    }

    @FragmentScope
    @Provides
    RecyclerView.ItemAnimator provideItemAnimator() {
        return new DefaultItemAnimator();
    }

}
