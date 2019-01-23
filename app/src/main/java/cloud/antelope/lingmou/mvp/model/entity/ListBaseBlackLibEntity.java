package cloud.antelope.lingmou.mvp.model.entity;

/**
 * 作者：陈新明
 * 创建日期：2018/05/10
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class ListBaseBlackLibEntity<T> {
    private int resultSize;
    private T resultList;

    public int getResultSize() {
        return resultSize;
    }

    public void setResultSize(int resultSize) {
        this.resultSize = resultSize;
    }

    public T getResultList() {
        return resultList;
    }

    public void setResultList(T resultList) {
        this.resultList = resultList;
    }
}
