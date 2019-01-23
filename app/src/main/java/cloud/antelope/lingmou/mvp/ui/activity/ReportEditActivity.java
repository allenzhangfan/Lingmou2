package cloud.antelope.lingmou.mvp.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.cjt2325.cameralibrary.JCameraView;
import com.example.apple.transcodedemo.TranscodeCmd;
import com.example.apple.transcodedemo.TranscodeParam;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.modle.PermissionDialog;
import com.lingdanet.safeguard.common.modle.SweetDialog;
import com.lingdanet.safeguard.common.net.RequestUtils;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.DeviceUtil;
import com.lingdanet.safeguard.common.utils.EncryptUtils;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.ImageUtils;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;
import com.lingdanet.safeguard.common.utils.LocationUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lzy.imagepicker.ImageDataSource;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.MediaItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.DefaultTextWatcher;
import cloud.antelope.lingmou.app.utils.GisUtils;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.app.utils.UrlUtils;
import cloud.antelope.lingmou.app.utils.ViewUtils;
import cloud.antelope.lingmou.app.utils.loader.GlideImageLoader;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerReportEditComponent;
import cloud.antelope.lingmou.di.module.CyqzConfigModule;
import cloud.antelope.lingmou.di.module.ReportEditModule;
import cloud.antelope.lingmou.mvp.contract.CyqzConfigContract;
import cloud.antelope.lingmou.mvp.contract.ReportEditContract;
import cloud.antelope.lingmou.mvp.model.entity.AttachmentBean;
import cloud.antelope.lingmou.mvp.model.entity.EventEntity;
import cloud.antelope.lingmou.mvp.model.entity.OperationEntity;
import cloud.antelope.lingmou.mvp.presenter.CyqzConfigPresenter;
import cloud.antelope.lingmou.mvp.presenter.ReportEditPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.ImagePickerAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.SelectDialog;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class ReportEditActivity extends BaseActivity<ReportEditPresenter>
        implements ReportEditContract.View,
        EasyPermissions.PermissionCallbacks,
        PermissionUtils.HasPermission,
        GisUtils.OnLocateActionListener,
        ImagePickerAdapter.OnRecyclerViewItemClickListener, CyqzConfigContract.View {

    private static final int REQUEST_CODE_EDIT_ADDRESS = 0x15;
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 0x01;
    public static final int REQUEST_CODE_PREVIEW = 0x02;
    private static final int MAX_TEXT_LENGTH = 140;
    private static final int MIN_TEXT_LENGTH = 10;
    private static final int MAX_PICTURE_SIZE = 512000;
    private static final int MAX_VIDEO_SIZE = 4718592;


    @BindView(R.id.loading_progress)
    ProgressBar mLoadingProgress;
    @BindView(R.id.report_desc_et)
    EditText mDescEt;       //举报内容
    @BindView(R.id.upload_images_rw)
    RecyclerView mSelectLV;       //图片list
    @BindView(R.id.location_area)
    RelativeLayout mLocationArea;
    @BindView(R.id.location_address_tv)
    TextView mLocationAddrTv;
    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.head_right_tv)
    TextView mHeadRightTv;

    @Inject
    CyqzConfigPresenter mCyqzConfigPresenter;
    @Inject
    @Named("SelectMediaList")
    List<MediaItem> mSelectMediaList;
    @Inject
    @Named("SelectVideoList")
    List<MediaItem> mSelectVideoList;
    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    LoadingDialog mDialog;

    private String mCaseId; // 举报关联的告警

    private Dialog mCompressDialog;   //图片或视频压缩中的dialog
    private ImagePickerAdapter mMediaAdapter;

    private int maxImgCount = 3;               //允许选择媒体文件最大数
    private int maxVideoCount = 1;             //允许选择的视频文件最大数

    private String mSelectAddr;
    private double mSelectLon = 0.0;
    private double mSelectLan = 0.0;

    // 是否是第一次定位
    private boolean mIsFirstLocate = true;

    private String mContent;

    private ArrayList<MultipartBody.Part> files;

    private EventEntity message;

    private List<AttachmentBean> attachments;

    private AMapLocation mCurLoc;

    private GisUtils mGisUtils;

    private boolean mIsSending;

    private boolean mIsFinishUploadClue;
    private boolean mIsFinishUploadClueMessage;

    private int mType;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerReportEditComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .reportEditModule(new ReportEditModule(this))
                .cyqzConfigModule(new CyqzConfigModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_report_edit; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mTitleTv.setText(R.string.submit_case);
        mHeadRightTv.setVisibility(View.VISIBLE);
        mHeadRightTv.setText(R.string.common_send_text);

        initImagePicker();

        mCaseId = getIntent().getStringExtra(Constants.CASE_ID);
        mType = getIntent().getIntExtra(Constants.EXTRA_REPORT_TYPE, -1);
        if (-1 == mType) {
            mType = Constants.TYPE_1;
        }
        initView();
        mGisUtils = new GisUtils(this, 2000);
        mGisUtils.setLocateListener(this);

        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_OPERATION_ID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_CASE_ID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_CLUE_ID))) {
            // 如果发现运营中心的信息不完全，则再查询一次
            mCyqzConfigPresenter.getCyqzConfig();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGisUtils.start();
    }

    @Override
    protected void onStop() {
        mGisUtils.stop();
        super.onStop();
    }

    @Override
    public void onEnterAnimationComplete() {
        KeyboardUtils.showSoftInput(mDescEt);
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setMultiMode(true);                       //设置多选
        imagePicker.setShowCamera(false);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setMediaLimit(maxImgCount);              //选中媒体文件数量限制
        imagePicker.setVideoLimit(maxVideoCount);             //选中视频数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    private void initView() {
        mSelectLV = (RecyclerView) findViewById(R.id.upload_images_rw);
        mMediaAdapter = new ImagePickerAdapter(this, mSelectMediaList, maxImgCount);
        mMediaAdapter.setOnItemClickListener(this);
        mSelectLV.setLayoutManager(mLayoutManager);
        mSelectLV.setHasFixedSize(true);
        mSelectLV.setAdapter(mMediaAdapter);
        mDescEt.addTextChangedListener(new DefaultTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= MAX_TEXT_LENGTH) {
                    ToastUtils.showShort(R.string.report_max_length_limit, MAX_TEXT_LENGTH);
                }
            }
        });
    }

    @Override
    public void doNext(int permId) {
        if (PermissionUtils.RC_READ_SDCARD_PERM == permId) {
            startImageGridActivity(1);
        } else if (PermissionUtils.RC_LOCATION_PERM == permId) {
            startActivityForResult(new Intent(this, ReportAddressActivity.class), REQUEST_CODE_EDIT_ADDRESS);
        } else if (PermissionUtils.RC_RECORD_AUDIO_PERM == permId) {
            startImageGridActivity(0);
        }
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
            if (perms.contains(Manifest.permission.CAMERA)) {
                final PermissionDialog dialog = new PermissionDialog(this);
                dialog.setTitle(getString(R.string.request_permission_tag));
                dialog.setMessage(getString(R.string.need_camera_permission_tips));
                dialog.setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ToastUtils.showShort(R.string.can_not_use_camera);
                    }
                });
                dialog.show();
            } else if (perms.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                final PermissionDialog dialog = new PermissionDialog(this);
                dialog.setTitle(getString(R.string.request_permission_tag));
                dialog.setMessage(getString(R.string.need_read_sdcard_permission_tips));
                dialog.setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ToastUtils.showShort(R.string.can_not_read_picture);
                    }
                });
                dialog.show();
            } else if (perms.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                final PermissionDialog dialog = new PermissionDialog(this);
                dialog.setTitle(getString(R.string.request_permission_tag));
                dialog.setMessage(getString(R.string.need_location_permission_tips));
                dialog.setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ToastUtils.showShort(R.string.can_not_get_location);
                    }
                });
                dialog.show();
            } else if (perms.contains(Manifest.permission.RECORD_AUDIO)) {
                final PermissionDialog dialog = new PermissionDialog(this);
                dialog.setTitle(getString(R.string.request_permission_tag));
                dialog.setMessage(getString(R.string.need_audio_permission_tips));
                dialog.setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ToastUtils.showShort(R.string.can_not_use_audio);
                    }
                });
                dialog.show();
            }

        }
    }

    @AfterPermissionGranted(PermissionUtils.RC_READ_SDCARD_PERM)
    public void checkSdCardPerm() {
        PermissionUtils.readSdCardTask(ReportEditActivity.this);
    }

    @AfterPermissionGranted(PermissionUtils.RC_LOCATION_PERM)
    public void checkLocationPerm() {
        PermissionUtils.locationTask(ReportEditActivity.this);
    }

    @AfterPermissionGranted(PermissionUtils.RC_RECORD_AUDIO_PERM)
    public void checkAudioPerm() {
        PermissionUtils.audioTask(ReportEditActivity.this);
    }

    @OnClick({R.id.location_area, R.id.head_left_iv, R.id.head_right_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_iv:
                onBackPressedSupport();
                break;
            case R.id.head_right_tv:
                if (!mIsSending && !mIsFinishUploadClue) {
                    mIsSending = true;
                    send();
                }
                break;
            case R.id.location_area:
                checkLocationPerm();
                if (!LocationUtils.isGpsEnabled()) {
                    LocationUtils.openGpsSettings();
                }
                break;
        }
    }

    /**
     * 发送准备.
     */
    private void send() {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(R.string.network_disconnect);
            mIsSending = false;
            return;
        }
        mContent = mDescEt.getText().toString().trim();
        if (TextUtils.isEmpty(mContent)) {
            ToastUtils.showShort(R.string.plz_input_report_desc_text);
            mIsSending = false;
            return;
        }
        if (mContent.length() < 10) {
            ToastUtils.showShort(R.string.report_min_length_limit, MIN_TEXT_LENGTH);
            mIsSending = false;
            return;
        }

        message = new EventEntity();
        files = new ArrayList<>();
        EventEntity.BodyBean body = new EventEntity.BodyBean();
        // body.setTipType(String.valueOf(reportType));
        body.setTipType(String.valueOf(mType));
        // body.setToken(SPUtils.getInstance().getString(CommonConstant.TOKEN));
        // body.setUpLoadToken(SPUtils.getInstance().getString(Constants.CONFIG_CYQZ_LY_TOKEN));
        body.setColumnId(SPUtils.getInstance().getString(Constants.CONFIG_CLUE_ID));
        body.setCaseId("");
        body.setContent(mContent);
        body.setIsTop(Constants.OBJECT_STORAGE_IS_TOP);
        body.setIsNeedAudit(Constants.IS_ALLOW_AUDIT);
        body.setIsAllowReply(Constants.IS_ALLOW_REPLY);
        body.setUuid(EncryptUtils.generateRandomUUID());
        if (!TextUtils.isEmpty(mCaseId)) {
            body.setCaseId(mCaseId);
        }

        if (TextUtils.isEmpty(mSelectAddr) || getString(R.string.get_address).equals(mSelectAddr)
                || mSelectLan == 0.0 || mSelectLon == 0.0) {
            if (null != mCurLoc) {
                body.setGpsAddr(GisUtils.getAbbrAddr(mCurLoc.getAddress()));
                body.setGpsX(String.valueOf(mCurLoc.getLatitude()));
                body.setGpsY(String.valueOf(mCurLoc.getLongitude()));
            } else {
                body.setGpsAddr("未知地理位置");
                body.setGpsX("");
                body.setGpsY("");
            }
        } else {
            body.setGpsAddr(mSelectAddr);
            body.setGpsX(String.valueOf(mSelectLan));
            body.setGpsY(String.valueOf(mSelectLon));
        }
        message.setSubject(Constants.OBJECT_STORAGE_SUBJECT);
        message.setChannelId(Constants.CHANNEL_ID);
        message.setBody(body);
        attachments = new ArrayList<>();

        // 如果有视频
        if (mSelectVideoList != null && mSelectVideoList.size() > 0) {
            mCompressDialog = ViewUtils.showAppleProgressMessage(getString(R.string.loading_clue_is_compressing), getActivity(), null);
            final MediaItem mediaItem = mSelectVideoList.get(0);
            if (mediaItem.size > MAX_VIDEO_SIZE) { // 如果视频的尺寸大于4.5MB，需要先进行压缩
                compressVideo();
            } else {  // 否则压缩图片
                createVideoAttachment(mediaItem);
                compressImage();
            }
        }
        // 如果有图片
        else if (mSelectMediaList != null && mSelectMediaList.size() > 0) {
            mCompressDialog = ViewUtils.showAppleProgressMessage(getString(R.string.loading_clue_is_compressing), getActivity(), null);
            compressImage();
        }
        // 如果图片和视频都没有，直接发送
        else {
            sendMediaFile();
        }

    }

    /**
     * 对视频进行压缩
     */
    private void compressVideo() {
        final MediaItem mediaItem = mSelectVideoList.get(0);
        final long begin = SystemClock.elapsedRealtime();
        final TranscodeParam param = new TranscodeParam();
        param.videobitrate = 1800;  // 设置视频码率为 1750Kbps
        param.videoframerate = 20;  // 设置视频的帧率为 20fps
        param.autorotate = 0; // 保持视频的原始旋转方向不变
        //        int ratation = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));//视频方向
        // 有可能从媒体数据库没有查询到视频的宽高数据，即默认都等于0
        if (mediaItem.width == 0 || mediaItem.height == 0) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(mediaItem.path);
            try {
                mediaItem.width = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));//宽
                mediaItem.height = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));//高
            } catch (NumberFormatException e) {
                e.printStackTrace();
                createVideoAttachment(mediaItem);
                compressImage();
                return;
            }
        }
        // 将视频分辨率压缩到960*540级别
        if (mediaItem.width > 960 || mediaItem.height > 960) {
            if (mediaItem.width > mediaItem.height) {
                float radioX = 960.0f / mediaItem.width;
                param.width = 960;
                param.height = (int) (mediaItem.height * radioX);
            } else {
                float radioY = 960.0f / mediaItem.height;
                param.height = 960;
                param.width = (int) (mediaItem.width * radioY);
            }
        } else {
            param.width = mediaItem.width;
            param.height = mediaItem.height;
        }
        param.srcpath = mediaItem.path;
        param.dstpath = Configuration.getVideoDirectoryPath() + "VID_" + System.nanoTime() + ".mp4";
        TranscodeCmd.exec(param, new TranscodeCmd.OnExecListener() {
            @Override
            public void onExecuted(final int ret) {

                if (ret == 0) {
                    if (mediaItem.path.contains("/cyqz/video/")) {  // 针对华为手机，拍出来的视频很大，需要删除原视频
                        new File(mediaItem.path).delete();
                    }
                    mediaItem.path = param.dstpath;
                    // 更新图片媒体库的数据
                    ImagePicker.galleryAddPic(getActivity(), new File(param.dstpath));
                    createVideoAttachment(mediaItem);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // ViewUtils.showToast(ReportEditActivity.this.getApplicationContext(), "开始压缩图片");
                            compressImage();
                        }
                    });
                }
            }
        });
    }

    private void createVideoAttachment(MediaItem mediaItem) {
        createAttachment(mediaItem.path,
                "video_" + EncryptUtils.generateRandomUUID(), mediaItem.mimeType, Constants.EVENT_ATTACHMENT_NOT_THUMBNAIL);
        String videoThumbMap = getVideoThumbMap();
        createAttachment(videoThumbMap, "image_" + EncryptUtils.generateRandomUUID(), "image/jpeg", Constants.EVENT_ATTACHMENT_IS_THUMBNAIL);
    }

    private void createAttachment(String filePath, String formField, String mimeType, String isThumbnail) {
        File file = new File(filePath);
        MultipartBody.Part part = RequestUtils.prepareFilePart(formField, file.getAbsolutePath());
        files.add(part);
        AttachmentBean attachment = new AttachmentBean();
        attachment.setFileName(file.getName());
        attachment.setFormField(formField);
        AttachmentBean.MetadataBean metadata = new AttachmentBean.MetadataBean();
        metadata.setMimeType(mimeType);
        metadata.setSize(String.valueOf(file.length()));
        metadata.setThumbnail(isThumbnail);
        metadata.setFilePath(filePath);
        attachment.setMetadata(metadata);
        attachments.add(attachment);
    }

    /**
     * 压缩图片资源，并在压缩完成之后发送.
     */
    private void compressImage() {
        final Map<String, File> fileMap = new HashMap<>();
        Observable.fromIterable(mSelectMediaList)
                .map(new Function<MediaItem, MediaItem>() {
                    @Override
                    public MediaItem apply(MediaItem mediaItem) throws Exception {
                        if (mediaItem.mimeType.startsWith("image")) {
                            if (mediaItem.mimeType.equals("image/png")) { // 如果是 png 格式图片，先将其转换为 jpg 格式
                                String newJpgPath = Configuration.getPictureDirectoryPath() + "IMG_" + System.nanoTime() + ".jpeg";
                                ImageUtils.save(mediaItem.path, newJpgPath, CommonConstant.MAX_IMAGE_BOUNDS,
                                        Bitmap.CompressFormat.JPEG, CommonConstant.MAX_IMAGE_SIZE);
                                mediaItem.path = newJpgPath;
                                //发送广播通知图片增加了
                                ImagePicker.galleryAddPic(getActivity(), new File(newJpgPath));
                            }
                            File file = new File(mediaItem.path);
                            if (file.length() > MAX_PICTURE_SIZE) { // 如果图片尺寸大于500K则进行压缩
                                String newSavePath = Configuration.getPictureDirectoryPath() + "IMG_" + System.nanoTime() + ".jpeg";
                                ImageUtils.save(mediaItem.path, newSavePath, CommonConstant.MAX_IMAGE_BOUNDS,
                                        Bitmap.CompressFormat.JPEG, CommonConstant.MAX_IMAGE_SIZE);
                                mediaItem.path = newSavePath;
                                //发送广播通知图片增加了
                                ImagePicker.galleryAddPic(getActivity(), new File(newSavePath));
                            }
                        }
                        return mediaItem;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MediaItem>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MediaItem mediaItem) {
                        if (mediaItem.mimeType.startsWith("image")) {
                            String newName = calculateSize(mediaItem.path);
                            File file = new File(mediaItem.path);
                            createAttachment(mediaItem.path,
                                    "image_" + EncryptUtils.generateRandomUUID(),
                                    mediaItem.mimeType, Constants.EVENT_ATTACHMENT_NOT_THUMBNAIL);
                            fileMap.put(newName + ".jpeg", file);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mCompressDialog && mCompressDialog.isShowing()) {
                            mCompressDialog.dismiss();
                        }
                        mIsSending = false;
                        ToastUtils.showShort("图片压缩失败");
                    }

                    @Override
                    public void onComplete() {
                        if (null != mCompressDialog && mCompressDialog.isShowing()) {
                            mCompressDialog.dismiss();
                        }
                        sendMediaFile();
                    }
                });
    }

    /**
     * 视频首帧集合.
     */
    private String getVideoThumbMap() {
        String imagePath = null;
        if (mSelectVideoList != null && mSelectVideoList.size() > 0) {
            MediaItem mediaItem = mSelectVideoList.get(0);

            String videoPath = mediaItem.path;
            imagePath = Configuration.getPictureDirectoryPath()
                    + "IMG_" + System.currentTimeMillis() + Constants.DEFAULT_IMAGE_SUFFIX;
            ImageUtils.save(ThumbnailUtils.createVideoThumbnail(videoPath,
                    MediaStore.Video.Thumbnails.MINI_KIND),
                    imagePath, Bitmap.CompressFormat.JPEG, 15, false);
        }
        return imagePath;
    }

    /**
     * 计算指定路径文件的大小.
     *
     * @param newPath
     * @return
     */
    private String calculateSize(String newPath) {
        long size = 0;
        File file = new File(newPath);
        FileChannel fc = null;
        try {
            if (file.exists() && file.isFile()) {
                FileInputStream fis = new FileInputStream(file);
                fc = fis.getChannel();
                size = fc.size();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return String.valueOf(size);
        } catch (IOException e) {
            return String.valueOf(size);
        } finally {
            if (null != fc) {
                try {
                    fc.close();
                } catch (IOException e) {
                }
            }
        }

        return String.valueOf(size);
    }


    private void sendMediaFile() {
        if (attachments.size() == 0) {
            attachments.add(new AttachmentBean());
        }
        message.setAttachments(attachments);
        mPresenter.submitClue(message, files);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SELECT:
                if (data != null && resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    // 添加图片返回
                    ArrayList<MediaItem> images = (ArrayList<MediaItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    ArrayList<MediaItem> videos = (ArrayList<MediaItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_VIDEOS);
                    // mSelectMediaList.clear();
                    // mSelectVideoList.clear();
                    if (images != null) {
                        mSelectMediaList.addAll(images);
                        mMediaAdapter.setImages(mSelectMediaList);
                    }
                    if (videos != null) {
                        mSelectVideoList.addAll(videos);
                    }
                }
                break;
            case REQUEST_CODE_PREVIEW:
                if (data != null && resultCode == ImagePicker.RESULT_CODE_BACK) {
                    //预览图片返回
                    ArrayList<MediaItem> images = (ArrayList<MediaItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                    ArrayList<MediaItem> videos = (ArrayList<MediaItem>) data.getSerializableExtra(ImagePicker.EXTRA_VIDEO_ITEMS);
                    mSelectMediaList.clear();
                    mSelectVideoList.clear();
                    if (images != null) {
                        mSelectMediaList.addAll(images);
                        mMediaAdapter.setImages(mSelectMediaList);
                    }
                    if (videos != null) {
                        mSelectVideoList.addAll(videos);
                    }
                }
                break;
            case REQUEST_CODE_EDIT_ADDRESS:
                if (resultCode == RESULT_OK) {
                    mSelectAddr = data.getStringExtra(Constants.EXTRA_ADDR);
                    mSelectLan = data.getDoubleExtra(Constants.EXTRA_LAT, 0.0d);
                    mSelectLon = data.getDoubleExtra(Constants.EXTRA_LON, 0.0d);
                    if (!TextUtils.isEmpty(mSelectAddr) && !getString(R.string.get_address).equals(mSelectAddr)) {
                        mLocationAddrTv.setText(mSelectAddr);
                        mLocationAddrTv.setTextColor(getResources().getColor(R.color.report_address_text_color));
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mGisUtils.destory();
        super.onDestroy();
    }

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle,
                listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                List<String> names = new ArrayList<>();
                names.add(getString(R.string.take_photo_or_video_text));
                names.add(getString(R.string.select_photo_text));
                showDialog(new SelectDialog.SelectDialogListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        checkPerm(position);
                    }
                }, names);
                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<MediaItem>) mMediaAdapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                break;
        }
    }

    /**
     * 检测拍摄或读取相册的权限
     *
     * @param position
     */
    private void checkPerm(int position) {
        if (position == 0) {
            checkAudioPerm();  // 检测是否有摄像的权限
        } else {
            checkSdCardPerm(); // 检测是否有读取手机SD的权限
        }
    }


    private void startImageGridActivity(int position) {
        Intent intent = new Intent(ReportEditActivity.this, ImageGridActivity.class);
        if (position == 0) { // 直接调起相机
            /**
             * 0.4.7 目前直接调起相机不支持裁剪，如果开启裁剪后不会返回图片，请注意，后续版本会解决
             *
             * 但是当前直接依赖的版本已经解决，考虑到版本改动很少，所以这次没有上传到远程仓库
             *
             * 如果实在有所需要，请直接下载源码引用。
             */
            //打开选择,本次允许选择的数量
            intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        }
        //打开选择,本次允许选择的数量
        int videoCount = maxVideoCount - mSelectVideoList.size();
        ImagePicker.getInstance().setVideoLimit(videoCount);
        ImagePicker.getInstance().setMediaLimit(maxImgCount - mSelectMediaList.size());
        if (videoCount <= 0) {
            intent.putExtra(ImageGridActivity.EXTRAS_LOAD_TYPE, ImageDataSource.LOADER_ALL_IMAGE);
            intent.putExtra(JCameraView.TYPE_CAPTURE, JCameraView.BUTTON_STATE_ONLY_CAPTURE);
        } else {
            intent.putExtra(ImageGridActivity.EXTRAS_LOAD_TYPE, ImageDataSource.LOADER_ALL);
        }
        /* 如果需要进入选择的时候显示已经选中的图片，
         * 详情请查看ImagePickerActivity
         * */
        // intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, mSelectImageList);
        // intent.putExtra(ImageGridActivity.EXTRAS_VIDEOS, mSelectVideoList);
        startActivityForResult(intent, REQUEST_CODE_SELECT);
    }

    @Override
    public void onSubmitClueSuccess(EventEntity eventEntity) {
        List<AttachmentBean> returnAttachments = eventEntity.getAttachments();
        mPresenter.uploadClueMessage(eventEntity);
        Observable.fromIterable(returnAttachments)
                .map(new Function<AttachmentBean, Object>() {
                    @Override
                    public Object apply(AttachmentBean attachmentBean) throws Exception {
                        AttachmentBean.MetadataBean metadata = attachmentBean.getMetadata();
                        if (null != metadata) {
                            String srcPath = metadata.getFilePath();
                            String mimeType = metadata.getMimeType();
                            String url = UrlUtils.getEventObjectUrl(attachmentBean.getUrl());
                            String fileName = EncryptUtils.encryptMD5ToString(url);
                            if (mimeType.startsWith("video")) {  // 如果原始文件是视频
                                String destPath = Configuration.getVideoDirectoryPath() + fileName + ".mp4";
                                if (srcPath.contains(CommonConstant.MAIN_PATH + CommonConstant.VIDEO_PATH)) {
                                    String destName = fileName + ".mp4";
                                    FileUtils.rename(srcPath, destName);
                                    DeviceUtil.galleryAddMedia(srcPath);
                                } else {
                                    FileUtils.copyFile(srcPath, destPath);  // 将文件拷贝一份到cyqz目录
                                }
                                DeviceUtil.galleryAddMedia(destPath);
                            } else {
                                String extension = FileUtils.getFileExtension(srcPath);
                                String destPath = Configuration.getPictureDirectoryPath() + fileName + "." + extension;
                                if (srcPath.contains(CommonConstant.MAIN_PATH + CommonConstant.PICTURE_PATH)) {
                                    String destName = fileName + "." + extension;
                                    FileUtils.rename(srcPath, destName);
                                    DeviceUtil.galleryAddMedia(srcPath);
                                } else {
                                    FileUtils.copyFile(srcPath, destPath);
                                }
                                DeviceUtil.galleryAddMedia(destPath);
                            }
                        }
                        return new Object();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mIsSending = false;
                    }

                    @Override
                    public void onComplete() {
                        mIsSending = false;
                        mIsFinishUploadClue = true;
                        // Intent intent = new Intent();
                        // intent.putExtra(Constants.INTENT_SHOW_SUCCESS_DIALOG, true);
                        // setResult(RESULT_OK, intent);
                        if (mIsFinishUploadClueMessage) {
                            ToastUtils.showShort(R.string.send_clue_success);
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onSubmitClueError() {
        if (null != mCompressDialog && mCompressDialog.isShowing()) {
            mCompressDialog.dismiss();
        }
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mIsSending = false;
        ToastUtils.showShort(R.string.send_clue_fail_msg);
    }

    @Override
    public void onUploadClueMessageSuccess() {
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mIsFinishUploadClueMessage = true;
        if (!mIsSending) {
            ToastUtils.showShort(R.string.send_clue_success);
            finish();
        }
    }

    @Override
    public void onUploadClueMessageError() {
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mIsFinishUploadClueMessage = true;
        if (!mIsSending) {
            ToastUtils.showShort(R.string.send_clue_success);
            finish();
        }
    }


    @Override
    public void onBackPressedSupport() {
        if (!TextUtils.isEmpty(mDescEt.getText().toString().trim())
                || mSelectMediaList.size() > 0) {
            final SweetDialog sweetDialog = new SweetDialog(this);
            sweetDialog.setTitle(getString(R.string.exit_edit));
            sweetDialog.setPositive(getString(R.string.exit));
            sweetDialog.setNegative(getString(R.string.cancel));
            sweetDialog.setNegativeListener(null);
            sweetDialog.setPositiveListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sweetDialog.dismiss();
                    finish();
                }
            });
            sweetDialog.show();
        } else {
            KeyboardUtils.hideSoftInput(this);
            finish();
        }
    }

    @Override
    public void onLocateStart() {

    }

    @Override
    public void onLocateOK(AMapLocation location) {
        // 定位成功回调信息，设置相关消息
        if (mIsFirstLocate) {
            this.mCurLoc = location;
            mLoadingProgress.setVisibility(View.GONE);
            if (null != mCurLoc && !TextUtils.isEmpty(mCurLoc.getAddress())) {
                mLocationAddrTv.setText(GisUtils.getAbbrAddr(mCurLoc.getAddress()));
                mLocationAddrTv.setTextColor(getResources().getColor(R.color.report_address_text_color));
            } else {
                mLocationAddrTv.setText("暂时无法获取地理位置");
            }
            mIsFirstLocate = false;
        }
    }

    @Override
    public void onLocateFail(int errCode) {

    }


    @Override
    public void showLoading(String message) {
        mDialog.setShowText(R.string.loading_clue_is_sending);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    @Override
    public void hideLoading() {
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.toastText(message);
        mIsSending = false;
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
        }
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
    public void onGetCyqzConfigSuccess(OperationEntity entity) {

    }

    @Override
    public void onGetCyqzConfigError() {

    }
}
