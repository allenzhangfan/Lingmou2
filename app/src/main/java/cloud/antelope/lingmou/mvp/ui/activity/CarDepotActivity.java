package cloud.antelope.lingmou.mvp.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.FormatUtils;
import cloud.antelope.lingmou.di.component.DaggerCarDepotComponent;
import cloud.antelope.lingmou.di.module.CarDepotModule;
import cloud.antelope.lingmou.mvp.contract.CarDepotContract;
import cloud.antelope.lingmou.mvp.model.entity.CarBrandBean;
import cloud.antelope.lingmou.mvp.model.entity.CarColorBean;
import cloud.antelope.lingmou.mvp.model.entity.CarDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.CarDepotListRequest;
import cloud.antelope.lingmou.mvp.model.entity.CarInfo;
import cloud.antelope.lingmou.mvp.model.entity.CarTypeBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.DailyFilterItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.DetailCommonEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotDetailEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.PlateColorBean;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.presenter.CarDepotPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.CarDepotAdapter;
import cloud.antelope.lingmou.mvp.ui.fragment.CollectionListFragment;
import cloud.antelope.lingmou.mvp.ui.widget.LmDatePickDialog;
import cloud.antelope.lingmou.mvp.ui.widget.LmTimePickerDialog;
import cloud.antelope.lingmou.mvp.ui.widget.MenuView;
import cloud.antelope.lingmou.mvp.ui.widget.RightMenu;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CarDepotActivity extends BaseActivity<CarDepotPresenter> implements CarDepotContract.View, BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
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
    @BindView(R.id.view_divider)
    View viewDivider;
    @BindView(R.id.tv_range)
    TextView tvRange;
    @BindView(R.id.ll_range)
    LinearLayout llRange;
    @BindView(R.id.tv_plate_color)
    TextView tvPlateColor;
    @BindView(R.id.ll_plate_color)
    LinearLayout llPlateColor;
    @BindView(R.id.tv_car_color)
    TextView tvCarColor;
    @BindView(R.id.ll_car_color)
    LinearLayout llCarColor;
    @BindView(R.id.tv_car_type)
    TextView tvCarType;
    @BindView(R.id.ll_car_type)
    LinearLayout llCarType;
    @BindView(R.id.tv_filter)
    TextView tvFilter;
    @BindView(R.id.ll_filter)
    LinearLayout llFilter;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;
    //    @BindView(R.id.tv_empty)
//    TextView tvEmpty;
    @BindView(R.id.tv_no_network)
    TextView tvNoNetwork;
    @BindView(R.id.filter_rclv)
    RecyclerView filterRclv;
    TextView cleanTv;
    TextView cofirmTv;
    TextView tvRangeAll;
    TextView tvRangeCollect;
    TextView tvStartTime;
    LinearLayout llStartTime;
    TextView tvStartTimeChoice;
    TextView tvEndTime;
    LinearLayout llEndTime;
    TextView tvEndTimeChoice;
    ImageView ivPlateColorMore;
    LinearLayout llPlateColorMore;
    RecyclerView rvPlateColor;
    ImageView ivCarColorMore;
    LinearLayout llCarColorMore;
    RecyclerView rvCarColor;
    ImageView ivCarTypeMore;
    LinearLayout llCarTypeMore;
    RecyclerView rvCarType;
    ImageView ivCarBrandMore;
    LinearLayout llCarBrandMore;
    RecyclerView rvCarBrand;
    RelativeLayout popRoot;
    //    @BindView(R.id.shaixuan_ll)
    RelativeLayout shaixuanLl;
    LinearLayout llPlateColorFold;
    LinearLayout llCarColorFold;
    LinearLayout llCarTypeFold;
    @Inject
    CarDepotAdapter mAdapter;
    @Inject
    LoadMoreView mLoadMoreView;
    Long startTime = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000);//传参用的开始时间
    Long endTime = System.currentTimeMillis();//传参用的结束时间
    LinearLayout llCarBrand;
    TextView tvPlateColorMore;
    TextView tvCarColorMore;
    TextView tvCarTypeMore;
    TextView tvCarBrandMore;
    ScrollView scrollView;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    private ArrayList<PlateColorBean> plateColorList = new ArrayList<>();//车牌颜色
    private ArrayList<CarColorBean> carColorList = new ArrayList<>();//车辆颜色
    private ArrayList<CarTypeBean> carTypeList = new ArrayList<>();//车辆类型
    private ArrayList<PlateColorBean> plateColorListRight = new ArrayList<>();//右侧筛选列表临时展示的车牌颜色
    private ArrayList<CarColorBean> carColorListRight = new ArrayList<>();//右侧筛选列表临时展示的车辆颜色
    private ArrayList<CarTypeBean> carTypeListRight = new ArrayList<>();//右侧筛选列表临时展示的车辆类型
    private ArrayList<CarBrandBean> carBrandList = new ArrayList<>();//右侧筛选列表临时展示的车辆品牌
    private Animation mRightIn;
    private Animation mRightOut;
    //折叠的高度
    private int plate_color_height;
    private int car_color_height;
    private int car_type_height;
    //展开的高度
    private int plateColorFullHeight;
    private int carColorFullHeight;
    private int carTypeFullHeight;
    private boolean plateColorListFold = true;
    private boolean carColorListFold = true;
    private boolean carTypeListFold = true;
    private MenuView menuView;
    private RightMenu rightMenu;
    private RecyclerView rvCollect;
    private ArrayList<DailyFilterItemEntity> rangeList;
    private Drawable mFilterArrowDownYellowDrawable, mFilterArrowDownBlackDrawable,
            mFilterArrowUpYellowDrawable, mFilterArrowUpBlackDrawable,
            mFilterBlackDrawable, mFilterYellowDrawable;
    private RecyclerView rvPlateColorTop;
    private RecyclerView rvCarColorTop;
    private RecyclerView rvCarTypeTop;
    //真实选中position
    private int rangePosition;
    private int plateColorPosition;
    private int carColorPosition;
    private int carTypePostion;
    //右侧临时展示position
    private int rangePositionPre;
    private int plateColorPositionPre;
    private int carColorPositionPre;
    private int carTypePostionPre;
    private CarBrandBean preCarBrand, setCarBrand;//展示车辆品牌及真实选中车辆品牌
    private LmDatePickDialog lmDatePickDialog;
    Calendar calendar;
    int year, month, day, hour, minute,
            setStartYear, setStartMonthOfYear, setStartDayOfMonth, setStartHour, setStartMinute,
            setEndYear, setEndMonthOfYear, setEndDayOfMonth, setEndHour, setEndMinute,
            tempStartYear, tempStartMonthOfYear, tempStartDayOfMonth, tempStartHour,
            tempStartMinute, tempEndYear, tempEndMonthOfYear, tempEndDayOfMonth, tempEndHour, tempEndMinute;

    private boolean isAnimating;//折叠动画执行中
    int pageNum = 1;
    int pageSize = 16;
    private boolean hasMore;
    private boolean rightOpen;//右侧开启
    private boolean timeSet;//时间设置过
    private boolean timeModify;//时间选择过（界面上）
    private ArrayList<String> plates;
    private ArrayList<String> devices;
    List<OrgCameraEntity> mOrgCameraEntities = new ArrayList<>();
    List<String> mCameraIds;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerCarDepotComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .carDepotModule(new CarDepotModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_car_depot; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        plates = intent.getStringArrayListExtra("plates");
        devices = intent.getStringArrayListExtra("devices");
        onRefresh();
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        titleTv.setText(R.string.car_depot);
        srl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        rv.setLayoutManager(new GridLayoutManager(CarDepotActivity.this, 2));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.top = position < 2 ? getResources().getDimensionPixelSize(R.dimen.dp8) : 0;
                outRect.left = position % 2 == 0 ? 0 : getResources().getDimensionPixelSize(R.dimen.dp3_5);
                outRect.right = position % 2 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp3_5) : 0;
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.dp8);
            }
        });
        mAdapter.setOnLoadMoreListener(this, rv);
        mAdapter.setLoadMoreView(mLoadMoreView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(CarDepotActivity.this, PictureDetailActivity.class);
                intent.putParcelableArrayListExtra("bean", changeData());
                intent.putExtra("position", position);
                intent.putExtra("fromCar", true);
                startActivity(intent);
            }
        });
        srl.setOnRefreshListener(this);
        onRefresh();
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(true);
            }
        });
        initFilterData();
        initTime();
        mPresenter.getInfo();
        mPresenter.getCollections();
    }

    private ArrayList<DetailCommonEntity> changeData() {
        if (null != mAdapter.getData()) {
            ArrayList<DetailCommonEntity> list = new ArrayList<>();
            for (CarDepotEntity entity : mAdapter.getData()) {
                DetailCommonEntity commonEntity = new DetailCommonEntity();
                commonEntity.deviceName = entity.deviceName;
                commonEntity.endTime = entity.passTime;
                commonEntity.carRect = entity.vehicleRect;
                commonEntity.srcImg = entity.picUrl.bigPicUrl;
                commonEntity.id = String.valueOf(entity.id);
                commonEntity.cameraId = String.valueOf(entity.deviceId);
                commonEntity.cameraName = entity.deviceName;
                commonEntity.faceImg = entity.picUrl.smallPicUrl;
                commonEntity.captureTime = entity.passTime;
                list.add(commonEntity);
            }
            return list;
        }
        return null;
    }

    @Override
    public void onRefresh() {
        pageNum = 1;
        getList();
    }

    private void getList() {
        CarDepotListRequest request = new CarDepotListRequest();
        request.page = pageNum;
        request.pageSize = pageSize;
        request.startTime = startTime;
        request.endTime = endTime;
        if (plateColorList.size() > 0) {
            for (PlateColorBean bean : plateColorList) {
                if (bean.selected) {
                    request.plateColor = bean.code;
                }
            }
        }
        if (carColorList.size() > 0) {
            for (CarColorBean bean : carColorList) {
                if (bean.selected) {
                    request.vehicleColor = bean.code;
                }
            }
        }
        if (carTypeList.size() > 0) {
            for (CarTypeBean bean : carTypeList) {
                if (bean.selected) {
                    request.vehicleClasses = bean.ids;
                }
            }
        }
        if (setCarBrand != null && setCarBrand.code != null) {
            ArrayList<Long> carBrandIds = new ArrayList<>();
            carBrandIds.add(setCarBrand.code);
            request.vehicleBrands = carBrandIds;
        }
        request.plateNos = plates;
        if (null == mCameraIds) {
            mCameraIds = new ArrayList<>();
        }
        if (rangePosition == 0) {
            mCameraIds = null;
        } else if (!mOrgCameraEntities.isEmpty()) {
            //mCameraIds = 收藏的所有摄像机
            mCameraIds.clear();
            for (OrgCameraEntity entity : mOrgCameraEntities) {
                mCameraIds.add(String.valueOf(entity.getManufacturerDeviceId()));
            }
        } else {
            mCameraIds.clear();
            mCameraIds.add("");
        }
        request.devices = mCameraIds;
        /*if (devices != null) {
            request.devices = new ArrayList<>();
            for (int i = 0; i < devices.size(); i++) {
                request.devices.add(Long.valueOf(devices.get(i)));
            }
        }*/
        mPresenter.getList(request);
    }

    private void initTime() {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        setStartYear = year;
        setStartMonthOfYear = month + 1;
        setStartDayOfMonth = day - 3;
        setStartHour = hour;
        setStartMinute = minute;
        setEndYear = year;
        setEndMonthOfYear = month + 1;
        setEndDayOfMonth = day;
        setEndHour = hour;
        setEndMinute = minute;
        tempStartYear = setStartYear;
        tempStartMonthOfYear = setStartMonthOfYear;
        tempStartDayOfMonth = setStartDayOfMonth;
        tempStartHour = setStartHour;
        tempStartMinute = setStartMinute;
        tempEndYear = setEndYear;
        tempEndMonthOfYear = setEndMonthOfYear;
        tempEndDayOfMonth = setEndDayOfMonth;
        tempEndHour = setEndHour;
        tempEndMinute = setEndMinute;
        timeSet = true;
        timeModify = true;
    }

    private void initFilterData() {
        shaixuanLl = (RelativeLayout) LayoutInflater.from(CarDepotActivity.this).inflate(R.layout.popupwindow_car_depot, null);
        popRoot = shaixuanLl.findViewById(R.id.pop_root);
        cleanTv = shaixuanLl.findViewById(R.id.clean_tv);
        cleanTv.setOnClickListener(this);
        cofirmTv = shaixuanLl.findViewById(R.id.cofirm_tv);
        cofirmTv.setOnClickListener(this);
        scrollView = shaixuanLl.findViewById(R.id.scroll_view);
        scrollView.setOnClickListener(this);
        tvRangeAll = shaixuanLl.findViewById(R.id.tv_range_all);
        tvRangeAll.setOnClickListener(this);
        tvRangeCollect = shaixuanLl.findViewById(R.id.tv_range_collect);
        tvRangeCollect.setOnClickListener(this);
        llStartTime = shaixuanLl.findViewById(R.id.ll_start_time);
        llStartTime.setOnClickListener(this);
        tvStartTime = shaixuanLl.findViewById(R.id.tv_start_time);
        tvStartTimeChoice = shaixuanLl.findViewById(R.id.tv_start_time_choice);
        tvStartTimeChoice.setOnClickListener(this);
        llEndTime = shaixuanLl.findViewById(R.id.ll_end_time);
        llEndTime.setOnClickListener(this);
        tvEndTime = shaixuanLl.findViewById(R.id.tv_end_time);
        tvEndTime.setOnClickListener(this);
        tvEndTimeChoice = shaixuanLl.findViewById(R.id.tv_end_time_choice);
        tvEndTimeChoice.setOnClickListener(this);
        llPlateColorMore = shaixuanLl.findViewById(R.id.ll_plate_color_more);
        llPlateColorMore.setOnClickListener(this);
        tvPlateColorMore = shaixuanLl.findViewById(R.id.tv_plate_color_more);
        tvPlateColorMore.setOnClickListener(this);
        ivPlateColorMore = shaixuanLl.findViewById(R.id.iv_plate_color_more);
        ivPlateColorMore.setOnClickListener(this);
        llPlateColorFold = shaixuanLl.findViewById(R.id.ll_plate_color_fold);
        llPlateColorFold.setOnClickListener(this);
        rvPlateColor = shaixuanLl.findViewById(R.id.rv_plate_color);
        rvPlateColor.setOnClickListener(this);
        llCarColorMore = shaixuanLl.findViewById(R.id.ll_car_color_more);
        llCarColorMore.setOnClickListener(this);
        tvCarColorMore = shaixuanLl.findViewById(R.id.tv_car_color_more);
        tvCarColorMore.setOnClickListener(this);
        ivCarColorMore = shaixuanLl.findViewById(R.id.iv_car_color_more);
        ivCarColorMore.setOnClickListener(this);
        llCarColorFold = shaixuanLl.findViewById(R.id.ll_car_color_fold);
        llCarColorFold.setOnClickListener(this);
        rvCarColor = shaixuanLl.findViewById(R.id.rv_car_color);
        rvCarColor.setOnClickListener(this);
        llCarTypeMore = shaixuanLl.findViewById(R.id.ll_car_type_more);
        llCarTypeMore.setOnClickListener(this);
        tvCarTypeMore = shaixuanLl.findViewById(R.id.tv_car_type_more);
        tvCarTypeMore.setOnClickListener(this);
        ivCarTypeMore = shaixuanLl.findViewById(R.id.iv_car_type_more);
        ivCarTypeMore.setOnClickListener(this);
        llCarTypeFold = shaixuanLl.findViewById(R.id.ll_car_type_fold);
        llCarTypeFold.setOnClickListener(this);
        rvCarType = shaixuanLl.findViewById(R.id.rv_car_type);
        rvCarType.setOnClickListener(this);
        llCarBrandMore = shaixuanLl.findViewById(R.id.ll_car_brand_more);
        llCarBrandMore.setOnClickListener(this);
        tvCarBrandMore = shaixuanLl.findViewById(R.id.tv_car_brand_more);
        ivCarBrandMore = shaixuanLl.findViewById(R.id.iv_car_brand_more);
        llCarBrand = shaixuanLl.findViewById(R.id.ll_car_brand);
        llCarBrand.setOnClickListener(this);
        rvCarBrand = shaixuanLl.findViewById(R.id.rv_car_brand);
        rvCarBrand.setOnClickListener(this);


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

        //右侧车牌颜色列表
        rvPlateColor.setHasFixedSize(true);
        rvPlateColor.setNestedScrollingEnabled(false);
        rvPlateColor.setLayoutManager(new GridLayoutManager(CarDepotActivity.this, 3));
        rvPlateColor.setAdapter(new BaseQuickAdapter<PlateColorBean, BaseViewHolder>(R.layout.item_plate_color, plateColorListRight) {
            @Override
            protected void convert(BaseViewHolder helper, PlateColorBean item) {
                LinearLayout llRoot = helper.getView(R.id.ll_root);
                ImageView iv = helper.getView(R.id.iv);
                helper.setTextColor(R.id.tv_name, item.selected ? getResources().getColor(R.color.yellow_ff8f00) : getResources().getColor(R.color.gray_424242));
                llRoot.setBackgroundResource(!item.selected ? R.drawable.shape_rect_corner_2_f7f7f7 : R.drawable.shape_rect_corner_2_fff8e1);
                if (TextUtils.equals(item.name, PlateColorBean.ALL) || TextUtils.equals(item.name, PlateColorBean.OTHER)) {
                    iv.setVisibility(View.GONE);
                } else {
                    iv.setVisibility(View.VISIBLE);
                }
                helper.setText(R.id.tv_name, item.name);
                switch (item.name) {
                    case PlateColorBean.BLUE:
                        iv.setBackgroundResource(R.drawable.brand_blue);
                        break;
                    case PlateColorBean.YELLOW:
                        iv.setBackgroundResource(R.drawable.brand_yellow);
                        break;
                    case PlateColorBean.WHITE:
                        iv.setBackgroundResource(R.drawable.brand_white);
                        break;
                    case PlateColorBean.BLACK:
                        iv.setBackgroundResource(R.drawable.brand_black);
                        break;
                    case PlateColorBean.GREEN:
                        iv.setBackgroundResource(R.drawable.brand_green);
                        break;
                    case PlateColorBean.YELLOW_GREEN:
                        iv.setBackgroundResource(R.drawable.brand_yellow_green);
                        break;
                }
                llRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < plateColorListRight.size(); i++) {
                            plateColorListRight.get(i).selected = i == helper.getPosition();
                        }
                        notifyDataSetChanged();
                        plateColorPositionPre = helper.getPosition();
                        if (plateColorPositionPre == 0) {
                            tvPlateColorMore.setTextColor(getResources().getColor(R.color.gray_9e9e9e));
                        } else {
                            tvPlateColorMore.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                        }
                        tvPlateColorMore.setText(plateColorListRight.get(plateColorPositionPre).name);
                        foldPlateColorList(true);
                    }
                });
            }

        });
        rvPlateColor.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.left = position % 3 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 3 == 2 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.top = position < 3 ? getResources().getDimensionPixelSize(R.dimen.dp8) : getResources().getDimensionPixelSize(R.dimen.dp16);
            }
        });

        //右侧车辆颜色列表
        rvCarColor.setHasFixedSize(true);
        rvCarColor.setNestedScrollingEnabled(false);
        rvCarColor.setLayoutManager(new GridLayoutManager(CarDepotActivity.this, 4));
        rvCarColor.setAdapter(new BaseQuickAdapter<CarColorBean, BaseViewHolder>(R.layout.item_plate_color, carColorListRight) {
            @Override
            protected void convert(BaseViewHolder helper, CarColorBean item) {
                LinearLayout llRoot = helper.getView(R.id.ll_root);
                ImageView iv = helper.getView(R.id.iv);
                helper.setTextColor(R.id.tv_name, item.selected ? getResources().getColor(R.color.yellow_ff8f00) : getResources().getColor(R.color.gray_424242));
                llRoot.setBackgroundResource(!item.selected ? R.drawable.shape_rect_corner_2_f7f7f7 : R.drawable.shape_rect_corner_2_fff8e1);
                if (item.mark == null) {
                    iv.setVisibility(View.GONE);
                } else {
                    iv.setVisibility(View.VISIBLE);
                    GradientDrawable gd = new GradientDrawable();
                    gd.setColor(Color.parseColor("#" + item.mark));
                    gd.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.dp2));
                    iv.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dp18), getResources().getDimensionPixelSize(R.dimen.dp18)));
                    iv.setBackground(gd);
                }
                helper.setText(R.id.tv_name, item.name);
                llRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < carColorListRight.size(); i++) {
                            if (i == helper.getPosition()) {
                                carColorListRight.get(i).selected = true;
                            } else {
                                carColorListRight.get(i).selected = false;
                            }
                        }
                        notifyDataSetChanged();
                        carColorPositionPre = helper.getPosition();
                        tvCarColorMore.setText(carColorListRight.get(carColorPositionPre).name);
                        if (carColorPositionPre == 0) {
                            tvCarColorMore.setTextColor(getResources().getColor(R.color.gray_9e9e9e));
                        } else {
                            tvCarColorMore.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                        }
                        foldCarColorList(true);
                    }
                });
            }
        });
        rvCarColor.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.left = position % 4 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 4 == 3 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.top = position < 4 ? getResources().getDimensionPixelSize(R.dimen.dp8) : getResources().getDimensionPixelSize(R.dimen.dp16);
            }
        });

        //右侧车辆类型列表
        rvCarType.setHasFixedSize(true);
        rvCarType.setNestedScrollingEnabled(false);
        rvCarType.setLayoutManager(new GridLayoutManager(CarDepotActivity.this, 4));
        rvCarType.setAdapter(new BaseQuickAdapter<CarTypeBean, BaseViewHolder>(R.layout.item_car_type, carTypeListRight) {
            @Override
            protected void convert(BaseViewHolder helper, CarTypeBean item) {
                LinearLayout llRoot = helper.getView(R.id.ll_root);
                helper.setTextColor(R.id.tv_name, item.selected ? getResources().getColor(R.color.yellow_ff8f00) : getResources().getColor(R.color.gray_424242));
                llRoot.setBackgroundResource(!item.selected ? R.drawable.shape_rect_corner_2_f7f7f7 : R.drawable.shape_rect_corner_2_fff8e1);
                helper.setText(R.id.tv_name, item.label);
                llRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < carTypeListRight.size(); i++) {
                            if (i == helper.getPosition()) {
                                carTypeListRight.get(i).selected = true;
                            } else {
                                carTypeListRight.get(i).selected = false;
                            }
                        }
                        notifyDataSetChanged();
                        carTypePostionPre = helper.getPosition();
                        tvCarTypeMore.setText(carTypeListRight.get(carTypePostionPre).label);
                        if (carTypePostionPre == 0) {
                            tvCarTypeMore.setTextColor(getResources().getColor(R.color.gray_9e9e9e));
                        } else {
                            tvCarTypeMore.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                        }
                        foldCarTypeList(true);
                    }
                });
            }
        });
        rvCarType.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.left = position % 4 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 4 == 3 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.top = position < 4 ? getResources().getDimensionPixelSize(R.dimen.dp8) : getResources().getDimensionPixelSize(R.dimen.dp16);
            }
        });

        //右侧车辆品牌列表
        rvCarBrand.setHasFixedSize(true);
        rvCarBrand.setNestedScrollingEnabled(false);
        rvCarBrand.setLayoutManager(new GridLayoutManager(CarDepotActivity.this, 3));
        rvCarBrand.setAdapter(new BaseQuickAdapter<CarBrandBean, BaseViewHolder>(R.layout.item_car_brand, carBrandList) {
            @Override
            protected void convert(BaseViewHolder helper, CarBrandBean item) {
                LinearLayout llRoot = helper.getView(R.id.ll_root);
                ImageView iv = helper.getView(R.id.iv);
                helper.setTextColor(R.id.tv_name, item.selected ? getResources().getColor(R.color.yellow_ff8f00) : getResources().getColor(R.color.gray_424242));
                llRoot.setBackgroundResource(item.selected ? R.drawable.shape_car_brand_selected : R.drawable.shape_car_brand_unselected);
                iv.setVisibility(item.pic == null ? View.GONE : View.VISIBLE);
                iv.setImageBitmap(FormatUtils.stringToBitmap(item.pic));
                helper.setText(R.id.tv_name, item.name);
                llRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < carBrandList.size(); i++) {
                            if (i == helper.getPosition()) {
                                carBrandList.get(i).selected = true;
                            } else {
                                carBrandList.get(i).selected = false;
                            }
                        }
                        notifyDataSetChanged();
                        preCarBrand = carBrandList.get(helper.getPosition());
                        tvCarBrandMore.setText(preCarBrand.name);
                        if ("全部".equals(preCarBrand.name)) {
                            tvCarBrandMore.setTextColor(getResources().getColor(R.color.gray_9e9e9e));
                        } else {
                            tvCarBrandMore.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                        }
                    }
                });
            }
        });
        rvCarBrand.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.left = position % 3 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 3 == 2 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.top = position < 3 ? getResources().getDimensionPixelSize(R.dimen.dp8) : getResources().getDimensionPixelSize(R.dimen.dp16);
            }
        });
        plate_color_height = getResources().getDimensionPixelSize(R.dimen.dp144);
        car_color_height = getResources().getDimensionPixelSize(R.dimen.dp144);
        car_type_height = getResources().getDimensionPixelSize(R.dimen.dp52);
        mRightIn = AnimationUtils.loadAnimation(getActivity(), R.anim.right_in);
        mRightOut = AnimationUtils.loadAnimation(getActivity(), R.anim.right_out);
        //顶部范围列表
        rangeList = new ArrayList<>();
        rangeList.add(new DailyFilterItemEntity(getResources().getString(R.string.all), true));
        rangeList.add(new DailyFilterItemEntity(getResources().getString(R.string.my_collection), false));
        rvCollect = new RecyclerView(CarDepotActivity.this);
        rvCollect.setBackgroundColor(getResources().getColor(R.color.white));
        rvCollect.setLayoutManager(new GridLayoutManager(CarDepotActivity.this, 3));
        rvCollect.setAdapter(new BaseQuickAdapter<DailyFilterItemEntity, BaseViewHolder>(R.layout.item_daily_filter, rangeList) {
            @Override
            protected void convert(BaseViewHolder helper, DailyFilterItemEntity item) {
                LinearLayout llRoot = helper.getView(R.id.ll_root);
                llRoot.setBackgroundResource(!item.isSelect() ? R.drawable.shape_rect_corner_2_f7f7f7 : R.drawable.shape_rect_corner_2_fff8e1);
                helper.setText(R.id.item_filter_name_tv, item.getName());
                helper.setTextColor(R.id.item_filter_name_tv, item.isSelect() ? Utils.getContext().getResources().getColor(R.color.yellow_ff8f00) : Utils.getContext().getResources().getColor(R.color.gray_212121));
//                helper.setVisible(R.id.item_filter_select_iv, item.isSelect());
                helper.getView(R.id.ll_root).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < rangeList.size(); i++) {
                            if (helper.getAdapterPosition() == i) {
                                rangeList.get(i).setSelect(true);
                            } else {
                                rangeList.get(i).setSelect(false);
                            }
                        }
                        rangePosition = helper.getAdapterPosition();
                        rangePositionPre = helper.getAdapterPosition();
                        notifyDataSetChanged();
                        notifyRightFilter();
                        notifyTopFilter();
                        menuView.dismiss();
                        onRefresh();
                    }
                });
            }
        });
        rvCollect.addItemDecoration(new RecyclerView.ItemDecoration() {
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

        //顶部车牌颜色列表
        rvPlateColorTop = new RecyclerView(CarDepotActivity.this);
        rvPlateColorTop.setBackgroundColor(getResources().getColor(R.color.white));
        rvPlateColorTop.setLayoutManager(new GridLayoutManager(CarDepotActivity.this, 3));
        rvPlateColorTop.setAdapter(new BaseQuickAdapter<PlateColorBean, BaseViewHolder>(R.layout.item_plate_color, plateColorList) {
            @Override
            protected void convert(BaseViewHolder helper, PlateColorBean item) {
                LinearLayout llRoot = helper.getView(R.id.ll_root);
                ImageView iv = helper.getView(R.id.iv);
                helper.setTextColor(R.id.tv_name, item.selected ? getResources().getColor(R.color.yellow_ff8f00) : getResources().getColor(R.color.gray_424242));
                llRoot.setBackgroundResource(!item.selected ? R.drawable.shape_rect_corner_2_f7f7f7 : R.drawable.shape_rect_corner_2_fff8e1);
                if (TextUtils.equals(item.name, PlateColorBean.ALL) || TextUtils.equals(item.name, PlateColorBean.OTHER)) {
                    iv.setVisibility(View.GONE);
                } else {
                    iv.setVisibility(View.VISIBLE);
                }
                helper.setText(R.id.tv_name, item.name);
                switch (item.name) {
                    case PlateColorBean.BLUE:
                        iv.setBackgroundResource(R.drawable.brand_blue);
                        break;
                    case PlateColorBean.YELLOW:
                        iv.setBackgroundResource(R.drawable.brand_yellow);
                        break;
                    case PlateColorBean.WHITE:
                        iv.setBackgroundResource(R.drawable.brand_white);
                        break;
                    case PlateColorBean.BLACK:
                        iv.setBackgroundResource(R.drawable.brand_black);
                        break;
                    case PlateColorBean.GREEN:
                        iv.setBackgroundResource(R.drawable.brand_green);
                        break;
                    case PlateColorBean.YELLOW_GREEN:
                        iv.setBackgroundResource(R.drawable.brand_yellow_green);
                        break;
                }
                llRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < plateColorList.size(); i++) {
                            if (i == helper.getPosition()) {
                                plateColorList.get(i).selected = true;
                            } else {
                                plateColorList.get(i).selected = false;
                            }
                        }
                        plateColorPosition = helper.getPosition();
                        plateColorPositionPre = helper.getPosition();
                        notifyDataSetChanged();
                        notifyRightFilter();
                        notifyTopFilter();
                        menuView.dismiss();
                        onRefresh();
                    }
                });
            }
        });
        rvPlateColorTop.addItemDecoration(new RecyclerView.ItemDecoration() {
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

        //顶部车辆颜色列表
        rvCarColorTop = new RecyclerView(CarDepotActivity.this);
        rvCarColorTop.setBackgroundColor(getResources().getColor(R.color.white));
        rvCarColorTop.setLayoutManager(new GridLayoutManager(CarDepotActivity.this, 4));
        rvCarColorTop.setAdapter(new BaseQuickAdapter<CarColorBean, BaseViewHolder>(R.layout.item_plate_color, carColorList) {
            @Override
            protected void convert(BaseViewHolder helper, CarColorBean item) {
                LinearLayout llRoot = helper.getView(R.id.ll_root);
                ImageView iv = helper.getView(R.id.iv);
                helper.setTextColor(R.id.tv_name, item.selected ? getResources().getColor(R.color.yellow_ff8f00) : getResources().getColor(R.color.gray_424242));
                llRoot.setBackgroundResource(!item.selected ? R.drawable.shape_rect_corner_2_f7f7f7 : R.drawable.shape_rect_corner_2_fff8e1);
                if (item.mark == null) {
                    iv.setVisibility(View.GONE);
                } else {
                    iv.setVisibility(View.VISIBLE);
                    GradientDrawable gd = new GradientDrawable();
                    gd.setColor(Color.parseColor("#" + item.mark));
                    gd.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.dp2));
                    iv.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dp18), getResources().getDimensionPixelSize(R.dimen.dp18)));
                    iv.setBackground(gd);
                }
                helper.setText(R.id.tv_name, item.name);
                llRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < carColorList.size(); i++) {
                            if (i == helper.getPosition()) {
                                carColorList.get(i).selected = true;
                            } else {
                                carColorList.get(i).selected = false;
                            }
                        }
                        carColorPosition = helper.getPosition();
                        carColorPositionPre = helper.getPosition();
                        notifyDataSetChanged();
                        notifyRightFilter();
                        notifyTopFilter();
                        menuView.dismiss();
                        onRefresh();
                    }
                });
            }
        });
        rvCarColorTop.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.left = position % 4 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 4 == 3 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.top = position < 4 ? getResources().getDimensionPixelSize(R.dimen.dp16) : 0;
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.dp16);
            }
        });

        //顶部车辆类型列表
        rvCarTypeTop = new RecyclerView(CarDepotActivity.this);
        rvCarTypeTop.setBackgroundColor(getResources().getColor(R.color.white));
        rvCarTypeTop.setLayoutManager(new GridLayoutManager(CarDepotActivity.this, 4));
        rvCarTypeTop.setAdapter(new BaseQuickAdapter<CarTypeBean, BaseViewHolder>(R.layout.item_car_type, carTypeList) {
            @Override
            protected void convert(BaseViewHolder helper, CarTypeBean item) {
                LinearLayout llRoot = helper.getView(R.id.ll_root);
                helper.setTextColor(R.id.tv_name, item.selected ? getResources().getColor(R.color.yellow_ff8f00) : getResources().getColor(R.color.gray_424242));
                llRoot.setBackgroundResource(!item.selected ? R.drawable.shape_rect_corner_2_f7f7f7 : R.drawable.shape_rect_corner_2_fff8e1);
                helper.setText(R.id.tv_name, item.label);
                llRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < carTypeList.size(); i++) {
                            if (i == helper.getPosition()) {
                                carTypeList.get(i).selected = true;
                            } else {
                                carTypeList.get(i).selected = false;
                            }
                        }
                        carTypePostion = helper.getPosition();
                        carTypePostionPre = helper.getPosition();
                        notifyDataSetChanged();
                        notifyRightFilter();
                        notifyTopFilter();
                        menuView.dismiss();
                        onRefresh();
                    }
                });
            }
        });
        rvCarTypeTop.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.left = position % 4 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 4 == 3 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.top = position < 4 ? getResources().getDimensionPixelSize(R.dimen.dp16) : 0;
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.dp16);
            }
        });

    }

    private void initFullHeight() {
        int plateColorLine = plateColorList.size() % 3 == 0 ? plateColorList.size() / 3 : (plateColorList.size() / 3 + 1);
        plateColorFullHeight = getResources().getDimensionPixelSize(R.dimen.dp56) * plateColorLine + getResources().getDimensionPixelSize(R.dimen.dp16) * (plateColorLine - 1) + getResources().getDimensionPixelSize(R.dimen.dp8);
        int carColorLine = carColorList.size() % 4 == 0 ? carColorList.size() / 4 : (carColorList.size() / 4 + 1);
        carColorFullHeight = getResources().getDimensionPixelSize(R.dimen.dp56) * carColorLine + getResources().getDimensionPixelSize(R.dimen.dp16) * (carColorLine - 1) + getResources().getDimensionPixelSize(R.dimen.dp8);
        int carTypeLine = carTypeList.size() % 4 == 0 ? carTypeList.size() / 4 : (carTypeList.size() / 4 + 1);
        carTypeFullHeight = getResources().getDimensionPixelSize(R.dimen.dp44) * carTypeLine + getResources().getDimensionPixelSize(R.dimen.dp16) * (carTypeLine - 1) + getResources().getDimensionPixelSize(R.dimen.dp8);
    }

    /**
     * 折叠右侧车牌颜色列表
     *
     * @param fold 是否折叠
     */
    private void foldPlateColorList(boolean fold) {
        if (isAnimating || plateColorListFold == fold) return;
        ValueAnimator heightAnimator;
        ValueAnimator rotationAnimator;
        if (fold) {
            heightAnimator = ValueAnimator.ofInt(plateColorFullHeight, plate_color_height);
            rotationAnimator = ValueAnimator.ofInt(270, 90);
        } else {
            heightAnimator = ValueAnimator.ofInt(plate_color_height, plateColorFullHeight);
            rotationAnimator = ValueAnimator.ofInt(90, 270);
        }
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                llPlateColorFold.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, value));
            }
        });
        rotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ivPlateColorMore.setRotation(value);
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
                plateColorListFold = !plateColorListFold;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimating = true;
            }
        });
        animatorSet.setDuration(300);
        animatorSet.playTogether(heightAnimator, rotationAnimator);
        animatorSet.start();
    }

    /**
     * 折叠右侧车辆颜色列表
     *
     * @param fold 是否折叠
     */
    private void foldCarColorList(boolean fold) {
        if (isAnimating || carColorListFold == fold) return;
        ValueAnimator heightAnimator;
        ValueAnimator rotationAnimator;
        if (fold) {
            heightAnimator = ValueAnimator.ofInt(carColorFullHeight, car_color_height);
            rotationAnimator = ValueAnimator.ofInt(270, 90);
        } else {
            heightAnimator = ValueAnimator.ofInt(car_color_height, carColorFullHeight);
            rotationAnimator = ValueAnimator.ofInt(90, 270);
        }

        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                llCarColorFold.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, value));
            }
        });
        rotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ivCarColorMore.setRotation(value);
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
                carColorListFold = !carColorListFold;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimating = true;
            }
        });
        animatorSet.setDuration(300);
        animatorSet.playTogether(heightAnimator, rotationAnimator);
        animatorSet.start();
    }

    /**
     * 折叠右侧车牌类型列表
     *
     * @param fold 是否折叠
     */
    private void foldCarTypeList(boolean fold) {
        if (isAnimating || carTypeListFold == fold) return;
        ValueAnimator heightAnimator;
        ValueAnimator rotationAnimator;
        if (fold) {
            heightAnimator = ValueAnimator.ofInt(carTypeFullHeight, car_type_height);
            rotationAnimator = ValueAnimator.ofInt(270, 90);
        } else {
            heightAnimator = ValueAnimator.ofInt(car_type_height, carTypeFullHeight);
            rotationAnimator = ValueAnimator.ofInt(90, 270);
        }

        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                llCarTypeFold.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, value));
            }
        });
        rotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ivCarTypeMore.setRotation(value);
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
                carTypeListFold = !carTypeListFold;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimating = true;
            }
        });
        animatorSet.setDuration(300);
        animatorSet.playTogether(heightAnimator, rotationAnimator);
        animatorSet.start();
    }

    /**
     * 更新顶部文字、箭头
     */
    private void notifyTopFilter() {
        if (rangeList.size() > 0) {
            if (rangeList.get(0).isSelect()) {
                tvRange.setText(R.string.range);
                tvRange.setTextColor(getResources().getColor(R.color.gray_212121));
                tvRange.setCompoundDrawables(null, null, mFilterArrowDownBlackDrawable, null);
            } else {
                for (DailyFilterItemEntity entity : rangeList) {
                    if (entity.isSelect())
                        tvRange.setText(entity.getName());
                }
                tvRange.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                tvRange.setCompoundDrawables(null, null, mFilterArrowDownYellowDrawable, null);
            }
        }
        if (plateColorList.size() > 0) {
            if (plateColorList.get(0).selected) {
                tvPlateColor.setText(R.string.plate_color);
                tvPlateColor.setTextColor(getResources().getColor(R.color.gray_212121));
                tvPlateColor.setCompoundDrawables(null, null, mFilterArrowDownBlackDrawable, null);
            } else {
                for (PlateColorBean bean : plateColorList) {
                    if (bean.selected)
                        tvPlateColor.setText(bean.name);
                }
                tvPlateColor.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                tvPlateColor.setCompoundDrawables(null, null, mFilterArrowDownYellowDrawable, null);
            }
        }
        if (carColorList.size() > 0) {

            if (carColorList.get(0).selected) {
                tvCarColor.setText(R.string.car_color);
                tvCarColor.setTextColor(getResources().getColor(R.color.gray_212121));
                tvCarColor.setCompoundDrawables(null, null, mFilterArrowDownBlackDrawable, null);
            } else {
                for (CarColorBean bean : carColorList) {
                    if (bean.selected)
                        tvCarColor.setText(bean.name);
                }
                tvCarColor.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                tvCarColor.setCompoundDrawables(null, null, mFilterArrowDownYellowDrawable, null);
            }
        }
        if (carTypeList.size() > 0) {
            if (carTypeList.get(0).selected) {
                tvCarType.setText(R.string.car_type);
                tvCarType.setTextColor(getResources().getColor(R.color.gray_212121));
                tvCarType.setCompoundDrawables(null, null, mFilterArrowDownBlackDrawable, null);
            } else {
                for (CarTypeBean bean : carTypeList) {
                    if (bean.selected)
                        tvCarType.setText(bean.label);
                }
                tvCarType.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                tvCarType.setCompoundDrawables(null, null, mFilterArrowDownYellowDrawable, null);
            }
        }
    }

    /**
     * 更新右侧列表上面文字、箭头
     */
    private void notifyRightFilter() {
        if (rangeList.get(0).isSelect()) {
            tvRangeAll.setSelected(true);
            tvRangeCollect.setSelected(false);
        } else {
            tvRangeAll.setSelected(false);
            tvRangeCollect.setSelected(true);
        }
        rvPlateColor.getAdapter().notifyDataSetChanged();
        rvCarColor.getAdapter().notifyDataSetChanged();
        rvCarType.getAdapter().notifyDataSetChanged();
        rvCarBrand.getAdapter().notifyDataSetChanged();
        if (plateColorList.size() > 0) {
            if (plateColorList.get(0).selected) {
                tvPlateColorMore.setTextColor(getResources().getColor(R.color.gray_9e9e9e));
                tvPlateColorMore.setText(plateColorList.get(0).name);
            } else {
                tvPlateColorMore.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                for (int i = 0; i < plateColorList.size(); i++) {
                    if (plateColorList.get(i).selected)
                        tvPlateColorMore.setText(plateColorList.get(i).name);
                }
            }
        }
        if (carColorList.size() > 0) {

            if (carColorList.get(0).selected) {
                tvCarColorMore.setTextColor(getResources().getColor(R.color.gray_9e9e9e));
                tvCarColorMore.setText(carColorList.get(0).name);
            } else {
                tvCarColorMore.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                for (int i = 0; i < carColorList.size(); i++) {
                    if (carColorList.get(i).selected)
                        tvCarColorMore.setText(carColorList.get(i).name);
                }
            }
        }
        if (carTypeList.size() > 0) {
            if (carTypeList.get(0).selected) {
                tvCarTypeMore.setTextColor(getResources().getColor(R.color.gray_9e9e9e));
                tvCarTypeMore.setText(carTypeList.get(0).label);
            } else {
                tvCarTypeMore.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                for (int i = 0; i < carTypeList.size(); i++) {
                    if (carTypeList.get(i).selected)
                        tvCarTypeMore.setText(carTypeList.get(i).label);
                }
            }
        }
    }

    @Override
    public void showLoading(String message) {
        if (srl != null) {
            srl.setRefreshing(true);
        }
    }

    @Override
    public void hideLoading() {
        if (srl != null) {
            srl.setRefreshing(false);
        }
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

    @OnClick({R.id.head_left_iv, R.id.tv_search, R.id.ll_range, R.id.ll_plate_color, R.id.ll_car_color
            , R.id.ll_car_type, R.id.ll_filter,})
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.head_left_iv:
                onBackPressedSupport();
                break;
            case R.id.tv_search:
                ActivityOptions activityOptions = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), tvSearch, "searchText");
                    startActivity(new Intent(CarDepotActivity.this, CarDepotSearchActivity.class), activityOptions.toBundle());
                } else {
                    startActivity(new Intent(CarDepotActivity.this, CarDepotSearchActivity.class));
                }
                break;
            case R.id.ll_range:
                menuView = new MenuView(getActivity(), rvCollect, rangeList.size() * getResources().getDimensionPixelSize(R.dimen.dp40) + rangeList.size() - 1);
                menuView.show(viewDivider);
                if (rangeList.get(0).isSelect()) {
                    tvRange.setCompoundDrawables(null, null, mFilterArrowUpBlackDrawable, null);
                } else {
                    tvRange.setCompoundDrawables(null, null, mFilterArrowUpYellowDrawable, null);
                }
                hideFilter();
                break;
            case R.id.ll_plate_color:
                if (plateColorList.size() == 0) return;
                menuView = new MenuView(getActivity(), rvPlateColorTop, plateColorList.size() / 3 * getResources().getDimensionPixelSize(R.dimen.dp72) + getResources().getDimensionPixelSize(R.dimen.dp16));
                menuView.show(viewDivider);
                if (plateColorList.get(0).selected) {
                    tvPlateColor.setCompoundDrawables(null, null, mFilterArrowUpBlackDrawable, null);
                } else {
                    tvPlateColor.setCompoundDrawables(null, null, mFilterArrowUpYellowDrawable, null);
                }
                hideFilter();
                break;
            case R.id.ll_car_color:
                if (carColorList.size() == 0) return;
                menuView = new MenuView(getActivity(), rvCarColorTop, carColorList.size() / 4 * getResources().getDimensionPixelSize(R.dimen.dp72) + getResources().getDimensionPixelSize(R.dimen.dp16));
                menuView.show(viewDivider);
                if (carColorList.get(0).selected) {
                    tvCarColor.setCompoundDrawables(null, null, mFilterArrowUpBlackDrawable, null);
                } else {
                    tvCarColor.setCompoundDrawables(null, null, mFilterArrowUpYellowDrawable, null);
                }
                hideFilter();
                break;
            case R.id.ll_car_type:
                if (carTypeList.size() == 0) return;
                menuView = new MenuView(getActivity(), rvCarTypeTop, carTypeList.size() / 4 * getResources().getDimensionPixelSize(R.dimen.dp56) + getResources().getDimensionPixelSize(R.dimen.dp16));
                menuView.show(viewDivider);
                if (carTypeList.get(0).selected) {
                    tvCarType.setCompoundDrawables(null, null, mFilterArrowUpBlackDrawable, null);
                } else {
                    tvCarType.setCompoundDrawables(null, null, mFilterArrowUpYellowDrawable, null);
                }
                hideFilter();
                break;
            case R.id.ll_filter:
                if (rightOpen) {
                    hideFilter();
                } else {
                    showFilter();
                }
                break;

        }
    }

    /**
     * 关闭右侧筛选
     */
    private void hideFilter() {
        if (menuView != null) {
            menuView.setOnDismissListener(new MenuView.OnDismissListener() {
                @Override
                public void onDismiss() {
                    notifyTopFilter();
                }
            });
        }
        if (!rightOpen) return;
        tvFilter.setTextColor(getResources().getColor(R.color.gray_212121));
        tvFilter.setCompoundDrawables(mFilterBlackDrawable, null, null, null);
        if (rightMenu != null && rightMenu.getStatus() == RightMenu.SHOW) {
            rightMenu.dismiss();
        }
        rightOpen = false;
        rangePositionPre = rangePosition;
        plateColorPositionPre = plateColorPosition;
        carColorPositionPre = carColorPosition;
        carTypePostionPre = carTypePostion;
        preCarBrand = setCarBrand;
    }

    /**
     * 打开右侧筛选
     */
    private void showFilter() {
        if (rightOpen) return;
        tvFilter.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
        tvFilter.setCompoundDrawables(mFilterYellowDrawable, null, null, null);
        if (rightMenu == null) {
            rightMenu = new RightMenu(CarDepotActivity.this, shaixuanLl);
        }
        rightMenu.setOnDismissListener(new RightMenu.OnDismissListener() {
            @Override
            public void onDismiss() {
                hideFilter();
//                tvFilter.setTextColor(getResources().getColor(R.color.gray_212121));
//                tvFilter.setCompoundDrawables(mFilterBlackDrawable, null, null, null);
//                rightOpen = false;
            }
        });
        scrollView.scrollTo(0, 0);
        rightMenu.show(llFilter);

        rightOpen = true;

        if (rangeList.get(0).isSelect()) {
            tvRangeAll.setSelected(true);
            tvRangeCollect.setSelected(false);
        } else {
            tvRangeAll.setSelected(false);
            tvRangeCollect.setSelected(true);
        }

        if (timeSet) {
            tvStartTimeChoice.setVisibility(View.GONE);
            tvStartTime.setText(setStartYear + "-" + FormatUtils.twoNumber(setStartMonthOfYear) + "-" + FormatUtils.twoNumber(setStartDayOfMonth) + " " + FormatUtils.twoNumber(setStartHour) + ":" + FormatUtils.twoNumber(setStartMinute));
            tvEndTimeChoice.setVisibility(View.GONE);
            tvEndTime.setText(setEndYear + "-" + FormatUtils.twoNumber(setEndMonthOfYear) + "-" + FormatUtils.twoNumber(setEndDayOfMonth) + " " + FormatUtils.twoNumber(setEndHour) + ":" + FormatUtils.twoNumber(setEndMinute));
        } else {
            tvStartTimeChoice.setVisibility(View.VISIBLE);
            tvEndTimeChoice.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < plateColorListRight.size(); i++) {
            plateColorListRight.get(i).selected = i == plateColorPosition;
        }
        for (int i = 0; i < carColorListRight.size(); i++) {
            carColorListRight.get(i).selected = i == carColorPosition;
        }
        for (int i = 0; i < carTypeListRight.size(); i++) {
            carTypeListRight.get(i).selected = i == carTypePostion;
        }
        for (int i = 0; i < carBrandList.size(); i++) {
            carBrandList.get(i).selected = setCarBrand.code == carBrandList.get(i).code;
        }
        notifyRightFilter();
        rvPlateColor.getAdapter().notifyDataSetChanged();
        rvCarColor.getAdapter().notifyDataSetChanged();
        rvCarType.getAdapter().notifyDataSetChanged();
        rvCarBrand.getAdapter().notifyDataSetChanged();
    }

    /**
     * 显式时间选择框
     *
     * @param type 开始或结束
     */
    private void showTimePickerDialog(int type) {
        new LmTimePickerDialog(CarDepotActivity.this, 0, (view, hourOfDay, minute) -> {
            if (type == START_TIME) {
                tempStartHour = hourOfDay;
                tempStartMinute = minute;
                tvStartTimeChoice.setVisibility(View.GONE);
                tvStartTime.setText(tempStartYear + "-" + FormatUtils.twoNumber(tempStartMonthOfYear) + "-" + FormatUtils.twoNumber(tempStartDayOfMonth) + " " + FormatUtils.twoNumber(tempStartHour) + ":" + FormatUtils.twoNumber(tempStartMinute));
            } else {
                tempEndHour = hourOfDay;
                tempEndMinute = minute;
                tvEndTimeChoice.setVisibility(View.GONE);
                tvEndTime.setText(tempEndYear + "-" + FormatUtils.twoNumber(tempEndMonthOfYear) + "-" + FormatUtils.twoNumber(tempEndDayOfMonth) + " " + FormatUtils.twoNumber(tempEndHour) + ":" + FormatUtils.twoNumber(tempEndMinute));
            }
            timeModify = true;
        }, type == START_TIME ? tempStartHour : tempEndHour, type == START_TIME ? tempStartMinute : tempEndMinute, true).show();
    }

    /**
     * 重置右侧临时展示的时间
     */
    private void resetTempTime() {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        tempStartYear = year;
        tempStartMonthOfYear = month + 1;
        tempStartDayOfMonth = day - 3;
        tempStartHour = hour;
        tempStartMinute = minute;
        tempEndYear = year;
        tempEndMonthOfYear = month + 1;
        tempEndDayOfMonth = day;
        tempEndHour = hour;
        tempEndMinute = minute;

        timeModify = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            CarBrandBean brandBean = data.getParcelableExtra("brandBean");
            for (int i = 0; i < carBrandList.size(); i++) {
                if (TextUtils.equals(carBrandList.get(i).name, brandBean.name)) {
                    carBrandList.get(i).selected = true;
                } else {
                    carBrandList.get(i).selected = false;
                }
            }
            rvCarBrand.getAdapter().notifyDataSetChanged();
            preCarBrand = brandBean;
            tvCarBrandMore.setText(preCarBrand.name);
            if ("全部".equals(preCarBrand.name)) {
                tvCarBrandMore.setTextColor(getResources().getColor(R.color.gray_9e9e9e));
            } else {
                tvCarBrandMore.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
            }
        }
    }

    @Override
    public void onBackPressedSupport() {
        if (rightOpen) {
            hideFilter();
            return;
        }
        super.onBackPressedSupport();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showList(List<CarDepotEntity> list) {
        mAdapter.setNewData(list);
        if (list == null || list.isEmpty()) {
            hasMore = false;
            rlEmpty.setVisibility(View.VISIBLE);
        } else {
            rlEmpty.setVisibility(View.GONE);
            tvNoNetwork.setVisibility(View.GONE);
            hasMore = list.size() >= pageSize;
            if (!hasMore) {
                mAdapter.loadMoreEnd(false);//加载完毕
            } else {
                mAdapter.loadMoreComplete();//加载中
            }
        }
    }

    @Override
    public void showMore(List<CarDepotEntity> list) {
        hasMore = list.size() >= pageSize;
        mAdapter.addData(list);
        if (!hasMore) {
            mAdapter.loadMoreEnd(false);//加载完毕
        } else {
            mAdapter.loadMoreComplete();//加载中
        }
    }

    @Override
    public void onError() {
        if (pageNum > 1) {
            pageNum--;
            if (mAdapter != null) mAdapter.loadMoreFail();
        } else {
            tvNoNetwork.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGetInfoSuccess(CarInfo carInfo) {
        plateColorList.addAll(carInfo.vehiclePlateVo);
        plateColorListRight.addAll(carInfo.vehiclePlateVo);
        carColorList.addAll(carInfo.vehicleBodyVo);
        carColorListRight.addAll(carInfo.vehicleBodyVo);
        carTypeList.addAll(carInfo.vehicleTypeVo);
        carTypeListRight.addAll(carInfo.vehicleTypeVo);
        carBrandList.addAll(generateBrand());
        carBrandList.addAll(carInfo.vehicleInfo.subList(0, 5));
        setCarBrand = carBrandList.get(0);
        preCarBrand = carBrandList.get(0);
        rvCarBrand.getAdapter().notifyDataSetChanged();
        rvCarType.getAdapter().notifyDataSetChanged();
        rvCarColor.getAdapter().notifyDataSetChanged();
        rvPlateColor.getAdapter().notifyDataSetChanged();
        rvCarTypeTop.getAdapter().notifyDataSetChanged();
        rvCarColorTop.getAdapter().notifyDataSetChanged();
        rvPlateColorTop.getAdapter().notifyDataSetChanged();
        initFullHeight();
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

    /**
     * 生成右侧展示用车辆品牌
     *
     * @return
     */
    private List<CarBrandBean> generateBrand() {
        String json = "[{id:null,name:全部,code:null,simpleSpelling:null,fullSpelling:null,remark:null,isDelete:null,pic:null}]";
        return new Gson().fromJson(json, new TypeToken<List<CarBrandBean>>() {
        }.getType());
    }

    @Override
    public void onLoadMoreRequested() {
        if (hasMore) {
            pageNum++;
            getList();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_plate_color_more:
                foldPlateColorList(!plateColorListFold);

                break;
            case R.id.ll_car_color_more:
                foldCarColorList(!carColorListFold);
                break;
            case R.id.ll_car_type_more:
                foldCarTypeList(!carTypeListFold);

                break;
            case R.id.ll_car_brand_more:
                Intent intent = new Intent(CarDepotActivity.this, CarBrandActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.tv_range_all:
                rangePositionPre = 0;
                tvRangeAll.setSelected(true);
                tvRangeCollect.setSelected(false);
                break;
            case R.id.tv_range_collect:
                rangePositionPre = 1;
                tvRangeAll.setSelected(false);
                tvRangeCollect.setSelected(true);
                break;
            case R.id.tv_start_time_choice:
            case R.id.ll_start_time:
                lmDatePickDialog = new LmDatePickDialog(CarDepotActivity.this, (view12, year, month, dayOfMonth) -> {
                    tempStartYear = year;
                    tempStartMonthOfYear = month + 1;
                    tempStartDayOfMonth = dayOfMonth;
                    lmDatePickDialog.dismiss();
                    showTimePickerDialog(START_TIME);
                }, null, setStartYear, setStartMonthOfYear - 1, setStartDayOfMonth);
                lmDatePickDialog.getDatePicker().setMinDate(System.currentTimeMillis()-30L*24*60*60*1000);
                lmDatePickDialog.show();
                break;
            case R.id.tv_end_time_choice:
            case R.id.ll_end_time:
                lmDatePickDialog = new LmDatePickDialog(CarDepotActivity.this, (view12, year, month, dayOfMonth) -> {
                    tempEndYear = year;
                    tempEndMonthOfYear = month + 1;
                    tempEndDayOfMonth = dayOfMonth;
                    lmDatePickDialog.dismiss();
                    showTimePickerDialog(END_TIME);
                }, null, tempEndYear, tempEndMonthOfYear - 1, tempEndDayOfMonth);
                lmDatePickDialog.getDatePicker().setMinDate(System.currentTimeMillis()-30L*24*60*60*1000);
                lmDatePickDialog.show();
                break;
            case R.id.clean_tv:
                resetTempTime();
                startTime = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000);
                endTime = System.currentTimeMillis();
                tvRangeAll.setSelected(true);
                tvRangeCollect.setSelected(false);
                rangePositionPre = 0;
                plateColorPositionPre = 0;
                carColorPositionPre = 0;
                carTypePostionPre = 0;
                preCarBrand = carBrandList.get(0);
                tvPlateColorMore.setTextColor(getResources().getColor(R.color.gray_9e9e9e));
                tvPlateColorMore.setText(plateColorListRight.get(plateColorPositionPre).name);
                for (int i = 0; i < plateColorListRight.size(); i++) {
                    plateColorListRight.get(i).selected = i == plateColorPositionPre;
                }
                rvPlateColor.getAdapter().notifyDataSetChanged();
                tvCarColorMore.setTextColor(getResources().getColor(R.color.gray_9e9e9e));
                tvCarColorMore.setText(carColorListRight.get(carColorPositionPre).name);
                for (int i = 0; i < carColorListRight.size(); i++) {
                    carColorListRight.get(i).selected = i == carColorPositionPre;
                }
                rvCarColor.getAdapter().notifyDataSetChanged();
                tvCarTypeMore.setTextColor(getResources().getColor(R.color.gray_9e9e9e));
                tvCarTypeMore.setText(carTypeListRight.get(carTypePostionPre).label);
                for (int i = 0; i < carTypeListRight.size(); i++) {
                    carTypeListRight.get(i).selected = i == carTypePostionPre;
                }
                rvCarType.getAdapter().notifyDataSetChanged();
                tvCarBrandMore.setTextColor(getResources().getColor(R.color.gray_9e9e9e));
                tvCarBrandMore.setText(preCarBrand.name);
                for (int i = 0; i < carBrandList.size(); i++) {
                    carBrandList.get(i).selected = preCarBrand.code == carBrandList.get(i).code;
                }
                rvCarBrand.getAdapter().notifyDataSetChanged();
                onClick(cofirmTv);
                break;
            case R.id.cofirm_tv:
                if (tvStartTimeChoice.getVisibility() == View.VISIBLE && tvEndTimeChoice.getVisibility() == View.GONE) {
                    ToastUtils.showShort("请选择开始时间");
                    return;
                }
                if (tvEndTimeChoice.getVisibility() == View.VISIBLE && tvStartTimeChoice.getVisibility() == View.GONE) {
                    ToastUtils.showShort("请选择结束时间");
                    return;
                }

                calendar.set(tempStartYear, tempStartMonthOfYear - 1, tempStartDayOfMonth, tempStartHour, tempStartMinute);
                long start = calendar.getTimeInMillis();
                calendar.set(tempEndYear, tempEndMonthOfYear - 1, tempEndDayOfMonth, tempEndHour, tempEndMinute);

                long end = calendar.getTimeInMillis();
                if (end <= start) {
                    ToastUtils.showLong(getText(R.string.time_set_error_tip));
                    return;
                }
                setStartYear = tempStartYear;
                setStartMonthOfYear = tempStartMonthOfYear;
                setStartDayOfMonth = tempStartDayOfMonth;
                setStartHour = tempStartHour;
                setStartMinute = tempStartMinute;
                setEndYear = tempEndYear;
                setEndMonthOfYear = tempEndMonthOfYear;
                setEndDayOfMonth = tempEndDayOfMonth;
                setEndHour = tempEndHour;
                setEndMinute = tempEndMinute;
                calendar.set(setStartYear, setStartMonthOfYear - 1, setStartDayOfMonth, setStartHour, setStartMinute);
                startTime = calendar.getTimeInMillis();
                calendar.set(setEndYear, setEndMonthOfYear - 1, setEndDayOfMonth, setEndHour, setEndMinute);
                endTime = calendar.getTimeInMillis();

                timeSet = timeModify;
                for (int i = 0; i < rangeList.size(); i++) {
                    if (i == rangePositionPre) {
                        rangeList.get(i).setSelect(true);
                    } else {
                        rangeList.get(i).setSelect(false);
                    }
                }
                rangePosition = rangePositionPre;
                for (int i = 0; i < plateColorList.size(); i++) {
                    if (i == plateColorPositionPre) {
                        plateColorList.get(i).selected = true;
                    } else {
                        plateColorList.get(i).selected = false;
                    }
                }
                plateColorPosition = plateColorPositionPre;
                for (int i = 0; i < carColorList.size(); i++) {
                    if (i == carColorPositionPre) {
                        carColorList.get(i).selected = true;
                    } else {
                        carColorList.get(i).selected = false;
                    }
                }
                carColorPosition = carColorPositionPre;
                for (int i = 0; i < carTypeList.size(); i++) {
                    if (i == carTypePostionPre) {
                        carTypeList.get(i).selected = true;
                    } else {
                        carTypeList.get(i).selected = false;
                    }
                }
                carTypePostion = carTypePostionPre;
                setCarBrand = preCarBrand;
                notifyTopFilter();
                hideFilter();
                onRefresh();
                break;
        }
    }
}
