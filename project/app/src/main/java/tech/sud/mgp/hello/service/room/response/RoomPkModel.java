package tech.sud.mgp.hello.service.room.response;

import java.io.Serializable;

import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.service.room.model.PkStatus;

/**
 * pk 结果
 */
public class RoomPkModel implements Serializable {
    public int pkStatus = PkStatus.MATCH_CLOSED; // pk状态（1：待匹配 2：pk已匹配，未开始 3：pk已匹配，已开始 4：pk匹配关闭 5：pk已结束）
    public long pkId; // pkId
    public int remainSecond; // PK剩余时间（单位：秒）
    public int totalMinute = APPConfig.ROOM_PK_MINUTE; // 选择的总分钟数
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

    /** 获取pk对手 */
    public RoomPkRoomInfo getPkRival() {
        if (srcRoomInfo != null && !srcRoomInfo.isSelfRoom) {
            return srcRoomInfo;
        }
        if (destRoomInfo != null && !destRoomInfo.isSelfRoom) {
            return destRoomInfo;
        }
        return null;
    }

    /** 移除pk对手 */
    public void removePkRival() {
        if (srcRoomInfo != null && !srcRoomInfo.isSelfRoom) {
            srcRoomInfo = null;
        }
        if (destRoomInfo != null && !destRoomInfo.isSelfRoom) {
            destRoomInfo = null;
        }
    }

    /** 恢复数据为原始状态 */
    public void clear() {
        pkStatus = PkStatus.MATCH_CLOSED;
        pkId = 0;
        remainSecond = 0;
        srcRoomInfo = null;
        destRoomInfo = null;
    }

}
