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

public class BaseLyResponseEntity implements Serializable {

    public int code;
    public String message;

    public boolean isSuccess() {
        return code == 200;
    }
}
