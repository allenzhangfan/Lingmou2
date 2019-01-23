package cloud.antelope.lingmou.mvp.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
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
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerCarDepotSearchDeviceComponent;
import cloud.antelope.lingmou.di.module.CarDepotSearchDeviceModule;
import cloud.antelope.lingmou.mvp.contract.CarDepotSearchDeviceContract;
import cloud.antelope.lingmou.mvp.model.entity.CarDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.CarDepotListRequest;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.presenter.CarDepotSearchDevicePresenter;
import cloud.antelope.lingmou.mvp.ui.activity.CarDepotActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchCameraAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.HorizontalItemDecoration;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CarDepotSearchDeviceFragment extends BaseFragment<CarDepotSearchDevicePresenter> implements CarDepotSearchDeviceContract.View {

    @BindView(R.id.rv)
    RecyclerView rv;
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
    private static CarDepotSearchDeviceFragment instance;
    @Inject
    SearchCameraAdapter mAdapter;
    private String key;
    private ArrayList<String> list;
    private boolean visible;

    public static CarDepotSearchDeviceFragment newInstance() {
        CarDepotSearchDeviceFragment fragment = new CarDepotSearchDeviceFragment();
        return fragment;
    }

    public static CarDepotSearchDeviceFragment getInstance() {
        return instance;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerCarDepotSearchDeviceComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .carDepotSearchDeviceModule(new CarDepotSearchDeviceModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_car_depot_search_device, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        instance = this;
        HorizontalItemDecoration decoration = new HorizontalItemDecoration.Builder(getActivity())
                .color(Utils.getContext().getResources().getColor(R.color.home_divider_lint))
                .sizeResId(R.dimen.a_half_dp)
                .marginResId(R.dimen.dp16, R.dimen.dp16)
                .build();
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.addItemDecoration(decoration);
        rv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                OrgCameraEntity entity = (OrgCameraEntity) adapter.getData().get(position);
                entity.setSelect(!entity.isSelect());
                mAdapter.notifyItemChanged(position);
                notifySize();
            }
        });
        srl.setEnabled(false);
    }

    private void notifySize() {
        list = new ArrayList<>();
        for (OrgCameraEntity entity : mAdapter.getData()) {
            if (entity.isSelect()) {
                list.add(entity.getManufacturerDeviceId() + "");
            }
        }
        llBottom.setVisibility(View.VISIBLE);
        tvSize.setText(String.format("已选择%d张", list.size()));
        tvChoice.setText(list.size() == mAdapter.getData().size() ? R.string.cancel_select : R.string.select_all);
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
        Cursor cursor = DataSupport.findBySQL("select * from orgcameraentity where deviceName like '%" + key + "%'");
        if (cursor != null) {
            List<OrgCameraEntity> orgCameraList = new ArrayList<>();
            while (cursor.moveToNext()) {
                OrgCameraEntity entity = new OrgCameraEntity();
                String deviceName = cursor.getString(cursor.getColumnIndex("devicename"));
                Long deviceType = cursor.getLong(cursor.getColumnIndex("devicetype"));
                String sn = cursor.getString(cursor.getColumnIndex("sn"));
                Long manufacturerDeviceId = cursor.getLong(cursor.getColumnIndex("manufacturerdeviceid"));
                Long deviceState = cursor.getLong(cursor.getColumnIndex("devicestate"));
                Double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                Double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));

                entity.setDeviceName(deviceName);
                entity.setDeviceType(deviceType);
                entity.setSn(sn);
                entity.setManufacturerDeviceId(manufacturerDeviceId);
                entity.setDeviceState(deviceState);
                entity.setLongitude(longitude);
                entity.setLatitude(latitude);
                orgCameraList.add(entity);
            }
            if (orgCameraList != null && !orgCameraList.isEmpty()) {
                tvEmpty.setVisibility(View.GONE);
                tv.setVisibility(View.VISIBLE);
                rv.setVisibility(View.VISIBLE);
                mAdapter.setNewData(orgCameraList);
                notifySize();
            } else {
                tv.setVisibility(View.GONE);
                rv.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick({R.id.tv_choice, R.id.tv_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_choice:
                if (getString(R.string.cancel_select).equals(tvChoice.getText().toString())) {
                    for (OrgCameraEntity entity : mAdapter.getData()) {
                        entity.setSelect(false);
                    }
                } else {
                    for (OrgCameraEntity entity : mAdapter.getData()) {
                        entity.setSelect(true);
                    }
                }
                mAdapter.notifyDataSetChanged();
                notifySize();
                break;
            case R.id.tv_confirm:
                if (list.size() > 0) {
                    Intent intent = new Intent(getActivity(), CarDepotActivity.class);
                    intent.putStringArrayListExtra("devices", list);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else {
                    ToastUtils.showShort("请选择要搜索的设备");
                }
                break;
        }
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
}
