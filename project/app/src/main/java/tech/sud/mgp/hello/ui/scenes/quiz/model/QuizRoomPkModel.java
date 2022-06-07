package tech.sud.mgp.hello.ui.scenes.quiz.model;

/**
 * 竞猜跨房玩游戏
 */
public class QuizRoomPkModel {

    public int status; // 0正在进行 1已结束
    public int countdownCycle; // 倒计时循环(秒)
    public int memberNumber; // 多少人参与了竞猜
    public int winNumber; // 多少人预测成功了
    public String jackpotCount; // 奖池的数量

    public final RoomPkInfoModel leftInfo = new RoomPkInfoModel();
    public final RoomPkInfoModel rightInfo = new RoomPkInfoModel();

    public static class RoomPkInfoModel {
        public int icon; // 图标，本地资源id
        public long roomId; // 房间id
        public String roomName; // 房间名称
        public long betNumber; // 已投注的数量
        public boolean isWin; // true为赢了
    }

}
