package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerLocationSelectActivityComponent;
import cloud.antelope.lingmou.di.module.LocationSelectActivityModule;
import cloud.antelope.lingmou.mvp.contract.LocationSelectActivityContract;
import cloud.antelope.lingmou.mvp.model.entity.UrlEntity;
import cloud.antelope.lingmou.mvp.presenter.LocationSelectActivityPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.LocationAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.HeartbeatView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class LocationSelectActivity extends BaseActivity<LocationSelectActivityPresenter> implements LocationSelectActivityContract.View {

    @BindView(R.id.ib_close)
    ImageView ibClose;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.rl_root)
    RelativeLayout rlRoot;
    @Inject
    LocationAdapter adapter;
    private ArrayList<UrlEntity> mUrlEntityList;
    private int selectedIndex;


    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerLocationSelectActivityComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .locationSelectActivityModule(new LocationSelectActivityModule(this))
                .build()
                .inject(this);
    }


    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_location_select; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mUrlEntityList = getIntent().getParcelableArrayListExtra("UrlEntityList");
        selectedIndex = getIntent().getIntExtra("selectedIndex", -1);
        setAdapter();
    }


    private void setAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter.setIndex(selectedIndex);
        rv.setAdapter(adapter);
        adapter.addData(mUrlEntityList);
        adapter.setOnItemClickListener(new LocationAdapter.onItemClickListener() {
            @Override
            public void onClick(int position, UrlEntity urlEntity) {
                selectedIndex = position;
                adapter.setIndex(selectedIndex);
                adapter.notifyDataSetChanged();
                Intent intent = new Intent();
                intent.putExtra("SelectUrlEntity", urlEntity);
                intent.putExtra("selectedIndex", position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        for (int i = 0; i < mUrlEntityList.size(); i++) {
            UrlEntity urlEntity = mUrlEntityList.get(i);
            HeartbeatView heartbeatView = new HeartbeatView(getApplicationContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.dp40)
                    , (int) getResources().getDimension(R.dimen.dp40));
            heartbeatView.setColor(getResources().getColor(R.color.yellow_ffa000));
            if ("南昌".equals(urlEntity.getPlatformName())) {
                layoutParams.setMargins((int) getResources().getDimension(R.dimen.dp222), (int) getResources().getDimension(R.dimen.dp267), 0, 0);
                heartbeatView.setLayoutParams(layoutParams);
                rlRoot.addView(heartbeatView);
            } else if ("东莞".equals(urlEntity.getPlatformName())) {
                layoutParams.setMargins((int) getResources().getDimension(R.dimen.dp207), (int) getResources().getDimension(R.dimen.dp295), 0, 0);
                heartbeatView.setLayoutParams(layoutParams);
                rlRoot.addView(heartbeatView);
            }
        }
    }

    @OnClick(R.id.ib_close)
    public void onClick(View view) {
        finish();
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

}
