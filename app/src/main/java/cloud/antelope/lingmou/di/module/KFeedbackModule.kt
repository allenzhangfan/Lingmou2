package cloud.antelope.lingmou.di.module

import cloud.antelope.lingmou.mvp.contract.FeedbackContract
import cloud.antelope.lingmou.mvp.contract.KFeedbackContract
import cloud.antelope.lingmou.mvp.model.FeedbackModel
import cloud.antelope.lingmou.mvp.model.KFeedbackModel
import com.jess.arms.di.scope.ActivityScope
import com.lingdanet.safeguard.common.modle.LoadingDialog
import dagger.Module
import dagger.Provides

@Module
class KFeedbackModule
( val view: KFeedbackContract.View) {

    @ActivityScope
    @Provides
    internal fun provideFeedbackView(): KFeedbackContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    internal fun provideFeedbackModel(model: KFeedbackModel): KFeedbackContract.Model {
        return model
    }


    @ActivityScope
    @Provides
    internal fun provideLoadingDialog(): LoadingDialog {
        return LoadingDialog(view.getActivity())
    }
}