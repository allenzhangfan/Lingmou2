package cloud.antelope.lingmou.mvp.model.entity;

/**
 * 作者：陈新明
 * 创建日期：2018/09/07
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class DailyFilterItemEntity {
    private String name;
    private boolean isSelect;

    public DailyFilterItemEntity() {
    }
    public DailyFilterItemEntity(String name, boolean isSelect) {
        this.name = name;
        this.isSelect = isSelect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
