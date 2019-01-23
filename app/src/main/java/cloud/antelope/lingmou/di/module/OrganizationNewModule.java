package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.di.scope.FragmentScope;
import com.lingdanet.safeguard.common.modle.LoadingDialog;

import cloud.antelope.lingmou.mvp.contract.OrganizationNewContract;
import cloud.antelope.lingmou.mvp.model.OrganizationNewModel;
import cloud.antelope.lingmou.mvp.ui.adapter.OrganizationAdapter;
import dagger.Module;
import dagger.Provides;


@Module
public class OrganizationNewModule {
    private OrganizationNewContract.View view;

    /**
     * 构建OrganizationNewModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public OrganizationNewModule(OrganizationNewContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    OrganizationNewContract.View provideOrganizationNewView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    OrganizationNewContract.Model provideOrganizationNewModel(OrganizationNewModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(view.getFragmentActivity());
    }

    @FragmentScope
    @Provides
    OrganizationAdapter provideOrganizationAdapter() {
        return new OrganizationAdapter(null);
    }

    @FragmentScope
    @Provides
    RecyclerView.ItemAnimator provideItemAnimator() {
        return new DefaultItemAnimator();
    }

    @FragmentScope
    @Provides
    RecyclerView.ItemDecoration provideItemDecoration() {
        return new DividerItemDecoration(view.getFragmentActivity(), DividerItemDecoration.VERTICAL);
    }

    @FragmentScope
    @Provides
    LoadingDialog provideLoadingDialog() {
        return new LoadingDialog(view.getFragmentActivity());
    }
}
