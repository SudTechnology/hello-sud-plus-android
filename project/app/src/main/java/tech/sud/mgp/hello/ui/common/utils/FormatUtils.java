package tech.sud.mgp.hello.ui.common.utils;

import java.text.DecimalFormat;

public class FormatUtils {

    /**
     * 格式化时长，返回"xx:xx"（分:秒）
     *
     * @param duration 单位为秒
     */
    public static String formatTime(long duration) {
        long second = duration % 60;
        long minute = duration / 60;
        return coverTime(minute) + ":" + coverTime(second);
    }

    /** 小于0时进行补位 */
    private static String coverTime(long time) {
        if (time < 10) {
            return "0" + time;
        }
        return time + "";
    }

    /**
     * 格式化时长，返回"xx:xx:xx:xx"（天:时:分:秒）
     *
     * @param duration 单位为秒
     */
    public static String formatTimeDay(long duration) {
        long oneMinute = 60;
        long oneHour = 60 * 60;
        long oneDay = oneHour * 24;

        // 计算秒
        long second = duration % oneMinute;

        // 计算分钟
        long minute = duration % oneHour / oneMinute;

        // 计算小时
        long hour = duration % oneDay / oneHour;

        // 计算天
        long day = duration / oneDay;

        return coverTime(day) + ":" + coverTime(hour) + ":" + coverTime(minute) + ":" + coverTime(second);
    }

    /**
     * 格式化金钱
     *
     * @param money
     * @return
     */
    public static String formatMoney(double money) {
        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
        return decimalFormat.format(money);
    }

    /**
     * 格式化金钱
     *
     * @param money
     * @return
     */
    public static String formatMoney(long money) {
        DecimalFormat decimalFormat = new DecimalFormat("###,##0");
        return decimalFormat.format(money);
    }
    
}
