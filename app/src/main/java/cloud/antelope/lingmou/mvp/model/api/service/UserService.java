/**
 * Copyright 2017 JessYan
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cloud.antelope.lingmou.mvp.model.api.service;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.AlarmDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.AppUpdateEntity;
import cloud.antelope.lingmou.mvp.model.entity.BaseListEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyDepotRequest;
import cloud.antelope.lingmou.mvp.model.entity.BodyFeatureInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyRecorgnizeRequest;
import cloud.antelope.lingmou.mvp.model.entity.CameraDevicesRequest;
import cloud.antelope.lingmou.mvp.model.entity.CameraLikeRequest;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamRequest;
import cloud.antelope.lingmou.mvp.model.entity.CameraStreamRequest;
import cloud.antelope.lingmou.mvp.model.entity.CamerasStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.CarBrandBean;
import cloud.antelope.lingmou.mvp.model.entity.CarDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.CarDepotListRequest;
import cloud.antelope.lingmou.mvp.model.entity.CarInfo;
import cloud.antelope.lingmou.mvp.model.entity.CarListResponse;
import cloud.antelope.lingmou.mvp.model.entity.CollectAlarmInfoRequest;
import cloud.antelope.lingmou.mvp.model.entity.CollectRequest;
import cloud.antelope.lingmou.mvp.model.entity.CollectResponse;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListRequest;
import cloud.antelope.lingmou.mvp.model.entity.CommentItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.CommentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.CommonListResponse;
import cloud.antelope.lingmou.mvp.model.entity.ContentRecordRequest;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.model.entity.DeployDetailEntity;
import cloud.antelope.lingmou.mvp.model.entity.DeployListRequest;
import cloud.antelope.lingmou.mvp.model.entity.DeployResponse;
import cloud.antelope.lingmou.mvp.model.entity.DeviceIdListRequest;
import cloud.antelope.lingmou.mvp.model.entity.DeviceLoginParamBean;
import cloud.antelope.lingmou.mvp.model.entity.DeviceLoginResponseBean;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.EventEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmBlackEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmBlackRequest;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmRequest;
import cloud.antelope.lingmou.mvp.model.entity.FaceDealRequest;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotRequest;
import cloud.antelope.lingmou.mvp.model.entity.FaceFeatureInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceFeatureRequest;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgnizeRequest;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchRequest;
import cloud.antelope.lingmou.mvp.model.entity.FeedbackRequest;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.ListBaseBlackLibEntity;
import cloud.antelope.lingmou.mvp.model.entity.ListBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.LmBaseResponseEntity;
import cloud.antelope.lingmou.mvp.model.entity.LocationRequest;
import cloud.antelope.lingmou.mvp.model.entity.LoginEntity;
import cloud.antelope.lingmou.mvp.model.entity.LoginRequest;
import cloud.antelope.lingmou.mvp.model.entity.LyTokenEntity;
import cloud.antelope.lingmou.mvp.model.entity.MessageCodeRequest;
import cloud.antelope.lingmou.mvp.model.entity.NewDeployMissionRequest;
import cloud.antelope.lingmou.mvp.model.entity.ObjectEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraParentEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraRequest;
import cloud.antelope.lingmou.mvp.model.entity.OrgMainEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrganizationEntity;
import cloud.antelope.lingmou.mvp.model.entity.QueryReplyPageEntity;
import cloud.antelope.lingmou.mvp.model.entity.RecordLyCameraBean;
import cloud.antelope.lingmou.mvp.model.entity.SoldierInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.StartOrPauseDeployMissionRequest;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import cloud.antelope.lingmou.mvp.model.entity.TrackingPersonListBean;
import cloud.antelope.lingmou.mvp.model.entity.TrackingPersonRequest;
import cloud.antelope.lingmou.mvp.model.entity.UpdateLastLoginTimeRequest;
import cloud.antelope.lingmou.mvp.model.entity.UpdatePswRequest;
import cloud.antelope.lingmou.mvp.model.entity.UrlEntity;
import cloud.antelope.lingmou.mvp.model.entity.User;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserUpdateEntity;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;


/**
 * ================================================
 * 展示 {@link Retrofit#create(Class)} 中需要传入的 ApiService 的使用方式
 * 存放关于用户的一些 API
 * <p>
 * Created by JessYan on 08/05/2016 12:05
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public interface UserService {
    String HEADER_ACCEPT_CONTENT = "Accept: */*";
    String HEADER_CONTENT_TYPE = "Content-type: application/json";

    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT})
    @GET("users")
    Observable<List<User>> getUsers(@Query("since") int lastIdQueried, @Query("per_page") int perPage);

    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
//    @POST("user/login")
    @POST("app/user/v3/login")
    Observable<LmBaseResponseEntity<LoginEntity>> login(@Body LoginRequest request);

    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @POST("app/user/sendLoginIdentifyCode")
    Observable<LmBaseResponseEntity<Object>> sendMessageCode(@Body MessageCodeRequest request);

    // 获取组织结构
    @GET("video_access_copy/list_cameras")
    Observable<LmBaseResponseEntity<OrganizationEntity>> getOrganizations(@Query("isRoot") String isRoot, @Query("id") String id, @Query("type") String type);

    //获取组织结构2.0
    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @POST("org/getAllByPid")
    Observable<LmBaseResponseEntity<List<OrgMainEntity>>> getMainOrgs(@Body EmptyEntity request);

    //根据组织Id获取组织下的摄像机2.0
    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @POST("device/selectDeviceByOrgIds")
    Observable<LmBaseResponseEntity<OrgCameraParentEntity>> getMainOrgCameras(@Body OrgCameraRequest request);

    //根据组织Id获取组织下的摄像机2.0
    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @POST("device/selectDeviceByUserId")
    Observable<LmBaseResponseEntity<OrgCameraParentEntity>> getAllCameras(@Body OrgCameraRequest request);

    //获取摄像机的直播流
    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @POST("device/getCameraStatus")
    Observable<LmBaseResponseEntity<CameraNewStreamEntity>> getCameraNewStream(@Body CameraNewStreamRequest request);

    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @GET("device/token/getLyTokenByCameraId/{cameraId}")
    Observable<LmBaseResponseEntity<String>> getCameraToken(@Path("cameraId") Long cameraId);

    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @POST("app/user/updateLastLoginTime")
    Observable<LmBaseResponseEntity<Object>> updateLastLoginTime(@Body UpdateLastLoginTimeRequest request);

    // 获取个人信息
    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @GET("app/user/listUserPrivilege")
//    @GET("user/queryUserPrivileges")
    Observable<LmBaseResponseEntity<UserInfoEntity>> queryUserInfo();

    @GET("queryCollectionCameras")
    Observable<LmBaseResponseEntity<OrganizationEntity>> getCollections(@Query("userId") String userId);


    // 获取对象或事件存储的Token
    @Headers({"Domain-Name: baseUrl"})
    @GET("device/token/getLyObjectToken")
    Observable<LmBaseResponseEntity<TokenEntity>> getStorageToken();


    // 上传头像
    @Headers({"Domain-Name: ly_name"})
    @Multipart
    @POST("/upload2")
    Observable<ObjectEntity> uploadAvatar(@Query("access_token") String accessToken,
                                          @Query("size") String size,
                                          @Part("metadata") RequestBody metadata,
                                          @Part MultipartBody.Part file,
                                          @Query("expiretype") String expiretype);

    // 更新个人信息
    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @POST("user/changeUserImg")
    Observable<LmBaseResponseEntity<Object>> updateUserInfo(@Body UserUpdateEntity updateEntity);

    // 查询摄像机列表
    @GET("video_access_copy/search_only_camera")
    Observable<LmBaseResponseEntity<OrganizationEntity>> searchCamera(@Query("key") String key,
                                                                      @Query("type") String type,
                                                                      @Query("count") String count,
                                                                      @Query("offset") String offset,
                                                                      @Query("isOnline") String isOnline
    );

    // 收藏或取消收藏摄像机
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("cameraLike")
    Observable<LmBaseResponseEntity<EmptyEntity>> cameraLike(@Body CameraLikeRequest request);

    // 登录单兵设备
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("app/solosLogin")
    Observable<LmBaseResponseEntity<DeviceLoginResponseBean>> loginDevice(@Body DeviceLoginParamBean request);

    // 心跳接口
    // 获取羚羊事件上传的Token
    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @GET("/app/user/heartbeat")
    Observable<LmBaseResponseEntity<EmptyEntity>> heart(/*@Query("_") long time*/);

    //意见反馈
    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT})
    @POST("usr/feed_back")
    Observable<LmBaseResponseEntity<EmptyEntity>> feedBack(@Body FeedbackRequest request);


    // 通过旧密码修改密码
    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @POST("user/changePwd")
    Observable<LmBaseResponseEntity<Object>> updatePassword(@Body UpdatePswRequest request);

    // 检测更新
    @Headers({"Content-type: application/json", "Accept: */*"})
    @GET()
    Observable<AppUpdateEntity> checkUpdate(@Url String url);

    //获取布控报警列表
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("alarm/selectHistoryAlarmResult")
    Observable<LmBaseResponseEntity<ListBaseEntity<List<FaceAlarmBlackEntity>>>> faceAlarmList(@Body FaceAlarmRequest request);

    // 提交线索
    @Multipart
    @POST("/upload3")
    @Headers("Domain-Name: submitClue")
    Observable<EventEntity> submitClue(@Query("access_token") String accessToken,
                                       @Part("message") RequestBody message,
                                       @Part List<MultipartBody.Part> files);

    // 获取朝阳群众的Token
    @Headers({"Content-type: application/json", "Accept: */*"})
    @GET("getCyqzUserToken")
    Observable<LmBaseResponseEntity<TokenEntity>> getCyqzUserToken();

    // 获取羚羊事件上传的Token
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @GET("cms/v1/app/getstoragetoken")
    Observable<LmBaseResponseEntity<TokenEntity>> getCyqzLyToken();

    //获取人脸图库列表
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("largeData/faceList")
    Observable<LmBaseResponseEntity<FaceDepotEntity>> getFaceDepot(@Body FaceDepotRequest request);

    /**
     * 获取布控库
     *
     * @return
     */
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("alarm/selectBlacklist")
    Observable<LmBaseResponseEntity<ListBaseBlackLibEntity<List<AlarmDepotEntity>>>> getAlarmDepot(@Body EmptyEntity emptyEntity);


    //获取已授权的摄像机羚羊token
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("video/lingyangtoken")
    Observable<LmBaseResponseEntity<LyTokenEntity>> getLyToken(@Body CameraStreamRequest request);

    //获取视频流
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("video/getCurrentVedioList")
    Observable<LmBaseResponseEntity<CamerasStreamEntity>> getCameraStream(@Body CameraStreamRequest request);

    //获取录像时间段
    @Headers({"Domain-Name: ly_name"})
    @GET("/v2/record/{cid}")
    Observable<RecordLyCameraBean> getRecordTimes(@Path("cid") String cid, @Query("client_token") String token, @Query("begin") long start, @Query("end") long end);

    //处理报警详情
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("alarm/saveHistoryAlarmResult")
    Observable<LmBaseResponseEntity<EmptyEntity>> dealFaceAlarm(@Body FaceDealRequest request);

    //获取人体图库信息
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("largeData/bodyList")
    Observable<LmBaseResponseEntity<BodyDepotEntity>> getBodyDepot(@Body BodyDepotRequest request);


    // 上传图片url到服务器，获取人脸参数
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("largeData/face")
    Observable<LmBaseResponseEntity<FaceUrlSearchBaseEntity<FaceUrlSearchEntity>>> getFaceFeature(@Body FaceUrlSearchRequest request);



    //获取用户的KEY-VALUE
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("userKvStore/getUserKvStore")
    Observable<LmBaseResponseEntity<GetKeyStoreBaseEntity>> getUserKvStore(@Body GetKeyStoreRequest request);

    //设置用户的KEY-VALUE
    @Headers({"Domain-Name: baseUrl"})
    @FormUrlEncoded
    @POST("userKvStore/setUserKvStore")
    Observable<LmBaseResponseEntity<GetKeyStoreBaseEntity>> setUserKvStore(@Field("userId")String userId, @Field("storeKey")String storeKey,@Field("storeValue")String storeValue);

    @Headers({"Domain-Name: baseUrl"})
    @GET("app/searchUserSolosDevice/{uid}")
    Observable<LmBaseResponseEntity<SoldierInfoEntity>> getSolierInfo(@Path("uid") String uid);

    // 查询评论
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("cms/v1/app/queryReplyPage")
    // @POST("/json.php")
    Observable<LmBaseResponseEntity<CommentListEntity>> queryReplyPage(@Body QueryReplyPageEntity request);


    // 针对内容进行评论
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("cms/v1/app/addReply")
    Observable<LmBaseResponseEntity<EmptyEntity>> addReply(@Body CommentItemEntity addReplyRequest);

    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("cms/v1/app/addContentRecord")
    Observable<LmBaseResponseEntity<EmptyEntity>> contentRecord(@Body ContentRecordRequest request);

    @Headers({"Domain-Name: baseUrl"})
    @GET("alarm/selectHistoryAlarmResultDetail/{id}")
    Observable<LmBaseResponseEntity<EmptyEntity>> getAlarmDetailPermission(@Path("id") String uid);

    /**
     *
     * @param locationRequest 地理位置信息
     * @return
     */
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("app/updateDeviceGeo")
    Observable<LmBaseResponseEntity<EmptyEntity>> upLoadLocation(@Body LocationRequest locationRequest);

    /**
     * 退出登录
     * @return
     */
    @Headers({"Domain-Name: baseUrl"})
    @GET("user/logout")
    Observable<LmBaseResponseEntity<String>> logOut();

    /**
     * 请求归属地
     * @return
     */
    @Headers({"Domain-Name: getBaseUrls"})
    @GET("/")
    Observable<List<UrlEntity>> getBaseUrls();

    /**
     * 根据图片Vid,获取人脸特征信息
     *
     * @return
     */
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("largeData/getCaptureFaceInfoById")
    Observable<LmBaseResponseEntity<FaceFeatureInfoEntity>> getCaptureFaceInfoById(@Body FaceFeatureRequest request);

    /**
     * 根据图片Vid,获取人体特征信息
     *
     * @return
     */
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("largeData/getBodyFeatruByEntity")
    Observable<LmBaseResponseEntity<BodyFeatureInfoEntity>> getBodyFeatruByEntity(@Body FaceFeatureRequest request);

    /**
     * 获取人体搜图数据
     * @param objRequest
     * @return
     */
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("largeData/geBodyListByFeature")
    Observable<LmBaseResponseEntity<BodyRecorgBaseEntity<FaceNewEntity>>> getBodyListByFeature(@Body BodyRecorgnizeRequest objRequest);

    /**
     * 获取人脸搜图数据
     * @param objRequest
     * @return
     */
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("largeData/getFaceListByFeature")
    Observable<LmBaseResponseEntity<FaceRecorgBaseEntity<FaceNewEntity>>> getFaceListByFeature(@Body FaceRecorgnizeRequest objRequest);
    /**
     * 新建临控任务
     * @param objRequest
     * @return
     */
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("app/alarm/saveSurveillanceTask")
    Observable<LmBaseResponseEntity<String>> newDeployMission(@Body NewDeployMissionRequest objRequest);
    /**
     * 临控任务列表
     * @param objRequest
     * @return
     */
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("app/alarm/getSurveillanceTaskList")
    Observable<LmBaseResponseEntity<DeployResponse>> getdeployMissionList(@Body DeployListRequest objRequest);
    /**
     * 临控任务详情
     * @return
     */
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @GET("app/alarm/getSurveillanceTask/{id}")
    Observable<LmBaseResponseEntity<DeployDetailEntity>> getDeployDetail(@Path("id") String id);
    /**
     * 删除临控任务
     * @return
     */
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @GET("app/alarm/deleteSurveillanceTask/{id}")
    Observable<LmBaseResponseEntity<String>> deleteDeployMission(@Path("id") String id);

    /**
     * 开始或暂停临控任务
     * @param objRequest
     * @return
     */
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("app/alarm/changeSurveillanceTaskType")
    Observable<LmBaseResponseEntity<String>> startOrPauseMission(@Body StartOrPauseDeployMissionRequest objRequest);
    /**
     * 收藏列表
     * @param objRequest
     * @return
     */
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("app/favorites/getPictureFavoritesList")
    Observable<LmBaseResponseEntity<CommonListResponse<CollectionListBean>>> getCollectionList(@Body CollectionListRequest objRequest);

    /**
     *  获取所有摄像机接口
     * @param objRequest
     * @return
     */
    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @POST("app/device/getVideoList")
    Observable<LmBaseResponseEntity<BaseListEntity<OrgCameraEntity>>> getCameraDevices(@Body CameraDevicesRequest objRequest);


    /**
     * 获取设备的详情
     * @return
     */
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @GET("app/device/getDeviceInfo/{id}")
    Observable<LmBaseResponseEntity<OrgCameraEntity>> getDeviceInfo(@Path("id") String id);

    /**
     * 获取历史报警列表
     * @param objRequest
     * @return
     */
    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @POST("/app/alarm/selectHistoryAlarmResult")
    Observable<LmBaseResponseEntity<BaseListEntity<DailyPoliceAlarmEntity>>> getAlarms(@Body FaceAlarmBlackRequest objRequest);


    /**
     *  收藏/取消收藏图片
     * @param objRequest
     * @return
     */
    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @POST("app/favorites/savePicture")
    Observable<LmBaseResponseEntity<CollectResponse>> collectPicture(@Body CollectRequest objRequest);
    /**
     *  追踪的人员列表
     */
     @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
     @POST("app/alarm/getTemporarySurveillancePeople")
     Observable<LmBaseResponseEntity<CommonListResponse<TrackingPersonListBean>>> getTrackingPeople(@Body TrackingPersonRequest objRequest);

    /**
     *  搜索设备ID列表
     * @param objRequest
     * @return
     */
    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @POST("app/device/getUserDeviceCIdList")
    Observable<LmBaseResponseEntity<LmBaseResponseEntity<ArrayList<String>>>> getUserDeviceIdList(@Body DeviceIdListRequest objRequest);

    /**
     *  收藏/取消收藏告警
     * @param objRequest
     * @return
     */
    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @POST("app/favorites/saveAlarm")
    Observable<LmBaseResponseEntity<Object>> collectAlarmInfo(@Body CollectAlarmInfoRequest objRequest);

    /**
     *  车辆信息
     * @return
     */
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @GET("/app/vehicle/selectVehicleLib")
    Observable<LmBaseResponseEntity<CarInfo>> getCarInfo();
    /**
     *  车辆列表
     * @return
     */
    @Headers({HEADER_CONTENT_TYPE, HEADER_ACCEPT_CONTENT, "Domain-Name: baseUrl"})
    @POST("vehicle/getVehiclePassRecordsByFilterV2")
    Observable<LmBaseResponseEntity<CarListResponse>> getCarList(@Body CarDepotListRequest objRequest);
}
