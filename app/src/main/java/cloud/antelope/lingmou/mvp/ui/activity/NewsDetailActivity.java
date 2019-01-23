package cloud.antelope.lingmou.mvp.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.modle.PermissionDialog;
import com.lingdanet.safeguard.common.modle.SweetDialog;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.DeviceUtil;
import com.lingdanet.safeguard.common.utils.EncryptUtils;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.MediaItem;
import com.mob.moblink.ActionListener;
import com.mob.moblink.MobLink;
import com.mob.moblink.Scene;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.JsUtils;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.app.utils.SaveImageTask;
import cloud.antelope.lingmou.app.utils.ShareUtils;
import cloud.antelope.lingmou.app.utils.loader.DecodeQRTask;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.di.component.DaggerNewsDetailComponent;
import cloud.antelope.lingmou.di.module.AllCommentModule;
import cloud.antelope.lingmou.di.module.NewsDetailModule;
import cloud.antelope.lingmou.mvp.contract.AllCommentContract;
import cloud.antelope.lingmou.mvp.contract.NewsDetailContract;
import cloud.antelope.lingmou.mvp.model.entity.AnswerItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.AttachmentBean;
import cloud.antelope.lingmou.mvp.model.entity.CommentItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.CommentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.LikeCountEntity;
import cloud.antelope.lingmou.mvp.model.entity.NewsItemEntity;
import cloud.antelope.lingmou.mvp.presenter.AllCommentPresenter;
import cloud.antelope.lingmou.mvp.presenter.NewsDetailPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.CommentAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.X5WebView;
import cloud.antelope.lingmou.mvp.ui.widget.dialog.ContextMenuDialog;
import cloud.antelope.lingmou.mvp.ui.widget.loadmore.ScrollViewFinal;
import cloud.antelope.lingmou.mvp.ui.widget.loadmore.SwipeRefreshLayoutFinal;
import cloud.antelope.lingmou.mvp.ui.widget.loadmore.listener.OnLoadMoreListener;
import cloud.antelope.lingmou.mvp.ui.widget.popup.CommentPopWindow;
import cloud.antelope.lingmou.mvp.ui.widget.popup.SharePopWindow;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class NewsDetailActivity extends BaseActivity<NewsDetailPresenter>
        implements NewsDetailContract.View,
        AllCommentContract.View,
        SharePopWindow.onPlatformClickListener,
        SwipeRefreshLayout.OnRefreshListener, PermissionUtils.HasPermission,
        EasyPermissions.PermissionCallbacks,
        OnLoadMoreListener, CommentPopWindow.onSendCommentClickListener, ShareUtils.OnCompleteListener {


    private static final int GET_ALL_IMAGES = 0x01;
    private static final int ADD_SUBMIT_CLUE_LISTENER = 0x02;
    private static final int ADD_IMAGE_CLICK_LISTENER = 0x03;
    private static final int ADD_VIDEO_CLICK_LISTENER = 0x04;
    private static final int MSG_WHAT_CONNECT_NETWORK = 0x07;
    private static final int SHOW_COMMENT_LIST = 0x08;

    private static final int QQ_SHARE = 0x11;
    private static final int QZONE_SHARE = 0x12;
    private static final int WECHAR_SHARE = 0x13;
    private static final int WECHAT_MOMENTS_SHARE = 0x14;
    private static final int WEIBO_SHARE = 0x15;
    private static final int CHECK_CASE_SHARE = 0x16;

    private static final int CODE_PREVIEW_IMAGE = 0x09;
    public static final int PAGE_SIZE = 10;

    public static final String CASE_ITEM = "case_item";
    public static final String COLUMN_ID = "column_id";
    public static final String CONTENT_ID = "content_id";
    public static final String COMMENT_LIST = "comment_list";
    public static final String LOAD_FIRST_PAGE = "load_first_page";
    public static final String CREATE_USER_NICK_NAME = "create_user_nick_name";
    public static final String CREATE_USER_ID = "create_user_id";
    public static final String REPLY_NEWS = "reply_news";


    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayoutFinal mRefreshView;
    @BindView(R.id.empty_layout)
    LinearLayout mEmptyLayout;
    @BindView(R.id.detail_scrollView)
    ScrollViewFinal mDetailScrollView;
    @BindView(R.id.main_content)
    LinearLayout mMainContent;
    @BindView(R.id.comment_tool_bar_ll)
    LinearLayout mCommentToolBarLl;
    @BindView(R.id.comment_tv)
    TextView mCommentTv;
    @BindView(R.id.see_comment_iv)
    ImageView mSeeCommentIv;
    @BindView(R.id.thumb_up_iv)
    ImageView mThumbUpIv;
    @BindView(R.id.thumb_up_number_tv)
    TextView mThumbUpNumberTv;
    @BindView(R.id.thumb_up_rl)
    RelativeLayout mThumbUpRl;
    @BindView(R.id.load_pbar)
    ProgressBar mProgressBar;
    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.head_right_iv)
    ImageView mHeadRightIv;
    @BindView(R.id.root_detail_ll)
    LinearLayout mRootView;

    private float mToobarHeight;
    // @BindView(R.id.detail_webview)
    X5WebView mX5WebView;

    // @BindView(R.id.comment_rv)
    @Inject
    RecyclerView mRecyclerView;

    @Inject
    AllCommentPresenter mCommentPresenter;
    @Inject
    @Named("mobIdCache")
    Map<String, String> mobIdCache; //mobID缓存
    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    RecyclerView.ItemDecoration mItemDecoration;
    @Inject
    List<String> mSaveImages;
    @Inject
    CommentAdapter mCommentAdapter;
    @Inject
    @Named("draftMap")
    Map<String, String> mCommentDraft;

    private int page = -1;

    private int mX5WebViewHeight = -1;
    private int mTopOffset = 0;
    private boolean mMove = false;

    private List<MediaItem> mMediaList;


    private ContextMenuDialog mContextMenuDialog;

    private String mQRResult;

    private String[] mImageUrls;

    private NewsItemEntity mNewsItem;

    private String mTitle;
    private String mShareUrl;

    private boolean mIsNetworkError = false;
    private int mConnectTime = 0;

    // 加载进度条的数值
    private int mProgressCount = 0;

    private ArrayAdapter<String> mAdapter;

    // 分享工具类
    ShareUtils mShareUtils;
    private String mShareImage;

    private NetworkChangeReceiver mReceiver;
    private boolean mChangeToMobileNetwork;

    private boolean mPlayInMobileNetwork = false; // 在移动网络下是否播放，默认不播放
    private long mLikeCount;

    private boolean mHasLikeIt;
    private List<CommentItemEntity> mFirstPageCommentList;

    private CommentPopWindow mCommentPopWindow;

    private int mClickPosition = -1;

    private boolean mLoadFirstCommentPage = false;

    private String mobID;
    private String mNewsId;


    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_ALL_IMAGES:
                    mX5WebView.loadUrl(JsUtils.getImages());
                    break;
                case ADD_SUBMIT_CLUE_LISTENER:
                    mX5WebView.loadUrl(JsUtils.addSubmitClueListener(mNewsId));
                    break;
                case ADD_IMAGE_CLICK_LISTENER:
                    mX5WebView.loadUrl(JsUtils.addImageClickListener());
                    break;
                case ADD_VIDEO_CLICK_LISTENER:
                    mX5WebView.loadUrl(JsUtils.addVideoClickListener());
                    break;
                case MSG_WHAT_CONNECT_NETWORK:
                    // 如果联网时间超过20s，则停止加载，并显示网络不给力的布局文件
                    if (mConnectTime >= 200) {
                        if (null != mX5WebView && mX5WebView.getProgress() < 100) {
                            removeWebView();
                            showRetry();
                        }
                        mConnectTime = 0;
                        mProgressCount = 0;
                        return;
                    }
                    if (mProgressCount < 90) {
                        mProgressCount++;
                        mProgressBar.setProgress(mProgressCount);
                    }
                    mConnectTime++;
                    mHandle.sendEmptyMessageDelayed(MSG_WHAT_CONNECT_NETWORK, 100);  // 每100ms发送一次
                    break;
                case CHECK_CASE_SHARE:
                    checkCaseShare();
                    break;
                case SHOW_COMMENT_LIST:
                    showCommentList();
                    break;
                default:
                    break;
            }
        }

    };


    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerNewsDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .newsDetailModule(new NewsDetailModule(this))
                .allCommentModule(new AllCommentModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_news_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mTitleTv.setText(R.string.detail_tag);
        mHeadRightIv.setVisibility(View.VISIBLE);
        mHeadRightIv.setImageResource(R.drawable.ic_case_detail_share);

        initView();
        initData();
        initListener();
        showWebView();
    }

    private void initView() {
        mRefreshView.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mRefreshView.setOnRefreshListener(this);
        mRefreshView.setEnabled(false);

        mThumbUpNumberTv.setEnabled(false);
        mThumbUpIv.setEnabled(false);

        mEmptyLayout.setVisibility(View.GONE);
        mCommentToolBarLl.setVisibility(View.GONE);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        mRecyclerView.setItemAnimator(mItemAnimator);
        mRecyclerView.addItemDecoration(mItemDecoration);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mCommentAdapter);

    }

    private void initListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mMove) {
                    mMove = false;
                    mRecyclerView.scrollBy(0, mTopOffset);
                }
            }
        });
        mDetailScrollView.setOnLoadMoreListener(this);
        getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {

                if (((ViewGroup) getWindow().getDecorView()).getChildCount() > 2) {
                    ArrayList<View> advView = new ArrayList<>();
                    getWindow().getDecorView().findViewsWithText(advView, "QQ浏览器",
                            View.FIND_VIEWS_WITH_TEXT);
                    if (advView != null && advView.size() > 0) {
                        advView.get(0).setVisibility(View.GONE);
                    }
                }
            }
        });
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
                List<AnswerItemEntity> newAnswerList = new ArrayList<>(); // 这里需要创建一个新的集合对象，防止原集合被修改
                if (null != answerList) {
                    newAnswerList.addAll(answerList);
                }
                newAnswerList.add(0, first);
                Intent intent = new Intent(NewsDetailActivity.this, CommentDetailActivity.class);
                intent.putExtra("answerList", (Serializable) newAnswerList);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.i("onStart");
        mHandle.sendEmptyMessageDelayed(CHECK_CASE_SHARE, 150);
    }

    private void checkCaseShare() {
        if (null != mShareUtils &&
                (mShareUtils.isShareToWeChatSuccess() || mShareUtils.isShareToQQSuccess())) {
            share();
        }
    }

    private int getDistanceToTop() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        // 获取第一个可见item
        View firstVisibleChildView = layoutManager.findViewByPosition(0);

        return firstVisibleChildView == null ? 0 : -firstVisibleChildView.getTop();
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        try {
            super.onConfigurationChanged(newConfig);
            if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
                // 在横屏播放视频时记录当前页面滑动的高度。
                mX5WebViewHeight = mX5WebView.getHeight();
                mMove = true;
                mTopOffset = getDistanceToTop();
                mTintManager.setStatusBarTintEnabled(false);
            } else if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
                // 在恢复竖屏状态时还原当前页面滑动的高度。
                if (-1 != mX5WebViewHeight) {
                    LinearLayout.LayoutParams params =
                            (LinearLayout.LayoutParams) mX5WebView.getLayoutParams();
                    params.height = mX5WebViewHeight;
                    mX5WebView.setLayoutParams(params);
                }
                mTintManager.setStatusBarTintEnabled(true);
                // // 同时重新设置statusbar的颜色
                // mTintManager.setStatusBarTintResource(R.drawable.bg_home_title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        mNewsItem = (NewsItemEntity) getIntent().getSerializableExtra(CASE_ITEM);
        String urlBase = SPUtils.getInstance().getString(Constants.URL_BASE);
        mShareImage = urlBase+"/" + UrlConstant.SHARE_LOGO;
        if (null == mNewsItem) {
            mNewsItem = new NewsItemEntity();
        } else {
            List<AttachmentBean> imgInfoJson = mNewsItem.getImgInfoJson();
            if (null != imgInfoJson) {
                Iterator<AttachmentBean> iterator = imgInfoJson.iterator();
                while (iterator.hasNext()) {
                    AttachmentBean next = iterator.next();
                    AttachmentBean.MetadataBean metadata = next.getMetadata();
                    if (!metadata.getMimeType().startsWith("image")) {
                        iterator.remove();
                    }
                }
                if (imgInfoJson.size() > 0) {
                    mShareImage = imgInfoJson.get(0).getUrl();
                }
            }
        }
        mTitle = mNewsItem.getTitle();
        mNewsId = mNewsItem.getCaseId();
        mShareUrl = mNewsItem.getHtmlUrl();
        // mShareUrl = "http://sanwuwy.imwork.net/2_share.htm";
        // mShareUrl = "https://cyqzmain.netposa.com/s3-img/case/20170628RT0010/20170628RT0010_share.htm";
        // mShareUrl = "http://192.168.100.16/case/test/20170803/9fde6fe1200840cfa2e729bedf2ff051_bdd91f90c09a4149a051d375a86b2634_share.htm";
    }

    public void showWebView() {
        mX5WebView = new X5WebView(this);

        final WebSettings webSettings = mX5WebView.getSettings();
        mX5WebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 根据协议的参数，判断是否是所需要的url
                Uri uri = Uri.parse(url);
                if (uri.getScheme().equals("js")) {
                    if (uri.getAuthority().equals("webview")) {
                        Set<String> parameterNames = uri.getQueryParameterNames();
                        if (parameterNames.contains("img")) {
                            openImage(uri.getQuery());
                        } else if (parameterNames.contains("allImg")) {
                            String message = uri.getQuery();
                            parseAllImg(message);
                        } else if (parameterNames.contains("report")) {
                            reportClue(uri.getQueryParameter("report"));
                        } else if (parameterNames.contains("hasVideo")) {
                            registerNetworkChangeReceiver();
                        } else if (parameterNames.contains("changeTo3G")) {
                            String id = uri.getQueryParameter("id");
                            showTrafficDialog(id);
                            LogUtils.w("cxm", "3G videoId = " + id);
                        } else if (parameterNames.contains("play")) {
                            String id = uri.getQueryParameter("id");
                            LogUtils.w("cxm", "play videoId = " + id);
                            onPlayVideoClick(id);
                        }
                    }
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        LogUtils.e("shouldOverrideUrlLoading with uri = " + uri);
                        e.printStackTrace();
                    }
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtils.d("onPageStarted = " + System.currentTimeMillis());
                super.onPageStarted(view, url, favicon);
                pageStarted();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtils.d("onPageFinished = " + System.currentTimeMillis());
                super.onPageFinished(view, url);
                webSettings.setBlockNetworkImage(false);
                pageFinished();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String
                    description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                receivedError();
            }
        });
        mX5WebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressChanged(newProgress);
            }

            // @Override
            // public boolean onJsAlert(com.tencent.smtt.sdk.WebView webView, String url, String message, com.tencent.smtt.export.external.interfaces.JsResult jsResult) {
            //     parseAllImg(message);
            //     return false;
            // }
        });
        mX5WebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                X5WebView.HitTestResult result = ((X5WebView) v.getParent()).getHitTestResult();
                if (result != null) {
                    int type = result.getType();
                    if (type == X5WebView.HitTestResult.IMAGE_TYPE
                            || type == X5WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                        String imgUrl = result.getExtra();
                        if (!TextUtils.isEmpty(imgUrl)) {
                            onImageLongClick(imgUrl);
                        } else {
                            ToastUtils.showShort(R.string.image_download_url_error);
                        }
                    }
                }
                return false;
            }
        });

        // mProgressBar.bringToFront(); // 因为mWebView是后加入进来的，会遮挡mProgressBar，所以需要将mProgressBar前置

        mX5WebView.loadUrl(mShareUrl);
    }

    private void getLikeCount() {
        mPresenter.getContentLike(mNewsItem.getColumnId(), mNewsId);
    }

    private void setLikeState(boolean likeIt) {
        if (likeIt) {
            mThumbUpNumberTv.setEnabled(true);
            mThumbUpIv.setEnabled(true);
        } else {
            mThumbUpNumberTv.setEnabled(false);
            mThumbUpIv.setEnabled(false);
        }
        mThumbUpNumberTv.setText(getCountFormat(mLikeCount));
    }

    private String getCountFormat(long count) {
        if (count <= 0) {
            return String.valueOf(0);
        } else if (count < 1000) {
            return String.valueOf(count);
        } else if (count < 99000) {
            return String.format("%.1f", count / 1000.0f) + "k";
        } else {
            return "99k+";
        }
    }

    /**
     * 打开二维码解析后的QR链接.
     *
     * @param result 二维码解析后的QR链接.
     */
    private void openQRUrl(String result) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
        startActivity(intent);
    }

    @OnClick({R.id.see_comment_iv, R.id.thumb_up_rl, R.id.comment_tv, R.id.head_left_iv, R.id.head_right_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_iv:
                finish();
                break;
            case R.id.head_right_iv:
                showSharePopupWindow();
                break;
            case R.id.comment_tv:
                if (!SPUtils.getInstance().getBoolean(Constants.HAS_LOGIN)) {
                    gotoLoginActivity();
                    return;
                }
                showCommentPopupWindow(REPLY_NEWS, getString(R.string.tip_comment),
                        mCommentDraft.get(REPLY_NEWS), "0", null, mNewsItem.getCreateUserNickName(), mNewsItem.getCreateUserId());
                break;
            case R.id.see_comment_iv:
                gotoAllComment();
                break;
            case R.id.thumb_up_rl:
                if (!SPUtils.getInstance().getBoolean(Constants.HAS_LOGIN)) {
                    gotoLoginActivity();
                    return;
                }
                if (mHasLikeIt) {
                    mLikeCount -= 1;
                    mHasLikeIt = false;
                    setLikeState(mHasLikeIt);
                    likeIt(Constants.UNLIKE_IT);
                } else {
                    mLikeCount += 1;
                    mHasLikeIt = true;
                    setLikeState(mHasLikeIt);
                    likeIt(Constants.LIKE_IT);
                }
                break;
        }
    }

    private void showSharePopupWindow() {
        if (null != mX5WebView) {
            mX5WebView.loadUrl(JsUtils.pauseVideo());  // 如果视频正在播放，先暂停视频
        }
        mShareUtils = new ShareUtils();
        mShareUtils.setOnCompleteListener(this);
        SharePopWindow popWindow = new SharePopWindow(getActivity(),
                mDetailScrollView, R.layout.pop_share);
        popWindow.showPopupWindow();
        popWindow.setOnPlatformClickListener(this);
    }

    private void showCommentPopupWindow(String commentId, String hintText, String commentDraft, String topReplyId,
                                        String upReplyId, String reUserName, String reUserId) {
        if (null != mX5WebView) {
            mX5WebView.loadUrl(JsUtils.pauseVideo());  // 如果视频正在播放，先暂停视频
        }
        mCommentPopWindow = new CommentPopWindow(getActivity(),
                mRootView, R.layout.pop_comment, commentId, topReplyId, upReplyId, reUserName, reUserId);
        mCommentPopWindow.showPopupWindow();
        mCommentPopWindow.setCommentHint(hintText);
        mCommentPopWindow.setCommentDraft(commentDraft);
        mCommentPopWindow.onSendCommentClickListener(this);
    }

    private void gotoAllComment() {
        Intent intent = new Intent(this, AllCommentActivity.class);
        intent.putExtra(COLUMN_ID, mNewsItem.getColumnId());
        intent.putExtra(CONTENT_ID, mNewsId);
        intent.putExtra(COMMENT_LIST, (Serializable) mFirstPageCommentList);
        intent.putExtra(LOAD_FIRST_PAGE, mLoadFirstCommentPage);
        intent.putExtra(CREATE_USER_NICK_NAME, mNewsItem.getCreateUserNickName());
        intent.putExtra(CREATE_USER_ID, mNewsItem.getCreateUserId());
        startActivity(intent);
    }

    private void likeIt(String type) {
        mPresenter.likeIt(mNewsItem.getColumnId(), mNewsId, type);
    }

    private void gotoLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra(Constants.GOTO_MAIN, false);
        startActivity(intent);
    }

    private void showRetry() {
        mEmptyLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mCommentToolBarLl.setVisibility(View.GONE);
        mRefreshView.setRefreshing(false);
        mRefreshView.setEnabled(true);
        mIsNetworkError = true;
    }

    public void showCommentLabelHeader() {
        View commentLabelHeader = getLayoutInflater()
                .inflate(R.layout.include_comment_label_header, mMainContent, false);
        if (null != mMainContent) {
            mMainContent.addView(commentLabelHeader, 3);
        }
    }


    public void showNoCommentFooter() {
        View noCommentFooter = getLayoutInflater()
                .inflate(R.layout.include_no_comment_footer,
                        (ViewGroup) mRecyclerView.getParent(), false);
        mCommentAdapter.addFooterView(noCommentFooter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mX5WebView != null && mX5WebView.canGoBack()) {
            mX5WebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void qqClick() {
        // mShareUtils.qqShare(getString(R.string.app_name), mTitle, mShareImage, mShareUrl);
        getMobIDToShare(QQ_SHARE);
        // share(QQ_SHARE);
    }

    @Override
    public void qzoneClick() {
        // mShareUtils.qZoneShare(getString(R.string.app_name), mTitle, mShareImage, mShareUrl);
        getMobIDToShare(QZONE_SHARE);
        // share(QZONE_SHARE);
    }

    @Override
    public void wechatClick() {
        // mShareUtils.wechatShare(getString(R.string.app_name), mTitle, mShareImage, mShareUrl);
        getMobIDToShare(WECHAR_SHARE);
        // share(WECHAR_SHARE);
    }

    @Override
    public void wechatMomentsClick() {
        // mShareUtils.wechatMomentsShare(mTitle, mTitle, mShareImage, mShareUrl);
        getMobIDToShare(WECHAT_MOMENTS_SHARE);
        // share(WECHAT_MOMENTS_SHARE);

    }

    // @Override
    // public void weiboClick() {
    //     getMobIDToShare(WEIBO_SHARE);
    // }

    @Override
    protected void onDestroy() {
        if (null != mX5WebView) {
            removeWebView();
        }
        if (null != mHandle) {
            mHandle.removeMessages(MSG_WHAT_CONNECT_NETWORK);
            mHandle.removeMessages(GET_ALL_IMAGES);
            mHandle.removeMessages(ADD_SUBMIT_CLUE_LISTENER);
            mHandle.removeMessages(ADD_IMAGE_CLICK_LISTENER);
            mHandle.removeMessages(ADD_VIDEO_CLICK_LISTENER);
            mHandle = null;
        }
        if (null != mReceiver) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onDestroy();
    }

    private void removeWebView() {
        if (null != mX5WebView) {
            mX5WebView.stopLoading();
            if (null != mX5WebView.getParent()) {
                ((ViewGroup) mX5WebView.getParent()).removeView(mX5WebView);
            }
            mX5WebView.destroy();
            mX5WebView = null;
        }
    }

    @Override
    public void onRefresh() {
        mIsNetworkError = false;
        mEmptyLayout.setVisibility(View.GONE);
        showWebView();
    }

    @Override
    public void sendComment(String commentId, String topReplyId,
                            String upReplyId, String reUserName, String reUserId, String reply) {
        mCommentPresenter.addReply(mNewsItem.getColumnId(), mNewsId, commentId, topReplyId,
                reUserName, reUserId, reply);
    }

    @Override
    public void saveDraft(String commentId, String draft) {
        mCommentDraft.put(commentId, draft);
        float y = mCommentToolBarLl.getY();
        if (y < mToobarHeight) {
            KeyboardUtils.toggleSoftInput();
        }
    }

    // @Override
    // public void onLoadMoreRequested() {
    //     getCommentList();
    // }

    @Override
    public void loadMore() {
        if (mLoadFirstCommentPage) {
            getCommentList();
        }
    }

    @Override
    public void onAddReplySuccess(String replyId) {
        ToastUtils.showShort(R.string.comment_send_success);
        if (mCommentDraft.containsKey(replyId)) {
            mCommentDraft.remove(replyId);
        }
    }

    private void getCommentList() {
        page++;
        mCommentPresenter.queryReplyPage(mNewsItem.getColumnId(), mNewsId, page, PAGE_SIZE);
    }

    @Override
    public void onQueryReplyPageSuccess(CommentListEntity commentListEntity) {
        List<CommentItemEntity> list = commentListEntity.getList();
        if (null != list) {
            if (page == 0) {
                if (list.size() > 0) {
                    showCommentLabelHeader();
                    mFirstPageCommentList = list;
                    mCommentAdapter.setNewData(list);
                } else {
                    mDetailScrollView.setNoLoadMoreHideView(true);
                    mDetailScrollView.setHasLoadMore(false);
                    showNoCommentFooter();
                }
                mLoadFirstCommentPage = true;
            } else {
                mCommentAdapter.addData(list);
            }
            if (list.size() < PAGE_SIZE) {
                mDetailScrollView.setHasLoadMore(false);
            } else {
                mDetailScrollView.setHasLoadMore(true);
                mDetailScrollView.onLoadMoreComplete();
            }
        }
    }

    @Override
    public void onQueryReplyPageError() {
        page--;
        mDetailScrollView.showFailUI();
    }

    @Override
    public void onGetContentLikeSuccess(LikeCountEntity likeCountEntity) {
        mLikeCount = likeCountEntity.getCount();
        if (TextUtils.equals(likeCountEntity.getUserIds(),
                SPUtils.getInstance().getString(CommonConstant.UID, "-1"))) {
            mHasLikeIt = true;
        } else {
            mHasLikeIt = false;
        }
        setLikeState(mHasLikeIt);
    }

    private void getMobIDToShare(final int type) {
        if (mobIdCache.containsKey(mNewsId)) {
            mobID = String.valueOf(mobIdCache.get(mNewsId));
            if (!TextUtils.isEmpty(mobID)) {
                share(type);
                return;
            }
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("caseId", mNewsId);
        params.put("columnId", mNewsItem.getColumnId());
        params.put("title", mNewsItem.getTitle());
        params.put("htmlUrl", mNewsItem.getHtmlUrl());
        params.put("openOrientation", mNewsItem.getOpenOrientation());
        params.put("isAllowReply", mNewsItem.getIsAllowReply());
        Scene s = new Scene();
        s.path = "/lingmou/newsdetail";
        s.source = "android";
        s.params = params;
        MobLink.getMobID(s, new ActionListener<String>() {
            public void onResult(String mobID) {
                if (mobID != null) {
                    NewsDetailActivity.this.mobID = mobID;
                    mobIdCache.put(mNewsId, mobID);
                } else {
                    LogUtils.e("Get MobID Failed!");
                }
                share(type);
            }

            public void onError(Throwable t) {
                if (t != null) {
                    LogUtils.e("Get MobID Failed! error = " + t.getMessage());
                }
                share(type);
            }
        });
    }

    private void share(int type) {
        if (!TextUtils.isEmpty(mobID)) {
            mShareUrl += "?mobid=" + mobID;
        }
        switch (type) {
            case QQ_SHARE:
                mShareUtils.qqShare(getString(R.string.app_name), mTitle, mShareImage, mShareUrl);
                break;
            case QZONE_SHARE:
                mShareUtils.qZoneShare(getString(R.string.app_name), mTitle, mShareImage, mShareUrl);
                break;
            case WECHAR_SHARE:
                mShareUtils.wechatShare(getString(R.string.app_name), mTitle, mShareImage, mShareUrl);
                break;
            case WECHAT_MOMENTS_SHARE:
                mShareUtils.wechatMomentsShare(mTitle, mTitle, mShareImage, mShareUrl);
                break;
            case WEIBO_SHARE:
                // mShareUtils.weiboShare(getString(R.string.app_name), mTitle, mShareImage, mShareUrl);
                break;
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @OnClick(R.id.head_left_iv)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void share() {
        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(CommonConstant.UID))) {
            mPresenter.contentRecord(mNewsItem.getColumnId(), mNewsId, "2");
        }
    }

    private class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (NetworkUtils.isWifiConnected()) {
                    if (mChangeToMobileNetwork) {
                        mChangeToMobileNetwork = false;
                    }
                    // ToastUtils.showShort("切换到WIFI网络！");
                } else if (NetworkUtils.isMobileConnected()) {
                    if (!mChangeToMobileNetwork) {  // 如果当前没有切换到移动网络状态，则进行切换
                        mChangeToMobileNetwork = true;
                        if (mX5WebView != null) {
                            mX5WebView.loadUrl(JsUtils.checkVideoAndPause());
                        }
                    }
                    // ToastUtils.showShort("切换到移动网络！");
                    // } else {
                    //     ToastUtils.showShort("网络连接失败！");
                }

            }
        }

    }

    private class MySaveImageListener implements SaveImageTask.OnSaveImageSuccessListener {

        @Override
        public void onSaveSuccess(String imgUrl, String cachePath) {
            decodeQRImage(cachePath);
            if (mSaveImages.contains(imgUrl)) {
                String savePath = Configuration.getDownloadDirectoryPath()
                        + EncryptUtils.encryptMD5ToString(imgUrl) + Constants.DEFAULT_IMAGE_SUFFIX;
                if (FileUtils.copyFile(cachePath, savePath)) {
                    ToastUtils.showShort(R.string.image_save_to, savePath);
                    DeviceUtil.galleryAddMedia(savePath);
                } else {
                    ToastUtils.showShort(R.string.image_save_fail);
                }
                mSaveImages.remove(imgUrl);
            }
        }

        @Override
        public void onSaveFail() {

        }

    }

    private class MyDecodeQRListener implements DecodeQRTask.OnDecodeQRListener {

        @Override
        public void onDecodeQRSuccess(String result) {
            mAdapter.add(getString(R.string.recognize_qr));
            mAdapter.notifyDataSetChanged();
            mQRResult = result;
        }

    }

    /**
     * 解析图片二维码.
     *
     * @param imgUrl 图片的路径
     */
    private void decodeQRImage(String imgUrl) {
        DecodeQRTask decodeQRTask = new DecodeQRTask();
        decodeQRTask.setOnDecodeQRListener(new MyDecodeQRListener());
        decodeQRTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, imgUrl);
    }

    private void initAdapter() {
        mX5WebView.loadUrl(JsUtils.pauseVideo());  // 如果在播放视频，则先暂停
        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_context_menu_dialog);
        mAdapter.add(getString(R.string.save_image));
    }

    /**
     * 显示上下文对话框.
     *
     * @param imgUrl
     */
    private void showContextMenuDialog(final String imgUrl) {
        initAdapter();
        mContextMenuDialog = new ContextMenuDialog(getActivity()) {

            @Override
            public void initView() {
                ListView listView = (ListView) findViewById(R.id.context_menu_lv);
                listView.setAdapter(mAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mContextMenuDialog.closeDialog();
                        switch (position) {
                            case 0:
                                String savePath = Configuration.getDownloadDirectoryPath()
                                        + EncryptUtils.encryptMD5ToString(imgUrl)
                                        + Constants.DEFAULT_IMAGE_SUFFIX;
                                if (FileUtils.isFileExists(savePath)) {
                                    ToastUtils.showShort(R.string.image_save_to, savePath);
                                    return;
                                } else {
                                    String cachePath = Configuration.getAppCacheDirectoryPath()
                                            + EncryptUtils.encryptMD5ToString(imgUrl)
                                            + Constants.DEFAULT_IMAGE_SUFFIX;
                                    if (FileUtils.isFileExists(cachePath)) {
                                        if (FileUtils.copyFile(cachePath, savePath)) {
                                            DeviceUtil.galleryAddMedia(savePath);
                                            ToastUtils.showShort(R.string.image_save_to, savePath);
                                        } else {
                                            ToastUtils.showShort(R.string.image_save_fail);
                                        }
                                    } else {
                                        mSaveImages.add(imgUrl);
                                    }
                                }
                                break;
                            case 1:
                                openQRUrl(mQRResult);
                        }
                    }
                });
            }
        };
        mContextMenuDialog.show();
    }

    private void progressChanged(int newProgress) {
        if (!mIsNetworkError) {
            if (mProgressCount < newProgress) {
                mProgressCount = newProgress;
            }
            mProgressBar.setProgress(mProgressCount);
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
                if (mHandle != null) {
                    mHandle.removeMessages(MSG_WHAT_CONNECT_NETWORK);
                }
                mConnectTime = 0;
                mProgressCount = 0;
            }
        }
    }

    private void receivedError() {
        if (null != mHandle) {
            mHandle.removeMessages(MSG_WHAT_CONNECT_NETWORK);
        }
        removeWebView();
        showRetry();
        mConnectTime = 0;
        mProgressCount = 0;
    }

    private void pageFinished() {
        mRefreshView.setRefreshing(false);
        mRefreshView.setEnabled(false);
        if (!mIsNetworkError) {
            mPresenter.contentRecord(mNewsItem.getColumnId(), mNewsId, "1");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // params.addRule(RelativeLayout.ABOVE, R.id.comment_rv);
            // mX5WebView.setId(R.id.detail_webview);
            mX5WebView.setLayoutParams(params);
            if (null != mMainContent) {
                mMainContent.addView(mX5WebView);
            }

            if (Constants.IS_ALLOW_REPLY.equals(mNewsItem.getIsAllowReply())) {
                mCommentToolBarLl.setVisibility(View.VISIBLE);
                mCommentToolBarLl.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mToobarHeight = mCommentToolBarLl.getY();
                        mCommentToolBarLl.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
                mHandle.sendEmptyMessageDelayed(SHOW_COMMENT_LIST, 400);

                getLikeCount();

                mDetailScrollView.setHasLoadMore(true);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                mCommentToolBarLl.setVisibility(View.GONE);
            }
            // // 将mX5WebView作为头添加到Adapter中
            // LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            //         ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // mX5WebView.setLayoutParams(params);
            // mX5WebView.setBackgroundColor(getResources().getColor(R.color.white));
            // mCommentAdapter.addHeaderView(mX5WebView, 0);

            // 这里如果连续注入三段js代码，会导致前面的js代码有时会注入失败，所以需要有一定的延时
            mHandle.sendEmptyMessageDelayed(GET_ALL_IMAGES, 0);
            mHandle.sendEmptyMessageDelayed(ADD_IMAGE_CLICK_LISTENER, 100);
            mHandle.sendEmptyMessageDelayed(ADD_VIDEO_CLICK_LISTENER, 200);
            if (Constants.IS_ALLOW_CASE_REPORT.equals(mNewsItem.getOpenOrientation())) {
                mHandle.sendEmptyMessageDelayed(ADD_SUBMIT_CLUE_LISTENER, 300);
            }
        }
    }

    /**
     * 展示评论列表
     */
    private void showCommentList() {
        // mRecyclerView.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams rvParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // rvParams.addRule(RelativeLayout.BELOW, R.id.detail_webview);
        mRecyclerView.setLayoutParams(rvParams);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        relativeLayout.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        relativeLayout.addView(mRecyclerView);
        if (null != mMainContent) {
            mMainContent.addView(relativeLayout, 3);
        }
        getCommentList();
    }

    private void pageStarted() {
        mProgressBar.setVisibility(View.VISIBLE);
        mConnectTime = 0;
        if (null != mHandle) {
            mHandle.sendEmptyMessage(MSG_WHAT_CONNECT_NETWORK);
        }
    }

    /**
     * 在播放之前，先检测是否在移动网络状态，如果不是，说明在wifi网络下，则直接播放
     * 如果是移动网络状态，再检测在移动网络状态是否可以播放视频，如果不能播放，则弹窗提醒
     *
     * @param videoId
     */
    private void onPlayVideoClick(String videoId) {
        if (mChangeToMobileNetwork && !mPlayInMobileNetwork) {
            checkNetWork(videoId);
        }
    }

    private void showTrafficDialog(String videoId) {
        final SweetDialog sweetDialog = new SweetDialog(NewsDetailActivity.this);
        sweetDialog.setTitle("提示");
        sweetDialog.setMessage("正在使用移动网络，播放将产生流量费用");
        sweetDialog.setNegative("取消");
        sweetDialog.setPositive("继续播放");
        sweetDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetDialog.dismiss();
                mPlayInMobileNetwork = true;
                mX5WebView.loadUrl(JsUtils.playVideo(videoId));
            }
        });
        sweetDialog.setNegativeListener(null);
        sweetDialog.show();
    }

    private void registerNetworkChangeReceiver() {
        mReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
    }

    /**
     * 解析所有的图片.
     *
     * @param message
     */
    private void parseAllImg(String message) {
        String[] splits = message.split("allImg=");
        if (splits.length > 1) {
            String urls = splits[1];
            mImageUrls = urls.split("@@\\$@@");
        }
        if (null != mImageUrls && mImageUrls.length > 0) {
            mShareImage = mImageUrls[0];
            mMediaList = new ArrayList<>();
            for (int i = 0; i < mImageUrls.length; i++) {
                MediaItem imgInfo = new MediaItem();
                imgInfo.path = mImageUrls[i];
                imgInfo.mimeType = Constants.DEFAULT_PICTURE_MIME_TYPE;
                mMediaList.add(imgInfo);
            }
        }
    }

    /**
     * 打开指定url地址的图片.
     *
     * @param img 图片的url地址，
     *            如：img=https://osstest.topvdn.com/files2/2147489027/59914cce800015030500116b
     *            ?access_token=2147489027_3221225472_1534232410_2cbaa147967a01ef5ed20417a84602c1
     */
    private void openImage(String img) {
        // 删除前面的"img="
        int index = img.indexOf("=");
        img = img.substring(index + 1);
        if (null == mMediaList || mMediaList.size() <= 0) {
            ToastUtils.showShort(R.string.see_big_image_fail);
            return;
        }

        // 记录点击的图片的位置
        for (int i = 0; i < mMediaList.size(); i++) {
            MediaItem info = mMediaList.get(i);
            if (info.path.equals(img)) {
                mClickPosition = i;
                break;
            }
        }

        checkSdCardPerm();
    }

    private void reportClue(String caseId) {
        mX5WebView.loadUrl(JsUtils.pauseVideo());  // 如果视频正在播放，先暂停视频
        if (!SPUtils.getInstance().getBoolean(Constants.HAS_LOGIN)) {
            gotoLoginActivity();
            return;
        }
        Intent intent = new Intent(NewsDetailActivity.this, ReportEditActivity.class);
        intent.putExtra(Constants.CASE_ID, caseId);
        intent.putExtra(Constants.EXTRA_REPORT_TYPE, Constants.TYPE_2);
        startActivity(intent);
    }

    private void onImageLongClick(String imgUrl) {
        String cachePath = Configuration.getAppCacheDirectoryPath()
                + EncryptUtils.encryptMD5ToString(imgUrl) + Constants.DEFAULT_IMAGE_SUFFIX;
        if (!FileUtils.isFileExists(cachePath)) {
            cacheImage(imgUrl, cachePath);
        } else {
            decodeQRImage(cachePath);
        }
        showContextMenuDialog(imgUrl);
    }

    /**
     * 在播放视频之前，检测手机网络情况.
     */
    private void checkNetWork(String videoId) {
        if (NetworkUtils.isMobileConnected()) {
            mX5WebView.loadUrl(JsUtils.pauseVideo());
            showTrafficDialog(videoId);
        } else if (NetworkUtils.isWifiConnected()) {
            mX5WebView.loadUrl(JsUtils.playVideo(videoId));
        }
    }

    /**
     * 缓存图片.
     *
     * @param imgUrl    图片的Url地址
     * @param cachePath 图片的缓存路径
     */
    private void cacheImage(String imgUrl, String cachePath) {
        SaveImageTask saveImageTask = new SaveImageTask();
        saveImageTask.setSavedPath(cachePath);
        saveImageTask.setOnSaveImageSuccessListener(new MySaveImageListener());
        saveImageTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, imgUrl);
    }

    private void previewImage() {
        mX5WebView.loadUrl(JsUtils.pauseVideo());  // 如果视频正在播放，先暂停视频
        Intent intent = new Intent(NewsDetailActivity.this, ImagePreviewDownloadActivity.class);
        intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<MediaItem>) mMediaList);
        intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, mClickPosition);
        intent.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
        intent.putExtra(ImagePreviewDownloadActivity.EXTRA_SHOW_DOWNLOAD, true);
        startActivityForResult(intent, CODE_PREVIEW_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            if (perms.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                final PermissionDialog dialog = new PermissionDialog(this);
                dialog.setTitle(getString(R.string.request_permission_tag));
                dialog.setMessage(getString(R.string.need_read_sdcard_permission_tips));
                dialog.setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ToastUtils.showShort(R.string.see_big_image_fail);
                    }
                });
                dialog.show();
            }
        }
    }

    @Override
    public void doNext(int permId) {
        if (PermissionUtils.RC_READ_SDCARD_PERM == permId) {
            previewImage();
        }
    }


    @AfterPermissionGranted(PermissionUtils.RC_READ_SDCARD_PERM)
    public void checkSdCardPerm() {
        PermissionUtils.readSdCardTask(NewsDetailActivity.this);
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

}
