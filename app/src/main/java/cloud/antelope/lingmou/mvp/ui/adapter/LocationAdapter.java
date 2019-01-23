package cloud.antelope.lingmou.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.UrlEntity;

public class LocationAdapter extends BaseQuickAdapter<UrlEntity, BaseViewHolder> {
    private int index = -1;
    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(LocationAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public LocationAdapter(int layoutResId, @Nullable List<UrlEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UrlEntity item) {
        TextView tv = helper.getView(R.id.tv);
        String name = item.getPlatformName();
        if ("南昌".equals(name)) {
            name = "江西云";
        } else if ("东莞".equals(name)) {
            name = "广东云";
        }
        tv.setText(name);
        int position = helper.getPosition();
        if (position == index) {
            tv.setSelected(true);
        } else {
            tv.setSelected(false);
        }
        tv.setBackgroundResource(R.drawable.selector_location_choice_yellow);
        tv.setTextColor(tv.isSelected() ? Utils.getContext().getResources().getColor(R.color.white) : Utils.getContext().getResources().getColor(R.color.yellow_ff8f00));
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    index = position;
                    onItemClickListener.onClick(helper.getPosition(), item);
                }
            }
        });
    }

    public interface onItemClickListener {
        void onClick(int position, UrlEntity urlEntity);
    }
}
