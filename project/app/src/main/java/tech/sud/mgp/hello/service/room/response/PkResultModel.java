package tech.sud.mgp.hello.service.room.response;

/**
 * pk 结果
 */
public class PkResultModel {
    public int pkStatus; // pk状态（1：待匹配 2：pk已匹配，未开始 3：k已匹配，已开始 4：pk匹配关闭 5：pk已结束）
    public int remainSecond; // PK剩余时间（单位：秒）
    public RoomPkInfo srcRoomInfo; // 发起方房间信息
    public RoomPkInfo destRoomInfo; // 受邀方房间信息

    public static class RoomPkInfo {
        public long roomId; // 房间id
        public long score; // PK分数
        public String roomOwnerHeader; // 房主头像
        public String roomOwnerNickname; // 房主昵称
    }

}
