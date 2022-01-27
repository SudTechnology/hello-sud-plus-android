package tech.sud.mgp.audio.example.model.command;

import tech.sud.mgp.audio.example.model.UserInfo;

/**
 * 送礼消息信令
 */
public class SendGiftCommand extends BaseCommand {
    public SendGiftCommand(UserInfo sendUser) {
        super(CommandCmd.CMD_PUBLIC_SEND_GIFT_NTF, sendUser);
    }

    public int giftID; // 礼物ID
    public int giftCount; // 礼物数量
}
