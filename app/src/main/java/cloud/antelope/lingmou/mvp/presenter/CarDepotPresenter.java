package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.litepal.crud.DataSupport;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.SuccessNullException;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.CarBrandBean;
import cloud.antelope.lingmou.mvp.model.entity.CarColorBean;
import cloud.antelope.lingmou.mvp.model.entity.CarDepotListRequest;
import cloud.antelope.lingmou.mvp.model.entity.CarInfo;
import cloud.antelope.lingmou.mvp.model.entity.CarListResponse;
import cloud.antelope.lingmou.mvp.model.entity.CarTypeBean;
import cloud.antelope.lingmou.mvp.model.entity.DeployListRequest;
import cloud.antelope.lingmou.mvp.model.entity.DeployResponse;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.PlateColorBean;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import cloud.antelope.lingmou.mvp.contract.CarDepotContract;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;


@ActivityScope
public class CarDepotPresenter extends BasePresenter<CarDepotContract.Model, CarDepotContract.View> {
    String carType = "[{value: null,label: '全部',ids: null},{value: 'b',label: '轿车',ids: [117669]},{value: 'c',label: '面包车',ids: [117670]},{value: 'd',label: '越野车/SUV',ids: [117674]},{value: 'e',label: '皮卡',ids: [117671]},{value: 'f',label: '商务车/MPV',ids: [117672]},{value: 'g',label: '三轮车',ids: [117667, 117594]},{value: 'h',label: '摩托车',ids: [117585,117586,117587,117588,117589,117590,117591,117592,117593]},{value: 'i',label: '货车',ids: [117525,117526,117527,117528,117529,117530,117531,117532,117533,117534,117535,117536,117537,117538,117539,117540,117541,117542,117543,117544,117545,117546,117547,117548,117549,117550,117551,117552,117553,117554,117555,117556,117557,117558,117559,117560,117561,117562,117563,117564,117565,117566,117567,117675,117676]},{value: 'j',label: '客车',ids: [117501,117502,117503,117504,117505,117506,117507,117508,117509,117510,117511,117512,117513,117514,117515,117516,117517,117518,117519,117520,117521,117522,117523,117524]},{value: 'k',label: '公交车',ids: [117668]},{value: 'l',label: '校车',ids: [117673]},{value: 'm',label: '电车',ids: [117583, 117584]},{value: 'n',label: '拖拉机',ids: [117595, 117596, 117597, 117598, 117599]},{value: 'o',label: '牵引车',ids: [117568,117569,117570,117571,117572,117573,117574,117575,117576]},{value: 'p',label: '专项作业车',ids: [117577, 117578, 117579, 117580, 117581, 117582]},{value: 'q',label: '轮式机械',ids: [117600, 117601, 117602]},{value: 'r',label: '全挂车',ids: [117603,117604,117605,117606,117607,117608,117609,117610,117611,117612,117613,117614,117615,117616,117617,117618,117619,117620,117621,117622,117623,117624,117625,117626,117627,117628,117629,117630,117631]},{value: 's',label: '半挂车',ids: [117632,117633,117634,117635,117636,117637,117638,117639,117640,117641,117642,117643,117644,117645,117646,117647,117648,117649,117650,117651,117652,117653,117654,117655,117656,117657,117658,117659,117660,117661,117662,117663,117664,117665]},{value: 't',label: '其他',ids: [117666]}]";
    String carColor = "[{code:null,name:全部,mark:null,typeCode:117700},{code:117702,name:白色,mark:FFFFFF,typeCode:117700},{code:117701,name:黑色,mark:000000,typeCode:117700},{code:117703,name:灰色,mark:B5BBC7,typeCode:117700},{code:117704,name:蓝色,mark:0099FF,typeCode:117700},{code:117705,name:黄色,mark:FFDD00,typeCode:117700},{code:117706,name:橙色,mark:FF8800,typeCode:117700},{code:117707,name:棕色,mark:B36D57,typeCode:117700},{code:117708,name:绿色,mark:00FF33,typeCode:117700},{code:117709,name:紫色,mark:9900FF,typeCode:117700},{code:117711,name:粉色,mark:D82B8F,typeCode:117700},{code:117714,name:红色,mark:FF0000,typeCode:117700},{code:117715,name:银色,mark:C0C0C0,typeCode:117700},{code:117713,name:其他,mark:null,typeCode:117700}]";
    String plateColor = "[{code:null,name:全部},{code:117754,name:蓝色},{code:117753,name:黄色},{code:117752,name:白色},{code:117751,name:黑色},{code:117755,name:渐变绿色},{code:117756,name:黄绿双拼色},{code:117757,name:其他}]";
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public CarDepotPresenter(CarDepotContract.Model model, CarDepotContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

    public void getCollections() {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        String userId = SPUtils.getInstance().getString(CommonConstant.UID);
        mModel.getCollections(userId)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (null != mRootView) {
                        mRootView.hideLoading();//隐藏下拉刷新的进度条}
                    }
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<GetKeyStoreBaseEntity>(mErrorHandler) {

                    @Override
                    public void onNext(GetKeyStoreBaseEntity entity) {
                        mRootView.getCollectionsSuccess(entity);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (t instanceof SuccessNullException) {
                            mRootView.getCollectionsSuccess(null);
                        } else {
                            mRootView.getCollectionsFail();
                        }
                    }
                });

    }

    public void getInfo() {
        CarInfo info=new CarInfo();
        info.vehiclePlateVo = new Gson().fromJson(plateColor, new TypeToken<List<PlateColorBean>>() {
        }.getType());
        info.vehicleBodyVo = new Gson().fromJson(carColor, new TypeToken<List<CarColorBean>>() {
        }.getType());
        info.vehicleTypeVo = new Gson().fromJson(carType, new TypeToken<List<CarTypeBean>>() {
        }.getType());
        info.vehicleInfo=DataSupport.findAll(CarBrandBean.class);
        for (PlateColorBean bean : info.vehiclePlateVo) {
            if ("全部".equals(bean.name)) {
                bean.selected = true;
            }
        }
        for (CarColorBean bean : info.vehicleBodyVo) {
            if ("全部".equals(bean.name)) {
                bean.selected = true;
            }
        }
        for (CarTypeBean bean : info.vehicleTypeVo) {
            if ("全部".equals(bean.label)) {
                bean.selected = true;
            }
        }
        mRootView.onGetInfoSuccess(info);
    }

    public void getList(CarDepotListRequest request) {
        /**
         * devices : []
         * endTime : 1543198149074
         * page : 1
         * pageSize : 15
         * plateColor : 117754
         * plateNo : D86665
         * startTime : 1542988800000
         * vehicleBrands : [117111]
         * vehicleClasses : [117672]
         * vehicleColor : 117702
         */
        /*ToastUtils.showLong("startTime: " + TimeUtils.millis2String(request.startTime)
                + "\nendTime: " + TimeUtils.millis2String(request.endTime)
                + "\nplateColor: " + request.plateColor
                + "\nplateNo: " + request.plateNo
                + "\nvehicleColor: " + request.vehicleColor
                + "\nvehicleBrands: " + (request.vehicleBrands == null ? "null" : request.vehicleBrands.toString())
                + "\nvehicleClasses: " + (request.vehicleClasses == null ? "null" : request.vehicleClasses.toString())
                + "\nplateNos: " + (request.plateNos == null ? "null" : request.plateNos.toString())
                + "\ndevices: " + (request.devices == null ? "null" : request.devices.toString())
        );*/
        mModel.getCarList(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                })
                .doFinally(() -> {
                    if (mRootView != null)
                        mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<CarListResponse>(mErrorHandler) {
                    @Override
                    public void onNext(CarListResponse response) {
                        if (request.page == 1) {
                            mRootView.showList(response.list);
                        } else {
                            mRootView.showMore(response.list);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.onError();
                    }
                });
    }
}
