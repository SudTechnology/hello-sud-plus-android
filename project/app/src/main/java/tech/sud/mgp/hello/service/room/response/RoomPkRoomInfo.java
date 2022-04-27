package tech.sud.mgp.hello.service.room.response;

import java.io.Serializable;

/**
 * pk 结果当中的房间信息
 */
public class RoomPkRoomInfo implements Serializable {
    public long roomId; // 房间id
    public long score; // PK分数
    public String roomOwnerHeader; // 房主头像
    public String roomOwnerNickname; // 房主昵称

    // region 业务添加的参数
    public boolean isInitiator; // 是否是发起方
    public boolean isSelfRoom; // 是否是自己的房间
    // endregion 业务添加的参数

}
