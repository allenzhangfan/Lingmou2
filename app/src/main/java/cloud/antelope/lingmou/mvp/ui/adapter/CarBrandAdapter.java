package cloud.antelope.lingmou.mvp.ui.adapter;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.glide.GlideArms;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.FormatUtils;
import cloud.antelope.lingmou.mvp.model.entity.CarBrandBean;

public class CarBrandAdapter extends BaseQuickAdapter<CarBrandBean, BaseViewHolder> {
    private Activity mActivity;

    public CarBrandAdapter(Activity activity, @Nullable List<CarBrandBean> data) {
        super(R.layout.item_car_brand_list, data);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, CarBrandBean item) {
//        helper.setVisible(R.id.tv_letter,item.firstOfLetter);
//        helper.setText(R.id.tv_letter,item.letter);
        helper.setText(R.id.tv_name, item.name);
        if (TextUtils.isEmpty(item.pic)) {
            ((ImageView) helper.getView(R.id.iv_logo)).setImageBitmap(BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.holder));
        } else {
            ((ImageView) helper.getView(R.id.iv_logo)).setImageBitmap(FormatUtils.stringToBitmap(item.pic));
        }
    }
}
