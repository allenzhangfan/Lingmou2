package cloud.antelope.lingmou.app.utils;


import android.util.Base64;

import com.lingdanet.safeguard.common.utils.LogUtils;

/**
 * 作者：陈新明
 * 创建日期：2018/06/20
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class ConfidenceUtil {
    public static float[] convertBase64ToFloatArray(String feature) {
        byte[] buf = Base64.decode(feature, Base64.DEFAULT);
        int startIndex = 0;
        int length = buf.length / 4;
        float[] featureFloat = new float[length];
        for (int i = 0; i < length; i++) {
            featureFloat[i] = byte2float(buf, startIndex);
            startIndex += 4;
        }
        return featureFloat;
    }

    public static float byte2float(byte[] b, int index) {

        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    private static float a;
    private static float b;
    private static float _a;
    private static float _b;
    private static float start;
    private static float end;
    private static PointEntity[] _ctrl_points = new PointEntity[3];

    static class PointEntity{
        float x;
        float y;
    }

    private static void init() {
        a = 1.4084507f;
        b = -0.17746479f;
        start = 0.95f;
        end = 1.0f;
        for (int i = 0; i < 3; i++) {
            _ctrl_points[i] = new PointEntity();
        }
        _ctrl_points[0].x = (start - b) / a;
        _ctrl_points[0].y = start;
        _ctrl_points[1].x = (end - b) / a;
        _ctrl_points[1].y = end;
        _ctrl_points[2].x = end;
        _ctrl_points[2].y = end;
        _a = _ctrl_points[0].x + _ctrl_points[2].x - 2 * _ctrl_points[1].x;
        _b = 2 * (_ctrl_points[1].x - _ctrl_points[0].x);
    }

    public static float getNewConfidence(String feature, float[] pers) {
        init();
        float[] floats = convertBase64ToFloatArray(feature);
        float verify_value = Verify(floats, pers);
        return TransformScore(verify_value) * 100f;
    }

    private static float TransformScore(float original_score) {
        if (original_score > 1) {
            return 0.9999f;
        } else if (original_score < 0) {
            return 0.0001f;
        }
        float transformed_score = GetSmoothValue(original_score);
        if (transformed_score > 0.9999f) {
            return 0.9999f;
        } else if (transformed_score < 0.0001f) {
            return 0.0001f;
        } else {
            return transformed_score;
        }
    }

    private static float GetSmoothValue(float x) {
        float transformed_score = a * x + b;
        if (x <= _ctrl_points[0].x)  {         // x 低于0.7 那么余弦值直接换算
            return transformed_score;
        }
        float _c = _ctrl_points[0].x - x;
        float Delta = _b * _b - 4 * _a * _c;
        // Delta小于零则实无解
        if(Delta < 0) {
            LogUtils.e("Exception : Delta = %f < 0",Delta);
            return 0;
        }
        float deltaSqrt = (float) Math.sqrt(Delta);
        float root1 = (-_b + deltaSqrt ) / (2 * _a);
        float root2 = (-_b - deltaSqrt ) / (2 * _a);
        float eps = 0.0001f;
        // 两个根只能有一个落在0到1之间
        if( (root1 >= 0-eps && root1 <= 1+eps) == (root2 >= 0-eps && root2 <= 1+eps) ) {
            LogUtils.e("Exception : only one root@(0,1)");
            return 0;
        }
        // 取落在0到1之间的那个根
        float t = root2;
        if (( root1 >= 0-eps ) && (root1 <= 1+eps)) {
            t = root1;
        }
        float y = (1 - t) * (1 - t) * _ctrl_points[0].y + 2 * (1 - t) * t * _ctrl_points[1].y + t * t * _ctrl_points[2].y;

        return y;
    }

    private static float Verify(float[] f1, float[] f2) {
        float score = 0.0f;
        float dot = score;
        float denom_a = score;
        float denom_b = score;
        for (int i = 0; i < 256; i++) {
            dot += f1[i] * f2[i];
            denom_a += f1[i] * f1[i];
            denom_b += f2[i] * f2[i];
        }
        // 返回归一化距离[0,1]
        return 0.5f + 0.5f * dot / (float)(Math.sqrt(denom_a) * Math.sqrt(denom_b) + 1e-8);
    }

    public static float getConfidence(String feature, float[] pers) {
        float[] floats = convertBase64ToFloatArray(feature);
        float sum = 0f;
        for (int i = 0; i < 256; i++) {
            sum += floats[i] * pers[i];
        }
        float a;
        float b;
        if (sum < 0.54) {
            a = 38.96f;
            b = 38.96f;
        } else if (sum < 0.59) {
            a = 200.0f;
            b = -48.0f;
        } else if (sum < 0.63) {
            a = 374.99f;
            b = -151.24f;
        } else if (sum < 0.66) {
            a = 166.67f;
            b = -20.0f;
        } else if (sum < 0.709) {
            a = 102.04f;
            b = 22.66f;
        } else {
            a = 17.18f;
            b = 82.818f;
        }
        sum = a * sum + b;
        if (sum > 100) {
            sum  = -1;
        }
        return  sum;
    }
}
