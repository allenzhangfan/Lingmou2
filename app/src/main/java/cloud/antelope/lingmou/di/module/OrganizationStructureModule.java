package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.OrganizationStructureContract;
import cloud.antelope.lingmou.mvp.model.OrganizationStructureModel;


@Module
public class OrganizationStructureModule {
    private OrganizationStructureContract.View view;

    /**
     * 构建OrganizationStructureModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public OrganizationStructureModule(OrganizationStructureContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    OrganizationStructureContract.View provideOrganizationStructureView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    OrganizationStructureContract.Model provideOrganizationStructureModel(OrganizationStructureModel model) {
        return model;
    }
}