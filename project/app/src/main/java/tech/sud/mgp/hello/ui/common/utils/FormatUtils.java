package tech.sud.mgp.hello.ui.common.utils;

public class FormatUtils {

    /**
     * 格式化时长，返回"xx:xx"
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

}
