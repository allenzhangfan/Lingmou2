package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.FragmentScope;

import cloud.antelope.lingmou.mvp.contract.OrganizationParentContract;
import cloud.antelope.lingmou.mvp.model.OrganizationParentModel;
import dagger.Module;
import dagger.Provides;


@Module
public class OrganizationParentModule {
    private OrganizationParentContract.View view;

    /**
     * 构建OrganizationParentModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public OrganizationParentModule(OrganizationParentContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    OrganizationParentContract.View provideOrganizationParentView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    OrganizationParentContract.Model provideOrganizationParentModel(OrganizationParentModel model) {
        return model;
    }
}
