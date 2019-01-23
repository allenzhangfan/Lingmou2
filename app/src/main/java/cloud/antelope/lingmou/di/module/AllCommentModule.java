package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.contract.AllCommentContract;
import cloud.antelope.lingmou.mvp.model.AllCommentModel;
import cloud.antelope.lingmou.mvp.model.entity.CommentItemEntity;
import cloud.antelope.lingmou.mvp.ui.adapter.CommentAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.HorizontalItemDecoration;
import dagger.Module;
import dagger.Provides;


@Module
public class AllCommentModule {
    private AllCommentContract.View view;

    /**
     * 构建AllCommentModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public AllCommentModule(AllCommentContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    AllCommentContract.View provideAllCommentView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    AllCommentContract.Model provideAllCommentModel(AllCommentModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(view.getActivity());
    }

    @ActivityScope
    @Provides
    RecyclerView.ItemAnimator provideItemAnimator() {
        return new DefaultItemAnimator();
    }

    @ActivityScope
    @Provides
    RecyclerView.ItemDecoration provideItemDecoration() {
        return new HorizontalItemDecoration.Builder(view.getActivity())
                .color(Utils.getContext().getResources().getColor(R.color.comment_divide_line_color))
                .sizeResId(R.dimen.a_half_dp)
                .marginResId(R.dimen.dp16, R.dimen.dp16)
                .build();
    }

    @ActivityScope
    @Provides
    List<CommentItemEntity> provideList() {
        return new ArrayList<>();
    }

    @ActivityScope
    @Provides
    CommentAdapter provideCommentAdapter(List<CommentItemEntity> list) {
        return new CommentAdapter(list);
    }

    @ActivityScope
    @Provides
    @Named("draftMap")
    Map<String, String> provideMap() {
        return new HashMap<>();
    }

    @ActivityScope
    @Provides
    LoadingDialog provideLoadingDialog() {
        return new LoadingDialog(view.getActivity());
    }
}
