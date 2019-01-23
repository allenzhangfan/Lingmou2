package cloud.antelope.lingmou.mvp.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cjt2325.cameralibrary.util.ScreenUtils;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.modle.PermissionDialog;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lzy.imagepicker.ImagePicker;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.PackageUtil;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.app.utils.loader.GlideImageLoader;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerWorkbenchComponent;
import cloud.antelope.lingmou.di.module.WorkbenchModule;
import cloud.antelope.lingmou.mvp.contract.WorkbenchContract;
import cloud.antelope.lingmou.mvp.model.entity.DetailCommonEntity;
import cloud.antelope.lingmou.mvp.model.entity.WorkbenchBean;
import cloud.antelope.lingmou.mvp.presenter.WorkbenchPresenter;
import cloud.antelope.lingmou.mvp.ui.activity.BodyDepotActivity;
import cloud.antelope.lingmou.mvp.ui.activity.CarDepotActivity;
import cloud.antelope.lingmou.mvp.ui.activity.CloudSearchActivity;
import cloud.antelope.lingmou.mvp.ui.activity.DeployControlActivity;
import cloud.antelope.lingmou.mvp.ui.activity.EventRemindActivity;
import cloud.antelope.lingmou.mvp.ui.activity.FaceDepotActivity;
import cloud.antelope.lingmou.mvp.ui.activity.LmCaptureActivity;
import cloud.antelope.lingmou.mvp.ui.activity.NoPermissionActivity;
import cloud.antelope.lingmou.mvp.ui.activity.RealVideosListActivity;
import cloud.antelope.lingmou.mvp.ui.activity.SoloActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.WorkbenchAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.SizeChangeAppreciableViewPager;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import uk.co.senab.photoview.PhotoView;

public class WorkbenchFragment extends BaseFragment<WorkbenchPresenter> implements WorkbenchContract.View,
        PermissionUtils.HasPermission,
        EasyPermissions.PermissionCallbacks {
    //    @BindView(R.id.workbench_rclv)
//    RecyclerView mWorkbenchRclv;
    @BindView(R.id.cloud_search_tv)
    TextView cloudSearchTv;
    @BindView(R.id.vp)
    SizeChangeAppreciableViewPager vp;
    @BindView(R.id.ll_container)
    LinearLayout llContainer;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;


    private ImagePicker mImagePicker;
    private boolean video;
    private boolean searchPic;
    private boolean personTrack;
    private boolean face;
    private boolean body;
    private boolean car;
    private boolean broadcast;
    private boolean eventRemind;
    private boolean solo;
    private int iconSize = 6;
    private List<WorkbenchBean> workbenchBeans;
    //选中显示Indicator
    private int selectRes = R.drawable.shape_yellow_circle;
    //非选中显示Indicator
    private int unSelcetRes = R.drawable.shape_grey_circle;
    private boolean iconInit;

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
        DaggerWorkbenchComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .workbenchModule(new WorkbenchModule(this))
                .build()
                .inject(this);
    }

    public static WorkbenchFragment newInstance() {
        Bundle args = new Bundle();
        WorkbenchFragment fragment = new WorkbenchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workbench, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        intPrevileges();
        initAppData();
        vp.setOnSizeChangedListener(new SizeChangeAppreciableViewPager.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int height, int width) {
                vp.post(new Runnable() {
                    @Override
                    public void run() {
                        initIcon(height);
                    }
                });
            }
        });
    }


    private void initIcon(int height) {
        if (iconInit) return;
        if (height != 0) {
            int cont = height / getResources().getDimensionPixelOffset(R.dimen.dp154) * 3;
            if (cont != 0) {
                iconSize = cont;
            }
        }
        vp.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return workbenchBeans.size() % iconSize == 0 ? workbenchBeans.size() / iconSize : (workbenchBeans.size() / iconSize + 1);
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int vpPosition) {
                RecyclerView mWorkbenchRclv = (RecyclerView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_recycleview, null, face);
                ArrayList<WorkbenchBean> list = new ArrayList<>();
                for (int j = 0; j < workbenchBeans.size(); j++) {
                    if (j < (vpPosition + 1) * iconSize && j >= vpPosition * iconSize) {
                        list.add(workbenchBeans.get(j));
                    }
                }
                container.addView(mWorkbenchRclv);
                mWorkbenchRclv.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                mWorkbenchRclv.setItemAnimator(new DefaultItemAnimator());
                WorkbenchAdapter workbenchAdapter = new WorkbenchAdapter(list);
                mWorkbenchRclv.setAdapter(workbenchAdapter);
                mWorkbenchRclv.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        int position = parent.getChildAdapterPosition(view);
                        outRect.top = getResources().getDimensionPixelSize(R.dimen.dp8);
                        outRect.bottom = getResources().getDimensionPixelSize(R.dimen.dp8);
//                outRect.left = position % 3 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
//                outRect.right = position % 3 == 2 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                    }
                });
                workbenchAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        switch (workbenchBeans.get(position + vpPosition * iconSize).getId()) {
                            case R.id.workbench_videos:
                                if (video) {
                                    MobclickAgent.onEvent(getContext(), "liveVideo");
                                    startActivity(new Intent(getActivity(), RealVideosListActivity.class));
                                } else {
                                    //没有权限，进入无权限页面
//                            permissionIntent.putExtra("title", getString(R.string.real_time_video));
//                            startActivity(permissionIntent);
                                    ToastUtils.showShort(getString(R.string.hint_no_permission));
                                }
                                break;
                            case R.id.workbench_search_pic:
                                if (searchPic) {
                                    startFaceRecorg();
                                } else {
                                    //没有权限，进入无权限页面
//                            permissionIntent.putExtra("title", getString(R.string.search_image));
//                            startActivity(permissionIntent);
                                    ToastUtils.showShort(getString(R.string.hint_no_permission));
                                }
                                break;
                            case R.id.workbench_track_person:
                                if (personTrack) {
                                    MobclickAgent.onEvent(getContext(), "linkongManage");
                                    startActivity(new Intent(getActivity(), DeployControlActivity.class));
                                } else {
                                    //没有权限，进入无权限页面
//                            permissionIntent.putExtra("title", getString(R.string.control_management));
//                            startActivity(permissionIntent);
                                    ToastUtils.showShort(getString(R.string.hint_no_permission));
                                }
                                break;
                            case R.id.workbench_face_depot:
                                //进入人脸图库界面
                                if (face) {
                                    MobclickAgent.onEvent(getContext(), "faceLibrary");
                                    startActivity(new Intent(getContext(), FaceDepotActivity.class));
                                } else {
                                    //没有权限，进入无权限页面
//                            permissionIntent.putExtra("title", getString(R.string.face_depot_title));
//                            startActivity(permissionIntent);
                                    ToastUtils.showShort(getString(R.string.hint_no_permission));
                                }
                                break;
                            case R.id.workbench_body_depot:
                                //进入人体图库
                                if (body) {
                                    MobclickAgent.onEvent(getContext(), "bodyLibrary");
                                    startActivity(new Intent(getContext(), BodyDepotActivity.class));
                                } else {
                                    //没有权限，进入无权限页面
//                            permissionIntent.putExtra("title", getString(R.string.body_depot_title));
//                            startActivity(permissionIntent);
                                    ToastUtils.showShort(getString(R.string.hint_no_permission));
                                }
                                break;
                            case R.id.workbench_broadcast:
                                //进入广播
                                if (broadcast) {
                                    MobclickAgent.onEvent(getContext(), "broadcast");
                                    String mPackageName = "gqz.pagertest";
                                    if (PackageUtil.isPkgInstalled(getActivity(), mPackageName)) {
                                        String mActivityName = "gqz.pager.View.Activity.LaunchActivity";
                                        Intent paIntent = new Intent();
                                        paIntent.setComponent(new ComponentName(mPackageName, mActivityName));
//                                paIntent.putExtra("username", "root");
//                                paIntent.putExtra("password", "123456!");
                                        paIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                paIntent.putExtra("username", SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_REAL_NAME));
//                                paIntent.putExtra("SN", sn);
                                        startActivity(paIntent);
                                    } else {
                                        ToastUtils.showShort("未安装“互联网广播”，请联系管理员获取");
                                    }
                                }
                                break;
                            case R.id.workbench_event_remind:
                                if (eventRemind) {
                                    MobclickAgent.onEvent(getContext(), "eventRemind");
                                    startActivity(new Intent(getContext(), EventRemindActivity.class));
                                } else {
                                    ToastUtils.showShort(getString(R.string.hint_no_permission));
                                }

                                break;
                            case R.id.workbench_solo:
                                if (solo) {
                                    MobclickAgent.onEvent(getContext(), "solo");
                                    startActivity(new Intent(getContext(), SoloActivity.class));
                                } else {
                                    ToastUtils.showShort(getString(R.string.hint_no_permission));
                                }

                                break;
                            case R.id.workbench_car_depot:
                                if (car) {
                                    MobclickAgent.onEvent(getContext(), "carLibrary");
                                    startActivity(new Intent(getContext(), CarDepotActivity.class));
                                } else {
                                    ToastUtils.showShort(getString(R.string.hint_no_permission));
                                }


                                break;
                        }
                    }
                });
                return mWorkbenchRclv;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//                super.destroyItem(container, position, object);
            }
        });
        if (llContainer.getChildCount() != 0) {
            llContainer.removeAllViewsInLayout();
        }
        for (int i = 0; i < vp.getAdapter().getCount(); i++) {
            View dot = new View(getContext());
            dot.setBackgroundResource(i == 0 ? selectRes : unSelcetRes);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    SizeUtils.dp2px(6),
                    SizeUtils.dp2px(6));
            params.leftMargin = i == 0 ? 0 : SizeUtils.dp2px(12);
            dot.setLayoutParams(params);
            dot.setEnabled(false);
            llContainer.addView(dot);
        }
        if (vp.getAdapter().getCount() == 1) {
            llContainer.setVisibility(View.GONE);
        }
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                position = position % vp.getAdapter().getCount();
                for (int i = 0; i < llContainer.getChildCount(); i++) {
                    llContainer.getChildAt(i).setBackgroundResource(unSelcetRes);
                }
                llContainer.getChildAt(position).setBackgroundResource(selectRes);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        iconInit = true;
    }

    private void intPrevileges() {
        SPUtils utils = SPUtils.getInstance();
        video = utils.getBoolean(Constants.PERMISSION_HAS_VIDEO_LIVE);
        searchPic = utils.getBoolean(Constants.PERMISSION_HAS_HYJJ);
        personTrack = utils.getBoolean(Constants.PERMISSION_HAS_PERSON_TRACKING);
        face = utils.getBoolean(Constants.PERMISSION_HAS_FACE);
        body = utils.getBoolean(Constants.PERMISSION_HAS_BODY);
        broadcast = utils.getBoolean(Constants.PERMISSION_HAS_BROADCAST);
        eventRemind = utils.getBoolean(Constants.PERMISSION_EVENT_REMIND);
        solo = utils.getBoolean(Constants.PERMISSION_HAS_SOLO);
        car = utils.getBoolean(Constants.PERMISSION_HAS_CAR);
    }

    @AfterPermissionGranted(PermissionUtils.RC_CAMERA_PERM)
    public void startFaceRecorg() {
        PermissionUtils.cameraTask(this);
    }

    private void initAppData() {
        workbenchBeans = new ArrayList<>();
        WorkbenchBean bean0 = new WorkbenchBean();
        bean0.setName(getResources().getString(R.string.real_time_video));
        bean0.setId(R.id.workbench_videos);
        bean0.setBgDrawable(video ? R.drawable.shape_workbench_video_bg : R.drawable.shape_workbench_other_more_bg);
        bean0.setIvDrawable(video ? R.drawable.livestreaming_normal : R.drawable.livestreaming_noright);
        bean0.setTextColor(video ? getResources().getColor(R.color.gray_212121) : getResources().getColor(R.color.gray_9e9e9e));
        workbenchBeans.add(bean0);
        WorkbenchBean bean1 = new WorkbenchBean();
        bean1.setName(getResources().getString(R.string.search_by_picture));
        bean1.setId(R.id.workbench_search_pic);
        bean1.setBgDrawable(searchPic ? R.drawable.shape_workbench_search_pic_bg : R.drawable.shape_workbench_other_more_bg);
        bean1.setIvDrawable(searchPic ? R.drawable.imagesearch_normal : R.drawable.imagesearch_noright);
        bean1.setTextColor(searchPic ? getResources().getColor(R.color.gray_212121) : getResources().getColor(R.color.gray_9e9e9e));
        workbenchBeans.add(bean1);
        WorkbenchBean bean2 = new WorkbenchBean();
        bean2.setName(getResources().getString(R.string.track_person));
        bean2.setId(R.id.workbench_track_person);
        bean2.setBgDrawable(personTrack ? R.drawable.shape_workbench_track_person_bg : R.drawable.shape_workbench_other_more_bg);
        bean2.setIvDrawable(personTrack ? R.drawable.workbench_track_person : R.drawable.icon_person_tracking);
        bean2.setTextColor(personTrack ? getResources().getColor(R.color.gray_212121) : getResources().getColor(R.color.gray_9e9e9e));
        workbenchBeans.add(bean2);
        WorkbenchBean bean3 = new WorkbenchBean();
        bean3.setName(getResources().getString(R.string.face_depot_title));
        bean3.setId(R.id.workbench_face_depot);
        bean3.setBgDrawable(face ? R.drawable.shape_workbench_face_depot_bg : R.drawable.shape_workbench_other_more_bg);
        bean3.setIvDrawable(face ? R.drawable.workbench_face_depot : R.drawable.icon_face_library);
        bean3.setTextColor(face ? getResources().getColor(R.color.gray_212121) : getResources().getColor(R.color.gray_9e9e9e));
        workbenchBeans.add(bean3);
        WorkbenchBean bean4 = new WorkbenchBean();
        bean4.setName(getResources().getString(R.string.body_depot_title));
        bean4.setId(R.id.workbench_body_depot);
        bean4.setBgDrawable(body ? R.drawable.shape_workbench_body_depot_bg : R.drawable.shape_workbench_other_more_bg);
        bean4.setIvDrawable(body ? R.drawable.workbench_body_depot : R.drawable.icon_body_library);
        bean4.setTextColor(body ? getResources().getColor(R.color.gray_212121) : getResources().getColor(R.color.gray_9e9e9e));
        workbenchBeans.add(bean4);
        WorkbenchBean bean7 = new WorkbenchBean();
        bean7.setName(getResources().getString(R.string.car_depot));
        bean7.setId(R.id.workbench_car_depot);
        bean7.setBgDrawable(car ? R.drawable.shape_workbench_car_depot_bg : R.drawable.shape_workbench_other_more_bg);
        bean7.setIvDrawable(car ? R.drawable.carlibrary_normal : R.drawable.carlibrary_noright);
        bean7.setTextColor(car ? getResources().getColor(R.color.gray_212121) : getResources().getColor(R.color.gray_9e9e9e));
        workbenchBeans.add(bean7);

        WorkbenchBean bean5 = new WorkbenchBean();
        bean5.setName(getResources().getString(R.string.event_remind));
        bean5.setId(R.id.workbench_event_remind);
        bean5.setBgDrawable(eventRemind ? R.drawable.shape_workbench_event_remind_bg : R.drawable.shape_workbench_other_more_bg);
        bean5.setIvDrawable(eventRemind ? R.drawable.eventnotice_normal : R.drawable.eventnotice_noright);
        bean5.setTextColor(eventRemind ? getResources().getColor(R.color.gray_212121) : getResources().getColor(R.color.gray_9e9e9e));
        workbenchBeans.add(bean5);
        if (solo) {
            WorkbenchBean bean8 = new WorkbenchBean();
            bean8.setName(getResources().getString(R.string.solo));
            bean8.setId(R.id.workbench_solo);
            bean8.setBgDrawable(solo ? R.drawable.shape_workbench_solo_bg : R.drawable.shape_workbench_other_more_bg);
            bean8.setIvDrawable(solo ? R.drawable.singlepawn_normal : R.drawable.singlepawn_noright);
            bean8.setTextColor(solo ? getResources().getColor(R.color.gray_212121) : getResources().getColor(R.color.gray_9e9e9e));
            workbenchBeans.add(bean8);
        }
        if (broadcast) {
            WorkbenchBean bean6 = new WorkbenchBean();
            bean6.setName(getResources().getString(R.string.broadcast));
            bean6.setId(R.id.workbench_broadcast);
            bean6.setBgDrawable(R.drawable.shape_workbench_broadcast_bg);
            bean6.setIvDrawable(R.drawable.broadcast);
            bean6.setTextColor(getResources().getColor(R.color.gray_212121));
            workbenchBeans.add(bean6);
        }
    }

    @Override
    public void setData(Object data) {

    }

    @Override
    public void showLoading(String message) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(String message) {
        ToastUtils.showShort(message);
    }

    @Override
    public void launchActivity(Intent intent) {

    }

    @Override
    public void killMyself() {

    }

    @OnClick({R.id.cloud_search_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cloud_search_tv:
                MobclickAgent.onEvent(getContext(), "yunsou");
                ActivityOptions activityOptions = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), cloudSearchTv, "searchText");
                    startActivity(new Intent(getContext(), CloudSearchActivity.class), activityOptions.toBundle());
                } else {
                    startActivity(new Intent(getContext(), CloudSearchActivity.class));
                }
                break;
        }
    }

    @Override
    public Activity getFragmentContext() {
        return getActivity();
    }

    @Override
    public void doNext(int permId) {
        if (PermissionUtils.RC_CAMERA_PERM == permId) {
            // 进入以图搜图
            startFace();
        }
    }

    private void initImagePicker() {
        mImagePicker = ImagePicker.getInstance();
        mImagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        mImagePicker.setMultiMode(false);                      //单选模式
        mImagePicker.setShowCamera(false);                      //显示拍照按钮
        mImagePicker.setCrop(true);                            //允许裁剪（单选才有效）
        mImagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        mImagePicker.setMediaLimit(1);                         //选中媒体文件数量限制
        mImagePicker.setVideoLimit(0);                         //选中视频数量限制
    }

    public void startFace() {
        initImagePicker();
        MobclickAgent.onEvent(getContext(), "searchFaceImage");
        startActivity(new Intent(getContext(), LmCaptureActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            if (perms.contains(Manifest.permission.CAMERA)) {
                final PermissionDialog dialog = new PermissionDialog(getActivity());
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
                final PermissionDialog dialog = new PermissionDialog(getActivity());
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
            } else if (perms.contains(Manifest.permission.RECORD_AUDIO)) {
                final PermissionDialog dialog = new PermissionDialog(getActivity());
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
}
