package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：陈新明
 * 创建日期：2018/05/18
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class GetKeyStoreBaseEntity implements Serializable{
    private GetKeyStoreEntity userKvStroe;

    public GetKeyStoreEntity getUserKvStroe() {
        return userKvStroe;
    }

    public void setUserKvStroe(GetKeyStoreEntity userKvStroe) {
        this.userKvStroe = userKvStroe;
    }
}
