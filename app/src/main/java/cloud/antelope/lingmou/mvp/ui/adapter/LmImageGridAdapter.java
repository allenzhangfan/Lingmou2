package cloud.antelope.lingmou.mvp.ui.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lzy.imagepicker.bean.MediaItem;
import com.lzy.imagepicker.util.Utils;

import java.util.List;

import cloud.antelope.lingmou.R;

/**
 * 作者：陈新明
 * 创建日期：2018/01/25
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */

public class LmImageGridAdapter extends BaseQuickAdapter<MediaItem,BaseViewHolder> {

    private final int mImageSize;
    private Activity mActivity;

    public LmImageGridAdapter(Activity activity, @Nullable List<MediaItem> data) {
        super(R.layout.item_img_grid, data);
        mActivity = activity;
        mImageSize = Utils.getImageItemWidth(mActivity);
    }

    @Override
    protected void convert(BaseViewHolder helper, MediaItem item) {
        ImageView albumIv = helper.getView(R.id.album_iv);
        albumIv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)); //让图片是个正方形
        if (!TextUtils.isEmpty(item.path)) {
            GlideArms.with(mActivity).asBitmap().load(item.path).placeholder(R.drawable.default_image).centerCrop().into(albumIv);
        }
    }
}
