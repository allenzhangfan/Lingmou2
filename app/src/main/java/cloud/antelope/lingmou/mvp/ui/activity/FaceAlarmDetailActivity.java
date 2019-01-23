package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.WaterMarkUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.app.utils.SoftKeyboardStateHelper;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.di.component.DaggerFaceAlarmDetailComponent;
import cloud.antelope.lingmou.di.module.FaceAlarmDetailModule;
import cloud.antelope.lingmou.mvp.contract.FaceAlarmDetailContract;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmBlackEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmDetailBean;
import cloud.antelope.lingmou.mvp.presenter.FaceAlarmDetailPresenter;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.widget.CircleProgressView;


import static com.jess.arms.utils.Preconditions.checkNotNull;
import static com.lingdanet.safeguard.common.utils.WaterMarkUtils.getTextLength;


public class FaceAlarmDetailActivity extends BaseActivity<FaceAlarmDetailPresenter> implements FaceAlarmDetailContract.View,
        SoftKeyboardStateHelper.SoftKeyboardStateListener {

    @BindView(R.id.bukong_iv)
    ImageView mBukongIv;
    @BindView(R.id.tag_iv)
    ImageView mTagIv;
    @BindView(R.id.time_tv)
    TextView mTimeTv;
    @BindView(R.id.address_tv)
    TextView mAddressTv;
    @BindView(R.id.name_tv)
    TextView mNameTv;
    @BindView(R.id.type_tv)
    TextView mTypeTv;
    @BindView(R.id.task_tv)
    TextView mTaskTv;
    @BindView(R.id.gender_tv)
    TextView mGenderTv;
    @BindView(R.id.nation_tv)
    TextView mNationTv;
    @BindView(R.id.birth_tv)
    TextView mBirthTv;
    @BindView(R.id.idcard_tv)
    TextView mIdcardTv;
    @BindView(R.id.deal_tv)
    EditText mDealEt;
    @BindView(R.id.dealed_tv)
    TextView mDealedTv;
    @BindView(R.id.invalid_btn)
    Button mInvalidBtn;
    @BindView(R.id.valid_btn)
    Button mValidBtn;
    @BindView(R.id.undo_ll)
    LinearLayout mUndoLl;
    @BindView(R.id.score_progress)
    CircleProgressView mScoreProgress;
    @BindView(R.id.bottom_ll)
    LinearLayout mBottomLl;
    @BindView(R.id.redundant_view)
    View mRedundantView;
    @BindView(R.id.root_view)
    LinearLayout mRootView;
    @BindView(R.id.scroll_view)
    ScrollView mScrollView;
    @BindView(R.id.head_left_iv)
    ImageButton mLeftBackIb;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.water_tag_iv)
    ImageView mWaterTagIv;
    @BindView(R.id.water_bk_iv)
    ImageView mWaterBkIv;

    private FaceAlarmBlackEntity mFaceAlarmDetailBean;
    private Activity mActivity;
    private int mPosition;
    private SoftKeyboardStateHelper mKeyboardChangeListener;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerFaceAlarmDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .faceAlarmDetailModule(new FaceAlarmDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_face_alarm_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mActivity = this;
        mTitleTv.setText("告警详情");
        mFaceAlarmDetailBean = (FaceAlarmBlackEntity) getIntent().getSerializableExtra("bean");
        mPosition = getIntent().getIntExtra("position", -1);
        mWaterBkIv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = mWaterBkIv.getWidth();
                int height = mWaterBkIv.getHeight();
                Bitmap markerBitmap = getMarkerBitmap(width, height);
                mWaterBkIv.setImageBitmap(markerBitmap);
                mWaterBkIv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mWaterTagIv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = mWaterTagIv.getWidth();
                int height = mWaterTagIv.getHeight();
                Bitmap markerBitmap = getMarkerBitmap(width, height);
                mWaterTagIv.setImageBitmap(markerBitmap);
                mWaterTagIv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        updateDetail(mFaceAlarmDetailBean);
        initListener();
    }

    private void updateDetail(FaceAlarmBlackEntity detail) {
        if (detail == null) {
            return;
        }
        GlideArms.with(mActivity).asBitmap().load(detail.getFacePath()).placeholder(R.drawable.placeholder_alarm_list).centerCrop().into(mBukongIv);
        GlideArms.with(mActivity).asBitmap().load(detail.getImageUrl()).placeholder(R.drawable.placeholder_alarm_list).centerCrop().into(mTagIv);
        FaceAlarmBlackEntity.ObjectMainJsonBean mainJson = detail.getObjectMainJson();
        if (null != mainJson) {
            mNameTv.setText(mainJson.getName() == null ? "----" : mainJson.getName());
            mGenderTv.setText(mainJson.getGender() == null ? "----" : mainJson.getGender());
            if (!TextUtils.isEmpty(mainJson.getNationality())) {
                mNationTv.setText(mainJson.getNationality());
            }
            String birthday = mainJson.getBirthday();
            if (!TextUtils.isEmpty(birthday)) {
                mBirthTv.setText(birthday);
            }
            String idcard = mainJson.getIdentityNumber();
            if (!TextUtils.isEmpty(idcard)) {
                mIdcardTv.setText(idcard);
            }
        }
        mScoreProgress.setScore((float) detail.getSimilarity());
        if (!TextUtils.isEmpty(detail.getLibName())) {
            mTypeTv.setText(detail.getLibName());
        }
        if (!TextUtils.isEmpty(detail.getTaskName())) {
            mTaskTv.setText(detail.getTaskName());
        }
        mAddressTv.setText(detail.getCameraName());
        mTimeTv.setText(TimeUtils.millis2String(Long.parseLong(detail.getCaptureTime())));
        int isEffective = detail.getIsEffective();
        int isHandle = detail.getIsHandle();
        if (1 == isHandle) {
            if (1 == isEffective) {
                //有效
                mUndoLl.setVisibility(View.GONE);
                mDealedTv.setVisibility(View.VISIBLE);
                mDealedTv.setBackgroundResource(R.color.valid_color);
                mDealedTv.setText("已处理：有效");
                mDealEt.setText(detail.getOperationDetail());
                mDealEt.setEnabled(false);
            } else {
                //无效
                mUndoLl.setVisibility(View.GONE);
                mDealedTv.setVisibility(View.VISIBLE);
                mDealedTv.setText("已处理：无效");
                mDealedTv.setBackgroundResource(R.color.about_text);
                mDealEt.setText(detail.getOperationDetail());
                mDealEt.setEnabled(false);
            }
        }else{
            mUndoLl.setVisibility(View.VISIBLE);
            mDealedTv.setVisibility(View.GONE);
            mDealEt.setHint(R.string.hint_input_content);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(View.FOCUS_UP);
                // mScrollView.smoothScrollTo(0,0);
            }
        }, 250);
    }
    public Bitmap getMarkerBitmap(int width, int height) {
        String waterText = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_LOGIN_NAME);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        TextPaint paint = new TextPaint();
        paint.setFakeBoldText(true);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        //让画出的图形是实心的
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#22FFFFFF"));  // 设置字体中间实体部分的颜色
        //设置字体大小
        paint.setTextSize(SizeUtils.sp2px(18));
        canvas.rotate(-45, width / 2, width / 2);
        float textLength = getTextLength(paint, waterText);
        float startPos;
        if (textLength < width) {
            startPos = (width - textLength) / 2;
        } else {
            startPos = 0;
        }
        canvas.drawText(waterText, startPos, height / 2, paint);
        //让画出的图形是空心的
        paint.setStyle(Paint.Style.STROKE);
        //设置画出的线的 粗细程度
        paint.setStrokeWidth(SizeUtils.sp2px(1.1f));
        paint.setColor(Color.parseColor("#1A000000")); // 设置字体镂空部分的颜色
        canvas.drawText(waterText, startPos, height / 2, paint);
        return bitmap;
    }

    private String getGender(String sex) {
        if ("1".equals(sex)) {
            return "男";
        } else if ("2".equals(sex)) {
            return "女";
        }
        return "--";
    }

    private void initListener() {
        mKeyboardChangeListener = new SoftKeyboardStateHelper(mRootView);
        mKeyboardChangeListener.addSoftKeyboardStateListener(this);
    }

    @OnClick({R.id.head_left_iv, R.id.invalid_btn, R.id.valid_btn, R.id.deal_tv, R.id.bukong_iv, R.id.tag_iv})
    public void onViewClicked(View view) {
        Intent intent = new Intent(mActivity, FaceDetailActivity.class);
        switch (view.getId()) {
            case R.id.head_left_iv:
                KeyboardUtils.hideSoftInput(mActivity, mDealEt);
                finish();
                break;
            case R.id.invalid_btn:
                mPresenter.dealFaceAlarm(mFaceAlarmDetailBean.getId(), 0, mDealEt.getText().toString());
                break;
            case R.id.valid_btn:
                mPresenter.dealFaceAlarm(mFaceAlarmDetailBean.getId(), 1, mDealEt.getText().toString());
                break;
            case R.id.deal_tv:
                //进入到新的页面编辑，或者查看所有文字

                break;
            case R.id.bukong_iv:
                intent.putExtra("faceUrl", mFaceAlarmDetailBean.getScenePath());
                intent.putExtra("point", mFaceAlarmDetailBean.getFaceRect());
                startActivity(intent);
                break;
            case R.id.tag_iv:
                intent.putExtra("faceUrl", mFaceAlarmDetailBean.getImageUrl());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void showLoading(String message) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        ToastUtils.showShort(message);
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
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        // ViewGroup.LayoutParams params = mRedundantView.getLayoutParams();
        // params.height = keyboardHeightInPx - mBottomLl.getMeasuredHeight();
        // mRedundantView.setLayoutParams(params);
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onSoftKeyboardClosed() {
        ViewGroup.LayoutParams params = mRedundantView.getLayoutParams();
        params.height = 1;
        mRedundantView.setLayoutParams(params);
    }

    @Override
    protected void onDestroy() {
        mKeyboardChangeListener.destroy();
        super.onDestroy();
    }

    @Override
    public void dealSuccess() {
        KeyboardUtils.hideSoftInput(mActivity, mDealEt);
        ToastUtils.showShort("处理成功");
        Intent intent = new Intent();
        intent.putExtra("position", mPosition);
        setResult(RESULT_OK, intent);
        finish();
    }
}
