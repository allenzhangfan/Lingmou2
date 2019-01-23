package cloud.antelope.lingmou.mvp.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.SearchTextEntity;

/**
 * Created by Administrator on 2017/11/13.
 */

public class SearchHistoryAdapter extends BaseQuickAdapter<SearchTextEntity, BaseViewHolder> {
    public SearchHistoryAdapter(@Nullable List<SearchTextEntity> data) {
        super(R.layout.simple_text_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchTextEntity item) {
        helper.setText(R.id.simple_tv, item.getName());
    }
}
