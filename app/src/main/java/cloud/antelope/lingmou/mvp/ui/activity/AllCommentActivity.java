package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerAllCommentComponent;
import cloud.antelope.lingmou.di.module.AllCommentModule;
import cloud.antelope.lingmou.mvp.contract.AllCommentContract;
import cloud.antelope.lingmou.mvp.model.entity.AnswerItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.CommentItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.CommentListEntity;
import cloud.antelope.lingmou.mvp.presenter.AllCommentPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.CommentAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CommentLoadMoreView;
import cloud.antelope.lingmou.mvp.ui.widget.popup.CommentPopWindow;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class AllCommentActivity extends BaseActivity<AllCommentPresenter>
        implements AllCommentContract.View, BaseQuickAdapter.RequestLoadMoreListener,
        CommentPopWindow.onSendCommentClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.tip_no_comment_tv)
    TextView mTipNoCommentTv;
    @BindView(R.id.root_ll)
    LinearLayout mRootLl;
    @BindView(R.id.comment_srfl)
    SwipeRefreshLayout mCommentSrfl;
    @BindView(R.id.comment_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.comment_ll)
    FrameLayout mCommentLl;
    @BindView(R.id.comment_et)
    LinearLayout mCommentEt;

    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    RecyclerView.ItemDecoration mItemDecoration;
    @Inject
    List<CommentItemEntity> mCommentList;
    @Inject
    CommentAdapter mCommentAdapter;
    @Inject
    @Named("draftMap")
    Map<String, String> mCommentDraft;
    @Inject
    LoadingDialog mLoadingDialog;

    private int page = 0;

    private String mColumnId;
    private String mContentId;
    private String mCreateUserNickName;
    private String mCreateUserId;
    private boolean mLoadFirstPage;
    private float mCommentEtY;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerAllCommentComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .allCommentModule(new AllCommentModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_all_comment; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        mTitleTv.setText(R.string.all_comment_title);
        Intent intent = getIntent();
        mColumnId = intent.getStringExtra(NewsDetailActivity.COLUMN_ID);
        mContentId = intent.getStringExtra(NewsDetailActivity.CONTENT_ID);
        mLoadFirstPage = intent.getBooleanExtra(NewsDetailActivity.LOAD_FIRST_PAGE, false);
        mCreateUserNickName = intent.getStringExtra(NewsDetailActivity.CREATE_USER_NICK_NAME);
        mCreateUserId = intent.getStringExtra(NewsDetailActivity.CREATE_USER_ID);
        mCommentList = (ArrayList<CommentItemEntity>) intent.getSerializableExtra(NewsDetailActivity.COMMENT_LIST);

        mCommentSrfl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mCommentSrfl.setOnRefreshListener(this);

        initRecyclerView();
        mCommentSrfl.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCommentSrfl.setRefreshing(true);
                onRefresh();
            }
        }, 100);
        mTipNoCommentTv.setVisibility(View.GONE);
        if (null == mCommentList) {
            mCommentList = new ArrayList<>();
        }
        /*if (null != mCommentList && mCommentList.size() > 0) {
            mTipNoCommentTv.setVisibility(View.GONE);
            initRecyclerView();
        } else if (!mLoadFirstPage) { // 如果上一个页面的评论数据还没有加载出来就点击进入了这个页面，则这个页面重新加载第一页的数据
            mTipNoCommentTv.setVisibility(View.GONE);
            mCommentList = new ArrayList<>();
            initRecyclerView();
            mCommentSrfl.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCommentSrfl.setRefreshing(true);
                    onRefresh();
                }
            }, 100);
        }*/

        mCommentEt.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCommentEtY = mCommentEt.getY();
                mCommentEt.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        initListener();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(mItemAnimator);
        mRecyclerView.addItemDecoration(mItemDecoration);
        mRecyclerView.setNestedScrollingEnabled(false);
        mCommentAdapter.setNewData(mCommentList);
        mCommentAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mCommentAdapter.setLoadMoreView(new CommentLoadMoreView());
        mRecyclerView.setAdapter(mCommentAdapter);
    }

    private void initListener() {
        mCommentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CommentItemEntity commentItem = (CommentItemEntity) adapter.getItem(position);
                List<AnswerItemEntity> answerList = commentItem.getAnswerList();
                AnswerItemEntity first = new AnswerItemEntity();
                first.setCreateUserNickName(commentItem.getCreateUserNickName());
                first.setCreateUserId(commentItem.getCreateUserId());
                first.setCreateUserAvatar(commentItem.getCreateUserAvatar());
                first.setCreateTime(commentItem.getCreateTime());
                first.setReply(commentItem.getReply());
                first.setReplyId(commentItem.getId());
                first.setTopReplyId(commentItem.getId());
                first.setContentId(commentItem.getContentId());
                first.setColumnId(commentItem.getColumnId());
                first.setOperationCenterId(commentItem.getOperationCenterId());
                List<AnswerItemEntity> newAnswerList = new ArrayList<>();  // 这里需要创建一个新的集合对象，防止原集合被修改
                if (null != answerList) {
                    newAnswerList.addAll(answerList);
                }
                newAnswerList.add(0, first);
                Intent intent = new Intent(AllCommentActivity.this, CommentDetailActivity.class);
                intent.putExtra("answerList", (Serializable) newAnswerList);
                startActivity(intent);
            }
        });
        // mCommentAdapter.setOnAnswerItemClickListener(new CommentAdapter.OnAnswerItemClickListener() {
        //     @Override
        //     public void onAnswerItemClick(AnswerItemEntity answerItem) {
        //         showCommentPopupWindow(answerItem.getReplyId(), "回复" + answerItem.getCreateUserName() + "：",
        //                 mCommentDraft.get(answerItem.getReplyId()));
        //     }
        //
        //     @Override
        //     public void onSeeAllItemClick(List<AnswerItemEntity> answerList) {
        //         ToastUtils.showShort("查看全部");
        //     }
        // });
    }

    @Override
    public void onLoadMoreRequested() {
        mTipNoCommentTv.setVisibility(View.GONE);
        if ((page == 0 && mCommentList.size() >= NewsDetailActivity.PAGE_SIZE) || !mLoadFirstPage || page > 0) {
            getCommentList();
        } else {
            mCommentAdapter.loadMoreEnd(false);
        }
    }

    @OnClick({R.id.comment_et, R.id.head_left_iv})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_iv:
                finish();
                break;
            case R.id.comment_et:
                if (!SPUtils.getInstance().getBoolean(Constants.HAS_LOGIN)) {
                    gotoLoginActivity();
                    return;
                }
                showCommentPopupWindow(NewsDetailActivity.REPLY_NEWS,
                        getString(R.string.tip_comment),
                        mCommentDraft.get(NewsDetailActivity.REPLY_NEWS),
                        "0", null, mCreateUserNickName, mCreateUserId);
                break;
        }
    }

    private void showCommentPopupWindow(String commentId, String hintText, String commentDraft, String topReplyId,
                                        String upReplyId, String reUserName, String reUserId) {
        CommentPopWindow popWindow = new CommentPopWindow(getActivity(),
                mRootLl, R.layout.pop_comment, commentId, topReplyId, upReplyId, reUserName, reUserId);
        popWindow.showPopupWindow();
        popWindow.setCommentHint(hintText);
        popWindow.setCommentDraft(commentDraft);
        popWindow.onSendCommentClickListener(this);
    }

    private void gotoLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(Constants.GOTO_MAIN, false);
        startActivity(intent);
    }

    @Override
    public void sendComment(String commentId, String topReplyId,
                            String upReplyId, String reUserName, String reUserId, String reply) {
        mPresenter.addReply(mColumnId, mContentId, commentId, topReplyId, reUserName, reUserId, reply);
    }

    @Override
    public void saveDraft(String commentId, String draft) {
        mCommentDraft.put(commentId, draft);
        float y = mCommentEt.getY();
        if (y < mCommentEtY) {
            KeyboardUtils.toggleSoftInput();
        }
    }

    private void getCommentList() {
        if (page == 0) {
            int offset = mCommentList.size() % NewsDetailActivity.PAGE_SIZE;
            if (offset != 0) {
                mCommentAdapter.loadMoreEnd(false);
                return;
            }
            page = mCommentList.size() / NewsDetailActivity.PAGE_SIZE;
        } else {
            page++;
        }
        mPresenter.queryReplyPage(mColumnId, mContentId, page, NewsDetailActivity.PAGE_SIZE);
    }

    @Override
    public void onAddReplySuccess(String replyId) {
        ToastUtils.showShort(R.string.comment_send_success);
        if (mCommentDraft.containsKey(replyId)) {
            mCommentDraft.remove(replyId);
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onQueryReplyPageSuccess(CommentListEntity commentListEntity) {
        mCommentSrfl.setRefreshing(false);
        List<CommentItemEntity> list = commentListEntity.getList();
        if (null != list) {
            if (!mLoadFirstPage) {
                mLoadFirstPage = true;
                mCommentList = list;
            }
            if (page == 0) {
                mCommentAdapter.setNewData(mCommentList);
                if (list.isEmpty()) {
                    mTipNoCommentTv.setVisibility(View.VISIBLE);
                }
            } else {
                mCommentAdapter.addData(list);
            }
            if (list.size() < NewsDetailActivity.PAGE_SIZE) {
                mCommentAdapter.loadMoreEnd(false);
            } else {
                mCommentAdapter.loadMoreComplete();
            }
        } else {
            mTipNoCommentTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onQueryReplyPageError() {
        mCommentSrfl.setRefreshing(false);
        page--;
        mCommentAdapter.loadMoreFail();
    }


    @Override
    public void showLoading(String message) {
        mLoadingDialog.show();
    }

    @Override
    public void hideLoading() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.toastText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    @Override
    public void onRefresh() {
        mLoadFirstPage = false;
        page = 0;
        mPresenter.queryReplyPage(mColumnId, mContentId, page, NewsDetailActivity.PAGE_SIZE);
    }
}
