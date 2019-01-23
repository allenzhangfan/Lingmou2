package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.mvp.contract.WorkbenchContract;
import cloud.antelope.lingmou.mvp.model.WorkbenchModel;
import cloud.antelope.lingmou.mvp.ui.adapter.WorkbenchAdapter;
import dagger.Module;
import dagger.Provides;


@Module
public class WorkbenchModule {
    private WorkbenchContract.View view;


    /**
     * 构建AccountModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public WorkbenchModule(WorkbenchContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    WorkbenchContract.View provideWorkbenchContract() {
        return this.view;
    }

    @FragmentScope
    @Provides
    WorkbenchContract.Model provideWorkbenchModel(WorkbenchModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    WorkbenchAdapter provideWorkbenchAdapter() {
        return new WorkbenchAdapter(null);
    }

}
