package cloud.antelope.lingmou.mvp.model.entity;

/**
 * 作者：陈新明
 * 创建日期：2018/05/17
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class VideosBean {
    private long begin;
    private long end;

    public VideosBean(long begin, long end) {
        this.begin = begin;
        this.end = end;
    }

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "VideosBean{" +
                "begin=" + begin +
                ", end=" + end +
                '}';
    }
}
