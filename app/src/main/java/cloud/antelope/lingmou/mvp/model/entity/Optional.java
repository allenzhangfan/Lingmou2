package cloud.antelope.lingmou.mvp.model.entity;

import java.util.NoSuchElementException;

import io.reactivex.annotations.Nullable;

/**
 * 作者：安兴亚
 * 创建日期：2018/01/24
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class Optional <M> {

    private M optional;

    public Optional(@Nullable M optional) {
        this.optional = optional;
    }

    public boolean isEmpty() {
        return this.optional == null;
    }

    public M get() {
        if (optional == null) {
            throw new NoSuchElementException("No value present");
        }
        return optional;
    }
}
