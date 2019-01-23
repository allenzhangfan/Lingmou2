package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.google.gson.Gson;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.litepal.crud.DataSupport;
import org.simple.eventbus.Subscriber;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerBodyDepotComponent;
import cloud.antelope.lingmou.di.module.BodyDepotModule;
import cloud.antelope.lingmou.mvp.contract.BodyDepotContract;
import cloud.antelope.lingmou.mvp.model.entity.BodyDepotDetailEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.DailyFilterItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.DetailCommonEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.PictureCollectEvent;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.presenter.BodyDepotPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.BodyDepotAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.DailyFilterAdapter;
import cloud.antelope.lingmou.mvp.ui.fragment.CollectionListFragment;
import cloud.antelope.lingmou.mvp.ui.widget.LmDatePickDialog;
import cloud.antelope.lingmou.mvp.ui.widget.LmTimePickerDialog;
import cloud.antelope.lingmou.mvp.ui.widget.MenuView;
import cloud.antelope.lingmou.mvp.ui.widget.RightMenu;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class BodyDepotActivity extends BaseActivity<BodyDepotPresenter>
        implements BodyDepotContract.View,
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener,
        RadioGroup.OnCheckedChangeListener,
        View.OnClickListener {


    private static final int TYPE_SELECT_CAMERA = 0x01;
    @BindView(R.id.back_ib)
    ImageButton mBackIb;
    @BindView(R.id.face_title)
    TextView mFaceTitle;
    @BindView(R.id.title_rl)
    RelativeLayout mTitleRl;
    @BindView(R.id.face_depot_rclv)
    RecyclerView mFaceDepotRclv;
    @BindView(R.id.face_depot_srfl)
    SwipeRefreshLayout mFaceDepotSrfl;
    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;
    @BindView(R.id.empty_no_network_tv)
    TextView mEmptyNoNetworkTv;
    @BindView(R.id.no_permission_rl)
    RelativeLayout mNoPermissionRl;
    @BindView(R.id.filter_ll)
    LinearLayout mFilterLl;
    @BindView(R.id.filter_rclv)
    RecyclerView mFilterRclv;
    @BindView(R.id.filter_range_tv)
    TextView mFilterRangeTv;
    @BindView(R.id.filter_gender_tv)
    TextView mFilterGenderTv;
    @BindView(R.id.filter_address_tv)
    TextView mFilterAddressTv;
    //    @BindView(R.id.shaixuan_ll)
    RelativeLayout rlRight;
    @BindView(R.id.filter_tv)
    TextView mFilterTv;
    @BindView(R.id.range_rg)
    RadioGroup mRangeRg;
    @BindView(R.id.address_rg_three)
    RadioGroup mAddressRgThree;
    @BindView(R.id.address_rg_four)
    RadioGroup addressRgFour;
    @BindView(R.id.ll_filter)
    LinearLayout llFilter;
    @BindView(R.id.ll_address)
    LinearLayout llAddress;
    @BindView(R.id.ll_range)
    LinearLayout llEange;
    @BindView(R.id.view_divider)
    View viewDivider;
    @BindView(R.id.filter_ll1)
    LinearLayout filterLl1;
    RecyclerView rvUpperBodyTexture;
    RecyclerView rvLowerBodyCategory;
    RecyclerView rvBelongings;
    @BindView(R.id.scrollview)
    ScrollView scrollView;
    @BindView(R.id.tv_search)
    TextView tvSearch;

    @Inject
    @Named("gridlayout")
    RecyclerView.LayoutManager mGridLayoutManager;
    @Inject
    @Named("linearlayout")
    RecyclerView.LayoutManager mLinearLayoutManager;
    @Inject
    RecyclerView.ItemDecoration mItemDecoration;
    @Inject
    @Named("gridAnimator")
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    @Named("linearAnimator")
    RecyclerView.ItemAnimator mLinearItemAnimator;
    @Inject
    LoadMoreView mLoadMoreView;
    @Inject
    BodyDepotAdapter mBodyDepotAdapter;
    @Inject
    DailyFilterAdapter mDailyFilterAdapter;
    @BindView(R.id.ctl_search)
    CollapsingToolbarLayout ctlSearch;
    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;

    private RelativeLayout mPopRl;

    private TagFlowLayout mUpFlowLayout, mLowerFlowLayout;

    private TextView mConfirmTv;
    private TextView mCleanTv;
    private RadioGroup mAddressRgOne, mAddressRgTwo, mGenderRg, mHeadRgOne, mHeadRgTwo;
    private LinearLayout mStartTimeLl, mEndTimeLl;
    private TextView mStartTimeTv, mEndTimeTv;

    private LmDatePickDialog mStartDateDialog, mEndDateDialog;
    private LmTimePickerDialog mStartTimeDialog, mEndTimeDialog;
    private int mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute;
    private int mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute;


    private String mCameraType = ""; //默认null，是不限
    private String mTempCameraType = "";
    private Long mTempStartTimeStamp;
    private Long mTempEndTimeStamp;
    private String mSex = "";
    private String mTempSex = "";
    private Long mStartTimeStamp;
    private Long mEndTimeStamp;
    private String mUpColor = "";
    private String mLowerColor = "";
    private String mHeadFeature = "";
    private String mTempUpColor = "";
    private String mTempLowerColor = "";
    private String mTempHeadFeature = "";

    private List<String> mCameraTags;
    private List<String> noCameraTags = null;
    private List<String> mCameraIds;
    private List<String> mBodyTags;

    private String mMinId;
    private String mMinCaptureTime;
    private String mPageType = "0";
    private static int SIZE = 15;
    private Activity mActivity;

    private String[] mTags = new String[]{"全部", "白", "灰", "黑", "绿", "蓝", "红", "紫", "黄", "粉", "橙", "棕", "花色"};

    private int[] mTagColors = new int[]{R.color.white, R.color.body_color_gray, R.color.body_color_black,
            R.color.body_color_green, R.color.body_color_blue, R.color.body_color_red, R.color.body_color_zi, R.color.body_color_yellow,
            R.color.body_color_fen, R.color.body_color_cheng, R.color.body_color_zong};
    private LayoutInflater mLayoutInflater;
    private TagAdapter<String> mUpcolorTagAdapter;
    private TagAdapter<String> mLowercolorTagAdapter;
    private RadioGroup mAreaRg;
    private boolean mIsEnterEndTime;
    private static final long SEVEN_DAY_STAMP = 7 * 24 * 60 * 60 * 1000;

    private List<DailyFilterItemEntity> mRangeEntities;
    private List<DailyFilterItemEntity> mGenderEntities;
    private List<DailyFilterItemEntity> mAddressEntities;
    private List<DailyFilterItemEntity> mUpperEntities;
    private List<DailyFilterItemEntity> mLowerEntities;
    private List<DailyFilterItemEntity> mBelongingsEntities;
    private List<String> mUpperCodes;
    private List<String> mLowerCodes;
    private List<String> mBelongingsCodes;
    private int mPosition;

    private int mPreRangePosition;
    private int mPreGenderPosition;
    private int mPreAddressPosition;
    private int mTempPreRangePosition;
    private int mTempPreGenderPosition;
    private int mTempPreAddressPosition;

    private Drawable mFilterArrowDownYellowDrawable, mFilterArrowDownBlackDrawable,
            mFilterArrowUpYellowDrawable, mFilterArrowUpBlackDrawable,
            mFilterBlackDrawable, mFilterYellowDrawable;
    //    private Animation mTopOut;
//    private Animation mTopIn;
    private Animation mRightIn;
    private Animation mRightOut;
    private boolean mTimeChanged;
    private String mCameraIdsType = "";
    private String mTempCameraIdsType = "";
    List<OrgCameraEntity> mOrgCameraEntities;
    private MenuView menuView;
    private RightMenu rightMenu;
    private int preUpperPosition;
    private int preLowerPosition;
    private int preBelongingsPosition;
    private int setUpperPosition;
    private int setLowerPosition;
    private int setBelongingsPosition;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerBodyDepotComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .bodyDepotModule(new BodyDepotModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_body_depot; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mActivity = this;
        initFilterData();
        long cameraId = getIntent().getLongExtra("cameraId", -1);
        if (cameraId != -1) {
            mCameraIds = new ArrayList<>();
            mCameraIds.add(String.valueOf(cameraId));
        }
        mOrgCameraEntities = new ArrayList<>();
//        mTopOut = AnimationUtils.loadAnimation(getActivity(), R.anim.top_out);
//        mTopIn = AnimationUtils.loadAnimation(getActivity(), R.anim.top_in);
        mRightIn = AnimationUtils.loadAnimation(getActivity(), R.anim.right_in);
        mRightOut = AnimationUtils.loadAnimation(getActivity(), R.anim.right_out);
        mFilterArrowDownBlackDrawable = getResources().getDrawable(R.drawable.arrow_black_triangle_down);
        mFilterArrowUpBlackDrawable = getResources().getDrawable(R.drawable.arrow_black_triangle_up);
        mFilterArrowDownYellowDrawable = getResources().getDrawable(R.drawable.arrow_yellow_triangle_down);
        mFilterArrowUpYellowDrawable = getResources().getDrawable(R.drawable.arrow_yellow_triangle_up);
        mFilterBlackDrawable = getResources().getDrawable(R.drawable.filter_black_small);
        mFilterYellowDrawable = getResources().getDrawable(R.drawable.filter_orange_small);
        mFilterArrowDownBlackDrawable.setBounds(0, 0, mFilterArrowDownBlackDrawable.getMinimumWidth(), mFilterArrowDownBlackDrawable.getMinimumHeight());
        mFilterArrowUpBlackDrawable.setBounds(0, 0, mFilterArrowUpBlackDrawable.getMinimumWidth(), mFilterArrowUpBlackDrawable.getMinimumHeight());
        mFilterArrowDownYellowDrawable.setBounds(0, 0, mFilterArrowDownYellowDrawable.getMinimumWidth(), mFilterArrowDownYellowDrawable.getMinimumHeight());
        mFilterArrowUpYellowDrawable.setBounds(0, 0, mFilterArrowUpYellowDrawable.getMinimumWidth(), mFilterArrowUpYellowDrawable.getMinimumHeight());
        mFilterBlackDrawable.setBounds(0, 0, mFilterBlackDrawable.getMinimumWidth(), mFilterBlackDrawable.getMinimumHeight());
        mFilterYellowDrawable.setBounds(0, 0, mFilterYellowDrawable.getMinimumWidth(), mFilterYellowDrawable.getMinimumHeight());


        mLayoutInflater = LayoutInflater.from(this);
        mFaceTitle.setText(R.string.body_depot_title);
        if (getIntent() != null && getIntent().getBooleanExtra("fromDevice", false)) {
            mFaceTitle.setText(R.string.body_record);
            rlSearch.setVisibility(View.GONE);
        }


        mFaceDepotSrfl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mFaceDepotSrfl.setOnRefreshListener(this);
        mFaceDepotRclv.setHasFixedSize(true);
        mFaceDepotRclv.setLayoutManager(mGridLayoutManager);
        mFaceDepotRclv.addItemDecoration(mItemDecoration);
        mFaceDepotRclv.setItemAnimator(mItemAnimator);
        mBodyDepotAdapter.setOnLoadMoreListener(this, mFaceDepotRclv);
        mBodyDepotAdapter.setLoadMoreView(mLoadMoreView);
        mFaceDepotRclv.setAdapter(mBodyDepotAdapter);

        mFilterRclv.setLayoutManager(new GridLayoutManager(BodyDepotActivity.this, 3));
        mFilterRclv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.left = position % 3 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 3 == 2 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.top = position < 3 ? getResources().getDimensionPixelSize(R.dimen.dp16) : 0;
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.dp16);
            }
        });
        mFilterRclv.setAdapter(mDailyFilterAdapter);

        initUpAndLowerColor();
        mUpcolorTagAdapter.setSelectedList(0);
        mLowercolorTagAdapter.setSelectedList(0);

        mEndTimeStamp = System.currentTimeMillis();
        mTempEndTimeStamp = mEndTimeStamp;
        mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));

        mStartTimeStamp = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000 + 1000;
        mTempStartTimeStamp = mStartTimeStamp;
        mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));

        mStartDateDialog = new LmDatePickDialog(this, (view, year, month, dayOfMonth) -> {
            mStartYear = year;
            mStartMonth = month + 1;
            mStartDay = dayOfMonth;
            initStartTimeDialog();
        }, Calendar.getInstance(), mStartYear == 0 ? Calendar.getInstance().get(Calendar.YEAR) : mStartYear,
                mStartMonth == 0 ? Calendar.getInstance().get(Calendar.MONTH) : mStartMonth,
                mStartDay == 0 ? Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : mStartDay);
        mStartDateDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        mEndDateDialog = new LmDatePickDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mEndYear = year;
                mEndMonth = month + 1;
                mEndDay = dayOfMonth;
                initEndTimeDialog();
            }
        }, Calendar.getInstance(), mEndYear == 0 ? Calendar.getInstance().get(Calendar.YEAR) : mEndYear,
                mEndMonth == 0 ? Calendar.getInstance().get(Calendar.MONTH) : mEndMonth,
                mEndDay == 0 ? Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : mEndDay);
        mEndDateDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        mStartDateDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60));
        mEndDateDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60));
        mFaceDepotSrfl.post(() -> {
            mFaceDepotSrfl.setRefreshing(true);
            onRefresh();
        });
        mPresenter.getCollections();
        initListener();
    }

    private void initFilterData() {
        rlRight = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.popwindow_body_depot, null);
        mPopRl = rlRight.findViewById(R.id.pop_root);
        mUpFlowLayout = mPopRl.findViewById(R.id.up_color_layout);
        mLowerFlowLayout = mPopRl.findViewById(R.id.lower_color_layout);
        mConfirmTv = mPopRl.findViewById(R.id.cofirm_tv);
        mCleanTv = mPopRl.findViewById(R.id.clean_tv);
        mAddressRgOne = mPopRl.findViewById(R.id.address_rg_one);
        mAddressRgTwo = mPopRl.findViewById(R.id.address_rg_two);
        mStartTimeLl = mPopRl.findViewById(R.id.start_time_ll);
        mEndTimeLl = mPopRl.findViewById(R.id.end_time_ll);
        mStartTimeTv = mPopRl.findViewById(R.id.start_time_tv);
        mEndTimeTv = mPopRl.findViewById(R.id.end_time_tv);
        mGenderRg = mPopRl.findViewById(R.id.gender_rg);
        mHeadRgOne = mPopRl.findViewById(R.id.head_one_rg);
        mHeadRgTwo = mPopRl.findViewById(R.id.head_two_rg);
        mAreaRg = mPopRl.findViewById(R.id.area_rg);
        rvUpperBodyTexture = mPopRl.findViewById(R.id.rv_upper_body_texture);
        rvLowerBodyCategory = mPopRl.findViewById(R.id.rv_lower_body_category);
        rvBelongings = mPopRl.findViewById(R.id.rv_belongings);


        mRangeEntities = new ArrayList<>();
        mGenderEntities = new ArrayList<>();
        mAddressEntities = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            DailyFilterItemEntity entity = new DailyFilterItemEntity();
            if (i == 0) {
                entity.setSelect(true);
                entity.setName("全部");
            } else {
                entity.setSelect(false);
                entity.setName("我关注的");
            }
            mRangeEntities.add(entity);
        }

        for (int i = 0; i < 3; i++) {
            DailyFilterItemEntity entity = new DailyFilterItemEntity();
            if (i == 0) {
                entity.setSelect(true);
                entity.setName("全部");
            } else if (i == 1) {
                entity.setSelect(false);
                entity.setName("男");
            } else {
                entity.setSelect(false);
                entity.setName("女");
            }
            mGenderEntities.add(entity);
        }

        mAddressEntities.add(new DailyFilterItemEntity("不限", true));
        mAddressEntities.add(new DailyFilterItemEntity("社区", false));
        mAddressEntities.add(new DailyFilterItemEntity("网吧", false));
        mAddressEntities.add(new DailyFilterItemEntity("酒店", false));
        mAddressEntities.add(new DailyFilterItemEntity("餐厅", false));
        mAddressEntities.add(new DailyFilterItemEntity("商场", false));
        mAddressEntities.add(new DailyFilterItemEntity("重点区域", false));
        mAddressEntities.add(new DailyFilterItemEntity("重点商铺", false));
        mAddressEntities.add(new DailyFilterItemEntity("停车棚", false));
        mAddressEntities.add(new DailyFilterItemEntity("其他", false));

        mUpperEntities = new ArrayList<>();
        mUpperEntities.add(new DailyFilterItemEntity("全部", true));
        mUpperEntities.add(new DailyFilterItemEntity("格子", false));
        mUpperEntities.add(new DailyFilterItemEntity("花纹", false));
        mUpperEntities.add(new DailyFilterItemEntity("纯色", false));
        mUpperEntities.add(new DailyFilterItemEntity("条纹", false));
        mUpperCodes = Arrays.asList("", "112101", "112102", "112103", "112104");
        rvUpperBodyTexture.setLayoutManager(new GridLayoutManager(BodyDepotActivity.this, 3));
        rvUpperBodyTexture.setAdapter(new BaseQuickAdapter<DailyFilterItemEntity, BaseViewHolder>(R.layout.item_depot_filter, mUpperEntities) {
            @Override
            protected void convert(BaseViewHolder helper, DailyFilterItemEntity item) {
                TextView tv = helper.getView(R.id.tv);
                tv.setText(item.getName());
                tv.setSelected(item.isSelect());
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < getData().size(); i++) {
                            getData().get(i).setSelect(i == helper.getAdapterPosition());
                        }
                        notifyDataSetChanged();
                        preUpperPosition = helper.getPosition();
                    }
                });
            }
        });
        rvUpperBodyTexture.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.top = getResources().getDimensionPixelSize(R.dimen.dp18);
                outRect.left = position % 3 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 3 == 2 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
            }
        });
        rvUpperBodyTexture.setHasFixedSize(true);
        rvUpperBodyTexture.setNestedScrollingEnabled(false);
        mLowerEntities = new ArrayList<>();
        mLowerEntities.add(new DailyFilterItemEntity("全部", true));
        mLowerEntities.add(new DailyFilterItemEntity("短裤", false));
        mLowerEntities.add(new DailyFilterItemEntity("裙子", false));
        mLowerEntities.add(new DailyFilterItemEntity("长裤", false));
        mLowerCodes = Arrays.asList("", "112201", "112202", "112203");
        rvLowerBodyCategory.setLayoutManager(new GridLayoutManager(BodyDepotActivity.this, 3));
        rvLowerBodyCategory.setAdapter(new BaseQuickAdapter<DailyFilterItemEntity, BaseViewHolder>(R.layout.item_depot_filter, mLowerEntities) {
            @Override
            protected void convert(BaseViewHolder helper, DailyFilterItemEntity item) {
                TextView tv = helper.getView(R.id.tv);
                tv.setText(item.getName());
                tv.setSelected(item.isSelect());
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < getData().size(); i++) {
                            getData().get(i).setSelect(i == helper.getAdapterPosition());
                        }
                        notifyDataSetChanged();
                        preLowerPosition = helper.getPosition();
                    }
                });
            }
        });
        rvLowerBodyCategory.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.top = getResources().getDimensionPixelSize(R.dimen.dp18);
                outRect.left = position % 3 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 3 == 2 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
            }
        });
        rvLowerBodyCategory.setHasFixedSize(true);
        rvLowerBodyCategory.setNestedScrollingEnabled(false);
        mBelongingsEntities = new ArrayList<>();
        mBelongingsEntities.add(new DailyFilterItemEntity("全部", true));
        mBelongingsEntities.add(new DailyFilterItemEntity("双肩包", false));
        mBelongingsEntities.add(new DailyFilterItemEntity("手提包", false));
        mBelongingsEntities.add(new DailyFilterItemEntity("拎物品", false));
        mBelongingsEntities.add(new DailyFilterItemEntity("单肩包", false));
        mBelongingsEntities.add(new DailyFilterItemEntity("婴儿车", false));
        mBelongingsEntities.add(new DailyFilterItemEntity("行李箱", false));
        mBelongingsCodes = Arrays.asList("", "112301", "112302", "112303", "112304", "112305", "112306");
        rvBelongings.setLayoutManager(new GridLayoutManager(BodyDepotActivity.this, 3));
        rvBelongings.setAdapter(new BaseQuickAdapter<DailyFilterItemEntity, BaseViewHolder>(R.layout.item_depot_filter, mBelongingsEntities) {
            @Override
            protected void convert(BaseViewHolder helper, DailyFilterItemEntity item) {
                TextView tv = helper.getView(R.id.tv);
                tv.setText(item.getName());
                tv.setSelected(item.isSelect());
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < getData().size(); i++) {
                            getData().get(i).setSelect(i == helper.getAdapterPosition());
                        }
                        notifyDataSetChanged();
                        preBelongingsPosition = helper.getPosition();
                    }
                });
            }
        });
        rvBelongings.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.top = getResources().getDimensionPixelSize(R.dimen.dp18);
                outRect.left = position % 3 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 3 == 2 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
            }
        });
        rvBelongings.setHasFixedSize(true);
        rvBelongings.setNestedScrollingEnabled(false);
        if (getIntent() != null) {
            boolean fromDevice = getIntent().getBooleanExtra("fromDevice", false);
            if (fromDevice) {
                llAddress.setVisibility(View.GONE);
                llEange.setVisibility(View.GONE);
                viewDivider.setVisibility(View.GONE);
                llFilter.setVisibility(View.GONE);
                filterLl1.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressedSupport() {
        if (rightMenu != null && rightMenu.getStatus() == RightMenu.SHOW) {
            setPopDismiss();
        } else {
            super.onBackPressedSupport();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private void setPopDismiss() {
        rightMenu.dismiss();
    }

    private void initListener() {

        mRangeRg.setOnCheckedChangeListener(this);
        mAddressRgOne.setOnCheckedChangeListener(this);
        mAddressRgTwo.setOnCheckedChangeListener(this);
        mAddressRgThree.setOnCheckedChangeListener(this);
        addressRgFour.setOnCheckedChangeListener(this);
        mHeadRgOne.setOnCheckedChangeListener(this);
        mHeadRgTwo.setOnCheckedChangeListener(this);
        mGenderRg.setOnCheckedChangeListener(this);
        mAreaRg.setOnCheckedChangeListener(this);

        mConfirmTv.setOnClickListener(this);
        mCleanTv.setOnClickListener(this);
        mStartTimeLl.setOnClickListener(this);
        mEndTimeLl.setOnClickListener(this);
        mDailyFilterAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mPosition == 0) {
                    //证明当前是第一个筛选
                    mRangeEntities.get(mPreRangePosition).setSelect(false);
                    mPreRangePosition = position;
                    mRangeEntities.get(position).setSelect(true);
                    showArrowDown(0);
                    switch (position) {
                        case 0:
                            mCameraIdsType = "";
                            mCameraIds = null;
                            break;
                        case 1:
                            //mCameraIds = 收藏的所有摄像机
                            mCameraIdsType = "1";
                            if (null == mCameraIds) {
                                mCameraIds = new ArrayList<>();
                            }
                            mCameraIds.clear();
                            for (OrgCameraEntity entity : mOrgCameraEntities) {
                                mCameraIds.add(String.valueOf(entity.getManufacturerDeviceId()));
                            }
                            break;
                    }
                } else if (mPosition == 1) {
                    mGenderEntities.get(mPreGenderPosition).setSelect(false);
                    mPreGenderPosition = position;
                    mGenderEntities.get(position).setSelect(true);
                    showArrowDown(1);
                    switch (position) {
                        case 0:
                            mSex = "";
                            break;
                        case 1:
                            mSex = "100001";
                            break;
                        case 2:
                            mSex = "100002";
                            break;
                    }
                } else if (mPosition == 2) {
                    mAddressEntities.get(mPreAddressPosition).setSelect(false);
                    mPreAddressPosition = position;
                    mAddressEntities.get(position).setSelect(true);
                    showArrowDown(2);
                    switch (position) {
                        case 0:
                            mCameraType = "";
                            break;
                        case 1:
                            mCameraType = "102401";
                            break;
                        case 2:
                            mCameraType = "102402";
                            break;
                        case 3:
                            mCameraType = "102403";
                            break;
                        case 4:
                            mCameraType = "102404";
                            break;
                        case 5:
                            mCameraType = "102405";
                            break;
                        case 6:
                            mCameraType = "102406";
                            break;
                        case 7:
                            mCameraType = "102407";
                            break;
                        case 8:
                            mCameraType = "102408";
                            break;
                        case 9:
                            mCameraType = null;
                            break;
                    }
                }
                initNetParams();
                menuView.dismiss();
//                mShaixuanLl.setVisibility(View.GONE);
//                mShaixuanLl.startAnimation(mTopOut);
                mIsEnterEndTime = true;
                mFaceDepotSrfl.setRefreshing(true);
                onRefresh();
            }
        });

        mBodyDepotAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(mActivity, PictureDetailActivity.class);
                intent.putExtra("isFromBody", true);
                boolean hasPermission = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_HYJJ, false);
                if (hasPermission) {
                    intent.setClass(BodyDepotActivity.this, FaceDepotDetailActivity.class);
                    intent.putExtra("body_Entity", mBodyDepotAdapter.getData().get(position));
                    startActivity(intent);
                } else {
                    intent.putExtra("position", position);
                    List data = adapter.getData();
                    ArrayList changeData = getChangeData(data);
                    if (changeData != null) {
                        intent.putParcelableArrayListExtra("bean", changeData);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void initNetParams() {

        if (!TextUtils.isEmpty(mSex)
                || !TextUtils.isEmpty(mUpColor)
                || !TextUtils.isEmpty(mLowerColor)
                || !TextUtils.isEmpty(mHeadFeature)) {
            if (mBodyTags == null) {
                mBodyTags = new ArrayList<>();
            }
            mBodyTags.clear();
            if (!TextUtils.isEmpty(mSex)) {
                mBodyTags.add(mSex);
            }
            if (!TextUtils.isEmpty(mUpColor)) {
                mBodyTags.add(mUpColor);
            }
            if (!TextUtils.isEmpty(mLowerColor)) {
                mBodyTags.add(mLowerColor);
            }
            if (!TextUtils.isEmpty(mHeadFeature)) {
                mBodyTags.add(mHeadFeature);
            }
        } else {
            mBodyTags = null;
        }

        if (setUpperPosition != 0 || setLowerPosition != 0 || setBelongingsPosition != 0) {
            if (mBodyTags == null) {
                mBodyTags = new ArrayList<>();
            }
            if (!TextUtils.isEmpty(mUpperCodes.get(setUpperPosition))) {
                mBodyTags.add(mUpperCodes.get(setUpperPosition));
            }
            if (!TextUtils.isEmpty(mLowerCodes.get(setLowerPosition))) {
                mBodyTags.add(mLowerCodes.get(setLowerPosition));
            }
            if (!TextUtils.isEmpty(mBelongingsCodes.get(setBelongingsPosition))) {
                mBodyTags.add(mBelongingsCodes.get(setBelongingsPosition));
            }
        }

        if (mCameraType == null) {
            mCameraTags = null;
            noCameraTags = Arrays.asList("102401", "102402", "102403", "102404", "102405", "102406", "102407", "102408");
        } else if (!TextUtils.isEmpty(mCameraType)) {
            if (null == mCameraTags) {
                mCameraTags = new ArrayList<>();
            }
            mCameraTags.clear();
            mCameraTags.add(mCameraType);
            noCameraTags = null;
        } else {
            mCameraTags = null;
            noCameraTags = null;
        }


    }

    private void showArrowDown(int typePosition) {
        switch (typePosition) {
            case 0:
                //range
                if (mPreRangePosition == 0) {
                    //第一个，全部，则颜色变回黑色
                    mFilterRangeTv.setText(R.string.range);
                    mFilterRangeTv.setTextColor(getResources().getColor(R.color.gray_212121));
                    mFilterRangeTv.setCompoundDrawables(null, null, mFilterArrowDownBlackDrawable, null);
                } else {
                    mFilterRangeTv.setText(mRangeEntities.get(mPreRangePosition).getName());
                    mFilterRangeTv.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                    mFilterRangeTv.setCompoundDrawables(null, null, mFilterArrowDownYellowDrawable, null);
                }
                break;
            case 1:
                //type
                if (mPreGenderPosition == 0) {
                    //第一个，全部，则颜色变回黑色
                    mFilterGenderTv.setText(R.string.gender);
                    mFilterGenderTv.setTextColor(getResources().getColor(R.color.gray_212121));
                    mFilterGenderTv.setCompoundDrawables(null, null, mFilterArrowDownBlackDrawable, null);
                } else {
                    mFilterGenderTv.setText(mGenderEntities.get(mPreGenderPosition).getName());
                    mFilterGenderTv.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                    mFilterGenderTv.setCompoundDrawables(null, null, mFilterArrowDownYellowDrawable, null);
                }
                break;
            case 2:
                //state
                if (mPreAddressPosition == 0) {
                    //第一个，全部，则颜色变回黑色
                    mFilterAddressTv.setText(R.string.alarm_address);
                    mFilterAddressTv.setTextColor(getResources().getColor(R.color.gray_212121));
                    mFilterAddressTv.setCompoundDrawables(null, null, mFilterArrowDownBlackDrawable, null);
                } else {
                    mFilterAddressTv.setText(mAddressEntities.get(mPreAddressPosition).getName());
                    mFilterAddressTv.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                    mFilterAddressTv.setCompoundDrawables(null, null, mFilterArrowDownYellowDrawable, null);
                }
                break;
        }
    }

    private ArrayList<DetailCommonEntity> getChangeData(List<BodyDepotDetailEntity> entities) {
        if (null != entities) {
            ArrayList<DetailCommonEntity> list = new ArrayList<>();
            for (BodyDepotDetailEntity entity : entities) {
                DetailCommonEntity commonEntity = new DetailCommonEntity();
                commonEntity.deviceName = entity.cameraName;
                commonEntity.endTime = Long.parseLong(entity.captureTime);
                commonEntity.point = entity.bodyRect;
                commonEntity.srcImg = entity.scenePath;
                commonEntity.collectId = entity.collectId;
                commonEntity.id = entity.id;
                commonEntity.favoritesType = CollectionListFragment.BODY;
                commonEntity.cameraId = entity.cameraId;
                commonEntity.cameraName = entity.cameraName;
                commonEntity.faceImg = entity.bodyPath;
                commonEntity.captureTime = Long.parseLong(entity.captureTime);
                commonEntity.collected = "1".equals(entity.isCollect);
                list.add(commonEntity);
            }
            return list;
        }
        return null;
    }


    private void initStartTimeDialog() {
        mStartTimeDialog = new LmTimePickerDialog(this, 0, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mStartHour = hourOfDay;
                mStartMinute = minute;
                Long startTime = TimeUtils.string2Millis(mStartYear + "-" + mStartMonth + "-" + mStartDay + " " + mStartHour + ":" + mStartMinute, new SimpleDateFormat("yyyy-MM-dd HH:mm"));
                String timeStr = TimeUtils.millis2String(startTime, "yyyy.MM.dd HH:mm");
//                if (mTempEndTimeStamp != null && (mTempEndTimeStamp - startTime > SEVEN_DAY_STAMP)) {
//                    ToastUtils.showShort(R.string.hint_not_more_seven_days);
//                } else {
                mTempStartTimeStamp = startTime;
                mStartTimeTv.setText(timeStr);
                mStartDateDialog.dismiss();
//                }
            }
        }, mStartHour == 0 ? Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : mStartHour,
                mStartMinute == 0 ? Calendar.getInstance().get(Calendar.MINUTE) : mStartMinute, true);
        mStartTimeDialog.show();
    }

    private void initEndTimeDialog() {
        mEndTimeDialog = new LmTimePickerDialog(this, 0, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mEndHour = hourOfDay;
                mEndMinute = minute;
                Long endTime = TimeUtils.string2Millis(mEndYear + "-" + mEndMonth + "-" + mEndDay + " " + mEndHour + ":" + mEndMinute, new SimpleDateFormat("yyyy-MM-dd HH:mm"));
                String timeStr = TimeUtils.millis2String(endTime, "yyyy.MM.dd HH:mm");
                if (mTempStartTimeStamp != null && (endTime - mTempStartTimeStamp > SEVEN_DAY_STAMP)) {
                    ToastUtils.showShort(R.string.hint_not_more_seven_days);
                } else {
                    mTempEndTimeStamp = endTime;
                    mEndTimeTv.setText(timeStr);
                    mEndDateDialog.dismiss();
                }
            }
        }, mEndHour == 0 ? Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : mEndHour,
                mEndMinute == 0 ? Calendar.getInstance().get(Calendar.MINUTE) : mEndMinute, true);
        mEndTimeDialog.show();
    }

    private void getBodyDepotData() {
        mPresenter.getBodyDepot(SIZE, mStartTimeStamp, mEndTimeStamp, mCameraTags, noCameraTags, mBodyTags, mCameraIds, mMinId, mMinCaptureTime, mPageType);
    }

    private void initUpAndLowerColor() {
        mUpcolorTagAdapter = new TagAdapter(mTags) {
            @Override
            public View getView(FlowLayout parent, int position, Object o) {
                if (position == 0 || position == 12) {
                    return createTextView(position);
                } else {
                    return createImageView(position);
                }
            }

            @Override
            public void onSelected(int position, View view) {
                // super.onSelected(position, view);
                if (position == 1) {
                    ((ImageView) view).setImageResource(R.drawable.body_depot_blue_checked);
                } else if (position > 1 && position < 12) {
                    ((ImageView) view).setImageResource(R.drawable.body_depot_white_checked);
                }
                selectUpColor(position);
            }

            @Override
            public void unSelected(int position, View view) {
                // super.unSelected(position, view);
                if (position > 0 && position < 12) {
                    ((ImageView) view).setImageResource(0);
                }
            }
        };
        mUpFlowLayout.setAdapter(mUpcolorTagAdapter);

        mLowercolorTagAdapter = new TagAdapter(mTags) {
            @Override
            public View getView(FlowLayout parent, int position, Object o) {
                if (position == 0 || position == 12) {
                    return createTextView(position);
                } else {
                    return createImageView(position);
                }
            }

            @Override
            public void onSelected(int position, View view) {
                // super.onSelected(position, view);
                if (position == 1) {
                    ((ImageView) view).setImageResource(R.drawable.body_depot_blue_checked);
                } else if (position > 1 && position < 12) {
                    ((ImageView) view).setImageResource(R.drawable.body_depot_white_checked);
                }
                selectLowerColor(position);
            }

            @Override
            public void unSelected(int position, View view) {
                // super.unSelected(position, view);
                if (position > 0 && position < 12) {
                    ((ImageView) view).setImageResource(0);
                }
            }
        };
        mLowerFlowLayout.setAdapter(mLowercolorTagAdapter);


        mUpFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    if (i != position) {
                        parent.getChildAt(i).setEnabled(true);
                    } else {
                        parent.getChildAt(i).setEnabled(false);
                    }
                }
                return true;
            }

        });

        mLowerFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    if (i != position) {
                        parent.getChildAt(i).setEnabled(true);
                    } else {
                        parent.getChildAt(i).setEnabled(false);
                    }
                }
                return true;
            }

        });
    }

    private void selectUpColor(int position) {
        switch (position) {
            case 0:
                mTempUpColor = "";
                break;
            case 1:
                mTempUpColor = "112401";
                break;
            case 2:
                mTempUpColor = "112402";
                break;
            case 3:
                mTempUpColor = "112403";
                break;
            case 4:
                mTempUpColor = "112404";
                break;
            case 5:
                mTempUpColor = "112405";
                break;
            case 6:
                mTempUpColor = "112406";
                break;
            case 7:
                mTempUpColor = "112407";
                break;
            case 8:
                mTempUpColor = "112408";
                break;
            case 9:
                mTempUpColor = "112409";
                break;
            case 10:
                mTempUpColor = "112410";
                break;
            case 11:
                mTempUpColor = "112411";
                break;
            case 12:
                mTempUpColor = "112412";
                break;
        }
    }

    private void selectLowerColor(int position) {
        switch (position) {
            case 0:
                mTempLowerColor = "";
                break;
            case 1:
                mTempLowerColor = "112501";
                break;
            case 2:
                mTempLowerColor = "112502";
                break;
            case 3:
                mTempLowerColor = "112503";
                break;
            case 4:
                mTempLowerColor = "112504";
                break;
            case 5:
                mTempLowerColor = "112505";
                break;
            case 6:
                mTempLowerColor = "112506";
                break;
            case 7:
                mTempLowerColor = "112507";
                break;
            case 8:
                mTempLowerColor = "112508";
                break;
            case 9:
                mTempLowerColor = "112509";
                break;
            case 10:
                mTempLowerColor = "112510";
                break;
            case 11:
                mTempLowerColor = "112511";
                break;
            case 12:
                mTempLowerColor = "112512";
                break;
        }
    }

    private TextView createTextView(int position) {
        TextView textView = (TextView) mLayoutInflater.inflate(R.layout.body_depot_textview, null);
        switch (position) {
            case 0:
                textView.setText("全部");
                break;
            case 12:
                textView.setText("花色");
                break;
        }
        return textView;
    }

    private ImageView createImageView(int position) {
        ImageView imageView = (ImageView) mLayoutInflater.inflate(R.layout.body_depot_imageview, null);
        GradientDrawable gradientDrawable = (GradientDrawable) imageView.getBackground();
        if (position == 1) {
            gradientDrawable.setStroke(SizeUtils.dp2px(1), getResources().getColor(R.color.mine_divider_line_color));
        } else {
            gradientDrawable.setStroke(SizeUtils.dp2px(1), getResources().getColor(android.R.color.transparent));
        }
        gradientDrawable.setColor(getResources().getColor(mTagColors[position - 1]));
        return imageView;
    }

    private void showArrowUp(int typePosition) {
        switch (typePosition) {
            case 0:
                //range
                if (mPreRangePosition == 0) {
                    mFilterRangeTv.setCompoundDrawables(null, null, mFilterArrowUpBlackDrawable, null);
                } else {
                    mFilterRangeTv.setCompoundDrawables(null, null, mFilterArrowUpYellowDrawable, null);
                }
                break;
            case 1:
                //type
                if (mPreGenderPosition == 0) {
                    mFilterGenderTv.setCompoundDrawables(null, null, mFilterArrowUpBlackDrawable, null);
                } else {
                    mFilterGenderTv.setCompoundDrawables(null, null, mFilterArrowUpYellowDrawable, null);
                }
                break;
            case 2:
                //state
                if (mPreAddressPosition == 0) {
                    mFilterAddressTv.setCompoundDrawables(null, null, mFilterArrowUpBlackDrawable, null);
                } else {
                    mFilterAddressTv.setCompoundDrawables(null, null, mFilterArrowUpYellowDrawable, null);
                }
                break;
        }
    }

    @OnClick({R.id.back_ib, R.id.filter_range_ll, R.id.filter_gender_ll, R.id.filter_address_ll,
            R.id.filter_ll, R.id.filter_ll1, R.id.tv_search})
    public void onViewClicked(View view) {
        boolean rightOpen = false;
        if (rightMenu != null && rightMenu.getStatus() == RightMenu.SHOW) {
            rightOpen = true;
        }
        int filterVisibility = mPopRl.getVisibility();
        switch (view.getId()) {
            case R.id.back_ib:
                if (rightOpen) {
                    setPopDismiss();
                } else {
                    finish();
                }
                break;

            case R.id.filter_range_ll:
//                mPopRl.setVisibility(View.GONE);
                mFilterRclv.setVisibility(View.VISIBLE);
                mDailyFilterAdapter.setNewData(mRangeEntities);
                menuView = new MenuView(getActivity(), mFilterRclv, getViewHeight(mRangeEntities));
                menuView.show(llFilter);
                menuView.setOnDismissListener(new MenuView.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        showArrowDown(mPosition);
                    }
                });
                if (rightOpen) {
                    if (mPosition != 0) {
                        showArrowUp(0);
                        showArrowDown(mPosition);
                    } else {
                        showArrowDown(0);
                    }
                } else {
                    showArrowUp(0);
                }
                mPosition = 0;

                /*if (visibility == View.VISIBLE) {
                    mPopRl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    if (mPosition != 0) {
                        showArrowUp(0);
                        mDailyFilterAdapter.setNewData(mRangeEntities);
                        showArrowDown(mPosition);

                    } else {
                        mShaixuanLl.startAnimation(mTopOut);
                        mShaixuanLl.setVisibility(View.GONE);
                        showArrowDown(0);
                    }
                } else {
                    mPopRl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    mDailyFilterAdapter.setNewData(mRangeEntities);
                    mShaixuanLl.setVisibility(View.VISIBLE);
                    mShaixuanLl.startAnimation(mTopIn);
                    showArrowUp(0);
                }
                mPosition = 0;*/
                break;
            case R.id.filter_gender_ll:
//                mPopRl.setVisibility(View.GONE);
                mFilterRclv.setVisibility(View.VISIBLE);
                mDailyFilterAdapter.setNewData(mGenderEntities);
                menuView = new MenuView(getActivity(), mFilterRclv, getViewHeight(mGenderEntities));
                menuView.show(llFilter);
                menuView.setOnDismissListener(new MenuView.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        showArrowDown(mPosition);
                    }
                });
                if (rightOpen) {
                    if (mPosition != 1) {
                        showArrowUp(1);
                        showArrowDown(mPosition);
                    } else {
                        showArrowDown(1);
                    }
                } else {
                    showArrowUp(1);
                }
                mPosition = 1;

                /*if (visibility == View.VISIBLE) {
                    mPopRl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    if (mPosition != 1) {
                        showArrowUp(1);
                        mDailyFilterAdapter.setNewData(mGenderEntities);
                        showArrowDown(mPosition);
                    } else {
                        mShaixuanLl.startAnimation(mTopOut);
                        mShaixuanLl.setVisibility(View.GONE);
                        showArrowDown(1);
                    }
                } else {
                    mPopRl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    mDailyFilterAdapter.setNewData(mGenderEntities);
                    mShaixuanLl.setVisibility(View.VISIBLE);
                    mShaixuanLl.startAnimation(mTopIn);
                    showArrowUp(1);
                }
                mPosition = 1;*/
                break;
            case R.id.filter_address_ll:
//                mPopRl.setVisibility(View.GONE);
                mFilterRclv.setVisibility(View.VISIBLE);
                mDailyFilterAdapter.setNewData(mAddressEntities);
                menuView = new MenuView(getActivity(), mFilterRclv, getViewHeight(mAddressEntities));
                menuView.show(llFilter);
                menuView.setOnDismissListener(new MenuView.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        showArrowDown(mPosition);
                    }
                });
                if (rightOpen) {
                    if (mPosition != 2) {
                        showArrowUp(2);
                        showArrowDown(mPosition);
                    } else {
                        showArrowDown(2);
                    }
                } else {
                    showArrowUp(2);
                }
                mPosition = 2;

                /*if (visibility == View.VISIBLE) {
                    mPopRl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    if (mPosition != 2) {
                        showArrowUp(2);
                        mDailyFilterAdapter.setNewData(mAddressEntities);
                        showArrowDown(mPosition);
                    } else {
                        mShaixuanLl.startAnimation(mTopOut);
                        mShaixuanLl.setVisibility(View.GONE);
                        showArrowDown(2);
                    }
                } else {
                    mPopRl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    mDailyFilterAdapter.setNewData(mAddressEntities);
                    mShaixuanLl.startAnimation(mTopIn);
                    mShaixuanLl.setVisibility(View.VISIBLE);
                    showArrowUp(2);
                }
                mPosition = 2;*/
                break;
            case R.id.filter_ll1:
            case R.id.filter_ll:
                //显示过滤条件popWindow
                if (rightOpen) {
                    if (filterVisibility == View.VISIBLE) {
                        rightMenu.dismiss();
                        rightMenu.setOnDismissListener(new RightMenu.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                mPopRl.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        mFilterRclv.setVisibility(View.GONE);
                        restoreData();
                        mPopRl.setVisibility(View.VISIBLE);
                        showArrowDown(mPosition);
                    }
                } else {
                    showRight();

                }
                mPosition = 3;
                break;
            case R.id.tv_search:
                if (rightOpen) {
                    setPopDismiss();
                }
                //进入搜索界面
                ActivityOptions activityOptions = null;
                Intent intent = new Intent(BodyDepotActivity.this, SearchCameraActivity.class);
                intent.putExtra("fromDepot", true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation(BodyDepotActivity.this, tvSearch, "searchText");
                    startActivityForResult(intent, TYPE_SELECT_CAMERA, activityOptions.toBundle());
                } else {
                    startActivityForResult(intent, TYPE_SELECT_CAMERA);
                }
                break;
        }
    }

    private void showRight() {
        if (rightMenu == null) {
            rightMenu = new RightMenu(BodyDepotActivity.this, rlRight);
            mFilterRclv.setVisibility(View.GONE);
            restoreData();
            mPopRl.setVisibility(View.VISIBLE);

        }
        scrollView.scrollTo(0, 0);
        rightMenu.show(llFilter);
    }

    private int getViewHeight(List list) {
        return list.size() * getResources().getDimensionPixelSize(R.dimen.dp40) + list.size() - 1;
    }

    /**
     * 恢复数据到取消前
     */
    private void restoreData() {
        if (mStartTimeStamp != null) {
            //开始时间不为空，则表示已选择，恢复到取消前
            String startTimeStamp = TimeUtils.millis2String(mStartTimeStamp, "yyyy-MM-dd");
            String startTimeStr = TimeUtils.millis2String(mStartTimeStamp, "yyyy-MM-dd HH:mm");
            if (!TextUtils.isEmpty(startTimeStamp)) {
                String[] split = startTimeStamp.split("-");
                mStartDateDialog.getDatePicker().updateDate(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]));
            }
            mStartTimeTv.setText(startTimeStr);
        } else {
            mStartTimeTv.setText("请选择开始时间");
        }
        if (mEndTimeStamp != null) {
            //开始时间不为空，则表示已选择，恢复到取消前
            if (!mIsEnterEndTime) {
                mEndTimeStamp = System.currentTimeMillis();
                mTempEndTimeStamp = mEndTimeStamp;
                mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));
            }
            String endTimeStamp = TimeUtils.millis2String(mEndTimeStamp, "yyyy-MM-dd");
            String endTimeStr = TimeUtils.millis2String(mEndTimeStamp, "yyyy-MM-dd HH:mm");
            if (!TextUtils.isEmpty(endTimeStamp)) {
                String[] split = endTimeStamp.split("-");
                mEndDateDialog.getDatePicker().updateDate(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]));
            }
            mEndTimeTv.setText(endTimeStr);
        } else {
            mEndTimeTv.setText("请选择结束时间");
        }
        if (mCameraType == null) {
            setRadioButtonChecked(3, R.id.address_other_rb);
        } else if (TextUtils.isEmpty(mCameraType)) {
            setRadioButtonChecked(0, R.id.address_none_rb);
        } else {
            switch (mCameraType) {
                case "102401":
                    setRadioButtonChecked(0, R.id.address_shequ_rb);
                    break;
                case "102402":
                    setRadioButtonChecked(0, R.id.address_wangba_rb);
                    break;
                case "102403":
                    setRadioButtonChecked(1, R.id.address_hotel_rb);
                    break;
                case "102404":
                    setRadioButtonChecked(1, R.id.address_canting_rb);
                    break;
                case "102405":
                    setRadioButtonChecked(1, R.id.address_shop_rb);
                    break;
                case "102406":
                    setRadioButtonChecked(2, R.id.address_key_area_rb);
                    break;
                case "102407":
                    setRadioButtonChecked(2, R.id.address_key_shop_rb);
                    break;
                case "102408":
                    setRadioButtonChecked(2, R.id.address_parking_rb);
                    break;
            }
        }

        switch (mHeadFeature) {
            case "":
                setHeadRadioButtonChecked(0, R.id.head_none_rb);
                break;
            case "112001":
                setHeadRadioButtonChecked(0, R.id.head_glass_rb);
                break;
            case "112002":
                setHeadRadioButtonChecked(0, R.id.head_cap_rb);
                break;
            case "112003":
                setHeadRadioButtonChecked(1, R.id.head_casque_rb);
                break;
            case "112004":
                setHeadRadioButtonChecked(1, R.id.head_mouth_mask_rb);
                break;
        }

        switch (mSex) {
            case "":
                mGenderRg.check(R.id.gender_none_rb);
                break;
            case "100001":
                mGenderRg.check(R.id.gender_male_rb);
                break;
            case "100002":
                mGenderRg.check(R.id.gender_female_rb);
                break;
        }

        switch (mCameraIdsType) {
            case "":
                mRangeRg.check(R.id.range_all_rb);
                break;
            case "1":
                mRangeRg.check(R.id.range_fav_rb);
                break;
        }

        for (int i = 0; i < ((BaseQuickAdapter) rvUpperBodyTexture.getAdapter()).getData().size(); i++) {
            ((DailyFilterItemEntity) ((BaseQuickAdapter) rvUpperBodyTexture.getAdapter()).getData().get(i)).setSelect(i == setUpperPosition);
        }
        for (int i = 0; i < ((BaseQuickAdapter) rvLowerBodyCategory.getAdapter()).getData().size(); i++) {
            ((DailyFilterItemEntity) ((BaseQuickAdapter) rvLowerBodyCategory.getAdapter()).getData().get(i)).setSelect(i == setLowerPosition);
        }
        for (int i = 0; i < ((BaseQuickAdapter) rvBelongings.getAdapter()).getData().size(); i++) {
            ((DailyFilterItemEntity) ((BaseQuickAdapter) rvBelongings.getAdapter()).getData().get(i)).setSelect(i == setBelongingsPosition);
        }
        rvUpperBodyTexture.getAdapter().notifyDataSetChanged();
        rvLowerBodyCategory.getAdapter().notifyDataSetChanged();
        rvBelongings.getAdapter().notifyDataSetChanged();

        mUpcolorTagAdapter.setSelectedList(getSelectUpColor(mUpColor));
        mLowercolorTagAdapter.setSelectedList(getSelectLowerColor(mLowerColor));

    }

    private int getSelectUpColor(String tempLowerColor) {
        switch (tempLowerColor) {
            case "":
                return 0;
            case "112401":
                return 1;
            case "112402":
                return 2;
            case "112403":
                return 3;
            case "112404":
                return 4;
            case "112405":
                return 5;
            case "112406":
                return 6;
            case "112407":
                return 7;
            case "112408":
                return 8;
            case "112409":
                return 9;
            case "112410":
                return 10;
            case "112411":
                return 11;
            case "112412":
                return 12;
        }
        return 0;
    }

    private int getSelectLowerColor(String tempLowerColor) {
        switch (tempLowerColor) {
            case "":
                return 0;
            case "112501":
                return 1;
            case "112502":
                return 2;
            case "112503":
                return 3;
            case "112504":
                return 4;
            case "112505":
                return 5;
            case "112506":
                return 6;
            case "112507":
                return 7;
            case "112508":
                return 8;
            case "112509":
                return 9;
            case "112510":
                return 10;
            case "112511":
                return 11;
            case "112512":
                return 12;
        }
        return 0;
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
        if (null != mFaceDepotSrfl && mFaceDepotSrfl.isRefreshing()) {
            mFaceDepotSrfl.setRefreshing(false);
        }
        if (getString(R.string.network_disconnect).equals(message)) {
            if ("0".equals(mPageType)) {
                if (mBodyDepotAdapter.getData().isEmpty()) {
                    showNoNetworkView();
                }
                mBodyDepotAdapter.setEnableLoadMore(false);
                mFaceDepotSrfl.setEnabled(true);
            } else {
                mFaceDepotSrfl.setEnabled(true);
                mBodyDepotAdapter.setEnableLoadMore(true);
                mBodyDepotAdapter.loadMoreFail();
            }
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
    public void getBodyDepotSuccess(BodyDepotEntity bodyDepotEntity) {
        mFaceDepotSrfl.setRefreshing(false);
        mBodyDepotAdapter.setEnableLoadMore(true);
        List<BodyDepotDetailEntity> list = bodyDepotEntity.body;
        if (null != list) {
            //            mFaceDepotAdapter.setSelectText(mSearchEt.getText().toString());
            if ("0".equals(mPageType)) {
                if (list.size() == 0) {
                    showNoDataView();
                } else {
                    showDataView();
                }
                mBodyDepotAdapter.setNewData(list);
                if (list.size() < SIZE) {
                    mBodyDepotAdapter.disableLoadMoreIfNotFullPage(mFaceDepotRclv);
                }
                if (list.size() != 0) {
                    mFaceDepotRclv.scrollToPosition(0);
                }

            } else {
                mFaceDepotSrfl.setEnabled(true);
                mBodyDepotAdapter.addData(list);
                if (list.size() < SIZE) {
                    mBodyDepotAdapter.loadMoreEnd(false);
                } else {
                    mBodyDepotAdapter.loadMoreComplete();
                }
            }
            if (list.size() > 0) {
                BodyDepotDetailEntity minEntity = list.get(list.size() - 1);
                mMinId = minEntity.id;
                mMinCaptureTime = minEntity.captureTime;
            }
        } else {
            if ("0".equals(mPageType)) {
                showNoDataView();
            }
        }
    }

    @Override
    public void getBodyDepotError() {
        mFaceDepotSrfl.setRefreshing(false);
        if ("0".equals(mPageType)) {
            if (mBodyDepotAdapter.getData().isEmpty()) {
                showNoNetworkView();
            }
            mBodyDepotAdapter.setEnableLoadMore(false);
            mFaceDepotSrfl.setEnabled(true);
        } else {
            mFaceDepotSrfl.setEnabled(true);
            mBodyDepotAdapter.loadMoreFail();
            mBodyDepotAdapter.setEnableLoadMore(true);
        }
    }

    @Override
    public void getNoPermission() {
        mFaceDepotSrfl.setRefreshing(false);
        mFaceDepotSrfl.setEnabled(true);
        showNoPermissonView();
    }

    @Override
    public void getCollectionsSuccess(GetKeyStoreBaseEntity organizationEntity) {
        if (null != organizationEntity) {
            GetKeyStoreEntity userKvStroe = organizationEntity.getUserKvStroe();
            if (null != userKvStroe) {
                String storeValue = userKvStroe.getStoreValue();
                if (!TextUtils.isEmpty(storeValue)) {
                    Gson gson = new Gson();
                    SetKeyStoreRequest keyStore = gson.fromJson(storeValue, SetKeyStoreRequest.class);
                    List<String> storeSets = keyStore.getSets();
                    List<CollectCameraEntity> collectCameraList = new ArrayList<>();
                    for (String str : storeSets) {
                        CollectCameraEntity entity = new CollectCameraEntity();
                        String[] group_one = str.split(":");
                        entity.setGroup(group_one[0]);
                        String[] group_two = group_one[1].split("/");
                        entity.setCid(group_two[0]);
                        entity.setCameraName(group_two[1]);
                        collectCameraList.add(entity);
                    }
                    int size = collectCameraList.size();
                    List<CollectCameraEntity> repeatIndexs = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        CollectCameraEntity iEntity = collectCameraList.get(i);
                        String iCid = iEntity.getCid();
                        if (i == size - 1) {
                            break;
                        }
                        for (int j = i + 1; j < size; j++) {
                            CollectCameraEntity jEntity = collectCameraList.get(j);
                            if (iCid.equals(jEntity.getCid())) {
                                repeatIndexs.add(jEntity);
                            }
                        }
                    }
                    for (CollectCameraEntity indexEntity : repeatIndexs) {
                        collectCameraList.remove(indexEntity);
                    }

                    mOrgCameraEntities.clear();
                    for (CollectCameraEntity entity : collectCameraList) {
                        List<OrgCameraEntity> cursorEntities = DataSupport.where("manufacturerdeviceid = ?", entity.getCid()).find(OrgCameraEntity.class);
                        if (cursorEntities != null && !cursorEntities.isEmpty()) {
                            OrgCameraEntity cameraEntity = cursorEntities.get(0);
                            mOrgCameraEntities.add(cameraEntity);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void getCollectionsFail() {

    }

    @Override
    public void onRefresh() {
        if (!mIsEnterEndTime) {
            mEndTimeStamp = System.currentTimeMillis();
            mTempEndTimeStamp = mEndTimeStamp;
            mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));
        }
        mPageType = "0";
        mMinCaptureTime = null;
        mMinId = null;
        mBodyDepotAdapter.setEnableLoadMore(false);
        getBodyDepotData();
    }

    @Override
    public void onLoadMoreRequested() {
        mPageType = "2";
        mFaceDepotSrfl.setEnabled(false);
        getBodyDepotData();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.range_all_rb:
                //所有的摄像机
                mTempCameraIdsType = "";
                mTempPreRangePosition = 0;
                break;
            case R.id.range_fav_rb:
                //我关注的摄像机
                mTempCameraIdsType = "1";
                mTempPreRangePosition = 1;
                break;
            case R.id.address_none_rb:
                setRadioButtonChecked(0, R.id.address_none_rb);
                mTempCameraType = "";
                mTempPreAddressPosition = 0;
                break;
            case R.id.address_shequ_rb:
                setRadioButtonChecked(0, R.id.address_shequ_rb);
                mTempCameraType = "102401";
                mTempPreAddressPosition = 1;
                break;
            case R.id.address_wangba_rb:
                setRadioButtonChecked(0, R.id.address_wangba_rb);
                mTempCameraType = "102402";
                mTempPreAddressPosition = 2;
                break;
            case R.id.address_hotel_rb:
                setRadioButtonChecked(1, R.id.address_hotel_rb);
                mTempCameraType = "102403";
                mTempPreAddressPosition = 3;
                break;
            case R.id.address_canting_rb:
                setRadioButtonChecked(1, R.id.address_canting_rb);
                mTempCameraType = "102404";
                mTempPreAddressPosition = 4;
                break;
            case R.id.address_shop_rb:
                setRadioButtonChecked(1, R.id.address_shop_rb);
                mTempCameraType = "102405";
                mTempPreAddressPosition = 5;
                break;
            case R.id.address_key_area_rb:
                setRadioButtonChecked(2, R.id.address_key_area_rb);
                mTempCameraType = "102406";
                mTempPreAddressPosition = 6;
                break;
            case R.id.address_key_shop_rb:
                setRadioButtonChecked(2, R.id.address_key_shop_rb);
                mTempCameraType = "102407";
                mTempPreAddressPosition = 7;
                break;
            case R.id.address_parking_rb:
                setRadioButtonChecked(2, R.id.address_parking_rb);
                mTempCameraType = "102408";
                mTempPreAddressPosition = 8;
                break;
            case R.id.address_other_rb:
                setRadioButtonChecked(3, R.id.address_other_rb);
                mTempCameraType = null;
                mTempPreAddressPosition = 9;
                break;
            case R.id.head_none_rb:
                setHeadRadioButtonChecked(0, R.id.head_none_rb);
                mTempHeadFeature = "";
                break;
            case R.id.head_glass_rb:
                setHeadRadioButtonChecked(0, R.id.head_glass_rb);
                mTempHeadFeature = "112001";
                break;
            case R.id.head_cap_rb:
                setHeadRadioButtonChecked(0, R.id.head_cap_rb);
                mTempHeadFeature = "112002";
                break;
            case R.id.head_casque_rb:
                setHeadRadioButtonChecked(1, R.id.head_casque_rb);
                mTempHeadFeature = "112003";
                break;
            case R.id.head_mouth_mask_rb:
                setHeadRadioButtonChecked(1, R.id.head_mouth_mask_rb);
                mTempHeadFeature = "112004";
                break;
            case R.id.gender_none_rb:
                //从这里开始是性别
                mTempSex = "";
                mTempPreGenderPosition = 0;
                break;
            case R.id.gender_male_rb:
                mTempSex = "100001";
                mTempPreGenderPosition = 1;
                break;
            case R.id.gender_female_rb:
                mTempSex = "100002";
                mTempPreGenderPosition = 2;
                break;
        }
    }

    private void setRadioButtonChecked(int position, int id) {
        switch (position) {
            case 0:
                mAddressRgTwo.clearCheck();
                mAddressRgThree.clearCheck();
                addressRgFour.clearCheck();
                mAddressRgOne.check(id);
                break;
            case 1:
                mAddressRgOne.clearCheck();
                mAddressRgThree.clearCheck();
                addressRgFour.clearCheck();
                mAddressRgTwo.check(id);
                break;
            case 2:
                mAddressRgOne.clearCheck();
                mAddressRgTwo.clearCheck();
                addressRgFour.clearCheck();
                mAddressRgThree.check(id);
                break;
            case 3:
                mAddressRgOne.clearCheck();
                mAddressRgTwo.clearCheck();
                mAddressRgThree.clearCheck();
                addressRgFour.check(id);
                break;

        }
    }

    private void setHeadRadioButtonChecked(int position, int id) {
        switch (position) {
            case 0:
                mHeadRgTwo.clearCheck();
                mHeadRgOne.check(id);
                break;
            case 1:
                mHeadRgOne.clearCheck();
                mHeadRgTwo.check(id);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_time_ll:
                mStartDateDialog.show();
                break;
            case R.id.end_time_ll:
                mEndDateDialog.show();
                break;
            case R.id.clean_tv:
                if (null != rightMenu && rightMenu.getStatus() == RightMenu.SHOW) {
                    setPopDismiss();
                }
                mTempCameraType = "";
                mCameraType = "";
                mAddressRgTwo.clearCheck();
                mAddressRgOne.check(R.id.address_none_rb);
                mSex = "";
                mTempSex = "";
                mGenderRg.check(R.id.gender_none_rb);
                mTempUpColor = "";
                mUpColor = "";
                mTempLowerColor = "";
                mLowerColor = "";
                mUpcolorTagAdapter.setSelectedList(0);
                mLowercolorTagAdapter.setSelectedList(0);

                mTempHeadFeature = "";
                mHeadFeature = "";
                mHeadRgTwo.clearCheck();
                mHeadRgOne.check(R.id.head_none_rb);

                for (int i = 0; i < ((BaseQuickAdapter) rvUpperBodyTexture.getAdapter()).getData().size(); i++) {
                    ((DailyFilterItemEntity) ((BaseQuickAdapter) rvUpperBodyTexture.getAdapter()).getData().get(i)).setSelect(i == 0);
                }
                for (int i = 0; i < ((BaseQuickAdapter) rvLowerBodyCategory.getAdapter()).getData().size(); i++) {
                    ((DailyFilterItemEntity) ((BaseQuickAdapter) rvLowerBodyCategory.getAdapter()).getData().get(i)).setSelect(i == 0);
                }
                for (int i = 0; i < ((BaseQuickAdapter) rvBelongings.getAdapter()).getData().size(); i++) {
                    ((DailyFilterItemEntity) ((BaseQuickAdapter) rvBelongings.getAdapter()).getData().get(i)).setSelect(i == 0);
                }
                rvUpperBodyTexture.getAdapter().notifyDataSetChanged();
                rvLowerBodyCategory.getAdapter().notifyDataSetChanged();
                rvBelongings.getAdapter().notifyDataSetChanged();
                setBelongingsPosition = 0;
                setLowerPosition = 0;
                setUpperPosition = 0;

                mStartTimeStamp = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000 + 1000;
                mTempStartTimeStamp = mStartTimeStamp;
                mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));
                // mStartTimeTv.setText("请选择开始时间");
                // mTempStartTimeStamp = null;
                // mStartTimeStamp = null;
                mEndTimeStamp = System.currentTimeMillis();
                mTempEndTimeStamp = mEndTimeStamp;
                mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));
                mCameraIds = null;
                mCameraTags = null;
                mBodyTags = null;

                mTimeChanged = false;
                mTempCameraIdsType = "";
                mTempPreRangePosition = 0;
                mTempPreGenderPosition = 0;
                mTempPreAddressPosition = 0;
                mRangeEntities.get(mPreRangePosition).setSelect(false);
                mPreRangePosition = mTempPreRangePosition;
                mRangeEntities.get(mPreRangePosition).setSelect(true);
                mGenderEntities.get(mTempPreGenderPosition).setSelect(false);
                mPreGenderPosition = mTempPreGenderPosition;
                mGenderEntities.get(mPreGenderPosition).setSelect(true);
                mAddressEntities.get(mPreAddressPosition).setSelect(false);
                mPreAddressPosition = mTempPreAddressPosition;
                mAddressEntities.get(mPreAddressPosition).setSelect(true);
                showArrowDown(0);
                showArrowDown(1);
                showArrowDown(2);

                rightMenu.dismiss();
                mPageType = "0";
                mIsEnterEndTime = false;
                mFaceDepotSrfl.setRefreshing(true);
                onRefresh();
                break;
            case R.id.cofirm_tv:
                //重新根据条件 获取数据
                if (mTempStartTimeStamp != null && mTempEndTimeStamp != null && mTempStartTimeStamp >= mTempEndTimeStamp) {
                    ToastUtils.showShort("开始时间必须小于结束时间");
                    return;
                }
                setPopDismiss();
                mStartTimeStamp = mTempStartTimeStamp;
                mEndTimeStamp = mTempEndTimeStamp;
                mCameraType = mTempCameraType;
                mUpColor = mTempUpColor;
                mLowerColor = mTempLowerColor;
                mHeadFeature = mTempHeadFeature;
                mSex = mTempSex;
                mCameraIdsType = mTempCameraIdsType;
                setUpperPosition = preUpperPosition;
                setLowerPosition = preLowerPosition;
                setBelongingsPosition = preBelongingsPosition;
                initNetParams();

                mStartTimeStamp = mTempStartTimeStamp;
                mEndTimeStamp = mTempEndTimeStamp;
                mRangeEntities.get(mPreRangePosition).setSelect(false);
                mPreRangePosition = mTempPreRangePosition;
                mRangeEntities.get(mPreRangePosition).setSelect(true);
                mGenderEntities.get(mPreGenderPosition).setSelect(false);
                mPreGenderPosition = mTempPreGenderPosition;
                mGenderEntities.get(mPreGenderPosition).setSelect(true);
                mAddressEntities.get(mPreAddressPosition).setSelect(false);
                mPreAddressPosition = mTempPreAddressPosition;
                mAddressEntities.get(mPreAddressPosition).setSelect(true);
                showArrowDown(0);
                showArrowDown(1);
                showArrowDown(2);
                showFilterArrow();

                mPageType = "0";
                mIsEnterEndTime = true;
                mFaceDepotSrfl.setRefreshing(true);
                onRefresh();
                break;
        }
    }


    private void showFilterArrow() {
        if (mTimeChanged || !TextUtils.isEmpty(mUpColor) || !TextUtils.isEmpty(mLowerColor) || !TextUtils.isEmpty(mHeadFeature)) {
            mFilterTv.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
            mFilterTv.setCompoundDrawables(mFilterYellowDrawable, null, null, null);
        } else {
            mFilterTv.setTextColor(getResources().getColor(R.color.gray_212121));
            mFilterTv.setCompoundDrawables(mFilterBlackDrawable, null, null, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TYPE_SELECT_CAMERA:
                    //选择了Camera，或者"",重新刷新
                    String cameraIds = data.getStringExtra("cameraIds");
                    if (!TextUtils.isEmpty(cameraIds)) {
                        String[] split = cameraIds.split(",");
                        mCameraIds = Arrays.asList(split);
                    } else {
                        mCameraIds = null;
                    }
                    mFaceDepotSrfl.setRefreshing(true);
                    onRefresh();
                    break;
            }

        }
    }

    private void showDataView() {
        mNoPermissionRl.setVisibility(View.GONE);
        mEmptyNoNetworkTv.setVisibility(View.GONE);
        rlEmpty.setVisibility(View.GONE);
        mFaceDepotRclv.setVisibility(View.VISIBLE);
    }

    private void showNoDataView() {
        mNoPermissionRl.setVisibility(View.GONE);
        mEmptyNoNetworkTv.setVisibility(View.GONE);
        rlEmpty.setVisibility(View.VISIBLE);
        mFaceDepotRclv.setVisibility(View.GONE);
    }

    private void showNoNetworkView() {
        mNoPermissionRl.setVisibility(View.GONE);
        mEmptyNoNetworkTv.setVisibility(View.VISIBLE);
        rlEmpty.setVisibility(View.GONE);
        mFaceDepotRclv.setVisibility(View.GONE);
    }

    private void showNoPermissonView() {
        mNoPermissionRl.setVisibility(View.VISIBLE);
        mEmptyNoNetworkTv.setVisibility(View.GONE);
        rlEmpty.setVisibility(View.GONE);
        mFaceDepotRclv.setVisibility(View.GONE);
    }

    @Subscriber()
    public void modifyList(PictureCollectEvent event) {
        List<BodyDepotDetailEntity> data = mBodyDepotAdapter.getData();
        try {
            data.get(event.position).isCollect = event.isCollected ? "1" : "0";
            data.get(event.position).collectId = event.colleteId;
            mBodyDepotAdapter.notifyItemChanged(event.position);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
