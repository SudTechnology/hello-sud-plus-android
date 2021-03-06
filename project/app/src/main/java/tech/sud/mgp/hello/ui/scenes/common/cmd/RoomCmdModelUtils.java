package tech.sud.mgp.hello.ui.scenes.common.cmd;

import java.util.List;

import tech.sud.mgp.hello.common.model.Gender;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomService;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChangeGameModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChatTextModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdDownMicModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdEnterRoomModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdSendGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdUpMicModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.ContributionModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.DanceModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdBecomeDJModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdDiscoActionPayModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdDiscoInfoReqModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdDiscoInfoRespModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.order.RoomCmdOrderOperateModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.order.RoomCmdUserOrderModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKAnswerModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKChangeGameModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKFinishModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKOpenMatchModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKRemoveRivalModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKSendInviteModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKSettingsModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKStartModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.quiz.QuizBetModel;

/**
 * ?????????????????????
 */
public class RoomCmdModelUtils {

    // region ??????

    /** ??????????????????????????? */
    public static String buildPublicMsgCommand(String content) {
        RoomCmdChatTextModel command = new RoomCmdChatTextModel(getSendUser());
        command.content = content;
        return command.toJson();
    }

    /** ???????????????????????? */
    public static String buildSendGiftCommand(long giftID, int giftCount, UserInfo toUser) {
        RoomCmdSendGiftModel command = new RoomCmdSendGiftModel(getSendUser());
        command.giftID = giftID;
        command.giftCount = giftCount;
        command.toUser = toUser;
        return command.toJson();
    }

    /** ???????????????????????? */
    public static String buildSendGiftCommand(long giftID, int giftCount, UserInfo toUser,
                                              int type, String giftName, String giftUrl, String animationUrl) {
        RoomCmdSendGiftModel command = new RoomCmdSendGiftModel(getSendUser());
        command.giftID = giftID;
        command.giftCount = giftCount;
        command.toUser = toUser;
        command.type = type;
        command.giftName = giftName;
        command.giftUrl = giftUrl;
        command.animationUrl = animationUrl;
        return command.toJson();
    }

    /** ?????????????????? */
    public static String buildUpMicCommand(int micIndex, String streamId, int roleType) {
        RoomCmdUpMicModel command = new RoomCmdUpMicModel(getSendUser());
        command.micIndex = micIndex;
        command.streamID = streamId;
        command.roleType = roleType;
        return command.toJson();
    }

    /** ?????????????????? */
    public static String buildUpMicCommand(UserInfo userInfo, int micIndex, String streamId, int roleType) {
        RoomCmdUpMicModel command = new RoomCmdUpMicModel(userInfo);
        command.micIndex = micIndex;
        command.streamID = streamId;
        command.roleType = roleType;
        return command.toJson();
    }

    /** ?????????????????? */
    public static String buildDownMicCommand(int micIndex) {
        RoomCmdDownMicModel command = new RoomCmdDownMicModel(getSendUser());
        command.micIndex = micIndex;
        return command.toJson();
    }

    /** ??????????????????????????? */
    public static String buildGameChangeCommand(long gameId) {
        RoomCmdChangeGameModel command = new RoomCmdChangeGameModel(getSendUser());
        command.gameID = gameId;
        return command.toJson();
    }

    /** ???????????????????????? */
    public static String buildEnterRoomCommand() {
        RoomCmdEnterRoomModel command = new RoomCmdEnterRoomModel(getSendUser());
        return command.toJson();
    }
    // endregion ??????


    // region ??????pK

    /** ????????????:????????????PK?????? */
    public static String buildCmdPkSendInvite(int minuteDuration) {
        RoomCmdPKSendInviteModel command = new RoomCmdPKSendInviteModel(getSendUser());
        command.minuteDuration = minuteDuration;
        return command.toJson();
    }

    /**
     * ????????????:??????PK????????????
     *
     * @param isAccept ????????????
     */
    public static String buildCmdPkAnswer(UserInfo otherUser, boolean isAccept, String pkId) {
        RoomCmdPKAnswerModel command = new RoomCmdPKAnswerModel(getSendUser());
        command.isAccept = isAccept;
        command.otherUser = otherUser;
        command.pkId = pkId;
        return command.toJson();
    }

    /**
     * ????????????:????????????PK
     *
     * @param minuteDuration ??????????????????????????????
     */
    public static String buildCmdPkStart(int minuteDuration, String pkId) {
        RoomCmdPKStartModel command = new RoomCmdPKStartModel(getSendUser());
        command.minuteDuration = minuteDuration;
        command.pkId = pkId;
        return command.toJson();
    }

    /** ????????????:????????????PK */
    public static String buildCmdPkFinish() {
        RoomCmdPKFinishModel command = new RoomCmdPKFinishModel(getSendUser());
        return command.toJson();
    }

    /**
     * ????????????:??????PK??????
     *
     * @param minuteDuration ??????????????????????????????
     */
    public static String buildCmdPkSettings(int minuteDuration) {
        RoomCmdPKSettingsModel command = new RoomCmdPKSettingsModel(getSendUser());
        command.minuteDuration = minuteDuration;
        return command.toJson();
    }

    /** ????????????:??????????????????PK */
    public static String buildCmdPkOpenMatch() {
        RoomCmdPKOpenMatchModel command = new RoomCmdPKOpenMatchModel(getSendUser());
        return command.toJson();
    }

    /**
     * ????????????:??????PK???????????????
     *
     * @param gameID ??????id
     */
    public static String buildCmdPkChangeGame(long gameID) {
        RoomCmdPKChangeGameModel command = new RoomCmdPKChangeGameModel(getSendUser());
        command.gameID = gameID;
        return command.toJson();
    }

    /**
     * ????????????:??????PK???????????????
     */
    public static String buildCmdPkRemoveRival() {
        RoomCmdPKRemoveRivalModel command = new RoomCmdPKRemoveRivalModel(getSendUser());
        return command.toJson();
    }
    // endregion ??????pK


    // region ??????

    /** ?????????????????? */
    public static String buildCmdUserOrder(long orderId, long gameId, String gameName, List<String> toUsers, List<String> toUserNames) {
        RoomCmdUserOrderModel command = new RoomCmdUserOrderModel(getSendUser());
        command.orderId = orderId;
        command.gameId = gameId;
        command.gameName = gameName;
        command.toUsers = toUsers;
        command.toUserNames = toUserNames;
        return command.toJson();
    }

    /** ???????????????????????????????????? */
    public static String buildCmdOrderResult(long orderId, long gameId, String gameName, String toUser, boolean state) {
        RoomCmdOrderOperateModel command = new RoomCmdOrderOperateModel(getSendUser());
        command.orderId = orderId;
        command.gameId = gameId;
        command.gameName = gameName;
        command.toUser = toUser;
        command.operate = state;
        return command.toJson();
    }
    // endregion ??????

    // region ??????

    /** ?????? ?????????????????? ?????? */
    public static String buildCmdQuizBet(List<UserInfo> recUser) {
        QuizBetModel command = new QuizBetModel(getSendUser());
        command.recUser = recUser;
        return command.toJson();
    }
    // endregion ??????

    // region ??????

    /** ?????? ?????????????????? ?????? */
    public static String buildCmdDiscoInfoReq() {
        RoomCmdDiscoInfoReqModel command = new RoomCmdDiscoInfoReqModel(getSendUser());
        return command.toJson();
    }

    /** ?????? ?????????????????? ?????? */
    public static String buildCmdDiscoInfoResp(List<DanceModel> dancingMenu, List<ContributionModel> contribution, boolean isEnd) {
        RoomCmdDiscoInfoRespModel command = new RoomCmdDiscoInfoRespModel(getSendUser());
        command.dancingMenu = dancingMenu;
        command.contribution = contribution;
        command.isEnd = isEnd;
        return command.toJson();
    }

    /** ?????? ???DJ??? ?????? */
    public static String buildCmdBecomeDJ(String userId) {
        RoomCmdBecomeDJModel command = new RoomCmdBecomeDJModel(getSendUser());
        command.userID = userId;
        return command.toJson();
    }

    /** ?????? ?????????????????? ?????? */
    public static String buildCmdDiscoActionPay(int price) {
        RoomCmdDiscoActionPayModel command = new RoomCmdDiscoActionPayModel(getSendUser());
        command.price = price;
        return command.toJson();
    }
    // endregion ??????

    public static UserInfo getSendUser() {
        UserInfo user = new UserInfo();
        user.userID = HSUserInfo.userId + "";
        user.name = HSUserInfo.nickName;
        user.icon = HSUserInfo.avatar;
        if (Gender.MALE.equals(HSUserInfo.gender)) {
            user.sex = 1;
        } else if (Gender.FEMALE.equals(HSUserInfo.gender)) {
            user.sex = 2;
        }
        RoomInfoModel roomInfoModel = SceneRoomService.getRoomInfoModel();
        if (roomInfoModel != null && roomInfoModel.roomId != null) {
            user.roomID = roomInfoModel.roomId + "";
        }
        return user;
    }

}
