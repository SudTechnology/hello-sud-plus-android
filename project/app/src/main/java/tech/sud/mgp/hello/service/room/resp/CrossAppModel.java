package tech.sud.mgp.hello.service.room.resp;

import java.io.Serializable;
import java.util.List;

import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.service.room.model.CrossAppMatchStatus;

/**
 * 跨域匹配信息
 */
public class CrossAppModel implements Serializable {

    public static final int SINGLE_MATCH = 1;
    public static final int TEAM_MATCH = 2;

    public int matchStatus = CrossAppMatchStatus.TEAM; // 匹配状态(1未开启，2组队中，3匹配中)
    public String groupId; // 匹配组id
    public String matchRoomId; // 匹配房间id(用于后续加载游戏)，匹配状态为成功时该字段才有值
    public long captain; // 队长id
    public long matchGameId; // 当前匹配游戏id
    public List<UserInfoResp> userList; // 组队或匹配中用户列表

    public int curNum; // 已匹配人数
    public int totalNum; // 需匹配总人数

    // region 自定义参数
    public int enterType; // 0不做动作；1单人快速匹配；2组队匹配
    public GameModel gameModel; // 当前匹配的游戏模型
    // endregion 自定义参数

}