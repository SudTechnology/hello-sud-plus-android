package tech.sud.mgp.hello.ui.common.utils;

public class FormatUtils {

    /**
     * 格式化时长，返回"xx:xx"
     */
    public static String formatTime(long duration) {
        long first = duration % 60;
        long second = duration / 60;
        return second + ":" + first;
    }

}
