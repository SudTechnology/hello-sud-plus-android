package tech.sud.mgp.hello.service.main.req;

public class CreatRoomReq {
    public int sceneType;  // 场景类型 1：语聊房场景 2：1v1场景 3：才艺房场景 4：秀场场景
    public String rtcType; // RTC类型
    public Long gameId; // 游戏id
    public Integer gameLevel; // 游戏等级（适配当前门票场景）
}
