package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerDailyPersonDetailComponent;
import cloud.antelope.lingmou.di.module.DailyPersonDetailModule;
import cloud.antelope.lingmou.mvp.contract.DailyPersonDetailContract;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.presenter.DailyPersonDetailPresenter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class DailyPersonDetailActivity extends BaseActivity<DailyPersonDetailPresenter> implements DailyPersonDetailContract.View {

    @BindView(R.id.daily_person_back_ib)
    ImageButton mDailyPersonBackIb;
    @BindView(R.id.daily_person_head_iv)
    ImageView mDailyPersonHeadIv;
    @BindView(R.id.daily_person_name_tv)
    TextView mDailyPersonNameTv;
    @BindView(R.id.daily_person_phone_tv)
    TextView mDailyPersonPhoneTv;
    @BindView(R.id.detail_person_detail_nav_tv)
    TextView mDetailPersonDetailNavTv;
    @BindView(R.id.detail_person_idcard_tv)
    TextView mDetailPersonIdcardTv;
    @BindView(R.id.detail_person_phone_tv)
    TextView mDetailPersonPhoneTv;
    @BindView(R.id.detail_person_birth_tv)
    TextView mDetailPersonBirthTv;
    @BindView(R.id.detail_person_gender_tv)
    TextView mDetailPersonGenderTv;
    @BindView(R.id.detail_person_nation_tv)
    TextView mDetailPersonNationTv;
    @BindView(R.id.detail_remark_tv)
    TextView mDetailRemarkTv;

    private DailyPoliceAlarmEntity mPersonBean;

    private ImageLoader mImageLoader;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerDailyPersonDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .dailyPersonDetailModule(new DailyPersonDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_daily_person_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mTintManager.setStatusBarTintResource(R.color.yellow_ffb300);
        mImageLoader = ArmsUtils.obtainAppComponentFromContext(this).imageLoader();
        Serializable serializableExtra = getIntent().getSerializableExtra("person");
        if (null != serializableExtra) {
            mPersonBean = (DailyPoliceAlarmEntity) serializableExtra;
        }
        updatePerson(mPersonBean);
    }

    private void updatePerson(DailyPoliceAlarmEntity personBean) {
        if (null != personBean) {
            String imageUrl = personBean.getImageUrl();
            mImageLoader.loadImage(this, ImageConfigImpl
                    .builder()
                    .cacheStrategy(0)
                    .url(imageUrl)
                    .imageView(mDailyPersonHeadIv)
                    .build());
            DailyPoliceAlarmEntity.ObjectMainJsonBean person = mPersonBean.getObjectMainJson();
            if (null != person) {
                mDailyPersonPhoneTv.setText(person.getMobile());
                mDailyPersonPhoneTv.setText(person.getMobile());
                String name = person.getName();
                if (!TextUtils.isEmpty(name)) {
                    char xing = name.charAt(0);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 1; i < name.length(); i++) {
                        builder.append("*");
                    }
                    String nameNew = xing + builder.toString();
                    mDailyPersonNameTv.setText(nameNew);
                }
                mDetailPersonIdcardTv.setText(person.getIdentityNumber());
                mDetailPersonBirthTv.setText(person.getBirthday());
                mDetailPersonGenderTv.setText(person.getGender());
                mDetailPersonNationTv.setText(person.getNationality());
                mDetailRemarkTv.setText(personBean.getOperationDetail());
            }
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


    @OnClick(R.id.daily_person_back_ib)
    public void onViewClicked() {
        finish();
    }
}
