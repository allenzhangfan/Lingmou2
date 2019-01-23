package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.modle.LoadingDialog;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.MessageCodeActivityContract;
import cloud.antelope.lingmou.mvp.model.MessageCodeActivityModel;


@Module
public class MessageCodeActivityModule {
    private MessageCodeActivityContract.View view;

    /**
     * 构建MessageCodeActivityModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public MessageCodeActivityModule(MessageCodeActivityContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    MessageCodeActivityContract.View provideMessageCodeActivityView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    MessageCodeActivityContract.Model provideMessageCodeActivityModel(MessageCodeActivityModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    LoadingDialog provideLoadingDialog() {
        return new LoadingDialog(view.getActivity());
    }
}