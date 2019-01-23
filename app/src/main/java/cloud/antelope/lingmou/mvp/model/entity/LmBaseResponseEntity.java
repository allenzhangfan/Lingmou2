package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * Created by ChenXinming on 2017/11/17.
 * description:
 */

public class LmBaseResponseEntity<T> implements Serializable {
    public int code;
    public T result;
    public String message;

    public boolean isSuccess() {
        return code == 200;
    }

    public boolean isInvalidate() {
        return code == 401;
    }

    public boolean isGatewayBad() {
        return code == 500;
    }

    public boolean isNoPermission() {
        return code == 403;
    }

}
