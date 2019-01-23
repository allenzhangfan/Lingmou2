package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerCommentDetailComponent;
import cloud.antelope.lingmou.di.module.CommentDetailModule;
import cloud.antelope.lingmou.mvp.contract.CommentDetailContract;
import cloud.antelope.lingmou.mvp.model.entity.AnswerItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.CommentItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.CommentListEntity;
import cloud.antelope.lingmou.mvp.presenter.CommentDetailPresenter;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.adapter.CommentDetailAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.popup.CommentPopWindow;


import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CommentDetailActivity extends BaseActivity<CommentDetailPresenter> implements
        CommentDetailContract.View ,
        CommentPopWindow.onSendCommentClickListener {

    public static final String FROM_MY_MESSAGE = "from_my_message";

    @BindView(R.id.root_ll)
    LinearLayout mRootLl;
    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.comment_tv)
    TextView mCommentTv;
    @BindView(R.id.comment_ll)
    LinearLayout mCommentLl;

    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    RecyclerView.ItemDecoration mItemDecoration;
    @Inject
    List<AnswerItemEntity> mAnswerList;
    @Inject
    CommentDetailAdapter mCommentDetailAdapter;
    @Inject
    @Named("draftMap")
    Map<String, String> mCommentDraft;


    private String mColumnId;
    private String mContentId;
    private String mCreateUserNickName;
    private String mCreateUserType;
    private String mCreateUserId;
    private String mReplyId;
    private String mMsgId;
    private String mMsgType;
    private float mCommentLlY;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerCommentDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .commentDetailModule(new CommentDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_comment_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mTitleTv.setText("全部对话");

        Intent intent = getIntent();
        boolean fromMyMessage = intent.getBooleanExtra(FROM_MY_MESSAGE, false);
        if (fromMyMessage) {
            // mColumnId = intent.getStringExtra(Constants.CONFIG_COLUMN_ID);
            mColumnId = SPUtils.getInstance().getString(Constants.CONFIG_CASE_ID);
            mContentId = intent.getStringExtra(Constants.CONFIG_CONTENT_ID);
            mReplyId = intent.getStringExtra(Constants.CONFIG_REPLY_ID);
            mMsgId = intent.getStringExtra("id");
            mMsgType = intent.getStringExtra("type");
            mPresenter.queryReplyPage(mColumnId, mContentId, mReplyId);
        } else {
            mAnswerList = (List<AnswerItemEntity>) intent.getSerializableExtra("answerList");
            mColumnId = mAnswerList.get(0).getColumnId();
            mContentId = mAnswerList.get(0).getContentId();
            mReplyId = mAnswerList.get(0).getReplyId();
            mCreateUserNickName = mAnswerList.get(0).getCreateUserNickName();
            mCreateUserType = mAnswerList.get(0).getCreateUserType();
            mCreateUserId = mAnswerList.get(0).getCreateUserId();
        }
        mCommentLl.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCommentLlY = mCommentLl.getY();
                mCommentLl.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        initRecyclerView();
        initListener();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(mItemAnimator);
        mRecyclerView.addItemDecoration(mItemDecoration);
        mRecyclerView.setNestedScrollingEnabled(false);
        mCommentDetailAdapter.setNewData(mAnswerList);
        AppCompatTextView textView = new AppCompatTextView(this);
        textView.setText(getString(R.string.has_load_all_comment));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.0f);
        textView.setTextColor(getResources().getColor(R.color.edit_text_hint_color));
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, SizeUtils.dp2px(16), 0, SizeUtils.dp2px(16));
        textView.setLayoutParams(lp);
        // textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD)); // 设置粗体
        // textView.setPadding(SizeUtils.dp2px(8), SizeUtils.dp2px(8), SizeUtils.dp2px(8), SizeUtils.dp2px(8));
        mCommentDetailAdapter.addFooterView(textView);
        mRecyclerView.setAdapter(mCommentDetailAdapter);
    }

    private void initListener() {
        mCommentDetailAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!SPUtils.getInstance().getBoolean(Constants.HAS_LOGIN)) {
                    gotoLoginActivity();
                    return;
                }
                AnswerItemEntity answerItem = (AnswerItemEntity) adapter.getItem(position);
                String reName = "5".equals(answerItem.getCreateUserType()) ? "管理员" :answerItem.getCreateUserNickName();
                showCommentPopupWindow(answerItem.getReplyId(), "回复" + reName + "：",
                        mCommentDraft.get(answerItem.getReplyId()), answerItem.getTopReplyId(),
                        answerItem.getReplyId(), answerItem.getCreateUserNickName(), answerItem.getCreateUserId());
            }
        });
    }

    private void showCommentPopupWindow(String commentId, String hintText, String commentDraft, String topReplyId,
                                        String upReplyId, String reUserName, String reUserId) {
        CommentPopWindow popWindow = new CommentPopWindow(this,
                mRootLl, R.layout.pop_comment, commentId, topReplyId, upReplyId, reUserName, reUserId);
        popWindow.showPopupWindow();
        popWindow.setCommentHint(hintText);
        popWindow.setCommentDraft(commentDraft);
        popWindow.onSendCommentClickListener(this);
    }

    @OnClick({R.id.head_left_iv, R.id.comment_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_left_iv:
                finish();
                break;
            case R.id.comment_tv:
                if (!SPUtils.getInstance().getBoolean(Constants.HAS_LOGIN)) {
                    gotoLoginActivity();
                    return;
                }
                String reName = "5".equals(mCreateUserType) ? "管理员" : mCreateUserNickName;
                showCommentPopupWindow(mReplyId, TextUtils.isEmpty(mCreateUserNickName) ? getString(R.string.tip_comment) : "回复"+reName+"：",
                        mCommentDraft.get(mReplyId), mReplyId, mReplyId, mCreateUserNickName, mCreateUserId);
                break;
        }
    }

    private void gotoLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra(Constants.GOTO_MAIN, false);
        startActivity(intent);
    }

    @Override
    public void showLoading(String message) {

    }

    @Override
    public void hideLoading() {

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
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onAddReplySuccess(String replyId) {
        ToastUtils.showShort(R.string.comment_send_success);
        if (mCommentDraft.containsKey(replyId)) {
            mCommentDraft.remove(replyId);
        }
    }

    @Override
    public void onQueryReplyPageSuccess(CommentListEntity entity) {
        if (null != entity && entity.getList() != null && !entity.getList().isEmpty()) {
            CommentItemEntity commentItem = entity.getList().get(0);
            List<AnswerItemEntity> answerList = commentItem.getAnswerList();
            AnswerItemEntity first = new AnswerItemEntity();
            first.setCreateUserNickName(commentItem.getCreateUserNickName());
            mCreateUserNickName = commentItem.getCreateUserNickName();
            mCreateUserType = commentItem.getCreateUserType();
            first.setCreateUserId(commentItem.getCreateUserId());
            first.setCreateUserAvatar(commentItem.getCreateUserAvatar());
            first.setCreateTime(commentItem.getCreateTime());
            first.setReply(commentItem.getReply());
            first.setReplyId(commentItem.getId());
            first.setTopReplyId(commentItem.getId());
            first.setContentId(commentItem.getContentId());
            first.setColumnId(commentItem.getColumnId());
            first.setOperationCenterId(commentItem.getOperationCenterId());
            if (null == answerList) {
                answerList = new ArrayList<>();
            }
            answerList.add(0, first);
            mCommentDetailAdapter.setNewData(answerList);
        }
    }

    @Override
    public void onQueryReplyPageError() {

    }

    @Override
    public void sendComment(String commentId, String topReplyId, String upReplyId,
                            String reUserName, String reUserId, String reply) {
        mPresenter.addReply(mColumnId, mContentId, commentId, topReplyId, upReplyId, reUserName, reUserId, reply);
    }

    @Override
    public void saveDraft(String commentId, String draft) {
        mCommentDraft.put(commentId, draft);
        float y = mCommentLl.getY();
        if (y < mCommentLlY) {
            KeyboardUtils.toggleSoftInput();
        }
    }

}
