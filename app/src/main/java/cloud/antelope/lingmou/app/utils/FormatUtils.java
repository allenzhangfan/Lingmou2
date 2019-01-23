package cloud.antelope.lingmou.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import com.lingdanet.safeguard.common.utils.TimeUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FormatUtils {

    public static long threeDayAgo(long time) {
        String currentDayStr = TimeUtils.millis2String(time, "yyyy-MM-dd ");
        return TimeUtils.string2Millis(currentDayStr + "00:00:01");
    }

    private static final String MONEY_REGULAR = "\\d+(\\.\\d+)?";

    /**
     * 格式化金额
     *
     * @param s
     * @param len
     * @return
     */
    public static String formatMoney(Double s, int len) {
        if (s == null) {
            return "";
        }
        NumberFormat formater = null;

        if (len == 0) {
            formater = new DecimalFormat("###,###");
        } else {
            StringBuffer buff = new StringBuffer();
            buff.append("###,###.");
            for (int i = 0; i < len; i++) {
                buff.append("#");
            }
            formater = new DecimalFormat(buff.toString());
        }
        String result = formater.format(s);
        if (result.indexOf(".") == -1) {
            result = "￥" + result + ".00";
        } else {
            result = "￥" + result;
        }
        return result;
    }


    /*格式化金额为两位小数点*/
    public static String moneyFormat(double val) {
        DecimalFormat df = new DecimalFormat("#.00");
        String res = df.format(val);
        if (".00".equals(res)) {
            res = "0.00";
        } else if (Double.parseDouble(res) < 1.00 && Double.parseDouble(res) > 0.00) {
            res = 0 + res;
        }
        return res;
    }

    /*格式化小数点后一位*/
    public static String toInt(double val) {
        DecimalFormat df = new DecimalFormat("#.0");
        String res = df.format(val);
        return res;
    }

    /*给整数加括号（商品数量）*/
    public static String numFormat(int val) {
        String res = "（" + val + "）";
        return res;
    }

    /*格式化分期数*/
    public static String intFormat(int val) {
        String res = "" + val + "期";
        return res;
    }

    public static String moneyFormat(String val) {
        if (!TextUtils.isEmpty(val) && val.matches(MONEY_REGULAR)) {
            return moneyFormat(Double.parseDouble(val));
        }
        return "0.00";
    }

    public static String twoNumber(int number) {
        if (number > 9 || number < -9) {
            return String.valueOf(number);
        } else if (number >= 0) {
            return "0" + number;
        } else {
            return "-0" + Math.abs(number);
        }

    }

    /**
     * double保留1位
     */
    public static String doubleFormat(double val) {
        BigDecimal mData = new BigDecimal(val).setScale(1, BigDecimal.ROUND_HALF_UP);
        return String.valueOf(mData);
    }

    /**
     * String说保留1位
     */
    public static String tringFormat(String val) {
        BigDecimal mData = new BigDecimal(val).setScale(1, BigDecimal.ROUND_HALF_UP);
        return String.valueOf(mData);
    }

    public static String hiddenPhoneNumber(String phone) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < phone.length(); i++) {
            if (i <= 2 || i >= phone.length() - 4) {
                stringBuilder.append(phone.charAt(i));
            } else {
                stringBuilder.append("*");
            }
        }
        return stringBuilder.toString();
    }

    public static Bitmap stringToBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
