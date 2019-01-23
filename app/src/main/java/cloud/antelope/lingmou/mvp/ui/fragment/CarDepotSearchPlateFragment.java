package cloud.antelope.lingmou.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerCarDepotSearchPlateComponent;
import cloud.antelope.lingmou.di.module.CarDepotSearchPlateModule;
import cloud.antelope.lingmou.mvp.contract.CarDepotSearchPlateContract;
import cloud.antelope.lingmou.mvp.model.entity.CarDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.CarDepotListRequest;
import cloud.antelope.lingmou.mvp.presenter.CarDepotSearchPlatePresenter;
import cloud.antelope.lingmou.mvp.ui.activity.CarDepotActivity;
import cloud.antelope.lingmou.mvp.ui.activity.CarDepotSearchActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.CarDepotSearchPlateListAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.HorizontalItemDecoration;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CarDepotSearchPlateFragment extends BaseFragment<CarDepotSearchPlatePresenter> implements CarDepotSearchPlateContract.View, BaseQuickAdapter.RequestLoadMoreListener {
    @BindView(R.id.rv)
    RecyclerView rv;
    @Inject
    CarDepotSearchPlateListAdapter mAdapter;
    private static CarDepotSearchPlateFragment instance;
    int pageNum = 1;
    int pageSize = 200;
    Long startTime;//传参用的开始时间
    Long endTime;//传参用的结束时间
    //    Long startTime = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000);//传参用的开始时间
//    Long endTime = System.currentTimeMillis();//传参用的结束时间
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.tv_no_network)
    TextView tvNoNetwork;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    @BindView(R.id.tv_choice)
    TextView tvChoice;
    @BindView(R.id.tv_size)
    TextView tvSize;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.ll_bottom)
    public LinearLayout llBottom;
    @BindView(R.id.tv)
    TextView tv;
    @Inject
    LoadMoreView mLoadMoreView;
    private String key;
    ArrayList<CarDepotEntity> plateList = new ArrayList<>();
    ArrayList<String> plates = new ArrayList<>();
    private boolean hasMore;
    private ArrayList<String> list;
    private boolean visible;

    public static CarDepotSearchPlateFragment newInstance() {
        CarDepotSearchPlateFragment fragment = new CarDepotSearchPlateFragment();
        return fragment;
    }

    public static CarDepotSearchPlateFragment getInstance() {
        return instance;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerCarDepotSearchPlateComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .carDepotSearchPlateModule(new CarDepotSearchPlateModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_car_depot_search_plate, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        instance = this;
        srl.setEnabled(false);
        HorizontalItemDecoration decoration = new HorizontalItemDecoration.Builder(getActivity())
                .color(Utils.getContext().getResources().getColor(R.color.home_divider_lint))
                .sizeResId(R.dimen.a_half_dp)
                .marginResId(R.dimen.dp16, R.dimen.dp16)
                .build();
        rv.addItemDecoration(decoration);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(this, rv);
        mAdapter.setLoadMoreView(mLoadMoreView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CarDepotEntity entity = (CarDepotEntity) adapter.getData().get(position);
                entity.selected = !entity.selected;
                mAdapter.notifyItemChanged(position);
                notifySize();
            }
        });
    }

    private void notifySize() {
        list = new ArrayList<>();
        for (CarDepotEntity entity : mAdapter.getData()) {
            if (entity.selected) {
                list.add(entity.plateNo);
            }
        }
        llBottom.setVisibility(View.VISIBLE);
        tvSize.setText(String.format("已选择%d张", list.size()));
        tvChoice.setText(list.size() == mAdapter.getData().size() ? R.string.cancel_select : R.string.select_all);
    }

    @OnClick({R.id.tv_choice, R.id.tv_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_choice:
                if (getString(R.string.cancel_select).equals(tvChoice.getText().toString())) {
                    for (CarDepotEntity entity : mAdapter.getData()) {
                        entity.selected = false;
                    }
                } else {
                    for (CarDepotEntity entity : mAdapter.getData()) {
                        entity.selected = true;
                    }
                }
                mAdapter.notifyDataSetChanged();
                notifySize();
                break;
            case R.id.tv_confirm:
                if (list.size() > 0) {
                    Intent intent = new Intent(getActivity(), CarDepotActivity.class);
                    intent.putStringArrayListExtra("plates", list);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else {
                    ToastUtils.showShort("请选择要搜索的车牌");
                }
                break;
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (!TextUtils.isEmpty(key)) {
            search();
        }
        visible = true;
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        visible = false;
        llBottom.setVisibility(View.GONE);
    }

    @Override
    public void setData(@Nullable Object data) {
        Message message = (Message) data;
        key = (String) message.obj;
        if (visible) {
            search();
        }
    }

    private void search() {
        CarDepotListRequest request = new CarDepotListRequest();
        request.page = pageNum;
        request.pageSize = pageSize;
        request.startTime = startTime;
        request.endTime = endTime;
        request.plateNo = key;
        mPresenter.getList(request);
    }

    @Override
    public void showLoading(String s) {
        srl.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        srl.setRefreshing(false);
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

    }

    @Override
    public void showList(List<CarDepotEntity> list) {
        plates.clear();
        ArrayList<CarDepotEntity> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (!plates.contains(list.get(i).plateNo)) {
                newList.add(list.get(i));
                plates.add(list.get(i).plateNo);
            }
        }
        mAdapter.setNewData(newList);
        notifySize();
        mAdapter.setKey(key);
        if (list == null || list.isEmpty()) {
            hasMore = false;
            tvEmpty.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            tvNoNetwork.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
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
        ArrayList<CarDepotEntity> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (!plates.contains(list.get(i).plateNo)) {
                newList.add(list.get(i));
                plates.add(list.get(i).plateNo);
            }
        }
        mAdapter.addData(newList);
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
            tv.setVisibility(View.GONE);
            tvNoNetwork.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoadMoreRequested() {
        if (hasMore) {
            pageNum++;
            search();
        }
    }
}
