package cloud.antelope.lingmou.mvp.model.entity;

/**
 * 作者：陈新明
 * 创建日期：2018/09/20
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class WorkbenchBean {
    private String name;
    private int id;
    private int mBgDrawable;
    private int mIvDrawable;
    private int mTextColor;

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBgDrawable() {
        return mBgDrawable;
    }

    public void setBgDrawable(int bgDrawable) {
        mBgDrawable = bgDrawable;
    }

    public int getIvDrawable() {
        return mIvDrawable;
    }

    public void setIvDrawable(int ivDrawable) {
        mIvDrawable = ivDrawable;
    }
}
