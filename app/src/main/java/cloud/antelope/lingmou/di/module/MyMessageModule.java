package cloud.antelope.lingmou.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.MyMessageContract;
import cloud.antelope.lingmou.mvp.model.MyMessageModel;


@Module
public class MyMessageModule {
    private MyMessageContract.View view;

    /**
     * 构建MyMessageModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public MyMessageModule(MyMessageContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    MyMessageContract.View provideMyMessageView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    MyMessageContract.Model provideMyMessageModel(MyMessageModel model) {
        return model;
    }
}