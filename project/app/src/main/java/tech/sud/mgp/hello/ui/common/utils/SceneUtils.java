package tech.sud.mgp.hello.ui.common.utils;

public class SceneUtils {

    /**
     * 根据一个循环的时长以及当前时间戳，得出当前倒计时长
     *
     * @param cycle 单位为秒
     * @return 返回倒计时长
     */
    public static int getTotalCount(int cycle) {
        if (cycle <= 0) {
            cycle = 60 * 60 * 60;
        }
        int totalCount = (int) (cycle - (System.currentTimeMillis() / 1000) % cycle);
        if (totalCount <= 0) {
            totalCount = cycle;
        }
        return totalCount;
    }

}
