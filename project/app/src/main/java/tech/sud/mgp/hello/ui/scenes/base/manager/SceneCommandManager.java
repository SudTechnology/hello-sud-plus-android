package tech.sud.mgp.hello.ui.scenes.base.manager;

import com.blankj.utilcode.util.LogUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChangeGameModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChatTextModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdDownMicModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdEnterRoomModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdSendGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdUpMicModel;
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
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKRivalExitModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKSendInviteModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKSettingsModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKSettleModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKStartModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.quiz.QuizBetModel;

/**
 * ??????????????????
 */
public class SceneCommandManager extends BaseServiceManager {

    private final List<ICommandListener> listenerList = new ArrayList<>();

    /**
     * ??????????????????
     *
     * @param listener ???????????????????????????????????????????????????
     */
    public void setCommandListener(ICommandListener listener) {
        listenerList.add(listener);
    }

    /** ?????????????????? */
    public void removeCommandListener(ICommandListener listener) {
        if (listener != null) {
            listenerList.remove(listener);
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param userID  ?????????
     * @param command ????????????
     */
    public void onRecvCommand(String userID, String command) {
        LogUtils.d("onRecvCommand:" + command);
        int commandCmd = getCommandCmd(command);
        switch (commandCmd) {
            case RoomCmd.CMD_CHAT_TEXT_NOTIFY: // ????????????
                dispatchCommand(commandCmd, RoomCmdChatTextModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_SEND_GIFT_NOTIFY: // ????????????
                dispatchCommand(commandCmd, RoomCmdSendGiftModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_UP_MIC_NOTIFY: // ?????????
                dispatchCommand(commandCmd, RoomCmdUpMicModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_DOWN_MIC_NOTIFY: // ?????????
                dispatchCommand(commandCmd, RoomCmdDownMicModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_CHANGE_GAME_NOTIFY: // ????????????
                dispatchCommand(commandCmd, RoomCmdChangeGameModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ENTER_ROOM_NOTIFY: // ??????????????????
                dispatchCommand(commandCmd, RoomCmdEnterRoomModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_SEND_INVITE: // ????????????PK??????
                dispatchCommand(commandCmd, RoomCmdPKSendInviteModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_ANSWER: // ??????PK????????????
                dispatchCommand(commandCmd, RoomCmdPKAnswerModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_START: // ????????????PK
                dispatchCommand(commandCmd, RoomCmdPKStartModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_FINISH: // ????????????PK
                dispatchCommand(commandCmd, RoomCmdPKFinishModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_SETTINGS: // ??????PK??????
                dispatchCommand(commandCmd, RoomCmdPKSettingsModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_OPEN_MATCH: // ??????????????????PK
                dispatchCommand(commandCmd, RoomCmdPKOpenMatchModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_CHANGE_GAME: // ??????PK???????????????
                dispatchCommand(commandCmd, RoomCmdPKChangeGameModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_REMOVE_RIVAL: // ??????PK???????????????
                dispatchCommand(commandCmd, RoomCmdPKRemoveRivalModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_SETTLE: // ??????pk????????????????????????
                dispatchCommand(commandCmd, RoomCmdPKSettleModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_RIVAL_EXIT: // ??????PK????????????????????????
                dispatchCommand(commandCmd, RoomCmdPKRivalExitModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_USER_ORDER_NOTIFY: // ????????????
                dispatchCommand(commandCmd, RoomCmdUserOrderModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ORDER_OPERATE_NOTIFY: // ????????????????????????????????????
                dispatchCommand(commandCmd, RoomCmdOrderOperateModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_QUIZ_BET: // ??????????????????
                dispatchCommand(commandCmd, QuizBetModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_DISCO_INFO_REQ: // ??????????????????
                dispatchCommand(commandCmd, RoomCmdDiscoInfoReqModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_DISCO_INFO_RESP: // ??????????????????
                dispatchCommand(commandCmd, RoomCmdDiscoInfoRespModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_DISCO_BECOME_DJ: // ???DJ???
                dispatchCommand(commandCmd, RoomCmdBecomeDJModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_DISCO_ACTION_PAY: // ??????????????????
                dispatchCommand(commandCmd, RoomCmdDiscoActionPayModel.fromJson(command), userID);
                break;
        }
    }

    /**
     * ????????????
     *
     * @param cmd        ????????????
     * @param model      ????????????
     * @param fromUserID ?????????
     */
    private void dispatchCommand(int cmd, RoomCmdBaseModel model, String fromUserID) {
        if (model == null) return;
        for (ICommandListener listener : listenerList) {
            switch (cmd) {
                case RoomCmd.CMD_CHAT_TEXT_NOTIFY: // ????????????
                    if (listener instanceof PublicMsgCommandListener) {
                        ((PublicMsgCommandListener) listener).onRecvCommand((RoomCmdChatTextModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_SEND_GIFT_NOTIFY: // ????????????
                    if (listener instanceof SendGiftCommandListener) {
                        ((SendGiftCommandListener) listener).onRecvCommand((RoomCmdSendGiftModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_UP_MIC_NOTIFY: // ?????????
                    if (listener instanceof UpMicCommandListener) {
                        ((UpMicCommandListener) listener).onRecvCommand((RoomCmdUpMicModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_DOWN_MIC_NOTIFY: // ?????????
                    if (listener instanceof DownMicCommandListener) {
                        ((DownMicCommandListener) listener).onRecvCommand((RoomCmdDownMicModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_CHANGE_GAME_NOTIFY: // ????????????
                    if (listener instanceof GameChangeCommandListener) {
                        ((GameChangeCommandListener) listener).onRecvCommand((RoomCmdChangeGameModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ENTER_ROOM_NOTIFY: // ??????????????????
                    if (listener instanceof EnterRoomCommandListener) {
                        ((EnterRoomCommandListener) listener).onRecvCommand((RoomCmdEnterRoomModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_SEND_INVITE: // ????????????PK??????
                    if (listener instanceof PKSendInviteCommandListener) {
                        ((PKSendInviteCommandListener) listener).onRecvCommand((RoomCmdPKSendInviteModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_ANSWER: // ??????PK????????????
                    if (listener instanceof PKAnswerCommandListener) {
                        ((PKAnswerCommandListener) listener).onRecvCommand((RoomCmdPKAnswerModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_START: // ????????????PK
                    if (listener instanceof PKStartCommandListener) {
                        ((PKStartCommandListener) listener).onRecvCommand((RoomCmdPKStartModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_FINISH: // ????????????PK
                    if (listener instanceof PKFinishCommandListener) {
                        ((PKFinishCommandListener) listener).onRecvCommand((RoomCmdPKFinishModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_SETTINGS: // ??????PK??????
                    if (listener instanceof PKSettingsCommandListener) {
                        ((PKSettingsCommandListener) listener).onRecvCommand((RoomCmdPKSettingsModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_OPEN_MATCH: // ??????????????????PK
                    if (listener instanceof PKOpenMatchCommandListener) {
                        ((PKOpenMatchCommandListener) listener).onRecvCommand((RoomCmdPKOpenMatchModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_CHANGE_GAME: // ??????PK???????????????
                    if (listener instanceof PKChangeGameCommandListener) {
                        ((PKChangeGameCommandListener) listener).onRecvCommand((RoomCmdPKChangeGameModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_REMOVE_RIVAL: // ??????PK???????????????
                    if (listener instanceof PKRemoveRivalCommandListener) {
                        ((PKRemoveRivalCommandListener) listener).onRecvCommand((RoomCmdPKRemoveRivalModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_SETTLE: // ??????pk????????????????????????
                    if (listener instanceof PKSettleCommandListener) {
                        ((PKSettleCommandListener) listener).onRecvCommand((RoomCmdPKSettleModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_RIVAL_EXIT: // ??????PK????????????????????????
                    if (listener instanceof PKRivalExitCommandListener) {
                        ((PKRivalExitCommandListener) listener).onRecvCommand((RoomCmdPKRivalExitModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_USER_ORDER_NOTIFY: // ????????????
                    if (listener instanceof UserOrderCommandListener) {
                        ((UserOrderCommandListener) listener).onRecvCommand((RoomCmdUserOrderModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ORDER_OPERATE_NOTIFY: // ????????????????????????????????????
                    if (listener instanceof OrderResultCommandListener) {
                        ((OrderResultCommandListener) listener).onRecvCommand((RoomCmdOrderOperateModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_QUIZ_BET: // ??????????????????
                    if (listener instanceof QuizBetCommandListener) {
                        ((QuizBetCommandListener) listener).onRecvCommand((QuizBetModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_DISCO_INFO_REQ: // ??????????????????
                    if (listener instanceof DiscoInfoReqCommandListener) {
                        ((DiscoInfoReqCommandListener) listener).onRecvCommand((RoomCmdDiscoInfoReqModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_DISCO_INFO_RESP: // ??????????????????
                    if (listener instanceof DiscoInfoRespCommandListener) {
                        ((DiscoInfoRespCommandListener) listener).onRecvCommand((RoomCmdDiscoInfoRespModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_DISCO_BECOME_DJ: // ???DJ???
                    if (listener instanceof DiscoBecomeDJCommandListener) {
                        ((DiscoBecomeDJCommandListener) listener).onRecvCommand((RoomCmdBecomeDJModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_DISCO_ACTION_PAY: // ??????????????????
                    if (listener instanceof DiscoActionPayCommandListener) {
                        ((DiscoActionPayCommandListener) listener).onRecvCommand((RoomCmdDiscoActionPayModel) model, fromUserID);
                    }
                    break;
            }
        }
    }

    private int getCommandCmd(String command) {
        try {
            JSONObject obj = new JSONObject(command);
            return obj.getInt("cmd");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    interface ICommandListener {
    }

    // region ??????????????????
    interface PublicMsgCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdChatTextModel model, String userID);
    }

    interface SendGiftCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdSendGiftModel model, String userID);
    }

    interface UpMicCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdUpMicModel model, String userID);
    }

    interface DownMicCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdDownMicModel model, String userID);
    }

    interface GameChangeCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdChangeGameModel model, String userID);
    }

    interface EnterRoomCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdEnterRoomModel model, String userID);
    }
    // endregion ??????????????????

    // region ??????pk????????????
    interface PKSendInviteCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKSendInviteModel model, String userID);
    }

    interface PKAnswerCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKAnswerModel model, String userID);
    }

    interface PKStartCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKStartModel model, String userID);
    }

    interface PKFinishCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKFinishModel model, String userID);
    }

    interface PKSettingsCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKSettingsModel model, String userID);
    }

    interface PKOpenMatchCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKOpenMatchModel model, String userID);
    }

    interface PKChangeGameCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKChangeGameModel model, String userID);
    }

    interface PKRemoveRivalCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKRemoveRivalModel model, String userID);
    }

    interface PKSettleCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKSettleModel model, String userID);
    }

    interface PKRivalExitCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKRivalExitModel model, String userID);
    }
    // endregion ??????pk????????????

    // region ??????????????????
    interface UserOrderCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdUserOrderModel model, String userID);
    }

    interface OrderResultCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdOrderOperateModel model, String userID);
    }
    // endregion ??????????????????

    // region ??????????????????
    interface QuizBetCommandListener extends ICommandListener {
        void onRecvCommand(QuizBetModel model, String userID);
    }
    // endregion ??????????????????

    // region ??????????????????
    interface DiscoInfoReqCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdDiscoInfoReqModel model, String userID);
    }

    interface DiscoInfoRespCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdDiscoInfoRespModel model, String userID);
    }

    interface DiscoBecomeDJCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdBecomeDJModel model, String userID);
    }

    interface DiscoActionPayCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdDiscoActionPayModel model, String userID);
    }
    // endregion ??????????????????


    @Override
    public void onDestroy() {
        super.onDestroy();
        listenerList.clear();
    }
}
