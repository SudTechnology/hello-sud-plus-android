package tech.sud.mgp.hello.service.room.resp;

import java.util.List;

import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.service.room.model.CrossAppMatchStatus;

/**
 * 跨域匹配信息
 */
public class CrossAppModel {

    public int matchStatus = CrossAppMatchStatus.NOT_OPEN; // 匹配状态(1未开启，2组队中，3匹配中)
    public long captain; // 队长id
    public long matchGameId; // 当前匹配游戏id
    public List<UserInfoResp> userList; // 组队或匹配中用户列表

    // region 自定义参数
    public boolean isFastMatch; // 是否是单人快速匹配
    // endregion 自定义参数

}