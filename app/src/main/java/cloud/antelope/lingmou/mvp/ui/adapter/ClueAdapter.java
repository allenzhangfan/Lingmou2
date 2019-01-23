package cloud.antelope.lingmou.mvp.ui.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.AttachmentUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.AttachmentBean;
import cloud.antelope.lingmou.mvp.model.entity.ClueItemEntity;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/25
 * 邮箱：anxingya@lingdanet.com
 * 描述：线索列表页的适配器
 */
public class ClueAdapter extends BaseMultiItemQuickAdapter<ClueItemEntity, BaseViewHolder> {

    public static final int NO_IMAGE_CLUE = 1;
    public static final int ONE_IMAGE_CLUE = 2;
    public static final int MUlTI_IMAGE_CLUE = 3;

    private static final int MAX_WIDTH = 600;
    private static final int MAX_LENGTH = 8;

    public ClueAdapter(List<ClueItemEntity> list) {
        super(list);
        addItemType(NO_IMAGE_CLUE, R.layout.item_clue_no_img);
        addItemType(ONE_IMAGE_CLUE, R.layout.item_clue_one_img);
        addItemType(MUlTI_IMAGE_CLUE, R.layout.item_clue_multi_imgs);
    }

    @Override
    protected void convert(BaseViewHolder helper, ClueItemEntity clueItem) {
        List<AttachmentBean> attachments = clueItem.getImgInfoJson();
        String videoThumb = AttachmentUtils.hasVideo(attachments);
        List<String> imgs = AttachmentUtils.getRealImgs(attachments);
        if (TextUtils.equals(Constants.IS_VALUED_REPORT, clueItem.getTipValued())) {
            helper.setImageResource(R.id.report_status_iv, R.drawable.ic_valued_report);
        } else {
            helper.setImageResource(R.id.report_status_iv, R.drawable.ic_clue_sent);
        }
        helper.setText(R.id.report_title_tv, clueItem.getTitle());
        helper.setText(R.id.report_time_tv,
                TimeUtils.millis2String(Long.valueOf(clueItem.getCreateTime()), "yyyy.MM.dd"));
        if (TextUtils.isEmpty(clueItem.getGpsAddr())) {
            helper.setText(R.id.location_address_tv, R.string.unknow_text);
        } else {
            if (SizeUtils.getScreenWidth() <= MAX_WIDTH
                    && clueItem.getGpsAddr().length() > MAX_LENGTH) {
                helper.setText(R.id.location_address_tv,
                        clueItem.getGpsAddr().substring(0, MAX_LENGTH) + "...");
            } else {
                helper.setText(R.id.location_address_tv, clueItem.getGpsAddr());
            }
        }
        switch (helper.getItemViewType()) {
            case ONE_IMAGE_CLUE:
                if (!TextUtils.isEmpty(videoThumb)) {
                    helper.setVisible(R.id.video_flag_iv, true);
                    helper.setVisible(R.id.new_item_iv, true);
                    AttachmentUtils.showImgUrl(videoThumb,
                            (ImageView) helper.getView(R.id.new_item_iv));
                } else if (imgs.size() > 0) {
                    helper.setVisible(R.id.video_flag_iv, false);
                    AttachmentUtils.showImgUrl(imgs.get(0),
                            (ImageView) helper.getView(R.id.new_item_iv));
                } else {
                    helper.setVisible(R.id.video_flag_iv, false);
                    helper.setImageResource(R.id.new_item_iv, R.drawable.place_holder_big_bitmap);
                }
                break;
            case MUlTI_IMAGE_CLUE:
                // 先展示视频
                if (!TextUtils.isEmpty(videoThumb)) {
                    helper.setVisible(R.id.video_flag_iv, true);
                    AttachmentUtils.showImgUrl(videoThumb,
                            (ImageView) helper.getView(R.id.case_image_one));
                    AttachmentUtils.showImgUrl(imgs.get(0),
                            (ImageView) helper.getView(R.id.case_image_two));
                    AttachmentUtils.showImgUrl(imgs.get(1),
                            (ImageView) helper.getView(R.id.case_image_three));
                } else {
                    // 全是图片
                    helper.setVisible(R.id.video_flag_iv, false);
                    AttachmentUtils.showImgUrl(imgs.get(0),
                            (ImageView) helper.getView(R.id.case_image_one));
                    AttachmentUtils.showImgUrl(imgs.get(1),
                            (ImageView) helper.getView(R.id.case_image_two));
                    AttachmentUtils.showImgUrl(imgs.get(2),
                            (ImageView) helper.getView(R.id.case_image_three));
                }
                break;
            default:
                break;
        }
    }

}
