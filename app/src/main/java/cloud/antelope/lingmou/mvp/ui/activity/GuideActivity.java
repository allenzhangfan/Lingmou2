package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lingdanet.safeguard.common.utils.AppUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.viewpagerindicator.LinePageIndicator;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.ui.widget.DepthPageTransformer;

/**
 * @brief 程序第一次安装时的介绍页面
 */
public class GuideActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager mViewPager;
    // private LinePageIndicator mIndicator;
    // 保存图片ID
    private int[] imgIds = new int[]{R.drawable.guide_01, R.drawable.guide_02, R.drawable.guide_03, R.drawable.guide_04};
    // 保存图片视图
    private List<RelativeLayout> mImgView = new ArrayList<>();
    // 启动按钮
    private Button mStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.first_guide_layout);
        initView();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.guide_viewpager);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                RelativeLayout mContentView =
                        (RelativeLayout) LayoutInflater.from(GuideActivity.this)
                                .inflate(R.layout.guide_content_layout, null);
                ImageView imageView = (ImageView) mContentView.findViewById(R.id.guide_iv);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgIds[position]);
                imageView.setImageBitmap(bitmap);
                container.addView(mContentView);
                mImgView.add(mContentView);

                return mContentView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mImgView.get(position));
            }

            @Override
            public int getCount() {
                return imgIds.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }
        });

        // mIndicator = (LinePageIndicator) findViewById(R.id.guide_indicator);
        // mIndicator.setViewPager(mViewPager);
        // mIndicator.setOnPageChangeListener(this);

        mStartBtn = (Button) findViewById(R.id.app_start_btn);
        mStartBtn.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int arg) {
        if (arg == 3) {
            mStartBtn.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.fading_in);
            mStartBtn.startAnimation(anim);
        } else {
            mStartBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View v) {
        SPUtils.getInstance().put("firstIn" + AppUtils.getAppVersionName(), false);
        boolean hasLogin = SPUtils.getInstance().getBoolean(Constants.HAS_LOGIN, false);
        if (!hasLogin) {
            Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent();
            intent.setClass(GuideActivity.this, NewMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }

}
