package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.ui.adapter.DeployControlAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.FaceDepotDetailAdapter;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.FaceDepotDetailContract;
import cloud.antelope.lingmou.mvp.model.FaceDepotDetailModel;


@Module
public class FaceDepotDetailModule {
    private FaceDepotDetailContract.View view;

    /**
     * 构建FaceDepotDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public FaceDepotDetailModule(FaceDepotDetailContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    FaceDepotDetailContract.View provideFaceDepotDetailView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    FaceDepotDetailContract.Model provideFaceDepotDetailModel(FaceDepotDetailModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    FaceDepotDetailAdapter provideFaceDepotDetailAdapter(){
        return new FaceDepotDetailAdapter(view.getActivity(),new ArrayList<>());
    }}