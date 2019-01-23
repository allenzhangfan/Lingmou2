package cloud.antelope.lingmou.app.utils.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.lingdanet.safeguard.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.R;


/**
 * Created by yiyi.qi on 16/10/10.
 * 整体设计采用了两个线程,一个线程用于计算组织聚合数据,一个线程负责处理Marker相关操作
 */
public class ClusterOverlay implements AMap.OnCameraChangeListener,
        AMap.OnMarkerClickListener {
    private AMap mAMap;
    private Context mContext;
    private List<ClusterItem> mClusterItems;
    private List<Cluster> mClusters;
    private int mClusterSize;
    private ClusterClickListener mClusterClickListener;
    private MarkerChangeListener mMarkerChangeListener;
    private MapChangeListener mMapChangeListener;
    private ClusterRender mClusterRender;
    private List<Marker> mAddMarkers = new ArrayList<Marker>();
    private double mClusterDistance;
    private LruCache<Integer, BitmapDescriptor> mLruCache;
    private HandlerThread mMarkerHandlerThread = new HandlerThread("addMarker");
    private HandlerThread mSignClusterThread = new HandlerThread("calculateCluster");
    private Handler mMarkerHandler;
    private Handler mSignClusterHandler;
    private float mPXInMeters;
    private boolean mIsCanceled = false;
    /**
     * 设置地图上被选中Marker
     */
    private Marker mSelectMarker;
    /**
     * 设置地图上高亮显示的Marker
     */
    private Marker mHighlightMarker;

    private boolean mFromConstructor;

    /**
     * 构造函数
     *
     * @param amap
     * @param clusterSize 聚合范围的大小（指点像素单位距离内的点会聚合到一个点显示）
     * @param context
     */
    public ClusterOverlay(AMap amap, int clusterSize, Context context) {
        this(amap, null, clusterSize, context, null);
    }

    public void clearClusterItem(){
        mClusterItems.clear();
    }
    /**
     * 构造函数,批量添加聚合元素时,调用此构造函数
     *
     * @param amap
     * @param clusterItems 聚合元素
     * @param clusterSize
     * @param context
     */
    public ClusterOverlay(AMap amap, List<ClusterItem> clusterItems,
                          int clusterSize, Context context, Marker selectMarker) {
        //默认最多会缓存80张图片作为聚合显示元素图片,根据自己显示需求和app使用内存情况,可以修改数量
        mLruCache = new LruCache<Integer, BitmapDescriptor>(80) {
            protected void entryRemoved(boolean evicted, Integer key, BitmapDescriptor oldValue, BitmapDescriptor newValue) {
                oldValue.getBitmap().recycle();
            }
        };
        if (null != selectMarker) {
            mSelectMarker = selectMarker;
            mFromConstructor = true;
        }
        if (clusterItems != null) {
            mClusterItems = clusterItems;
        } else {
            mClusterItems = new ArrayList<ClusterItem>();
        }
        mContext = context;
        mClusters = new ArrayList<Cluster>();
        this.mAMap = amap;
        mClusterSize = clusterSize;
        mPXInMeters = mAMap.getScalePerPixel();
        mClusterDistance = mPXInMeters * mClusterSize;
        amap.setOnCameraChangeListener(this);
        amap.setOnMarkerClickListener(this);
        initThreadHandler();
        // assignClusters();
    }

    /**
     * 设置聚合点的点击事件
     *
     * @param clusterClickListener
     */
    public void setOnClusterClickListener(
            ClusterClickListener clusterClickListener) {
        mClusterClickListener = clusterClickListener;
    }

    /**
     * 设置选中的Marker变化的监听
     *
     * @param markerChangeListener
     */
    public void setMarkerChangeListener(MarkerChangeListener markerChangeListener) {
        mMarkerChangeListener = markerChangeListener;
    }

    /**
     * 地图变化的监听
     *
     * @param mapChangeListener
     */
    public void setOnMapChangeListener(MapChangeListener mapChangeListener) {
        mMapChangeListener = mapChangeListener;
    }

    /**
     * 设置被选中的Marker
     *
     * @param selectMarker
     */
    public void setSelectMarker(Marker selectMarker) {
        if (null != mHighlightMarker && null != mMarkerChangeListener) {
            mMarkerChangeListener.onSelectMarkerChange(mHighlightMarker);
            mHighlightMarker = null;
        }
        mSelectMarker = selectMarker;
    }

    /**
     * 添加一个聚合点
     *
     * @param item
     */
    public void addClusterItem(ClusterItem item) {
        Message message = Message.obtain();
        message.what = SignClusterHandler.CALCULATE_SINGLE_CLUSTER;
        message.obj = item;
        mSignClusterHandler.sendMessage(message);
    }

    /**
     * 设置聚合元素的渲染样式，不设置则默认为气泡加数字形式进行渲染
     *
     * @param render
     */
    public void setClusterRenderer(ClusterRender render) {
        mClusterRender = render;
    }

    public void onDestroy() {
        mIsCanceled = true;
        mSignClusterHandler.removeCallbacksAndMessages(null);
        mMarkerHandler.removeCallbacksAndMessages(null);
        mSignClusterThread.quit();
        mMarkerHandlerThread.quit();
        for (Marker marker : mAddMarkers) {
            marker.remove();

        }
        mAddMarkers.clear();
        mLruCache.evictAll();
    }

    //初始化Handler
    private void initThreadHandler() {
        mMarkerHandlerThread.start();
        mSignClusterThread.start();
        mMarkerHandler = new MarkerHandler(mMarkerHandlerThread.getLooper());
        mSignClusterHandler = new SignClusterHandler(mSignClusterThread.getLooper());
    }

    @Override
    public void onCameraChange(CameraPosition arg0) {
        if (null != mMapChangeListener) {
            mMapChangeListener.onCameraChange(arg0);
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition arg0) {
        LogUtils.d("onCameraChangeFinish in cluster");
        mPXInMeters = mAMap.getScalePerPixel();
        mClusterDistance = mPXInMeters * mClusterSize;
        mHighlightMarker = null;
        assignClusters();
        if (null != mMapChangeListener) {
            mMapChangeListener.onCameraChangeFinish(arg0);
        }
    }

    //点击事件
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mClusterClickListener == null) {
            return true;
        }
        Cluster cluster = (Cluster) marker.getObject();
        if (cluster != null) { // 如果Marker中包含的数据不为空或者没有被回收
            mClusterClickListener.onClick(marker, cluster.getClusterItems());
            return true;
        } else { // 如果聚合点中的高亮的Marker被点击了，这个Marker是在地图页被添加到地图上的
            mClusterClickListener.onClick(marker, null);
            return false;
        }

    }


    /**
     * 将聚合元素添加至地图上
     */
    private void addClusterToMap(List<Cluster> clusters) {
        ArrayList<Marker> removeMarkers = new ArrayList<>();
        removeMarkers.addAll(mAddMarkers);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        MyAnimationListener myAnimationListener = new MyAnimationListener(removeMarkers);
        for (Marker marker : removeMarkers) {
            marker.setAnimation(alphaAnimation);
            marker.setAnimationListener(myAnimationListener);
            marker.startAnimation();
        }

        for (Cluster cluster : clusters) {
            addSingleClusterToMap(cluster);
        }

        if (null != mHighlightMarker) { // 如果有高亮的Marker，则高亮的Marker单独就是一个聚合点，保持高亮Marker的高亮状态
            ArrayList<BitmapDescriptor> icons = mSelectMarker.getIcons();
            mHighlightMarker.setIcon(icons.get(icons.size() - 1));
            if (mFromConstructor) { // 如果被选中的Marker来自于构造函数，且不包含在聚合点中
                mMarkerChangeListener.onMarkerNotInMaxZoom();
                mFromConstructor = false;
            }
        } else { // 如果没有高亮的Marker
            if (mFromConstructor) { // 如果选中的Marker来自构造函数，则先将选中的Marker所在的聚合点从地图上移除
                mFromConstructor = false;
                if (null != mSelectMarker) {
                    Cluster cluster = getCluster(mSelectMarker.getPosition(), mClusters);
                    if (null != cluster && null != cluster.getMarker()) {
                        cluster.getMarker().remove();
                    }
                }
                if (null != mMarkerChangeListener) {
                    mMarkerChangeListener.onMarkerInMaxZoom();
                }

                // if (mAMap.getCameraPosition().zoom >= 19.0f) { //
                //     mFromConstructor = false;
                //     if (null != mSelectMarker) {
                //         Cluster cluster = getCluster(mSelectMarker.getPosition(), mClusters);
                //         if (null != cluster) {
                //             cluster.getMarker().remove();
                //         }
                //     }
                //     if (null != mMarkerChangeListener) {
                //         mMarkerChangeListener.onMarkerInMaxZoom();
                //     }
                // } else {
                //     if (null != mMarkerChangeListener) {
                //         mMarkerChangeListener.onMarkerClustered(true);
                //     }
                // }
            } else { // 如果高亮的Marker被聚合了，或者被移除屏幕之外了，则显示为未选中状态
                if (null != mMarkerChangeListener) {
                    mMarkerChangeListener.onMarkerClustered(false);
                }
                mSelectMarker = null;
            }
        }
    }

    private AlphaAnimation mADDAnimation = new AlphaAnimation(0, 1);

    /**
     * 将单个聚合元素添加至地图显示
     *
     * @param cluster
     */
    private void addSingleClusterToMap(Cluster cluster) {
        LatLng latlng = cluster.getCenterLatLng();
        MarkerOptions markerOptions = new MarkerOptions();
        boolean highLight = false;
        int clusterCount = cluster.getClusterCount();
        // 如果当前的聚合点只包含一个元素，则判断该元素的类型
        if (clusterCount == 1) {
            clusterCount = cluster.getClusterItems().get(0).getItemType();
            if (null != mSelectMarker) {  // 如果选中的元素不为空，则判断选中的当前元素是否与选中的元素相同
                Cluster selectCluster = (Cluster) mSelectMarker.getObject();
                if (null != selectCluster && TextUtils.equals(cluster.getClusterItems().get(0).getItemId(),
                        selectCluster.getClusterItems().get(0).getItemId())) {
                    highLight = true;
                }
            }
        }
        markerOptions.anchor(0.5f, 1.0f)
                .icon(getBitmapDes(clusterCount)).position(latlng);
        Marker marker = mAMap.addMarker(markerOptions);
        if (highLight) {
            mHighlightMarker = marker;
        }
        marker.setAnimation(mADDAnimation);
        marker.setObject(cluster);

        marker.startAnimation();
        cluster.setMarker(marker);
        mAddMarkers.add(marker);

    }

    private void calculateClusters() {
        mIsCanceled = false;
        mClusters.clear();
        LatLngBounds visibleBounds = mAMap.getProjection().getVisibleRegion().latLngBounds;
        for (ClusterItem clusterItem : mClusterItems) {
            if (mIsCanceled) {
                return;
            }
            LatLng latlng = clusterItem.getPosition();
            if (visibleBounds.contains(latlng)) {
                Cluster cluster = getCluster(latlng, mClusters);
                if (cluster != null) {
                    cluster.addClusterItem(clusterItem);
                } else {
                    cluster = new Cluster(latlng);
                    cluster.addClusterItem(clusterItem);
                    mClusters.add(cluster);
                }

            }
        }

        //复制一份数据，规避同步
        List<Cluster> clusters = new ArrayList<Cluster>();
        clusters.addAll(mClusters);
        Message message = Message.obtain();
        message.what = MarkerHandler.ADD_CLUSTER_LIST;
        message.obj = clusters;
        if (mIsCanceled) {
            return;
        }
        mMarkerHandler.sendMessage(message);
    }

    public void clearAllClusters() {
        Message message = Message.obtain();
        message.what = MarkerHandler.ADD_CLUSTER_LIST;
        message.obj = new ArrayList<>();
        mMarkerHandler.sendMessage(message);
    }

    /**
     * 对点进行聚合
     */
    public void assignClusters() {
        mIsCanceled = true;
        mSignClusterHandler.removeMessages(SignClusterHandler.CALCULATE_CLUSTER);
        mSignClusterHandler.sendEmptyMessage(SignClusterHandler.CALCULATE_CLUSTER);
    }

    /**
     * 在已有的聚合基础上，对添加的单个元素进行聚合
     *
     * @param clusterItem
     */
    private void calculateSingleCluster(ClusterItem clusterItem) {
        LatLngBounds visibleBounds = mAMap.getProjection().getVisibleRegion().latLngBounds;
        LatLng latlng = clusterItem.getPosition();
        if (!visibleBounds.contains(latlng)) {
            return;
        }
        Cluster cluster = getCluster(latlng, mClusters);
        if (cluster != null) {
            cluster.addClusterItem(clusterItem);
            Message message = Message.obtain();
            message.what = MarkerHandler.UPDATE_SINGLE_CLUSTER;

            message.obj = cluster;
            mMarkerHandler.removeMessages(MarkerHandler.UPDATE_SINGLE_CLUSTER);
            mMarkerHandler.sendMessageDelayed(message, 5);

        } else {

            cluster = new Cluster(latlng);
            mClusters.add(cluster);
            cluster.addClusterItem(clusterItem);
            Message message = Message.obtain();
            message.what = MarkerHandler.ADD_SINGLE_CLUSTER;
            message.obj = cluster;
            mMarkerHandler.sendMessage(message);

        }
    }

    /**
     * 根据一个点获取是否可以依附的聚合点，没有则返回null
     *
     * @param latLng
     * @return
     */
    private Cluster getCluster(LatLng latLng, List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            LatLng clusterCenterPoint = cluster.getCenterLatLng();
            double distance = AMapUtils.calculateLineDistance(latLng, clusterCenterPoint);
            if (distance < mClusterDistance && mAMap.getCameraPosition().zoom <= 19.0f) {
                return cluster;
            }
        }

        return null;
    }


    /**
     * 获取每个聚合点的绘制样式
     */
    private BitmapDescriptor getBitmapDes(int num) {
        BitmapDescriptor bitmapDescriptor = mLruCache.get(num);
        if (bitmapDescriptor == null) {
            TextView textView = new TextView(mContext);
            if (num > 1 && num < 1000) {
                textView.setText(String.valueOf(num));
            } else if (num >= 1000) {
                textView.setText("999+");
            }
            textView.setGravity(Gravity.CENTER);
            // LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // lp.setMargins(0, SizeUtils.dp2px(2), 0, 0);
            // textView.setLayoutParams(lp);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            if (mClusterRender != null) {
                Drawable drawAble = mClusterRender.getDrawAble(num);
                textView.setBackgroundDrawable(drawAble);
            }
            bitmapDescriptor = BitmapDescriptorFactory.fromView(textView);
            mLruCache.put(num, bitmapDescriptor);
        }
        return bitmapDescriptor;
    }

    /**
     * 更新已加入地图聚合点的样式
     */
    private void updateCluster(Cluster cluster) {

        Marker marker = cluster.getMarker();
        int clusterCount = cluster.getClusterCount();
        if (clusterCount == 1) {
            clusterCount = cluster.getClusterItems().get(0).getItemType();
        }
        marker.setIcon(getBitmapDes(clusterCount));


    }


    //-----------------------辅助内部类用---------------------------------------------

    /**
     * marker渐变动画，动画结束后将Marker删除
     */
    class MyAnimationListener implements Animation.AnimationListener {
        private List<Marker> mRemoveMarkers;

        MyAnimationListener(List<Marker> removeMarkers) {
            mRemoveMarkers = removeMarkers;
        }

        @Override
        public void onAnimationStart() {

        }

        @Override
        public void onAnimationEnd() {
            for (Marker marker : mRemoveMarkers) {
                marker.remove();
            }
            mRemoveMarkers.clear();
        }
    }

    /**
     * 处理market添加，更新等操作
     */
    class MarkerHandler extends Handler {

        static final int ADD_CLUSTER_LIST = 0;

        static final int ADD_SINGLE_CLUSTER = 1;

        static final int UPDATE_SINGLE_CLUSTER = 2;

        MarkerHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {

            switch (message.what) {
                case ADD_CLUSTER_LIST:
                    List<Cluster> clusters = (List<Cluster>) message.obj;
                    addClusterToMap(clusters);
                    break;
                case ADD_SINGLE_CLUSTER:
                    Cluster cluster = (Cluster) message.obj;
                    addSingleClusterToMap(cluster);
                    break;
                case UPDATE_SINGLE_CLUSTER:
                    Cluster updateCluster = (Cluster) message.obj;
                    updateCluster(updateCluster);
                    break;
            }
        }
    }

    /**
     * 处理聚合点算法线程
     */
    class SignClusterHandler extends Handler {
        static final int CALCULATE_CLUSTER = 0;
        static final int CALCULATE_SINGLE_CLUSTER = 1;

        SignClusterHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case CALCULATE_CLUSTER:
                    calculateClusters();
                    break;
                case CALCULATE_SINGLE_CLUSTER:
                    ClusterItem item = (ClusterItem) message.obj;
                    mClusterItems.add(item);
                    // Log.i("yiyi.qi", "calculate single cluster");
                    calculateSingleCluster(item);
                    break;
            }
        }
    }
}
