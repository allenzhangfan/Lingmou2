package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.modle.SweetDialog;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.EncryptUtils;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.MediaItem;
import com.lzy.imagepicker.ui.PlayVideoActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.AttachmentUtils;
import cloud.antelope.lingmou.app.utils.HttpCacheUtils;
import cloud.antelope.lingmou.app.utils.UrlUtils;
import cloud.antelope.lingmou.app.utils.loader.ClueBannerImageLoader;
import cloud.antelope.lingmou.app.utils.loader.GlideImageLoader;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerClueDetailComponent;
import cloud.antelope.lingmou.di.module.ClueDetailModule;
import cloud.antelope.lingmou.mvp.contract.ClueDetailContract;
import cloud.antelope.lingmou.mvp.model.entity.AnswerItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.AttachmentBean;
import cloud.antelope.lingmou.mvp.model.entity.ClueItemEntity;
import cloud.antelope.lingmou.mvp.presenter.ClueDetailPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.ClueTalkAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.popup.CommentPopWindow;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.jess.arms.utils.Preconditions.checkNotNull;


public class ClueDetailActivity extends BaseActivity<ClueDetailPresenter>
        implements ClueDetailContract.View,
        OnBannerListener, ViewPager.OnPageChangeListener,
        SwipeRefreshLayout.OnRefreshListener,
        CommentPopWindow.onSendCommentClickListener {

    public static final int PREVIEW_IMG = 0x17;
    public static final int PLAY_VIDEO = 0x18;

    @BindView(R.id.main_content)
    LinearLayout mRootLl;
    @BindView(R.id.scroll_view)
    NestedScrollView mScrollView;
    @BindView(R.id.clue_banner)
    Banner banner;
    @BindView(R.id.clue_content_tv)
    TextView mContentTv;
    @BindView(R.id.clue_address_tv)
    TextView mAddressTv;
    @BindView(R.id.clue_time_tv)
    TextView mClueTimeTv;
    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.talk_ll)
    LinearLayout mTalkLl;
    @BindView(R.id.comment_et)
    LinearLayout mCommentLl;
    @BindView(R.id.clue_detail_srfl)
    SwipeRefreshLayout mClueDetailSrfl;

    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    ClueTalkAdapter mClueTalkAdapter;

    private ImageView mPlayIcon;

    List<ClueBannerItem> mClueBannerItemList;


    private boolean hasVideo;

    /**
     * 当前position
     */
    protected int mCurrentPosition;
    private String mCommentDraft;
    private String mColumnId;
    private String mContentId;
    private ClueItemEntity mClueItem;
    private float mCommentLlY;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerClueDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .clueDetailModule(new ClueDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_clue_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mTitleTv.setText(R.string.clue_detail);
        mClueDetailSrfl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mClueDetailSrfl.setOnRefreshListener(this);

        initBanner();
        initRecyclerView();
        Intent intent = getIntent();
        mColumnId = SPUtils.getInstance().getString(Constants.CONFIG_CLUE_ID);
        mContentId = intent.getStringExtra(Constants.CONFIG_CONTENT_ID);
        // initClueContent(mClueItem);
        mClueDetailSrfl.postDelayed(new Runnable() {
            @Override
            public void run() {
                mClueDetailSrfl.setRefreshing(true);
                onRefresh();
            }
        }, 100);
        mCommentLl.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCommentLlY = mCommentLl.getY();
                mCommentLl.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initClueContent(ClueItemEntity clueItem) {
        try {
            mClueBannerItemList = new ArrayList<>();

            List<AttachmentBean> attachments = clueItem.getImgInfoJson();
            String videoThumb = AttachmentUtils.hasVideo(attachments);
            List<String> imgs = AttachmentUtils.getRealImgs(attachments);

            // 将视频放在第一个
            if (!TextUtils.isEmpty(videoThumb)) {
                hasVideo = true;
                mClueBannerItemList.add(new ClueBannerItem(videoThumb, true,
                        AttachmentUtils.getVideoPath(attachments)));
            }

            if (imgs.size() > 0) {
                for (String imgUrl : imgs) {
                    if (!TextUtils.isEmpty(imgUrl)) {
                        mClueBannerItemList.add(new ClueBannerItem(imgUrl, false, null));
                    }
                }
            }

            if (mClueBannerItemList.size() <= 0) {
                banner.setVisibility(GONE);
            } else {
                banner.setImages(mClueBannerItemList);
                banner.setOnBannerListener(this);
                banner.setOnPageChangeListener(this);
                banner.start();
            }

            if (!TextUtils.isEmpty(clueItem.getCreateTime())) {
                mClueTimeTv.setText(String.format(getString(R.string.clue_time_text),
                        TimeUtils.millis2String(
                                Long.valueOf(clueItem.getCreateTime()), "yyyy年MM月dd日 HH:mm:ss")));
            } else {
                mClueTimeTv.setText(String.format(getString(R.string.clue_time_text),
                        getString(R.string.unknow_text)));
            }
            if (!TextUtils.isEmpty(clueItem.getTitle())) {
                mContentTv.setText(clueItem.getTitle());
            }
            if (!TextUtils.isEmpty(clueItem.getGpsAddr())) {
                mAddressTv.setText(clueItem.getGpsAddr());
                mAddressTv.setText(String.format(getString(R.string.clue_address_text),
                        clueItem.getGpsAddr()));
            } else {
                mAddressTv.setText(String.format(getString(R.string.clue_address_text),
                        getString(R.string.unknow_text)));
            }

            List<AnswerItemEntity> answerList = clueItem.getAnswerList();
            if (null != answerList && !answerList.isEmpty()) {
                mCommentLl.setVisibility(VISIBLE);
                mTalkLl.setVisibility(VISIBLE);
                String uid = SPUtils.getInstance().getString(CommonConstant.UID);
                for (AnswerItemEntity item : answerList) {
                    if (uid.equals(item.getCreateUserId())) {
                        item.setItemType(ClueTalkAdapter.TYPE_TALK_RIGHT);
                    } else {
                        item.setItemType(ClueTalkAdapter.TYPE_TALK_LEFT);
                    }
                }
                mClueTalkAdapter.setNewData(answerList);
            } else {
                mCommentLl.setVisibility(GONE);
                mTalkLl.setVisibility(GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBanner() {
        mPlayIcon = (ImageView) banner.findViewById(R.id.play_icon_iv);
        // 设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new ClueBannerImageLoader());
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.Default);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(5000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(mItemAnimator);
        mRecyclerView.setFocusable(false);
        // mRecyclerView.addItemDecoration(mItemDecoration);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mClueTalkAdapter);
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = (position - 1) % mClueBannerItemList.size();
        if (mCurrentPosition < 0) {
            mCurrentPosition += mClueBannerItemList.size();
        }
        if (mClueBannerItemList.get(mCurrentPosition).isVideo()) {  // 如果是视频
            mPlayIcon.setVisibility(VISIBLE);
        } else {
            mPlayIcon.setVisibility(GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void OnBannerClick(int position) {
        banner.stopAutoPlay();
        ClueBannerItem clueBannerItem = mClueBannerItemList.get(position);
        if (clueBannerItem.isVideo()) {
            String videoUrl = clueBannerItem.getVideoUrl();
            if (!TextUtils.isEmpty(videoUrl)) {
                playVideo(videoUrl);
            } else {
                ToastUtils.showShort("视频播放地址错误");
            }
        } else {
            previewImage();
        }
    }

    /**
     * 预览图片.
     */
    private void previewImage() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        Intent intentPreview = new Intent(ClueDetailActivity.this, ImagePreviewDownloadActivity.class);
        ArrayList<MediaItem> mediaList = getMediaList();
        int position = mCurrentPosition;
        if (hasVideo) { // 如果Banner中存在视频，应该把视频所占的第一个位置减除
            position = mCurrentPosition - 1;
        }
        String saveDir = Configuration.getPictureDirectoryPath();
        intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, mediaList);
        intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
        intentPreview.putExtra(ImagePreviewDownloadActivity.EXTRA_SAVE_DIR, saveDir);
        intentPreview.putExtra(ImagePreviewDownloadActivity.EXTRA_SHOW_DOWNLOAD, true);
        intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
        startActivityForResult(intentPreview, ClueDetailActivity.PREVIEW_IMG);
    }

    /**
     * 创建MediaItem图片集合.
     *
     * @return
     */
    private ArrayList<MediaItem> getMediaList() {
        ArrayList<MediaItem> list = new ArrayList<>();
        for (ClueBannerItem clueBannerItem : mClueBannerItemList) {
            if (!clueBannerItem.isVideo()) {
                MediaItem mediaItem = new MediaItem();
                mediaItem.path = clueBannerItem.getImgUrl();
                list.add(mediaItem);
            }
        }
        return list;
    }

    /**
     * 将网络视频路径转换为本地视频路径.
     *
     * @param videoUrl 视频的网络路径
     * @return 转换之后的视频路径
     */
    private String convertVideoUrl(String videoUrl) {
        String localPath = Configuration.getVideoDirectoryPath()
                + EncryptUtils.encryptMD5ToString(videoUrl) + Constants.DEFAULT_VIDEO_SUFFIX;
        if (FileUtils.isFile(localPath)) {
            return localPath;
        } else {
            HttpProxyCacheServer proxy = UrlUtils.getProxy();
            String newUrl = UrlUtils.getEventObjectFullUrl(videoUrl);
            return proxy.getProxyUrl(newUrl, true);
        }
    }

    /**
     * 播放视频.
     *
     * @param videoUrl
     */
    private void playVideo(String videoUrl) {
        String url = convertVideoUrl(videoUrl);
        if (url != null && url.startsWith("http")) {
            if (NetworkUtils.isMobileConnected()) {
                showTrafficDialog(url);
                return;
            }
            if (NetworkUtils.NetworkType.NETWORK_NO.equals(NetworkUtils.getNetworkType())
                    || NetworkUtils.NetworkType.NETWORK_UNKNOWN.equals(NetworkUtils.getNetworkType())) {
                ToastUtils.showShort(R.string.network_disconnect);
                return;
            }
        }
        gotoPlayVideo(url);
    }

    /**
     * 弹出流量使用提示框.
     */
    private void showTrafficDialog(final String url) {
        final SweetDialog sweetDialog =
                new SweetDialog(ClueDetailActivity.this);
        sweetDialog.setTitle("提示");
        sweetDialog.setMessage("正在使用移动网络，播放将产生流量费用");
        sweetDialog.setNegative("取消");
        sweetDialog.setPositive("继续播放");
        sweetDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetDialog.dismiss();
                gotoPlayVideo(url);
            }
        });
        sweetDialog.setNegativeListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sweetDialog.dismiss();
                banner.startAutoPlay();
            }
        });
        sweetDialog.show();
    }

    /**
     * 进入播放视频页面.
     *
     * @param url 视频播放的url，可以是本地路径，也可以是本地缓存映射的http路径
     */
    private void gotoPlayVideo(String url) {
        Intent intent = new Intent(ClueDetailActivity.this, PlayVideoActivity.class);
        intent.putExtra(PlayVideoActivity.TYPE_VIDEO_PATH, url);
        intent.putExtra(PlayVideoActivity.TYPE_AUTO_FINISH, true);
        startActivityForResult(intent, ClueDetailActivity.PLAY_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PREVIEW_IMG || requestCode == PLAY_VIDEO) {
            banner.startAutoPlay();
        }
    }

    @OnClick({R.id.head_left_iv, R.id.comment_et})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_left_iv:
                finish();
                break;
            case R.id.comment_et:
                List<AnswerItemEntity> list = mClueTalkAdapter.getData();
                if (null != list) {
                    AnswerItemEntity first = list.get(0);
                    showCommentPopupWindow(NewsDetailActivity.REPLY_NEWS,
                            getString(R.string.submit_comment_hint), mCommentDraft, "0", null,
                            first.getCreateUserNickName(), first.getCreateUserId());
                }
                break;
        }

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

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onGetClueDetailSuccess(ClueItemEntity clueItem) {
        mClueDetailSrfl.setRefreshing(false);
        if (null != clueItem) {
            mClueItem = clueItem;
            initClueContent(mClueItem);
        } else {
            ToastUtils.showShort(R.string.error_obtain_clue_detail);
        }
    }

    @Override
    public void onGetClueFail() {
        mClueDetailSrfl.setRefreshing(false);
    }

    @Override
    public void onAddReplySuccess() {
        ToastUtils.showShort(R.string.comment_send_success);
        if (View.VISIBLE == mCommentLl.getVisibility()) {
            List<AnswerItemEntity> answerList = mClueItem.getAnswerList();
            AnswerItemEntity item = new AnswerItemEntity();
            item.setItemType(ClueTalkAdapter.TYPE_TALK_RIGHT);
            item.setCreateUserId(SPUtils.getInstance().getString(CommonConstant.UID));
            long serverTime = SPUtils.getInstance().getLong(CommonConstant.SERVER_DATE);
            long currentTime = System.currentTimeMillis();
            if (currentTime > serverTime) {
                item.setCreateTime(currentTime);
            } else {
                item.setCreateTime(serverTime + 90 * 1000);  // 在最新服务器的时间上加90s
            }
            item.setReply(mCommentDraft);
            item.setCreateUserNickName(SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_NICKNAME));
            item.setCreateUserAvatar("user/v1/cyrest/getAvatarUrlByuid/" + SPUtils.getInstance().getString(CommonConstant.UID));
            // answerList.add(item);
            mClueTalkAdapter.addData(item);
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.fullScroll(View.FOCUS_DOWN);
                }
            });

        }

        mCommentDraft = "";
    }

    @Override
    public void sendComment(String commentId, String topReplyId,
                            String upReplyId, String reUserName, String reUserId, String reply) {
        mPresenter.addReply(mColumnId, mContentId, topReplyId, reUserName, reUserId, reply);
    }

    @Override
    public void saveDraft(String commentId, String draft) {
        mCommentDraft = draft;
        float y = mCommentLl.getY();
        if (y < mCommentLlY) {
            KeyboardUtils.toggleSoftInput();
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.getClueDetail(mColumnId, mContentId);
    }

    public class ClueBannerItem {
        private String imgUrl;
        private boolean isVideo;
        private String videoUrl;

        public ClueBannerItem(String imgUrl, boolean isVideo, String videoUrl) {
            this.imgUrl = imgUrl;
            this.isVideo = isVideo;
            this.videoUrl = videoUrl;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public boolean isVideo() {
            return isVideo;
        }

        public void setVideo(boolean video) {
            isVideo = video;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }
    }
}
