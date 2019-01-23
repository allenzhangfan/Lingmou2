package cloud.antelope.lingmou.mvp.model.entity;

public class CarColorBean {
    /**
     * {
     "code": 117701,
     "name": "黑色",
     "typeCode": 117700
     }
     */
    public Long code;
    public String name;
    public String mark;
    public Long typeCode;
    public boolean selected;

    public CarColorBean(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }
}