package com.lzy.imagepicker.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.bean.MediaItem;
import com.lzy.imagepicker.util.BitmapUtil;
import com.lzy.imagepicker.view.CropImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ImageCropActivity extends ImageBaseActivity implements View.OnClickListener,
        CropImageView.OnBitmapSaveCompleteListener {

    private CropImageView mCropImageView;
    private Bitmap mBitmap;
    private boolean mIsSaveRectangle;
    private int mOutputX;
    private int mOutputY;
    private ArrayList<MediaItem> mMediaItems;
    private ImagePicker imagePicker;
    private ImageButton mRecognizeBtn;
    private TextView mLoadingTv;
    private static TextView mRecogTv;
    private ImageView mFaceBackBtn;
    private RelativeLayout mToobarLayout;
    private boolean mIsCaptrue;
    private RelativeLayout mFaceRl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        tintManager.setStatusBarTintResource(R.color.face_bg_color);
        imagePicker = ImagePicker.getInstance();
        mIsCaptrue = getIntent().getBooleanExtra("isCapture", false);
        mFaceRl = (RelativeLayout) findViewById(R.id.face_rl);

        //初始化View
        findViewById(R.id.btn_back).setOnClickListener(this);
        mRecognizeBtn = (ImageButton) findViewById(R.id.recognize_btn);
        mLoadingTv = (TextView) findViewById(R.id.loading_tv);
        mRecogTv = (TextView) findViewById(R.id.recog_tv);
        mRecognizeBtn.setOnClickListener(this);
        mFaceBackBtn = (ImageView) findViewById(R.id.back_btn);
        mToobarLayout = (RelativeLayout) findViewById(R.id.toobar_layout);
        if (imagePicker.getStyle() == CropImageView.Style.FACE) {
            //如果是人脸识别，则显示出来
            mLoadingTv.setVisibility(View.VISIBLE);
            mRecognizeBtn.setVisibility(View.VISIBLE);
            mFaceRl.setVisibility(View.VISIBLE);
            mToobarLayout.setVisibility(View.GONE);
        }

        mFaceBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("isCapture", mIsCaptrue);
                setResult(RESULT_CANCELED, intent);
                finish();

            }
        });

        Button btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setText(getString(R.string.complete));
        btn_ok.setOnClickListener(this);
        TextView tv_des = (TextView) findViewById(R.id.tv_des);
        tv_des.setText(getString(R.string.photo_crop));
        mCropImageView = (CropImageView) findViewById(R.id.cv_crop_image);
        mCropImageView.setOnBitmapSaveCompleteListener(this);

        //获取需要的参数
        mOutputX = imagePicker.getOutPutX();
        mOutputY = imagePicker.getOutPutY();
        mIsSaveRectangle = imagePicker.isSaveRectangle();
        mMediaItems = imagePicker.getSelectedMedias();
        String imagePath = mMediaItems.get(0).path;

        mCropImageView.setFocusStyle(imagePicker.getStyle());
        mCropImageView.setFocusWidth(imagePicker.getFocusWidth());
        mCropImageView.setFocusHeight(imagePicker.getFocusHeight());

        //缩放图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels);
        options.inJustDecodeBounds = false;
        mBitmap = BitmapFactory.decodeFile(imagePath, options);
        //        mCropImageView.setImageBitmap(mBitmap);
        //设置默认旋转角度
        mCropImageView.setImageBitmap(mCropImageView.rotate(mBitmap, BitmapUtil.getBitmapDegree(imagePath)));

        //        mCropImageView.setImageURI(Uri.fromFile(new File(imagePath)));
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = width / reqWidth;
            } else {
                inSampleSize = height / reqHeight;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.btn_ok) {
            mCropImageView.saveBitmapToFile(imagePicker.getCropCacheFolder(this), mOutputX, mOutputY, mIsSaveRectangle);
        } else if (id == R.id.recognize_btn) {
            mCropImageView.saveBitmapToFile(imagePicker.getCropCacheFolder(this), mOutputX, mOutputY, mIsSaveRectangle);
            //            mCropImageView.saveBitmapToFile(getCropFile(this), mOutputX, mOutputY, mIsSaveRectangle);
        }
    }

    public File getCropFile(Context context) {
        File file = new File(Configuration.getRootPath() + "/cropTemp/");
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    private static int mEllipsNum = 0;

    private static Handler mRecogHandler = new RecorgHandler();


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isCapture", mIsCaptrue);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void faceFail(String message) {
        mCropImageView.scanFace(false);
        mRecognizeBtn.setVisibility(View.VISIBLE);
        mLoadingTv.setVisibility(View.VISIBLE);
        mRecogTv.setVisibility(View.INVISIBLE);
        mRecogHandler.removeMessages(1);
        ToastUtils.showShort(message);
    }

    private static class RecorgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (mEllipsNum) {
                case 0:
                    mRecogTv.setText("识别人脸中" + "...");
                    break;
                case 1:
                    mRecogTv.setText("识别人脸中" + ".");
                    break;
                case 2:
                    mRecogTv.setText("识别人脸中" + "..");
                    //TEST
                    break;
            }
            mEllipsNum += 1;
            if (mEllipsNum > 2) {
                mEllipsNum = 0;
            }
            mRecogHandler.sendEmptyMessageDelayed(1, 500);
        }
    }

    @Override
    public void onBitmapSaveSuccess(File file) {
        //        ToastUtils.showShort("裁剪成功:" + file.getAbsolutePath());

        //裁剪后替换掉返回数据的内容，但是不要改变全局中的选中数据
        mMediaItems.remove(0);
        MediaItem mediaItem = new MediaItem();
        mediaItem.path = file.getAbsolutePath();
        mediaItem.mimeType = "image/jpeg";
        mediaItem.size = file.length();
        mediaItem.addTime = file.lastModified();
        mediaItem.name = file.getName();
        mMediaItems.add(mediaItem);
        if (imagePicker.getStyle() == CropImageView.Style.FACE) {
            //开始识别人脸的动画
            mCropImageView.scanFace(true);
            mRecognizeBtn.setVisibility(View.GONE);
            mLoadingTv.setVisibility(View.GONE);
            mRecogTv.setVisibility(View.VISIBLE);
            mRecogHandler.sendEmptyMessage(1);
            //开始获取token，然后上传到羚羊云，然后将objid传到自己服务器，返回参数看情况

        } else {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, mMediaItems);
            setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
            finish();
        }

    }

    @Override
    public void onBitmapSaveError(File file) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCropImageView.setOnBitmapSaveCompleteListener(null);
        if (null != mBitmap && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mCropImageView.removeHandler();
        mRecogHandler.removeMessages(1);
        mRecogTv = null;
    }
}
