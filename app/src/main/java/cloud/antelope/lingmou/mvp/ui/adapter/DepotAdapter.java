package cloud.antelope.lingmou.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.AlarmDepotEntity;

/**
 * Created by ChenXinming on 2018/1/22.
 * description:
 */

public class DepotAdapter extends BaseQuickAdapter<AlarmDepotEntity, BaseViewHolder> {

    private List<AlarmDepotEntity> mSelectPositions;

    public void setSelectDepots(List<AlarmDepotEntity> positions) {
        mSelectPositions.clear();
        mSelectPositions.addAll(positions);
        notifyDataSetChanged();
    }

    public DepotAdapter(@Nullable List data) {
        super(R.layout.item_depot, data);
        mSelectPositions = new ArrayList<>();
    }

    @Override
    protected void convert(BaseViewHolder helper, AlarmDepotEntity item) {
        helper.getLayoutPosition();
        TextView textView = helper.getView(R.id.depot_tv);
        textView.setText(item.getName());
        if (mSelectPositions.contains(item)) {
            textView.setSelected(true);
        } else {
            textView.setSelected(false);
        }
    }

}
