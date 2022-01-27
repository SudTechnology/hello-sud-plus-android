package tech.sud.mgp.audio.example.utils;

import com.blankj.utilcode.util.GsonUtils;

import tech.sud.mgp.audio.example.model.command.DownMicCommand;
import tech.sud.mgp.audio.example.model.command.PublicMsgCommand;
import tech.sud.mgp.audio.example.model.command.SendGiftCommand;
import tech.sud.mgp.audio.example.model.command.SendUser;
import tech.sud.mgp.audio.example.model.command.UpMicCommand;
import tech.sud.mgp.common.model.Gender;
import tech.sud.mgp.common.model.HSUserInfo;

/**
 * 房间信令工具类
 */
public class AudioRoomCommandUtils {

    /**
     * 构建公屏消息的信令
     */
    public static String buildPublicMsgCommand(String content) {
        PublicMsgCommand command = new PublicMsgCommand(getSendUser());
        command.content = content;
        return GsonUtils.toJson(command);
    }

    /**
     * 构建发礼消息信令
     */
    public static String buildSendGiftCommand(int giftID, int giftCount) {
        SendGiftCommand command = new SendGiftCommand(getSendUser());
        command.giftID = giftID;
        command.giftCount = giftCount;
        return GsonUtils.toJson(command);
    }

    /**
     * 构建上麦信令
     */
    public static String buildUpMicCommand(int micIndex) {
        UpMicCommand command = new UpMicCommand(getSendUser());
        command.micIndex = micIndex;
        return GsonUtils.toJson(command);
    }

    /**
     * 构建下麦信令
     */
    public static String buildDownMicCommand(int micIndex) {
        DownMicCommand command = new DownMicCommand(getSendUser());
        command.micIndex = micIndex;
        return GsonUtils.toJson(command);
    }

    private static SendUser getSendUser() {
        SendUser user = new SendUser();
        user.userID = HSUserInfo.userId;
        user.name = HSUserInfo.nickName;
        user.icon = HSUserInfo.avatar;
        if (Gender.MALE.equals(HSUserInfo.gender)) {
            user.sex = 1;
        } else if (Gender.FEMALE.equals(HSUserInfo.gender)) {
            user.sex = 2;
        }
        return user;
    }

}
