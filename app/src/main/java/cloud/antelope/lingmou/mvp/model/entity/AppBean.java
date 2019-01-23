package cloud.antelope.lingmou.mvp.model.entity;

import android.graphics.drawable.Drawable;

/**
 * 作者：安兴亚
 * 创建日期：2018/01/08
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class AppBean {

    private String mName;
    private int mId;
    private Drawable mTopDrawable;

    public Drawable getTopDrawable() {
        return mTopDrawable;
    }

    public void setTopDrawable(Drawable topDrawable) {
        mTopDrawable = topDrawable;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}
