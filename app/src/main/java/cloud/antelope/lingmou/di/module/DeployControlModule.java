package cloud.antelope.lingmou.di.module;

import android.support.v4.app.FragmentManager;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.ui.adapter.DeployControlAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.DeployPagerAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.DeployControlContract;
import cloud.antelope.lingmou.mvp.model.DeployControlModel;


@Module
public class DeployControlModule {
    private DeployControlContract.View view;
    private FragmentManager fragmentManager;
    /**
     * 构建DeployControlModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public DeployControlModule(DeployControlContract.View view, FragmentManager fragmentManager) {
        this.view = view;
        this.fragmentManager = fragmentManager;
    }

    @ActivityScope
    @Provides
    DeployControlContract.View provideDeployControlView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    DeployControlContract.Model provideDeployControlModel(DeployControlModel model) {
        return model;
    }


    @ActivityScope
    @Provides
    DeployPagerAdapter provideDeployPagerAdapter(){
        return new DeployPagerAdapter(fragmentManager);
    }

}