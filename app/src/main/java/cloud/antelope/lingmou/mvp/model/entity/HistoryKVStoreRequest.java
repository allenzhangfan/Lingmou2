package cloud.antelope.lingmou.mvp.model.entity;

import android.support.v4.util.Pools;

import java.util.List;

/**
 * Created by yexiaokang on 2018/8/2.
 */
public class HistoryKVStoreRequest {

    private static final int MAX_POOL_SIZE = 5;
    private static Pools.Pool<HistoryKVStoreRequest> sPool = new Pools.SynchronizedPool<>(MAX_POOL_SIZE);

    /**
     * [\"538378251_摄像机名_9_1533117330884\",
     * \"538378653_摄像机名_2_1533117313544\",
     * \"538379377_摄像机名_2_1533117431720\",
     * \"538378418_摄像机名_2_1533117519880\"]
     */

    private List<History> histories;

    private HistoryKVStoreRequest() {
    }

    public List<History> getHistories() {
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }

    public static HistoryKVStoreRequest obtain() {
        HistoryKVStoreRequest request = sPool.acquire();
        return (request != null) ? request : new HistoryKVStoreRequest();
    }

    public void recycle() {
        onPreRecycle();
        sPool.release(this);
    }

    private void onPreRecycle() {
        histories = null;
    }

    /**
     * 历史记录，"538378251_摄像机名_9_1533117330884"
     */
    public static class History {

        /**
         * manufacturerDeviceId
         */
        private long cameraId;
        /**
         * 摄像机名
         */
        private String cameraName;
        /**
         * 观看次数
         */
        private long times;
        /**
         * 上次观看时间
         */
        private long timestamp;

        /**
         * 构造函数
         *
         * @param cameraId   摄像机id
         * @param cameraName 摄像机名称
         * @param times      观看次数
         * @param timestamp  上次观看时间
         */
        public History(long cameraId, String cameraName, long times, long timestamp) {
            this.cameraId = cameraId;
            this.cameraName = cameraName;
            this.times = times;
            this.timestamp = timestamp;
        }

        public long getCameraId() {
            return cameraId;
        }

        public String getCameraName() {
            return cameraName;
        }

        public long getTimes() {
            return times;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void touch() {
            times++;
            timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return cameraId + "_" + cameraName + "_" + times + "_" + timestamp;
        }
    }
}
