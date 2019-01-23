package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.utils.Utils;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.FeedbackContract;
import cloud.antelope.lingmou.mvp.model.FeedbackModel;


@Module
public class FeedbackModule {
    private FeedbackContract.View view;

    /**
     * 构建FeedbackModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public FeedbackModule(FeedbackContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    FeedbackContract.View provideFeedbackView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    FeedbackContract.Model provideFeedbackModel(FeedbackModel model) {
        return model;
    }


    @ActivityScope
    @Provides
    LoadingDialog provideLoadingDialog() {
        return new LoadingDialog(view.getActivity());
    }
}