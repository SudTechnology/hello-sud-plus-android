package tech.sud.mgp.hello.ui.room.audio.model.command;

import tech.sud.mgp.hello.ui.room.audio.model.UserInfo;

/**
 * 送礼消息信令
 */
public class SendGiftCommand extends BaseCommand {
    public SendGiftCommand(UserInfo sendUser) {
        super(CommandCmd.CMD_PUBLIC_SEND_GIFT_NTF, sendUser);
    }

    public int giftID; // 礼物ID
    public int giftCount; // 礼物数量
    public UserInfo toUser; // 收礼人
}
