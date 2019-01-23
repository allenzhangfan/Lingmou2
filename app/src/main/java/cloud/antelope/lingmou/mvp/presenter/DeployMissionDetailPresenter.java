package cloud.antelope.lingmou.mvp.presenter;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.net.ApiException;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.contract.DeployMissionDetailContract;
import cloud.antelope.lingmou.mvp.model.entity.DeployDetailEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchEntity;
import cloud.antelope.lingmou.mvp.model.entity.NewDeployMissionRequest;
import cloud.antelope.lingmou.mvp.model.entity.StartOrPauseDeployMissionRequest;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;

import static cloud.antelope.lingmou.mvp.ui.activity.DeployMissionDetailActivity.MODIFY;
import static cloud.antelope.lingmou.mvp.ui.activity.DeployMissionDetailActivity.PAUSE;
import static cloud.antelope.lingmou.mvp.ui.activity.DeployMissionDetailActivity.START;


@ActivityScope
public class DeployMissionDetailPresenter extends BasePresenter<DeployMissionDetailContract.Model, DeployMissionDetailContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public DeployMissionDetailPresenter(DeployMissionDetailContract.Model model, DeployMissionDetailContract.View rootView) {
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
    public void getData(String id){
        mModel.getData(id)
                .observeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null)
                    mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<DeployDetailEntity>(mErrorHandler) {
                    @Override
                    public void onNext(DeployDetailEntity detailEntity) {
                        mRootView.showData(detailEntity);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                    }
                });
    }
    public void deleteMission(String id){
        mModel.deleteMissin(id)
                .observeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null)
                    mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                    @Override
                    public void onNext(String id) {
                        mRootView.onDeleteSuccess();
                    }
                });
    }

    public void modifyMission(NewDeployMissionRequest request){
        mModel.getFeature(request.getImageUrl())
                .observeOn(Schedulers.io())
                .flatMap(new Function<FaceUrlSearchBaseEntity<FaceUrlSearchEntity>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(FaceUrlSearchBaseEntity<FaceUrlSearchEntity> baseEntity) throws Exception {
                        if (null != baseEntity && null != baseEntity.getImgsList() && !baseEntity.getImgsList().isEmpty()) {
                            List<FaceUrlSearchEntity> imgsList = baseEntity.getImgsList();
                            String feature = null;
                            if (null != imgsList) {
                                for (FaceUrlSearchEntity urlSearchEntity : imgsList) {
                                    if (null != urlSearchEntity) {
                                        FaceUrlSearchEntity.FaceBean face = urlSearchEntity.getFace();
                                        if (face != null) {
                                            feature = face.getFeature();
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!TextUtils.isEmpty(feature)) {
                                request.setImportAttribute(feature);
                                return mModel.modifyDeployMission(request);
                            }
                        }
                        return Observable.error(new ApiException(((Activity) mRootView).getString(R.string.error_obtain_face_feature)));
                    }
                })
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null)
                    mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                    @Override
                    public void onNext(String id) {
                        mRootView.onModifySuccess(MODIFY);
                    }
                });
    }

    public void startOrPauseMission(StartOrPauseDeployMissionRequest request){
        mModel.startOrPauseMission(request)
                .observeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null)
                    mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                    @Override
                    public void onNext(String id) {
                        mRootView.onModifySuccess("0".equals(request.getType())?PAUSE:START);
                    }
                });
    }
}
