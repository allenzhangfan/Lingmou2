package cloud.antelope.lingmou.app.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import org.simple.eventbus.EventBus;

import java.util.HashMap;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.ShareEntity;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 作者：安兴亚
 * 创建日期：2017/05/27
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class ShareUtils implements PlatformActionListener {

    private static final int MSG_WHAT_QQ_SHARE_OK = 0x01;
    private static final int MSG_WHAT_QZONE_SHARE_OK = 0x02;
    private static final int MSG_WHAT_WECHAT_SHARE_OK = 0x03;
    private static final int MSG_WHAT_WECHAT_MOMENTS_SHARE_OK = 0x04;
    private static final int MSG_WHAT_WEIBO_SHARE_OK = 0x05;
    private static final int MSG_WHAT_SHARE_FAILED = 0x06;
    private static final int MSG_WHAT_SHARE_CANCELED = 0x07;

    /**
     * 点击的是分享到微信好友
     */
    private boolean mWechatShare;
    /**
     * 点击的是分享到QQ好友
     */
    private boolean mQQShare;

    /**
     * 取消分享
     */
    private boolean mCancelShare;
    /**
     * 分享成功
     */
    private boolean mCompleteShare;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_QQ_SHARE_OK:
                    ToastUtils.showShort(R.string.qq_share_ok);
                    break;
                case MSG_WHAT_QZONE_SHARE_OK:
                    ToastUtils.showShort(R.string.qzone_share_ok);
                    break;
                case MSG_WHAT_WECHAT_SHARE_OK:
                    ToastUtils.showShort(R.string.wechat_share_ok);
                    break;
                case MSG_WHAT_WECHAT_MOMENTS_SHARE_OK:
                    ToastUtils.showShort(R.string.wechat_moments_share_ok);
                    break;
                // case MSG_WHAT_WEIBO_SHARE_OK:
                //     ToastUtils.showShort(R.string.weibo_share_ok);
                //     break;
                case MSG_WHAT_SHARE_CANCELED:
                    ToastUtils.showShort(R.string.share_canceled);
                    break;
                case MSG_WHAT_SHARE_FAILED:
                    ToastUtils.showShort(R.string.share_failed);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 分享给QQ好友
     *
     * @param title
     * @param text
     * @param imageUrl
     * @param shareUrl
     */
    public void qqShare(String title, String text, String imageUrl, String shareUrl) {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);// 分享类型是网页
        sp.setTitle(title);
        sp.setText(text);
        // 案件有图片数据，则分享图片，没有就分享APP logo
        sp.setImageUrl(imageUrl);
        sp.setTitleUrl(shareUrl); // 网友点进链接后，可以看到分享的详情
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        boolean clientValid = qq.isClientValid();
        if (!clientValid) {
            ToastUtils.showShort("请安装QQ客户端");
            return;
        }
        qq.setPlatformActionListener(this); // 设置分享事件回调
        qq.share(sp);
        mQQShare = true;
    }

    /**
     * 分享到QQ空间
     *
     * @param title
     * @param text
     * @param imageUrl
     * @param shareUrl
     */
    public void qZoneShare(String title, String text, String imageUrl, String shareUrl) {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);// 分享类型是网页
        sp.setTitle(title);
        sp.setText(text);
        sp.setImageUrl(imageUrl);
        sp.setTitleUrl(shareUrl); // 网友点进链接后，可以看到分享的详情
        Platform qZone = ShareSDK.getPlatform(QZone.NAME);
        boolean clientValid = qZone.isClientValid();
        if (!clientValid) {
            ToastUtils.showShort("请安装QQ客户端");
            return;
        }
        qZone.setPlatformActionListener(this); // 设置分享事件回调
        qZone.share(sp);
    }

    /**
     * 分享给微信好友
     *
     * @param title
     * @param text
     * @param imageUrl
     * @param shareUrl
     */
    public void wechatShare(String title, String text, String imageUrl, String shareUrl) {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);// 分享类型是网页
        sp.setTitle(title);
        sp.setText(text);
        sp.setUrl(shareUrl); // 网友点进链接后，可以看到分享的详情
        sp.setImageUrl(imageUrl);
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        boolean clientValid = wechat.isClientValid();
        if (!clientValid) {
            ToastUtils.showShort("请安装微信客户端");
            return;
        }
        wechat.setPlatformActionListener(this); // 设置分享事件回调
        wechat.share(sp);
        mWechatShare = true;
    }

    /**
     * 分享到微信朋友圈
     *
     * @param title
     * @param text
     * @param imageUrl
     * @param shareUrl
     */
    public void wechatMomentsShare(String title, String text, String imageUrl, String shareUrl) {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE); // 分享类型是网页
        sp.setTitle(title);
        sp.setText(text);
        sp.setUrl(shareUrl); // 网友点进链接后，可以看到分享的详情
        sp.setImageUrl(imageUrl);
        Platform wechatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
        boolean clientValid = wechatMoments.isClientValid();
        if (!clientValid) {
            ToastUtils.showShort("请安装微信客户端");
            return;
        }

        wechatMoments.setPlatformActionListener(this); // 设置分享事件回调
        wechatMoments.share(sp);
    }

    /**
     * 分享到微博
     *
     * @param title
     * @param text
     * @param imageUrl
     * @param shareUrl
     */
    /*public void weiboShare(String title, String text, String imageUrl, String shareUrl) {
        SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE); // 分享类型是网页
        sp.setTitle(title);
        sp.setText(text);
        sp.setUrl(shareUrl); // 网友点进链接后，可以看到分享的详情
        sp.setImageUrl(imageUrl);
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        boolean clientValid = weibo.isClientValid();
        if (!clientValid) {
            ToastUtils.showShort("请安装微博客户端");
            return;
        }

        weibo.setPlatformActionListener(this); // 设置分享事件回调
        weibo.share(sp);
    }*/

    /**
     * 是否通过微信好友分享方式分享出去，并停留在微信，之后才返回。
     * 这种方式没有调到ShareSDK的onCancel和onComplete回调方法
     *
     * @return
     */
    public boolean isShareToWeChatSuccess() {
        if (mWechatShare && !mCancelShare && !mCompleteShare) {
            // 因为留在微信的时候会调用两次Activity的生命周期方法onStart，
            // 所以在第一次成功后要将mWechatShare和mCompleteShare设为false
            mWechatShare = false;
            mCompleteShare = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否通过QQ好友分享方式分享出去，并停留在QQ，之后才返回。
     * 这种方式没有调到ShareSDK的onCancel和onComplete回调方法
     *
     * @return
     */
    public boolean isShareToQQSuccess() {
        if (mQQShare && !mCancelShare && !mCompleteShare) {
            // 因为留在QQ的时候会调用两次Activity的生命周期方法onStart，
            // 所以在第一次成功后要将mQQShare和mCompleteShare设为false
            mQQShare = false;
            mCompleteShare = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onCancel(Platform arg0, int arg1) {// 回调的地方是子线程，进行UI操作要用handle处理
        if (!mCompleteShare) {
            mCancelShare = true;
            mHandler.sendEmptyMessage(MSG_WHAT_SHARE_CANCELED);
        }
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {// 回调的地方是子线程，进行UI操作要用handle处理
        if (!mCompleteShare) {
            mCompleteShare = true;
            if (mOnCompleteListener != null) {
                mOnCompleteListener.share();
            }
        }
        if (platform.getName().equals(QQ.NAME)) {
            mHandler.sendEmptyMessage(MSG_WHAT_QQ_SHARE_OK);
        } else if (platform.getName().equals(QZone.NAME)) {
            mHandler.sendEmptyMessage(MSG_WHAT_QZONE_SHARE_OK);
        } else if (platform.getName().equals(Wechat.NAME)) {
            mHandler.sendEmptyMessage(MSG_WHAT_WECHAT_SHARE_OK);
        } else if (platform.getName().equals(WechatMoments.NAME)) {
            mHandler.sendEmptyMessage(MSG_WHAT_WECHAT_MOMENTS_SHARE_OK);
        }
        /*else if (platform.getName().equals(SinaWeibo.NAME)) {
            mHandler.sendEmptyMessage(MSG_WHAT_WEIBO_SHARE_OK);
        }*/
    }

    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {// 回调的地方是子线程，进行UI操作要用handle处理
        mCancelShare = true;
        arg2.printStackTrace();
        Message msg = new Message();
        msg.what = MSG_WHAT_SHARE_FAILED;
        msg.obj = arg2.getMessage();
        mHandler.sendMessage(msg);
    }

    public interface OnCompleteListener {
        void share();
    }

    public OnCompleteListener mOnCompleteListener;

    public void setOnCompleteListener(OnCompleteListener listener) {
        mOnCompleteListener = listener;
    }
}
