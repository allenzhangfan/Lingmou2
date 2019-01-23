package cloud.antelope.lingmou.mvp.model.entity;

public class CollectChangeEvent {
    private int favoritesType;
    public CollectChangeEvent(int favoritesType) {
        this.favoritesType=favoritesType;
    }

    public int getFavoritesType() {
        return favoritesType;
    }
}
