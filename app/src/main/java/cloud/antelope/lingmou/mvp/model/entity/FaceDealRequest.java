package cloud.antelope.lingmou.mvp.model.entity;

/**
 * Created by ChenXinming on 2017/12/27.
 * description:
 */

public class FaceDealRequest {

    /**
     * id : 08A5DB9F6AA9436AAB2A55A1895FA5AB
     * operationDetail :
     * isEffective : 1
     */

    private String id;
    private String operationDetail;
    private int isEffective;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperationDetail() {
        return operationDetail;
    }

    public void setOperationDetail(String operationDetail) {
        this.operationDetail = operationDetail;
    }

    public int getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(int isEffective) {
        this.isEffective = isEffective;
    }
}
