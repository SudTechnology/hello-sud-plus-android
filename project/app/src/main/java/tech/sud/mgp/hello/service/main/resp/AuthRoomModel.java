package tech.sud.mgp.hello.service.main.resp;

import java.io.Serializable;

/**
 * 发现页房间数据
 */
public class AuthRoomModel implements Serializable {
    public String roomId; // 房间id
    public long mgId; // 游戏id
    public String status; // 房间状态
    public int playerTotal; // 玩家总数
    public int obTotal; // ob总数
    public long lastChangedTime; // 最新变更时间戳
    public String authSecret; // 授权码
    public long localRoomId; // 本地房间id
}
