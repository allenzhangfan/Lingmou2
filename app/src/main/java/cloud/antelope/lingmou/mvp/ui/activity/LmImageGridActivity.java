package cloud.antelope.lingmou.mvp.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lingdanet.safeguard.common.utils.SystemBarTintManager;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lzy.imagepicker.ImageDataSource;
import com.lzy.imagepicker.adapter.ImageFolderAdapter;
import com.lzy.imagepicker.bean.MediaFolder;
import com.lzy.imagepicker.bean.MediaItem;
import com.lzy.imagepicker.view.FolderPopUpWindow;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.adapter.LmImageGridAdapter;

import static com.lzy.imagepicker.ImageDataSource.LOADER_ALL_IMAGE;

/**
 * 作者：陈新明
 * 创建日期：2018/01/25
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */

public class LmImageGridActivity extends FragmentActivity implements ImageDataSource.OnImagesLoadedListener {

    public static final int REQUEST_PERMISSION_STORAGE = 0x01;

    @BindView(R.id.back_iv)
    ImageView mBackIv;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.btn_dir)
    Button mBtnDir;
    @BindView(R.id.footer_bar)
    RelativeLayout mFooterBar;

    private List<MediaFolder> mMediaFolders;   //所有的图片文件夹
    private FolderPopUpWindow mFolderPopupWindow;  //ImageSet的PopupWindow
    private ImageFolderAdapter mImageFolderAdapter;    //图片文件夹的适配器
    private LmImageGridAdapter mRecyclerAdapter;

    private boolean mIsCanClick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lm_image_grid);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.twenty_transparent_black);
        mImageFolderAdapter = new ImageFolderAdapter(this, null);
        mRecyclerAdapter = new LmImageGridAdapter(this, null);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        loadImages(LOADER_ALL_IMAGE);
        initListener();
    }

    private void initListener() {
        mRecyclerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mIsCanClick) {
                    //进入Crop界面
                    MediaItem mediaItem = (MediaItem) adapter.getItem(position);
                    String imagePath = mediaItem.path;
                    Intent intent = new Intent(LmImageGridActivity.this, LmCropActivity.class);
                    intent.putExtra("img_path", imagePath);
                    intent.putExtra("fromCapture", false);
                    startActivity(intent);
                    mIsCanClick = false;
                    finish();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsCanClick = true;
    }

    private void loadImages(int loadType) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new ImageDataSource(this, null, this, loadType);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
            }
        } else {
            new ImageDataSource(this, null, this, loadType);
        }
    }


    public boolean checkPermission(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onImagesLoaded(List<MediaFolder> mediaFolders) {
        this.mMediaFolders = mediaFolders;
        if (mediaFolders.size() == 0) {
            mRecyclerAdapter.setNewData(null);
        } else {
            mRecyclerAdapter.setNewData(mediaFolders.get(0).images);
            mBtnDir.setText(mediaFolders.get(0).name);
        }

        mImageFolderAdapter.refreshData(mediaFolders);
    }

    @OnClick({R.id.back_iv, R.id.btn_dir})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.btn_dir:
                if (mMediaFolders == null) {
                    return;
                }
                //点击文件夹按钮
                createPopupFolderList();
                mImageFolderAdapter.refreshData(mMediaFolders);  //刷新数据
                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.showAtLocation(mFooterBar, Gravity.NO_GRAVITY, 0, 0);
                    //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
                    int index = mImageFolderAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    mFolderPopupWindow.setSelection(index);
                }
                break;
        }
    }


    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList() {
        mFolderPopupWindow = new FolderPopUpWindow(this, mImageFolderAdapter);
        mFolderPopupWindow.setOnItemClickListener(new FolderPopUpWindow.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mImageFolderAdapter.setSelectIndex(position);
                mFolderPopupWindow.dismiss();
                MediaFolder mediaFolder = (MediaFolder) adapterView.getAdapter().getItem(position);
                if (null != mediaFolder) {
                    mRecyclerAdapter.setNewData(mediaFolder.images);
                    mBtnDir.setText(mediaFolder.name);
                }
            }
        });
        mFolderPopupWindow.setMargin(mFooterBar.getHeight());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new ImageDataSource(this, null, this, ImageDataSource.LOADER_ALL);
            } else {
                ToastUtils.showShort("权限被禁止，无法选择本地图片");
            }
        }
    }
}
