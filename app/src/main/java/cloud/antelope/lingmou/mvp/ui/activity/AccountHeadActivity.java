package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cjt2325.cameralibrary.JCameraView;
import com.google.gson.Gson;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.net.RequestUtils;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.EncryptUtils;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.ImageUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.LoadAvatarUtils;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerAccountHeadComponent;
import cloud.antelope.lingmou.di.module.AccountHeadModule;
import cloud.antelope.lingmou.mvp.contract.AccountHeadContract;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.presenter.AccountHeadPresenter;
import cloud.antelope.lingmou.mvp.ui.widget.SelectDialog;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class AccountHeadActivity extends BaseActivity<AccountHeadPresenter> implements AccountHeadContract.View, PermissionUtils.HasPermission {

    private static final int REQ_IMAGE_CROP = 0x03; /// 裁剪请求码

    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.head_right_iv)
    ImageView mHeadRightIv;
    @BindView(R.id.head_iv)
    ImageView mHeadIv;
    private boolean mIsUpdate;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerAccountHeadComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .accountHeadModule(new AccountHeadModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_account_head; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mTitleTv.setText(R.string.see_source_picture);
        mHeadRightIv.setImageResource(R.drawable.depot_more);
        mHeadRightIv.setVisibility(View.VISIBLE);
        mHeadIv.setLayoutParams(new LinearLayout.LayoutParams(SizeUtils.getScreenWidth(),SizeUtils.getScreenWidth()));
        showHead();
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

    @OnClick({R.id.head_left_iv, R.id.head_right_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.head_left_iv:
                Intent intent = new Intent();
                intent.putExtra("isUpdate", mIsUpdate);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.head_right_iv:
                List<String> names = new ArrayList<>();
                names.add(getString(R.string.take_photo_text));
                names.add(getString(R.string.select_photo_text));
                showPhotoDialog((parent, view1, position, id) -> checkPerm(position), names);
                break;
        }
    }

    private SelectDialog showPhotoDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
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
        PermissionUtils.cameraTask(AccountHeadActivity.this);
    }

    @AfterPermissionGranted(PermissionUtils.RC_READ_SDCARD_PERM)
    public void checkSdCardPerm() {
        PermissionUtils.readSdCardTask(AccountHeadActivity.this);
    }

    @Override
    public void doNext(int permId) {
        if (PermissionUtils.RC_CAMERA_PERM == permId) {
            startImageGridActivity(0);
        } else if (PermissionUtils.RC_READ_SDCARD_PERM == permId) {
            startImageGridActivity(1);
        }
    }


    private void startImageGridActivity(int position) {
        Intent intent = new Intent(AccountHeadActivity.this, ImageGridActivity.class);
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
        showHead();
        ToastUtils.showShort(R.string.toast_avatar_has_change);
        mIsUpdate = true;
    }

    private void showHead() {
        String imgUrl = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_AVATAR);
        Uri uri = Uri.parse(imgUrl);
        GlideArms.with(Utils.getContext())
                .load(uri)
                .placeholder(R.drawable.user_header_default)
                .error(R.drawable.user_header_default)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource,
                                                Transition<? super Drawable> glideAnimation) {
                        if (null != mHeadIv) {
                            mHeadIv.setImageDrawable(resource);
                        }
                    }
                });
    }

    @Override
    public void onBackPressedSupport() {
        Intent intent = new Intent();
        intent.putExtra("isUpdate", mIsUpdate);
        setResult(RESULT_OK, intent);
        super.onBackPressedSupport();
    }
}
