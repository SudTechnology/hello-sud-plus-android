package tech.sud.mgp.hello.service.room.response;

import java.io.Serializable;

import tech.sud.mgp.hello.service.room.model.PkStatus;

/**
 * pk 结果
 */
public class RoomPkModel implements Serializable {
    public int pkStatus = PkStatus.MATCH_CLOSED; // pk状态（1：待匹配 2：pk已匹配，未开始 3：pk已匹配，已开始 4：pk匹配关闭 5：pk已结束）
    public int remainSecond; // PK剩余时间（单位：秒）
    public int totalMinute; // 选择的总分钟数
    public RoomPkRoomInfo srcRoomInfo; // 发起方房间信息
    public RoomPkRoomInfo destRoomInfo; // 受邀方房间信息

    /** 对数据进行初始化整理 */
    public void initInfo(long selfRoomId) {
        if (srcRoomInfo != null) {
            srcRoomInfo.isInitiator = true;
            srcRoomInfo.isSelfRoom = srcRoomInfo.roomId == selfRoomId;
        }
        if (destRoomInfo != null) {
            destRoomInfo.isSelfRoom = destRoomInfo.roomId == selfRoomId;
        }
    }

}
