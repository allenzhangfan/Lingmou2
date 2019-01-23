package cloud.antelope.lingmou.mvp.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.cjt2325.cameralibrary.JCameraView;
import com.google.gson.Gson;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.modle.PermissionDialog;
import com.lingdanet.safeguard.common.net.RequestUtils;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lzy.imagepicker.ImageDataSource;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.MediaItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.nanchen.compresshelper.CompressHelper;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.FormatUtils;
import cloud.antelope.lingmou.app.utils.LogUploadUtil;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.app.utils.loader.GlideImageLoader;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerNewDeployMissionComponent;
import cloud.antelope.lingmou.di.module.NewDeployMissionModule;
import cloud.antelope.lingmou.mvp.contract.NewDeployMissionContract;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.LogUploadRequest;
import cloud.antelope.lingmou.mvp.model.entity.ModifyDeployListEvent;
import cloud.antelope.lingmou.mvp.presenter.NewDeployMissionPresenter;
import cloud.antelope.lingmou.mvp.ui.widget.LmDatePickDialog;
import cloud.antelope.lingmou.mvp.ui.widget.LmTimePickerDialog;
import cloud.antelope.lingmou.mvp.ui.widget.dialog.SelectUploadModeDialog;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class NewDeployMissionActivity extends BaseActivity<NewDeployMissionPresenter> implements NewDeployMissionContract.View, EasyPermissions.PermissionCallbacks, PermissionUtils.HasPermission {
    public static final int REQUEST_CODE_SELECT = 0x01;
    public static final int REQUEST_CODE_FACE = 0x02;
    public static final int START_TIME = 0x010;
    public static final int END_TIME = 0x011;

    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.head_right_iv)
    ImageView headRightIv;
    @BindView(R.id.head_right_tv)
    TextView headRightTv;
    @BindView(R.id.iv_face)
    ImageView ivFace;
    @BindView(R.id.iv_take_photo)
    ImageView ivTakePhoto;
    @BindView(R.id.iv_photos)
    ImageView ivPhotos;
    @BindView(R.id.ll_choice)
    LinearLayout llChoice;
    @BindView(R.id.iv_pic)
    ImageView ivPic;
    @BindView(R.id.rl_pic)
    RelativeLayout rlPic;
    @BindView(R.id.tv_mission_detail)
    TextView tvMissionDetail;
    @BindView(R.id.et_mission_name)
    EditText etMissionName;
    @BindView(R.id.tv_threshold)
    TextView tvThreshold;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.ll_fold)
    LinearLayout llFold;
    @BindView(R.id.et_mission_remark)
    EditText etMissionRemark;
    @BindView(R.id.tv_create)
    TextView tvCreate;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @Inject
    LoadingDialog mLoadingDialog;
    @Inject
    ImageLoader mImageLoader;
    boolean fold;
    boolean animating;
    int foldHeight;
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    int setStartYear = year, setStartMonthOfYear = month + 1, setStartDayOfMonth = day, setStartHour = hour, setStartMinute = minute;
    int setEndYear = year, setEndMonthOfYear = month + 1, setEndDayOfMonth = day, setEndHour = hour, setEndMinute = minute;
    private int selectedPosition;
    private LmDatePickDialog lmDatePickDialog;
    private String picPath = "";
    private ArrayList<String> list;
    private String facePath;
    private String picUrl;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerNewDeployMissionComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .newDeployMissionModule(new NewDeployMissionModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_new_deploy_mission; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        titleTv.setText(getString(R.string.create_temporary_mission));
        llFold.measure(0, 0);
        foldHeight = llFold.getMeasuredHeight();
        scrollView.measure(0, 0);
        list = new ArrayList<>();
        for (int i = 80; i <= 100; i++) {
            list.add(String.valueOf(i));
        }
        selectedPosition = list.indexOf(String.valueOf(85));
        initImagePicker();
        if (getIntent() != null && !TextUtils.isEmpty(getIntent().getStringExtra("facePath"))) {
            facePath = getIntent().getStringExtra("facePath");
            mImageLoader.loadImage(this, ImageConfigImpl
                    .builder()
                    .url(facePath)
                    .cacheStrategy(0)
                    .imageView(ivPic).build());
            llChoice.setVisibility(View.GONE);
            rlPic.setVisibility(View.VISIBLE);
        }
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setMultiMode(false);                       //设置多选
        imagePicker.setShowCamera(false);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setMediaLimit(1);                        //选中媒体文件数量限制
        imagePicker.setVideoLimit(0);                         //选中视频数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    @OnClick({R.id.head_left_iv, R.id.tv_mission_detail, R.id.rl_pic, R.id.iv_face, R.id.iv_take_photo, R.id.iv_photos,
            R.id.tv_start_time, R.id.tv_end_time, R.id.tv_threshold, R.id.tv_create})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_left_iv:
                finish();
                break;
            case R.id.tv_mission_detail:
                fold();
                break;
            case R.id.rl_pic:
                SelectUploadModeDialog dialog = new SelectUploadModeDialog(this);
                dialog.setOnItemClickListener(position -> {
                    switch (position) {
                        case 0:
                            Intent intent = new Intent(NewDeployMissionActivity.this, CollectedFaceActivity.class);
                            intent.putExtra("fromNewDeployMissionActivity", true);
                            startActivityForResult(intent, REQUEST_CODE_FACE);
                            break;
                        case 1://拍照
                            checkCameraPerm();  // 检测是否有摄像的权限
                            break;
                        case 2://相册
                            checkSdCardPerm(); // 检测是否有读取手机SD的权限
                            break;
                    }
                    dialog.dismiss();
                });
                dialog.show();
                break;
            case R.id.iv_face:
                Intent intent = new Intent(NewDeployMissionActivity.this, CollectedFaceActivity.class);
                intent.putExtra("fromNewDeployMissionActivity", true);
                startActivityForResult(intent, REQUEST_CODE_FACE);
                break;
            case R.id.iv_take_photo:
                checkCameraPerm();
                break;
            case R.id.iv_photos:
                checkSdCardPerm();
                break;
            case R.id.tv_start_time:
                lmDatePickDialog = new LmDatePickDialog(NewDeployMissionActivity.this, (view12, year, month, dayOfMonth) -> {
                    setStartYear = year;
                    setStartMonthOfYear = month + 1;
                    setStartDayOfMonth = dayOfMonth;
                    lmDatePickDialog.dismiss();
                    showTimePickerDialog(START_TIME);
                }, null, setStartYear, setStartMonthOfYear - 1, setStartDayOfMonth);
                lmDatePickDialog.show();
                break;
            case R.id.tv_end_time:
                lmDatePickDialog = new LmDatePickDialog(NewDeployMissionActivity.this, (view12, year, month, dayOfMonth) -> {
                    setEndYear = year;
                    setEndMonthOfYear = month + 1;
                    setEndDayOfMonth = dayOfMonth;
                    lmDatePickDialog.dismiss();
                    showTimePickerDialog(END_TIME);
                }, null, setEndYear, setEndMonthOfYear - 1, setEndDayOfMonth);
                lmDatePickDialog.show();
                break;
            case R.id.tv_threshold:
                KeyboardUtils.hideSoftInput(this);
                OptionsPickerView pickerView = new OptionsPickerView.Builder(this, (options1, options2, options3, v) -> {
                    selectedPosition = options1;
                    tvThreshold.setText(list.get(options1));
                }).setContentTextSize(20)//设置滚轮文字大小
                        .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                        .setTextColorCenter(Color.LTGRAY)
                        .setDecorView((ViewGroup) getWindow().getDecorView())
                        .build();
                pickerView.setPicker(list);
                pickerView.setSelectOptions(selectedPosition);
                pickerView.show();
                break;
            case R.id.tv_create:
                if (TextUtils.isEmpty(etMissionName.getText().toString().trim())) {
                    ToastUtils.showShort(etMissionName.getHint().toString());
                    KeyboardUtils.showSoftInput(etMissionName);
                    return;
                }
                if (TextUtils.isEmpty(tvStartTime.getText().toString().trim())) {
                    ToastUtils.showShort(tvStartTime.getHint().toString());
                    onClick(tvStartTime);
                    return;
                }
                if (TextUtils.isEmpty(tvEndTime.getText().toString().trim())) {
                    ToastUtils.showShort(tvEndTime.getHint().toString());
                    onClick(tvEndTime);
                    return;
                }
                uploadPic();
                break;
        }
    }

    private void showTimePickerDialog(int type) {
        new LmTimePickerDialog(NewDeployMissionActivity.this, 0, (view, hourOfDay, minute) -> {
            if (type == START_TIME) {
                setStartHour = hourOfDay;
                setStartMinute = minute;
                if (checkTime(false)) {
                    tvStartTime.setText(setStartYear + "-" + FormatUtils.twoNumber(setStartMonthOfYear) + "-" + FormatUtils.twoNumber(setStartDayOfMonth) + " " + FormatUtils.twoNumber(setStartHour) + ":" + FormatUtils.twoNumber(setStartMinute));
                } else {
                    ToastUtils.showLong(getText(R.string.time_set_error_tip));
                    restoreTime(tvStartTime);
                }
            } else {
                setEndHour = hourOfDay;
                setEndMinute = minute;
                if (checkTime(false)) {
                    tvEndTime.setText(setEndYear + "-" + FormatUtils.twoNumber(setEndMonthOfYear) + "-" + FormatUtils.twoNumber(setEndDayOfMonth) + " " + FormatUtils.twoNumber(setEndHour) + ":" + FormatUtils.twoNumber(setEndMinute));
                } else {
                    ToastUtils.showLong(getText(R.string.time_set_error_tip));
                    restoreTime(tvEndTime);
                }
            }
        }, type == START_TIME ? setStartHour : setEndHour, type == START_TIME ? setStartMinute : setEndMinute, true).show();
    }

    private void restoreTime(TextView textView) {
        if (textView.getId() == R.id.tv_start_time) {
            if (TextUtils.isEmpty(textView.getText().toString())) {
                setStartYear = year;
                setStartMonthOfYear = month + 1;
                setStartDayOfMonth = day;
                setStartHour = hour;
                setStartMinute = minute;
            } else {
                String[] s1 = textView.getText().toString().split(" ");
                String[] s2 = s1[0].split("-");
                String[] s3 = s1[1].split(":");
                setStartYear = Integer.valueOf(s2[0]);
                setStartMonthOfYear = Integer.valueOf(s2[1]);
                setStartDayOfMonth = Integer.valueOf(s2[2]);
                setStartHour = Integer.valueOf(s3[0]);
                setStartMinute = Integer.valueOf(s3[1]);
                Timber.e(setStartYear + " " + setStartMonthOfYear + " " + setStartDayOfMonth + " " + setStartHour + " " + setStartMinute);
            }
        } else {
            if (TextUtils.isEmpty(textView.getText().toString())) {
                setEndYear = year;
                setEndMonthOfYear = month + 1;
                setEndDayOfMonth = day;
                setEndHour = hour;
                setEndMinute = minute;
            } else {
                String[] s1 = textView.getText().toString().split(" ");
                String[] s2 = s1[0].split("-");
                String[] s3 = s1[1].split(":");
                setEndYear = Integer.valueOf(s2[0]);
                setEndMonthOfYear = Integer.valueOf(s2[1]);
                setEndDayOfMonth = Integer.valueOf(s2[2]);
                setEndHour = Integer.valueOf(s3[0]);
                setEndMinute = Integer.valueOf(s3[1]);

            }
        }
    }

    /**
     * 检查时间设置
     *
     * @return 没问题返回true
     */
    private boolean checkTime(boolean atLast) {
        if (TextUtils.isEmpty(tvStartTime.getText().toString()) && TextUtils.isEmpty(tvEndTime.getText().toString())) {
            return true;
        }
        calendar.set(setStartYear, setStartMonthOfYear - 1, setStartDayOfMonth, setStartHour, setStartMinute);
        long start = calendar.getTimeInMillis();
        calendar.set(setEndYear, setEndMonthOfYear - 1, setEndDayOfMonth, setEndHour, setEndMinute);
        long end = calendar.getTimeInMillis();
        return atLast ? end > start : end >= start;
    }

    @AfterPermissionGranted(PermissionUtils.RC_CAMERA_PERM)
    public void checkCameraPerm() {
        PermissionUtils.cameraTask(NewDeployMissionActivity.this);
    }

    @AfterPermissionGranted(PermissionUtils.RC_READ_SDCARD_PERM)
    public void checkSdCardPerm() {
        PermissionUtils.readSdCardTask(NewDeployMissionActivity.this);
    }

    @Override
    public void doNext(int permId) {
        if (PermissionUtils.RC_READ_SDCARD_PERM == permId) {
            startImageGridActivity(1);
        } else if (PermissionUtils.RC_CAMERA_PERM == permId) {
            startImageGridActivity(0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    private void startImageGridActivity(int position) {
        Intent intent = new Intent(NewDeployMissionActivity.this, ImageGridActivity.class);
        if (position == 0) { // 直接调起相机
            //打开选择,本次允许选择的数量
            intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        }
        //打开选择,本次允许选择的数量
        intent.putExtra(ImageGridActivity.EXTRAS_LOAD_TYPE, ImageDataSource.LOADER_ALL_IMAGE);
        intent.putExtra(JCameraView.TYPE_CAPTURE, JCameraView.BUTTON_STATE_ONLY_CAPTURE);
        ImagePicker.getInstance().setVideoLimit(0);
        ImagePicker.getInstance().setMediaLimit(1);
        /* 如果需要进入选择的时候显示已经选中的图片，
         * 详情请查看ImagePickerActivity
         * */
        // intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, mSelectImageList);
        // intent.putExtra(ImageGridActivity.EXTRAS_VIDEOS, mSelectVideoList);
        startActivityForResult(intent, REQUEST_CODE_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SELECT:
                if (data != null && resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    // 添加图片返回
                    ArrayList<MediaItem> images = (ArrayList<MediaItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    if (images != null && !images.isEmpty()) {
                        MediaItem mediaItem = images.get(0);
                        if (!TextUtils.isEmpty(mediaItem.mimeType) && (mediaItem.mimeType.startsWith("image/jpeg") || mediaItem.mimeType.startsWith("image/png"))) {
                            picPath = mediaItem.path;
                            facePath = null;
                            ImagePicker.getInstance().getImageLoader().displayImage(NewDeployMissionActivity.this, mediaItem.path, ivPic, 0, 0, null);
                            llChoice.setVisibility(View.GONE);
                            rlPic.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                }
                ToastUtils.showShort("获取图片文件失败");
                break;
            case REQUEST_CODE_FACE:
                if (data != null && resultCode == RESULT_OK) {
                    facePath = data.getStringExtra("facePath");
                    mImageLoader.loadImage(this, ImageConfigImpl
                            .builder()
                            .url(facePath)
                            .cacheStrategy(0)
                            .imageView(ivPic).build());
                    llChoice.setVisibility(View.GONE);
                    rlPic.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        KeyboardUtils.hideSoftInput(this);
    }

    /**
     * 上传图片.
     */
    private void uploadPic() {
        File file = new File(picPath);
        File newFile = null;
        MultipartBody.Part part = null;
        if (!FileUtils.isFileExists(file) && TextUtils.isEmpty(facePath)) {
            ToastUtils.showShort("获取图片失败");
            return;
        }
        if (FileUtils.isFileExists(file)) {
            newFile = CompressHelper.getDefault(this).compressToFile(file);
            if (FileUtils.isFileExists(newFile)) {
                part = RequestUtils.prepareFilePart("file", newFile.getAbsolutePath());
            }
        }
        EmptyEntity data = new EmptyEntity();
        RequestBody metadata = RequestUtils.createPartFromString(new Gson().toJson(data));
        calendar.set(setStartYear, setStartMonthOfYear - 1, setStartDayOfMonth, setStartHour, setStartMinute);
        String start = String.valueOf(calendar.getTimeInMillis());
        calendar.set(setEndYear, setEndMonthOfYear - 1, setEndDayOfMonth, setEndHour, setEndMinute);
        String end = String.valueOf(calendar.getTimeInMillis());
        if (checkTime(true)) {
            mPresenter.create(facePath, String.valueOf(newFile == null ? 0 : newFile.length()),
                    metadata,
                    part,
                    etMissionName.getText().toString(),
                    start,
                    end,
                    tvThreshold.getText().toString(),
                    etMissionRemark.getText().toString());
        } else {
            ToastUtils.showLong(getText(R.string.time_set_error_tip));
        }
    }

    private void fold() {
        if (animating) return;
        AnimatorSet set = new AnimatorSet();
        ValueAnimator alphaAnim, heightAnim;
        if (fold) {
            alphaAnim = ValueAnimator.ofFloat(0, 1.0f);
            heightAnim = ValueAnimator.ofFloat(foldHeight, 0f);
        } else {
            alphaAnim = ValueAnimator.ofFloat(1.0f, 0);
            heightAnim = ValueAnimator.ofFloat(0, foldHeight);
        }
        alphaAnim.setDuration(200);
        alphaAnim.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            llFold.setAlpha(value);
        });
        heightAnim.setDuration(300);
        heightAnim.addUpdateListener(animation -> {
            float value = -(float) animation.getAnimatedValue();
            llFold.setPadding(0, (int) value, 0, 0);
        });
        if (fold) {
            set.play(heightAnim).before(alphaAnim);
            alphaAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Drawable drawable = getResources().getDrawable(R.drawable.arrow_down);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tvMissionDetail.setCompoundDrawables(null, null, drawable, null);
                    fold = !fold;
                    animating = false;
                }
            });
        } else {
            set.play(alphaAnim).before(heightAnim);
            heightAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Drawable drawable = getResources().getDrawable(R.drawable.arrow_up);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tvMissionDetail.setCompoundDrawables(null, null, drawable, null);
                    fold = !fold;
                    animating = false;
                }
            });
        }
        set.start();
        animating = true;
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
        ArmsUtils.snackbarText(message);
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
    public void onCreateSuccess(String id) {
        Intent intent = new Intent(this, DeployMissionDetailActivity.class);
        intent.putExtra("mission_id", id);
        startActivity(intent);
        finish();
        EventBus.getDefault().post(new ModifyDeployListEvent());
        LogUploadUtil.uploadLog(new LogUploadRequest(107500, 107501, String.format("添加人员追踪任务【%s】，图片URL:%s", etMissionName.getText().toString().trim(), picUrl)));
    }

    @Override
    public void onUploadPictureSuccess(String url) {
        picUrl = url;
    }
}