/**
 * Copyright (C) 2016 LingDaNet.Co.Ltd. All Rights Reserved.
 */
package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：赵炜
 * 创建日期：2016/9/23
 * 邮箱：zhaowei0920@lingdanet.com
 * 描述：
 */

public class BaseCyResponseEntity<T> implements Serializable {

    //Http response status
    private static final int RESPONSE_SUCCESS_CODE = 20000;
    private static final int ABATE_TOKEN_CODE = 40104;
    private static final int INVALID_TOKEN_CODE = 40105;

    public int err_code;

    public String message;

    public T data;

    public boolean success() {
        return RESPONSE_SUCCESS_CODE == err_code;
    }

    public boolean tokenInvalid() {
        return INVALID_TOKEN_CODE == err_code;
    }

    public boolean tokenAbate() {
        return ABATE_TOKEN_CODE == err_code;
    }
}
