package com.lzy.imagepicker.bean;

import java.io.Serializable;

/**
 * Created by ChenXinming on 2017/11/20.
 * description:
 */

public class ObjRequest implements Serializable {
    private String objectId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
