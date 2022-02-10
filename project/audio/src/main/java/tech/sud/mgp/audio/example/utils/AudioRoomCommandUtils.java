package tech.sud.mgp.audio.example.utils;

import com.blankj.utilcode.util.GsonUtils;

import tech.sud.mgp.audio.example.model.UserInfo;
import tech.sud.mgp.audio.example.model.command.DownMicCommand;
import tech.sud.mgp.audio.example.model.command.EnterRoomCommand;
import tech.sud.mgp.audio.example.model.command.GameChangeCommand;
import tech.sud.mgp.audio.example.model.command.PublicMsgCommand;
import tech.sud.mgp.audio.example.model.command.SendGiftCommand;
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
    public static String buildSendGiftCommand(int giftID,
                                              int giftCount,
                                              UserInfo toUser) {
        SendGiftCommand command = new SendGiftCommand(getSendUser());
        command.giftID = giftID;
        command.giftCount = giftCount;
        command.toUser = toUser;
        return GsonUtils.toJson(command);
    }

    /**
     * 构建上麦信令
     */
    public static String buildUpMicCommand(int micIndex, String streamId, int roleType) {
        UpMicCommand command = new UpMicCommand(getSendUser());
        command.micIndex = micIndex;
        command.streamID = streamId;
        command.roleType = roleType;
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

    /**
     * 构建游戏切换的信令
     */
    public static String buildGameChangeCommand(long gameId) {
        GameChangeCommand command = new GameChangeCommand(getSendUser());
        command.gameID = gameId;
        return GsonUtils.toJson(command);
    }

    /**
     * 构建进入房间信令
     */
    public static String buildEnterRoomCommand() {
        EnterRoomCommand command = new EnterRoomCommand(getSendUser());
        return GsonUtils.toJson(command);
    }

    private static UserInfo getSendUser() {
        UserInfo user = new UserInfo();
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
