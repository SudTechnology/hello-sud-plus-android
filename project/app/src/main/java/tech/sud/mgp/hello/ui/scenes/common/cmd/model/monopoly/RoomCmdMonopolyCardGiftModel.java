package tech.sud.mgp.hello.ui.scenes.common.cmd.model.monopoly;

import java.util.List;

import tech.sud.gip.SudGIPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 大富翁道具卡送礼通知
 */
public class RoomCmdMonopolyCardGiftModel extends RoomCmdBaseModel {
    public RoomCmdMonopolyCardGiftModel(UserInfo sendUser) {
        super(RoomCmd.CMD_GAME_MONOPOLY_CARD_GIFT_NOTIFY, sendUser);
    }

    public Content content;

    public static RoomCmdMonopolyCardGiftModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdMonopolyCardGiftModel.class);
    }

    public static class Content {
        public int type; // 1: 重摇卡， 2：免租卡， 3：指定点数卡
        public String senderUid; // 发送者uid
        public List<String> receiverUidList; // 接收者用户id列表
        public int amount; // 道具卡数量
    }

}
