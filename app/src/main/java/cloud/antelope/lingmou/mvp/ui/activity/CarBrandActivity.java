package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.promeg.pinyinhelper.Pinyin;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.BarViewUtils;
import cloud.antelope.lingmou.app.utils.FormatUtils;
import cloud.antelope.lingmou.di.component.DaggerCarBrandComponent;
import cloud.antelope.lingmou.di.module.CarBrandModule;
import cloud.antelope.lingmou.mvp.contract.CarBrandContract;
import cloud.antelope.lingmou.mvp.model.entity.CarBrandBean;
import cloud.antelope.lingmou.mvp.presenter.CarBrandPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.CarBrandAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.DividerItemDecoration;
import cloud.antelope.lingmou.mvp.ui.widget.SideBar;
import timber.log.Timber;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;
import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CarBrandActivity extends BaseActivity<CarBrandPresenter> implements CarBrandContract.View {

    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.head_right_iv)
    ImageView headRightIv;
    @BindView(R.id.head_right_tv)
    TextView headRightTv;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.side_bar)
    SideBar sideBar;
    @BindView(R.id.view_bar)
    View viewBar;
    @BindView(R.id.tv_dialog)
    TextView tvDialog;
    @Inject
    CarBrandAdapter mAdapter;
    private List<CarBrandBean> list;
    private int mToPosition;
    private boolean mShouldScroll;
    private View header;
    private DividerItemDecoration itemDecoration;
    private Map<String, Integer> firstLetterPosition;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerCarBrandComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .carBrandModule(new CarBrandModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_car_brand; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        viewBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, BarViewUtils.getStatusBarHeight()));
        titleTv.setText(getResources().getText(R.string.car_brand));
        list = DataSupport.findAll(CarBrandBean.class);
        if (list == null || list.isEmpty()) return;
        addHeader();
//        for (CarBrandBean bean : list) {
//            if (TextUtils.isEmpty(bean.name)) continue;
//            if (Pinyin.isChinese(bean.name.toCharArray()[0])) {
//                bean.letter = Pinyin.toPinyin(bean.name.toCharArray()[0]).substring(0, 1);
//            } else {
//                bean.letter = String.valueOf(bean.name.toCharArray()[0]).toUpperCase();
//            }
//        }
        Collections.sort(list, new Comparator<CarBrandBean>() {
            @Override
            public int compare(CarBrandBean o1, CarBrandBean o2) {
//                Integer i1 = new Integer(o1.letter.charAt(0));
//                Integer i2 = new Integer(o2.letter.charAt(0));
//                return o1.letter.charAt(0) >= o2.letter.charAt(0) ? 1 : -1;
//                return i1.compareTo(i2);
                return o1.fullSpelling.compareTo(o2.fullSpelling);
            }
        });
        sortList(list);
        rv.setLayoutManager(new LinearLayoutManager(CarBrandActivity.this));
        rv.setAdapter(mAdapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0));
                String s = mAdapter.getData().get(position).fullSpelling.substring(0, 1).toUpperCase();
                sideBar.setChooseLetter(s);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState==SCROLL_STATE_SETTLING){
                    etSearch.clearFocus();
                    KeyboardUtils.hideSoftInput(CarBrandActivity.this);
                }
            }
        });
        mAdapter.setNewData(list);
        itemDecoration = new DividerItemDecoration(this, new DividerItemDecoration.OnGroupListener() {
            @Override
            public String getLetter(int position) {
                if (position == -1) return null;
                return mAdapter.getData().get(position).fullSpelling.substring(0, 1).toUpperCase();
            }
        });
        itemDecoration.setHeaderCount(mAdapter.getHeaderLayoutCount());
        rv.addItemDecoration(itemDecoration);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CarBrandBean brandBean = mAdapter.getData().get(position);
                Intent intent = new Intent();
                intent.putExtra("brandBean", brandBean);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(etSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void addHeader() {
        header = LayoutInflater.from(this).inflate(R.layout.layout_usual_brand, null);
        RecyclerView rv = header.findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(CarBrandActivity.this, 5));
        List<CarBrandBean> list = new ArrayList<>();
        list.addAll(this.list.subList(0, 10));
        rv.setAdapter(new BaseQuickAdapter<CarBrandBean, BaseViewHolder>(R.layout.item_usual_brand, list) {
            @Override
            protected void convert(BaseViewHolder helper, CarBrandBean item) {
                helper.setText(R.id.tv_name, item.name);
                if (TextUtils.isEmpty(item.pic)) {
                    ((ImageView) helper.getView(R.id.iv_pic)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.holder));
                } else {
                    ((ImageView) helper.getView(R.id.iv_pic)).setImageBitmap(FormatUtils.stringToBitmap(item.pic));
                }
                helper.getView(R.id.ll_root).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CarBrandBean brandBean = getData().get(helper.getPosition());
                        Intent intent = new Intent();
                        intent.putExtra("brandBean", brandBean);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }

        });
        mAdapter.addHeaderView(header);
    }

    private void sortList(List<CarBrandBean> list) {
        List<String> letters = new ArrayList<>();
        firstLetterPosition = new HashMap();
        for (int i = 0; i < list.size(); i++) {
            CarBrandBean bean = list.get(i);
            String letter = bean.fullSpelling.substring(0, 1).toUpperCase();
            if (!letters.contains(letter)) {
                letters.add(letter);
                bean.firstOfLetter = true;
                firstLetterPosition.put(letter, i);
            }
        }
        Collections.sort(letters, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.charAt(0) >= o2.charAt(0) ? 1 : -1;
            }
        });
        sideBar.setLetters(letters.toArray(new String[letters.size()]));
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                Integer position = firstLetterPosition.get(s);
                if (position != null) {
                    ((LinearLayoutManager) rv.getLayoutManager()).scrollToPositionWithOffset(position + mAdapter.getHeaderLayoutCount(), 0);
                }
            }
        });
        sideBar.setTextView(tvDialog);
    }

    private void search(String key) {
        List<CarBrandBean> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).name.contains(key)) {
                newList.add(list.get(i));
            }
        }
        sortList(newList);
        if (TextUtils.isEmpty(key) && mAdapter.getHeaderLayoutCount() == 0) {
            mAdapter.addHeaderView(header);
        } else {
            mAdapter.removeAllHeaderView();
        }
        itemDecoration.setHeaderCount(mAdapter.getHeaderLayoutCount());
        mAdapter.setNewData(newList);
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

    @OnClick({R.id.head_left_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_iv:
                finish();
                break;
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

}
