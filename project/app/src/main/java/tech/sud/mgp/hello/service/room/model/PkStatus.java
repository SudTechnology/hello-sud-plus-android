package tech.sud.mgp.hello.service.room.model;

/**
 * 跨房pk状态
 */
public class PkStatus {
    public static final int UNDEFINED = -1; // 未定义
    public static final int MATCHING = 1; // 匹配中
    public static final int MATCHED = 2; // pk已匹配，未开始
    public static final int STARTED = 3; // pk已匹配，已开始
    public static final int MATCH_CLOSED = 4; // pk匹配关闭
    public static final int PK_END = 5; // pk结束
}
