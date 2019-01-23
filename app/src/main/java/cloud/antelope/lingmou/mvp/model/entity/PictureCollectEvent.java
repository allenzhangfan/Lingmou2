package cloud.antelope.lingmou.mvp.model.entity;

public class PictureCollectEvent {
    public int position;
    public boolean isCollected;
    public String colleteId;

    public PictureCollectEvent(int position, boolean isCollected,String colleteId) {
        this.position = position;
        this.isCollected = isCollected;
        this.colleteId = colleteId;
    }
}
