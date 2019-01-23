package cloud.antelope.lingmou.mvp.model.entity;

public class PlateColorBean {
    public static final String ALL = "全部";
    public static final String BLUE = "蓝色";
    public static final String YELLOW = "黄色";
    public static final String WHITE = "白色";
    public static final String BLACK = "黑色";
    public static final String GREEN = "渐变绿色";
    public static final String YELLOW_GREEN = "黄绿双拼色";
    public static final String OTHER ="其他";
    public boolean selected;
    public String name;
    public Long code;
    public long typeCode;
    public Integer label;

    public PlateColorBean(String name, boolean selected) {
        this.selected = selected;
        this.name = name;
    }
}