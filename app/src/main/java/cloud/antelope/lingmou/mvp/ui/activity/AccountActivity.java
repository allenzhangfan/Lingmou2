package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.util.ScreenUtils;
import com.google.gson.Gson;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.modle.SweetDialog;
import com.lingdanet.safeguard.common.net.RequestUtils;
import com.lingdanet.safeguard.common.utils.AppStatusTracker;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.lzy.imagepicker.ImageDataSource;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.MediaItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.BarViewUtils;
import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.app.utils.LoadAvatarUtils;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.app.utils.loader.GlideImageLoader;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerAccountComponent;
import cloud.antelope.lingmou.di.module.AccountModule;
import cloud.antelope.lingmou.mvp.contract.AccountContract;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoEntity;
import cloud.antelope.lingmou.mvp.presenter.AccountPresenter;
import cloud.antelope.lingmou.mvp.ui.widget.SelectDialog;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;


public class AccountActivity extends BaseActivity<AccountPresenter> implements AccountContract.View,
        PermissionUtils.HasPermission {

    @BindView(R.id.account_arrow_iv)
    ImageView mAccountArrowIv;
    @BindView(R.id.header_rl)
    RelativeLayout mHeaderRl;
    @BindView(R.id.nick_tv)
    TextView mNickTv;
    @BindView(R.id.nick_ll)
    LinearLayout mNickLl;
    @BindView(R.id.password_tv)
    TextView mPasswordTv;
    @BindView(R.id.logout_btn)
    Button mLogoutBtn;
    @BindView(R.id.header_iv)
    ImageView mHeaderIv;
    @BindView(R.id.head_left_iv)
    ImageButton mBackIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.head_right_iv)
    ImageView mHeadRightIv;
    @BindView(R.id.head_right_tv)
    TextView mHeadRightTv;

    @Inject
    LoadingDialog mLoadingDialog;
    @Inject
    SweetDialog mSweetDialog;

    private boolean mIsUpdateHeader;
    public static final int REQ_IMAGE_CROP = 0x03; /// 裁剪请求码
    public static final int REQ_IMAGE_HEAD = 0x02; /// 裁剪请求码
    public static final int REQ_MODIFY_NICKNAME = 0x04; /// 修改昵称请求码
    private String mLoginName;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerAccountComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .accountModule(new AccountModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_account; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mTitleTv.setText(R.string.account_and_safty);
        mLoginName = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_LOGIN_NAME);
        initImagePicker();
        LoadAvatarUtils.updateAvatar(mHeaderIv);
        mNickTv.setText(mLoginName);
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setMultiMode(false);                      //单选模式
        imagePicker.setShowCamera(false);                      //显示拍照按钮
        imagePicker.setCrop(true);                            //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setMediaLimit(1);                         //选中媒体文件数量限制
        imagePicker.setVideoLimit(0);                         //选中视频数量限制
        imagePicker.setStyle(CropImageView.Style.CIRCLE);  //裁剪框的形状
        imagePicker.setFocusWidth(SizeUtils.dp2px(300));    //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(SizeUtils.dp2px(300));    //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(SizeUtils.dp2px(80));         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(SizeUtils.dp2px(80));         //保存文件的高度。单位像素
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
        ToastUtils.showShort(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
    }

    @Override
    public void killMyself() {

    }


    @Override
    public void doNext(int permId) {
        if (PermissionUtils.RC_CAMERA_PERM == permId) {
            startImageGridActivity(0);
        } else if (PermissionUtils.RC_READ_SDCARD_PERM == permId) {
            startImageGridActivity(1);
        }
    }

    @OnClick({R.id.head_left_iv, R.id.header_rl, R.id.password_tv, R.id.logout_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.head_left_iv:
                if (mIsUpdateHeader) {
                    setResult(RESULT_OK);
                }
                finish();
                break;
            case R.id.header_rl:
                /*List<String> names = new ArrayList<>();
                names.add(getString(R.string.take_photo_text));
                names.add(getString(R.string.select_photo_text));
                showDialog((parent, view1, position, id) -> checkPerm(position), names);*/
                Intent intent = new Intent(AccountActivity.this, AccountHeadActivity.class);
                startActivityForResult(intent, REQ_IMAGE_HEAD);
                break;
            case R.id.password_tv:
                startActivity(new Intent(Utils.getContext(), ResetPasswordActivity.class));
                break;
            case R.id.logout_btn:
                logout();
                break;
        }
    }

    @Override
    public void onBackPressedSupport() {
        if (mIsUpdateHeader) {
            setResult(RESULT_OK);
        }
        finish();
    }

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle,
                listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }


    private void checkPerm(int position) {
        if (position == 0) {
            checkCameraPerm();  // 检测是否有拍照的权限
        } else {
            checkSdCardPerm(); // 检测是否有读取手机SD的权限
        }
    }

    @AfterPermissionGranted(PermissionUtils.RC_CAMERA_PERM)
    public void checkCameraPerm() {
        PermissionUtils.cameraTask(AccountActivity.this);
    }

    @AfterPermissionGranted(PermissionUtils.RC_READ_SDCARD_PERM)
    public void checkSdCardPerm() {
        PermissionUtils.readSdCardTask(AccountActivity.this);
    }

    /**
     * 登出界面
     */
    private void logout() {
        mSweetDialog.setTitle(getString(R.string.sure_to_logout));
        mSweetDialog.setPositive(getString(R.string.logout));
        mSweetDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSweetDialog.dismiss();
                // JPushUtils.resumeJPush(); // 这里需要在确保服务开启的状态下删除别名
                mPresenter.logOut();
            }
        });
        mSweetDialog.setNegativeListener(null);
        mSweetDialog.show();
    }

    private void exitToLogin() {
        SaveUtils.clearCacheInfo();
        JPushUtils.deleteAlias();
        SaveUtils.clear();
        AppStatusTracker.getInstance().exitAllActivity();
        // 退出登录时，下次不再自动登录
        Intent intent = new Intent(Utils.getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        System.gc();
    }


    private void startImageGridActivity(int position) {
        Intent intent = new Intent(AccountActivity.this, ImageGridActivity.class);
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
        intent.putExtra(ImageGridActivity.EXTRAS_LOAD_TYPE, ImageDataSource.LOADER_ALL_IMAGE);
        intent.putExtra(JCameraView.TYPE_CAPTURE, JCameraView.BUTTON_STATE_ONLY_CAPTURE);
        ImagePicker.getInstance().setVideoLimit(0);
        ImagePicker.getInstance().setMediaLimit(1);
        /* 如果需要进入选择的时候显示已经选中的图片，
         * 详情请查看ImagePickerActivity
         * */
        // intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, mSelectImageList);
        // intent.putExtra(ImageGridActivity.EXTRAS_VIDEOS, mSelectVideoList);
        startActivityForResult(intent, REQ_IMAGE_CROP);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_IMAGE_HEAD:
                mIsUpdateHeader = data.getBooleanExtra("isUpdate", false);
                if (mIsUpdateHeader) {
                    LoadAvatarUtils.updateAvatar(mHeaderIv);
                }
                break;
            case REQ_MODIFY_NICKNAME:
                if (resultCode == RESULT_OK) {
                    String content = data.getStringExtra(Constants.EXTRA_NICKNAME);
                    mNickTv.setText(content);
                }
                break;
            // 裁剪之后处理并设置头像图片
            case REQ_IMAGE_CROP:
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    ArrayList<MediaItem> mediaItems = (ArrayList<MediaItem>)
                            data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    if (mediaItems.size() <= 0) {
                        break;
                    }
                    String avatarPath = mediaItems.get(0).path;
                    uploadAvatar(new File(avatarPath));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 上传头像.
     */
    private void uploadAvatar(File file) {
        if (!FileUtils.isFileExists(file)) {
            ToastUtils.showShort("获取头像文件失败");
            return;
        }
        MultipartBody.Part part = RequestUtils.prepareFilePart("file", file.getAbsolutePath());
        EmptyEntity data = new EmptyEntity();
        RequestBody metadata = RequestUtils.createPartFromString(new Gson().toJson(data));
        mPresenter.uploadAvatar(String.valueOf(file.length()), metadata, part, file.getAbsolutePath());
    }

    @Override
    public void uploadAvatarSuccess() {
        mIsUpdateHeader = true;
        LoadAvatarUtils.updateAvatar(mHeaderIv);
        ToastUtils.showShort(R.string.toast_avatar_has_change);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void logOutSuccess() {
        exitToLogin();
    }

    @Override
    public void logOutFail() {
        exitToLogin();
    }
}
