package cloud.antelope.lingmou.mvp.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 作者：陈新明
 * 创建日期：2017/9/12
 * 邮箱：
 * 描述：TODO
 */

public class HttpStatus {
    @SerializedName("code")
    private int mCode;

    public int getCode() {
        return mCode;
    }


    /**
     * API是否请求失败
     *
     * @return 失败返回true, 成功返回false
     */
    public boolean isCodeInvalid() {
        return mCode == 401;
    }

}
