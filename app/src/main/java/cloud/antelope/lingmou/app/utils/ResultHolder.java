package cloud.antelope.lingmou.app.utils;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;

/**
 * Created by ChenXinming on 2018/1/19.
 * description:
 */

public class ResultHolder {
    private static Bitmap image;
    private static List<FaceNewEntity> userEntity;

    private static List<FaceNewEntity> mSelectFaceEntities;
    public static void setImage(@Nullable Bitmap image) {
        ResultHolder.image = image;
    }

    @Nullable
    public static Bitmap getImage() {
        return image;
    }

    public static void disposeBitmap() {
        setImage(null);
    }

    public static void disposeFaceEntity() {
        setRecogData(null);
    }

    public static void setRecogData(List<FaceNewEntity> userEntity) {
        ResultHolder.userEntity = userEntity;
    }
    public static List<FaceNewEntity> getFaceEntities() {
        return userEntity;
    }

    public static List<FaceNewEntity> getSelectFaceEntities() {
        return mSelectFaceEntities;
    }

    public static void disposeSelectFaceEntities() {
        setSelectFaceEntities(null);
    }

    public static void setSelectFaceEntities(List<FaceNewEntity> faceEntities) {
        ResultHolder.mSelectFaceEntities = faceEntities;
    }



}
