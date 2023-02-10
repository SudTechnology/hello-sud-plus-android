package tech.sud.mgp.hello.service.room.model;

/**
 * 跨域匹配状态定义
 */
public class CrossAppMatchStatus {
    public static final int NOT_OPEN = 1; // 未开启
    public static final int TEAM = 2; // 组队中
    public static final int MATCHING = 3; // 匹配中
    public static final int MATCH_SUCCESS = 4; // 匹配成功
    public static final int MATCH_FAILED = 5; // 匹配失败
}
