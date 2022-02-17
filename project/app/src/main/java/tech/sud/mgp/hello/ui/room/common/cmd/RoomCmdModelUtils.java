package tech.sud.mgp.hello.ui.room.common.cmd;

import tech.sud.mgp.hello.common.model.Gender;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.ui.room.audio.model.UserInfo;
import tech.sud.mgp.hello.ui.room.common.cmd.model.RoomCmdChangeGameModel;
import tech.sud.mgp.hello.ui.room.common.cmd.model.RoomCmdChatTextModel;
import tech.sud.mgp.hello.ui.room.common.cmd.model.RoomCmdDownMicModel;
import tech.sud.mgp.hello.ui.room.common.cmd.model.RoomCmdEnterRoomModel;
import tech.sud.mgp.hello.ui.room.common.cmd.model.RoomCmdSendGiftModel;
import tech.sud.mgp.hello.ui.room.common.cmd.model.RoomCmdUpMicModel;

/**
 * 房间信令工具类
 */
public class RoomCmdModelUtils {

    /**
     * 构建公屏消息的信令
     */
    public static String buildPublicMsgCommand(String content) {
        RoomCmdChatTextModel command = new RoomCmdChatTextModel(getSendUser());
        command.content = content;
        return command.toJson();
    }

    /**
     * 构建发礼消息信令
     */
    public static String buildSendGiftCommand(int giftID,
                                              int giftCount,
                                              UserInfo toUser) {
        RoomCmdSendGiftModel command = new RoomCmdSendGiftModel(getSendUser());
        command.giftID = giftID;
        command.giftCount = giftCount;
        command.toUser = toUser;
        return command.toJson();
    }

    /**
     * 构建上麦信令
     */
    public static String buildUpMicCommand(int micIndex, String streamId, int roleType) {
        RoomCmdUpMicModel command = new RoomCmdUpMicModel(getSendUser());
        command.micIndex = micIndex;
        command.streamID = streamId;
        command.roleType = roleType;
        return command.toJson();
    }

    /**
     * 构建下麦信令
     */
    public static String buildDownMicCommand(int micIndex) {
        RoomCmdDownMicModel command = new RoomCmdDownMicModel(getSendUser());
        command.micIndex = micIndex;
        return command.toJson();
    }

    /**
     * 构建游戏切换的信令
     */
    public static String buildGameChangeCommand(long gameId) {
        RoomCmdChangeGameModel command = new RoomCmdChangeGameModel(getSendUser());
        command.gameID = gameId;
        return command.toJson();
    }

    /**
     * 构建进入房间信令
     */
    public static String buildEnterRoomCommand() {
        RoomCmdEnterRoomModel command = new RoomCmdEnterRoomModel(getSendUser());
        return command.toJson();
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
