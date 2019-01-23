package cloud.antelope.lingmou.mvp.model.entity;

import java.util.List;

public class CarTypeBean {
    public String label;
    public String value;
    public List<Long> ids;
    public boolean selected;

    public CarTypeBean(String label, boolean selected) {
        this.label = label;
        this.selected = selected;
    }
}