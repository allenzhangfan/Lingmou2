package cloud.antelope.lingmou.mvp.model.api.service;

import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.BannerItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.BaseCyResponseEntity;
import cloud.antelope.lingmou.mvp.model.entity.ClueItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.CommentItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.CommentLikeEntity;
import cloud.antelope.lingmou.mvp.model.entity.CommentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentRequestEntity;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.EventEntity;
import cloud.antelope.lingmou.mvp.model.entity.LikeCountEntity;
import cloud.antelope.lingmou.mvp.model.entity.LmBaseResponseEntity;
import cloud.antelope.lingmou.mvp.model.entity.NewsItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.OperationEntity;
import cloud.antelope.lingmou.mvp.model.entity.PushEntity;
import cloud.antelope.lingmou.mvp.model.entity.PushRequestList;
import cloud.antelope.lingmou.mvp.model.entity.QueryReplyPageEntity;
import cloud.antelope.lingmou.mvp.model.entity.ShareEntity;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * 作者：安兴亚
 * 创建日期：2018/01/10
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public interface CyqzService {


    // 获取
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @GET("cms/v1/app/getCyqzConfig")
    Observable<LmBaseResponseEntity<OperationEntity>> getCyqzConfig();

    // 将线索提交成功的数据传给灵达SAAS后台
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("cms/v1/app/cmsMessageQueueTest")
    Observable<LmBaseResponseEntity<EmptyEntity>> uploadClueMessage(@Body EventEntity eventEntity);

    // 查询栏目的最新更新时间
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("cms/v1/app/columnMaxCreateTime")
    Observable<LmBaseResponseEntity<Long>> getColumnLastUpdateTime(@Body ContentRequestEntity request);

    // 获取案件列表
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("cms/v1/app/getContentListForApp")
    Observable<LmBaseResponseEntity<ContentListEntity<NewsItemEntity>>> getNewsList(@Body ContentRequestEntity listRequest);

    // APP分享和告警分享
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("shop/v1/app/share")
    Observable<LmBaseResponseEntity<EmptyEntity>> share(@Body ShareEntity request);

    // 针对告警进行评论
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("cms/v1/app/addReply")
    Observable<LmBaseResponseEntity<EmptyEntity>> addReply(@Body CommentItemEntity addReplyRequest);

    // 针对告警进行点赞或取消点赞
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("cms/v1/app/addOrRemoveLikeIt")
    Observable<LmBaseResponseEntity<EmptyEntity>> likeIt(@Body CommentLikeEntity request);

    // 查询评论
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("cms/v1/app/queryReplyPage")
    Observable<LmBaseResponseEntity<CommentListEntity>> queryReplyPage(@Body QueryReplyPageEntity request);

    // 查询点赞数量
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("cms/v1/app/getContentLike")
    Observable<LmBaseResponseEntity<LikeCountEntity>> getContentLike(@Body CommentLikeEntity request);

    // 获取消息推送配置列表
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("user/v1/app/getUserConfig")
    Observable<LmBaseResponseEntity<List<PushEntity>>> getPushConfig(@Body EmptyEntity request);

    // 设置推送
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("user/v1/app/saveUserConfig")
    Observable<LmBaseResponseEntity<EmptyEntity>> setPushConfig(@Body PushRequestList request);

    // 获取线索列表
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("cms/v1/app/getContentListWithToken")
    Observable<LmBaseResponseEntity<ContentListEntity<ClueItemEntity>>> getClueList(@Body ContentRequestEntity listRequest);


    // 获取案件置顶
    @Headers({"Content-type: application/json", "Accept: */*", "Domain-Name: baseUrl"})
    @POST("cms/v1/app/getCaseTop4")
    Observable<LmBaseResponseEntity<ContentListEntity<BannerItemEntity>>> getNewsTop(@Body ContentRequestEntity listRequest);
}
