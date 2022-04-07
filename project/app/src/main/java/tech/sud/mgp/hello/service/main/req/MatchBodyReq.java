package tech.sud.mgp.hello.service.main.req;

/**
 * 匹配游戏请求参数
 */
public class MatchBodyReq {
    public Long gameId; // 游戏ID
    public Integer sceneType; // （1：语聊房场景 2：1v1场景 3：才艺房场景 4：秀场场景）
    public String rtcType; // RTC类型
    public Integer gameLevel; // 门票场景游戏级别 从1开始
}
