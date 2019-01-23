package cloud.antelope.lingmou.mvp.ui.widget;

import com.chad.library.adapter.base.loadmore.LoadMoreView;

import cloud.antelope.lingmou.R;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/28
 * 邮箱：anxingya@lingdanet.com
 * 描述：评论的加载更多View
 */

public class CommentLoadMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.comment_load_more_view;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }
}
