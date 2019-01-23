package cloud.antelope.lingmou.di.module;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.AnswerItemEntity;
import cloud.antelope.lingmou.mvp.ui.adapter.CommentDetailAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.HorizontalItemDecoration;
import dagger.Module;
import dagger.Provides;

import cloud.antelope.lingmou.mvp.contract.CommentDetailContract;
import cloud.antelope.lingmou.mvp.model.CommentDetailModel;


@Module
public class CommentDetailModule {
    private CommentDetailContract.View view;

    /**
     * 构建CommentDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public CommentDetailModule(CommentDetailContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    CommentDetailContract.View provideCommentDetailView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    CommentDetailContract.Model provideCommentDetailModel(CommentDetailModel model) {
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
                .color(Utils.getContext().getResources().getColor(R.color.answer_list_divide_line))
                .sizeResId(R.dimen.a_half_dp)
                .marginResId(R.dimen.dp16, R.dimen.dp16)
                .build();
    }

    @ActivityScope
    @Provides
    List<AnswerItemEntity> provideList() {
        return new ArrayList<>();
    }

    @ActivityScope
    @Provides
    CommentDetailAdapter provideAdapter(List<AnswerItemEntity> list) {
        return new CommentDetailAdapter(list);
    }

    @ActivityScope
    @Provides
    @Named("draftMap")
    Map<String, String> provideMap() {
        return new HashMap<>();
    }

}
