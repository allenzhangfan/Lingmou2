package cloud.antelope.lingmou.di.module;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.mvp.contract.CloudEyeContract;
import cloud.antelope.lingmou.mvp.model.CloudEyeModel;
import cloud.antelope.lingmou.mvp.ui.adapter.CloudEyeFragmentAdapter;
import cloud.antelope.lingmou.mvp.ui.fragment.CloudHistoryFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.CollectionFragment;
import dagger.Module;
import dagger.Provides;


@Module
public class CloudEyeModule {
    private CloudEyeContract.View view;
    private FragmentManager fragmentManager;

    /**
     * 构建CloudEyeModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CloudEyeModule(CloudEyeContract.View view, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.view = view;
    }

    @FragmentScope
    @Provides
    CloudEyeContract.View provideCloudEyeView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    CloudEyeContract.Model provideCloudEyeModel(CloudEyeModel model) {
        return model;
    }

    @FragmentScope
    @Provides
    List<Fragment> provideFragmentList() {
        return new ArrayList<>();
    }

    @FragmentScope
    @Provides
    CloudEyeFragmentAdapter provideHomeFragmentAdapter(List<Fragment> list) {
        return new CloudEyeFragmentAdapter(this.fragmentManager, list);
    }

    @FragmentScope
    @Provides
    CollectionFragment provideCollectionFragment() {
        return CollectionFragment.newInstance();
    }

    @FragmentScope
    @Provides
    CloudHistoryFragment provideCloudHistoryFragment() {
        return CloudHistoryFragment.newInstance();
    }
}
