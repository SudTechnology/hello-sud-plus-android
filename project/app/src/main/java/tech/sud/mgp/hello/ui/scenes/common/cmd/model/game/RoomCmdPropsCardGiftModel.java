package tech.sud.mgp.hello.ui.scenes.common.cmd.model.game;

import java.util.List;

import tech.sud.gip.SudGIPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 游戏道具卡送礼通知
 */
public class RoomCmdPropsCardGiftModel extends RoomCmdBaseModel {
    public RoomCmdPropsCardGiftModel(UserInfo sendUser) {
        super(RoomCmd.CMD_GAME_PROPS_CARD_GIFT_NOTIFY, sendUser);
    }

    public Content content;

    public static RoomCmdPropsCardGiftModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdPropsCardGiftModel.class);
    }

    public static class Content {
        public String paidEventType;
        public String senderUid; // 发送者uid
        public List<String> receiverUidList; // 接收者用户id列表
        public int amount; // 道具卡数量
    }

}
