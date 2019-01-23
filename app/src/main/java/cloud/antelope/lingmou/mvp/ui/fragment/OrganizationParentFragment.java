package cloud.antelope.lingmou.mvp.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import cloud.antelope.lingmou.di.component.DaggerOrganizationParentComponent;
import cloud.antelope.lingmou.di.module.OrganizationParentModule;
import cloud.antelope.lingmou.mvp.contract.OrganizationParentContract;
import cloud.antelope.lingmou.mvp.model.entity.CameraItem;
import cloud.antelope.lingmou.mvp.model.entity.OrganizationEntity;
import cloud.antelope.lingmou.mvp.presenter.OrganizationParentPresenter;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.activity.MainActivity;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static com.jess.arms.utils.Preconditions.checkNotNull;


public class OrganizationParentFragment extends BaseFragment<OrganizationParentPresenter>
        implements OrganizationParentContract.View,
        OrganizationNewFragment.OrgItemClickListener,
        MainActivity.HomeBackPressInterface {


    @BindView(R.id.org_ll)
    LinearLayout mOrgLl;
    @BindView(R.id.content_layout)
    FrameLayout mContentLayout;
    @BindView(R.id.parent_hz_scrollview)
    HorizontalScrollView mHorizontalScrollView;

    private Drawable mLeftDrawable;
    private MainActivity mMainActivity;

    private boolean mIsVisibleToUser;


    public static OrganizationParentFragment newInstance() {
        OrganizationParentFragment fragment = new OrganizationParentFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
        DaggerOrganizationParentComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .organizationParentModule(new OrganizationParentModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.org_parent_layout, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mLeftDrawable = getResources().getDrawable(R.drawable.org_right_arrow);
        mLeftDrawable.setBounds(0, 0, mLeftDrawable.getMinimumWidth(), mLeftDrawable.getMinimumHeight());
        OrganizationNewFragment organizationNewFragment = OrganizationNewFragment.newInstance(true, "", null, "");
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_layout, organizationNewFragment, "root");
        fragmentTransaction.addToBackStack("root");
        //        fragmentTransaction.show(organizationNewFragment);
        fragmentTransaction.commit();
        organizationNewFragment.setOrgItemClickListener(this);
        mMainActivity = (MainActivity) getActivity();
        mMainActivity.setHomeBackLisntener(this);
    }

    /**
     * 此方法是让外部调用使fragment做一些操作的,比如说外部的activity想让fragment对象执行一些方法,
     * 建议在有多个需要让外界调用的方法时,统一传Message,通过what字段,来区分不同的方法,在setData
     * 方法中就可以switch做不同的操作,这样就可以用统一的入口方法做不同的事
     * <p>
     * 使用此方法时请注意调用时fragment的生命周期,如果调用此setData方法时onCreate还没执行
     * setData里却调用了presenter的方法时,是会报空的,因为dagger注入是在onCreated方法中执行的,然后才创建的presenter
     * 如果要做一些初始化操作,可以不必让外部调setData,在initData中初始化就可以了
     *
     * @param data
     */

    @Override
    public void setData(Object data) {

    }

    @Override
    public void onItemLoad(boolean isRoot, String title, boolean isHasChildren, OrganizationEntity entity, String orgName) {
        if (isRoot) {
            //第一层
            mOrgLl.removeAllViews();
            createAddTextView(true, title);
        } else {
            if (isHasChildren) {
                List<CameraItem> cameras = entity.getCameras();
                CameraItem cameraItem = cameras.get(0);
                String type = cameraItem.getType();
                createAddTextView(isRoot, title);
                if ("group".equals(type)) {
                    //进入到下级组织结构
                    //true的话，标明有子item
                    OrganizationNewFragment orgNewFragment = OrganizationNewFragment.newInstance(false, title, entity, orgName);
                    @SuppressLint("RestrictedApi") List<Fragment> fragments = getChildFragmentManager().getFragments();
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    for (Fragment fragment : fragments) {
                        if (null != fragment) {
                            transaction.hide(fragment);
                        }
                    }
                    transaction.add(R.id.content_layout, orgNewFragment, title);
                    transaction.addToBackStack(title);
                    transaction.show(orgNewFragment);
                    transaction.commit();
                    orgNewFragment.setOrgItemClickListener(this);
                } else if ("camera".equals(type)) {
                    //进入到下级摄像头列表
                     CameraListFragment cameraListFragment = CameraListFragment.newInstance(title, orgName, entity);
                     @SuppressLint("RestrictedApi") List<Fragment> fragments = getChildFragmentManager().getFragments();
                     FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                     for (Fragment fragment : fragments) {
                         if (null != fragment) {
                             transaction.hide(fragment);
                         }
                     }
                     transaction.add(R.id.content_layout, cameraListFragment, title);
                     transaction.addToBackStack(title);
                     transaction.show(cameraListFragment);
                     transaction.commit();
                    /*
                    Intent intent = new Intent();
                    intent.putExtra("entity", entity);
                    intent.putExtra("orgnizationName", orgName);
                    intent.putExtra("itemName", title);
                    intent.setClass(getActivity(), CameraListActivity.class);
                    startActivity(intent);*/
                }

            } else {
                ToastUtils.showShort(R.string.error_no_organization);
            }
        }
    }

    private void createAddTextView(boolean isRoot, String title) {
        final TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.rightMargin = SizeUtils.dp2px(11);
        textView.setTextSize(14);
        textView.setTextColor(Color.parseColor("#999999"));
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(params);
        textView.setMaxLines(1);
        // textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setText(title);
        textView.setCompoundDrawables(null, null, mLeftDrawable, null);
        textView.setCompoundDrawablePadding(SizeUtils.dp2px(11));
        mOrgLl.addView(textView);
        mHorizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
        //循环，然后让最后一个TextView变蓝
        int childCount = mOrgLl.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView tv = (TextView) mOrgLl.getChildAt(i);
            if (i == childCount - 1) {
                //最后一个，颜色变蓝
                tv.setTextColor(Color.parseColor("#36A8FF"));
            } else {
                tv.setTextColor(Color.parseColor("#999999"));
            }
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int childCount = mOrgLl.getChildCount();
                for (int i = childCount - 1; i > -1; i--) {
                    TextView clickTv = (TextView) mOrgLl.getChildAt(i);
                    if (null != clickTv && textView.getText().equals(clickTv.getText())) {
                        //证明是同一个
                        if (i == childCount - 1) {
                            return;
                        } else {
                            //干掉i+1之后的所有view和fragment
                            @SuppressLint("RestrictedApi") List<Fragment> fragments = getChildFragmentManager().getFragments();
                            for (int j = mOrgLl.getChildCount() - 1; j > i; j--) {
                                TextView orgTv = (TextView) mOrgLl.getChildAt(j);
                                //                                Fragment fragment = fragments.get(j);
                                LogUtils.i("cxm", "orgTv -- i = " + i + ",orgTv = " + orgTv);
                                if (null != orgTv) {
                                    LogUtils.i("cxm", "orgTv -- i = " + i + ",orgTv.text = " + orgTv.getText());
                                    String orgName = orgTv.getText().toString();
                                    getChildFragmentManager().popBackStack(orgName, POP_BACK_STACK_INCLUSIVE);
                                    //                                    Fragment fragment = fragments.get(j);
                                    //                                    getChildFragmentManager().beginTransaction().remove(fragment).commit();
                                    mOrgLl.removeView(orgTv);
                                    ((TextView)(mOrgLl.getChildAt(mOrgLl.getChildCount() -1))).setTextColor(Color.parseColor("#36A8FF"));
                                }
                            }
                            if (i == 0) {
                                LogUtils.i("cxm", "i == 0");
                                Fragment fragmentByTag = getChildFragmentManager().findFragmentByTag("root");
                                getChildFragmentManager().beginTransaction().show(fragmentByTag).commit();
                            } else {
                                /*for (int k = 0; k < i; k++) {
                                    TextView hideTv = (TextView) mOrgLl.getChildAt(k);
                                    if (null != hideTv) {
                                        String hideTvTag = hideTv.getText().toString();
                                        Fragment fragmentByTagRoot = getChildFragmentManager().findFragmentByTag(hideTvTag);
                                        getChildFragmentManager().beginTransaction().hide(fragmentByTagRoot).commit();
                                    }
                                }*/
                                // @SuppressLint("RestrictedApi") List<Fragment> fragments = getChildFragmentManager().getFragments();
                                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                                for (Fragment fragment : fragments) {
                                    if (null != fragment) {
                                        transaction.hide(fragment);
                                    }
                                }
                                transaction.commit();
                                String clickTVStr = clickTv.getText().toString();
                                LogUtils.i("cxm", "i == " + i + ",clickTVStr =" + clickTVStr);
                                // Fragment fragmentByTagRoot = getChildFragmentManager().findFragmentByTag("root");
                                // getChildFragmentManager().beginTransaction().hide(fragmentByTagRoot).commit();
                                Fragment fragmentByTag = getChildFragmentManager().findFragmentByTag(clickTVStr);
                                getChildFragmentManager().beginTransaction().show(fragmentByTag).commit();
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;
    }

    @Override
    public boolean backPre(boolean isHomeVisible) {
        if (mIsVisibleToUser && isHomeVisible) {
            //对用户可见，才能返回
            if (null != mOrgLl) {
                int childSize = mOrgLl.getChildCount();
                if (childSize > 1) {
                    //表明点了二级结构
                    TextView textView = (TextView) mOrgLl.getChildAt(childSize - 1);
                    String removeTvTag = textView.getText().toString();
                    mOrgLl.removeView(textView);
                    TextView preTextView = (TextView) mOrgLl.getChildAt(childSize - 2);
                    preTextView.setTextColor(Color.parseColor("#36A8FF"));
                    String preTag = preTextView.getText().toString();
                    if (!TextUtils.isEmpty(removeTvTag)) {
                        getChildFragmentManager().popBackStack(removeTvTag, POP_BACK_STACK_INCLUSIVE);
                    }
                    @SuppressLint("RestrictedApi") List<Fragment> fragments = getChildFragmentManager().getFragments();
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    for (Fragment fragment : fragments) {
                        if (null != fragment) {
                            transaction.hide(fragment);
                        }
                    }
                    transaction.commit();
                    if (!TextUtils.isEmpty(preTag)) {
                        if (childSize == 2) {
                            preTag = "root";
                        }
                        Fragment fragmentByTag = getChildFragmentManager().findFragmentByTag(preTag);
                        getChildFragmentManager().beginTransaction().show(fragmentByTag).commit();
                    }
                    return true;
                }
            }
        }
        return false;
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
        ArmsUtils.toastText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {

    }

}
