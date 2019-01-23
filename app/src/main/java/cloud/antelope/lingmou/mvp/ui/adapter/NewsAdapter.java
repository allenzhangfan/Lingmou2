package cloud.antelope.lingmou.mvp.ui.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.AttachmentUtils;
import cloud.antelope.lingmou.app.utils.UrlUtils;
import cloud.antelope.lingmou.mvp.model.entity.AttachmentBean;
import cloud.antelope.lingmou.mvp.model.entity.NewsItemEntity;

/**
 * Created by liucheng on 16/6/12.
 * 告警列表适配器
 */
public class NewsAdapter extends BaseMultiItemQuickAdapter<NewsItemEntity, BaseViewHolder> {

    private static final int MAX_WIDTH = 600;

    public NewsAdapter(List<NewsItemEntity> list) {
        super(list);
        addItemType(NewsItemEntity.NO_IMAGE_CASE, R.layout.item_case_no_img);
        addItemType(NewsItemEntity.ONE_IMAGE_CASE, R.layout.item_case_one_img);
        addItemType(NewsItemEntity.IMPORT_CASE, R.layout.item_case_import);
        addItemType(NewsItemEntity.MULTI_IMAGE_CASE, R.layout.item_case_multi_imgs);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewsItemEntity caseItem) {
        helper.setText(R.id.case_title_tv, caseItem.getTitle());
        helper.setText(R.id.case_date_tv, caseItem.getCreateTime());
        List<AttachmentBean> imgInfoJson = caseItem.getImgInfoJson();
        switch (helper.getItemViewType()) {
            case NewsItemEntity.ONE_IMAGE_CASE:
                if (imgInfoJson != null && imgInfoJson.size() > 0) {
                    List<String> imgsWithToken = AttachmentUtils.getImgsWithToken(imgInfoJson);
                    // 取第一张图片
                    if (imgsWithToken.size() > 0) {
                        String url = UrlUtils.getAbbrUrl(imgsWithToken.get(0),
                                (int) Utils.getContext().getResources()
                                        .getDimension(R.dimen.case_one_iv_width),
                                (int) Utils.getContext().getResources()
                                        .getDimension(R.dimen.case_one_iv_height));
                        loadSmallImage(url, (ImageView) helper.getView(R.id.new_item_iv));
                        return;
                    }
                }
                GlideArms.with(Utils.getContext())
                        .load(R.drawable.place_holder_big_bitmap)
                        // .crossFade()
                        .into((ImageView) helper.getView(R.id.new_item_iv));
                break;
            case NewsItemEntity.IMPORT_CASE:
                if (imgInfoJson != null && imgInfoJson.size() > 0) {
                    List<String> imgsWithToken = AttachmentUtils.getImgsWithToken(imgInfoJson);
                    if (imgsWithToken.size() > 0) {
                        String url = UrlUtils.getAbbrUrl(imgsWithToken.get(0),
                                SizeUtils.getScreenWidth(),
                                (int) Utils.getContext().getResources()
                                        .getDimension(R.dimen.case_import_iv_height));
                        loadBigImage(url, (ImageView) helper.getView(R.id.new_item_iv));
                        return;
                    }
                }
                GlideArms.with(Utils.getContext())
                        .load(R.drawable.place_holder_big_bitmap)
                        // .crossFade()
                        .into((ImageView) helper.getView(R.id.new_item_iv));
                break;
            case NewsItemEntity.MULTI_IMAGE_CASE:
                int width = SizeUtils.getScreenWidth() / 3;
                int height = (int) Utils.getContext().getResources()
                        .getDimension(R.dimen.case_multi_iv_height);
                List<String> imgsWithToken = AttachmentUtils.getImgsWithToken(imgInfoJson);
                loadSmallImage(UrlUtils.getAbbrUrl(imgsWithToken.get(0), width, height),
                        (ImageView) helper.getView(R.id.case_image_one));
                loadSmallImage(UrlUtils.getAbbrUrl(imgsWithToken.get(1), width, height),
                        (ImageView) helper.getView(R.id.case_image_two));
                loadSmallImage(UrlUtils.getAbbrUrl(imgsWithToken.get(2), width, height),
                        (ImageView) helper.getView(R.id.case_image_three));
                break;
            default:
                break;
        }

    }


    /**
     * 默认图片为小图.
     *
     * @param url  图片的Url
     * @param view 图片的展示View
     */
    public void loadSmallImage(String url, ImageView view) {
        GlideArms.with(Utils.getContext())
                .load(url)
                .placeholder(R.drawable.place_holder_big_bitmap)
                .thumbnail(0.1f)
                // .transform(new GlideRoundTransform(MyApplication.getInstance(), 0))
                .error(R.drawable.place_holder_big_bitmap)
                // .crossFade()
                .into(view);
    }


    /**
     * 默认图片为大图.
     *
     * @param url  图片的Url
     * @param view 图片的展示View
     */
    public void loadBigImage(String url, ImageView view) {
        GlideArms.with(Utils.getContext())
                .load(url)
                .placeholder(R.drawable.place_holder_big_bitmap)
                .thumbnail(0.1f)
                // .transform(new GlideRoundTransform(MyApplication.getInstance(), 0))
                .error(R.drawable.place_holder_big_bitmap)
                // .crossFade()
                .into(view);
    }

}
