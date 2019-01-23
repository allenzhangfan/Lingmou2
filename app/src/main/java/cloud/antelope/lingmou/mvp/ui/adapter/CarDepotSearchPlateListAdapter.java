package cloud.antelope.lingmou.mvp.ui.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lingdanet.safeguard.common.utils.Utils;

import org.litepal.crud.DataSupport;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.CarBrandBean;
import cloud.antelope.lingmou.mvp.model.entity.CarDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.SearchDepotBean;

public class CarDepotSearchPlateListAdapter extends BaseQuickAdapter<CarDepotEntity, BaseViewHolder> {
    String key;

    public CarDepotSearchPlateListAdapter(@Nullable List<CarDepotEntity> data) {
        super(R.layout.item_search_plate, data);
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    protected void convert(BaseViewHolder helper, CarDepotEntity item) {
        ImageView ivCb = helper.getView(R.id.iv_cb);
        ImageView ivPic = helper.getView(R.id.iv_pic);
        TextView tvPlate = helper.getView(R.id.tv_plate);
        TextView tvBrand = helper.getView(R.id.tv_brand);
        if (item.selected) {
            ivCb.setImageResource(R.drawable.choosed_selected);
        } else {
            ivCb.setImageResource(R.drawable.search_unchecked);
        }
        switch (String.valueOf(item.plateColor)) {
            case "117751":
                ivPic.setBackgroundResource(R.drawable.brand_black);
                break;
            case "117752":
                ivPic.setBackgroundResource(R.drawable.brand_white);
                break;
            case "117753":
                ivPic.setBackgroundResource(R.drawable.brand_yellow);
                break;
            case "117754":
                ivPic.setBackgroundResource(R.drawable.brand_blue);
                break;
            case "117755":
                ivPic.setBackgroundResource(R.drawable.brand_green);
                break;
            case "117756":
                ivPic.setBackgroundResource(R.drawable.brand_yellow_green);
                break;
            default:
                ivPic.setBackgroundColor(Utils.getContext().getResources().getColor(R.color.gray_f7f7f7));
                break;
        }
        List<CarBrandBean> carBrandBeanList = DataSupport.where("code = ?", String.valueOf(item.vehicleBrand)).find(CarBrandBean.class);
        String brand = "";
        if (carBrandBeanList != null&&carBrandBeanList.size()>0) {
            brand = carBrandBeanList.get(0).name;
        }
        helper.setText(R.id.tv_brand, brand);

        if (!TextUtils.isEmpty(item.plateNo)) {
//            String plate=item.plateNo.substring(0,2)+" "+item.plateNo.substring(2,item.plateNo.length());
            int indexOfKey = item.plateNo.indexOf(key);
            SpannableString spannableString = new SpannableString(item.plateNo);
            ForegroundColorSpan yellow = new ForegroundColorSpan(Utils.getContext().getResources().getColor(R.color.yellow_ff8f00));
            ForegroundColorSpan black0 = new ForegroundColorSpan(Utils.getContext().getResources().getColor(R.color.gray_424242));
            ForegroundColorSpan black1 = new ForegroundColorSpan(Utils.getContext().getResources().getColor(R.color.gray_424242));
//            if (indexOfKey == 0) {
//                spannableString.setSpan(yellow, indexOfKey, key.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                spannableString.setSpan(black, key.length(), item.plateNo.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            } else {
                spannableString.setSpan(black0, 0, indexOfKey, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(yellow, indexOfKey, indexOfKey + key.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(black1, indexOfKey + key.length(), item.plateNo.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
            tvPlate.setText("");
            tvPlate.append(spannableString);
        }
    }
}
