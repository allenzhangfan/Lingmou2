package cloud.antelope.lingmou.di.component

import cloud.antelope.lingmou.di.module.KFeedbackModule
import cloud.antelope.lingmou.mvp.ui.activity.KFeedbackActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.di.scope.ActivityScope
import dagger.Component

@ActivityScope
@Component(modules = arrayOf(KFeedbackModule::class), dependencies = arrayOf(AppComponent::class))
interface KFeedbackComponent {
     fun inject(activity: KFeedbackActivity)
}