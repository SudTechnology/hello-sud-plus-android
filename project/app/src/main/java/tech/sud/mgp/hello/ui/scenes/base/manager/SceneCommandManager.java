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
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.order.RoomCmdOrderOperateModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.order.RoomCmdUserOrderModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKAgainModel;
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

/**
 * 房间信令相关
 */
public class SceneCommandManager extends BaseServiceManager {

    private final List<ICommandListener> listenerList = new ArrayList<>();

    /**
     * 设置信令监听
     *
     * @param listener 设置具体某个监听器来监听具体的信令
     */
    public void setCommandListener(ICommandListener listener) {
        listenerList.add(listener);
    }

    /** 移除信令监听 */
    public void removeCommandListener(ICommandListener listener) {
        if (listener != null) {
            listenerList.remove(listener);
        }
    }

    /**
     * 接收到信令，进行解析分发
     *
     * @param userID  发送者
     * @param command 信令字符
     */
    public void onRecvCommand(String userID, String command) {
        LogUtils.d("onRecvCommand:" + command);
        int commandCmd = getCommandCmd(command);
        switch (commandCmd) {
            case RoomCmd.CMD_CHAT_TEXT_NOTIFY: // 公屏消息
                dispatchCommand(commandCmd, RoomCmdChatTextModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_SEND_GIFT_NOTIFY: // 发送礼物
                dispatchCommand(commandCmd, RoomCmdSendGiftModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_UP_MIC_NOTIFY: // 上麦位
                dispatchCommand(commandCmd, RoomCmdUpMicModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_DOWN_MIC_NOTIFY: // 下麦位
                dispatchCommand(commandCmd, RoomCmdDownMicModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_CHANGE_GAME_NOTIFY: // 游戏切换
                dispatchCommand(commandCmd, RoomCmdChangeGameModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ENTER_ROOM_NOTIFY: // 进入房间通知
                dispatchCommand(commandCmd, RoomCmdEnterRoomModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_SEND_INVITE: // 发送跨房PK邀请
                dispatchCommand(commandCmd, RoomCmdPKSendInviteModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_ANSWER: // 跨房PK邀请应答
                dispatchCommand(commandCmd, RoomCmdPKAnswerModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_START: // 开始跨房PK
                dispatchCommand(commandCmd, RoomCmdPKStartModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_FINISH: // 结束跨房PK
                dispatchCommand(commandCmd, RoomCmdPKFinishModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_SETTINGS: // 跨房PK设置
                dispatchCommand(commandCmd, RoomCmdPKSettingsModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_OPEN_MATCH: // 开启匹配跨房PK
                dispatchCommand(commandCmd, RoomCmdPKOpenMatchModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_CHANGE_GAME: // 跨房PK，切换游戏
                dispatchCommand(commandCmd, RoomCmdPKChangeGameModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_REMOVE_RIVAL: // 跨房PK，移除对手
                dispatchCommand(commandCmd, RoomCmdPKRemoveRivalModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_AGAIN: // 跨房PK，再来一轮PK
                dispatchCommand(commandCmd, RoomCmdPKAgainModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_SETTLE: // 跨房pk游戏结算消息通知
                dispatchCommand(commandCmd, RoomCmdPKSettleModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_RIVAL_EXIT: // 跨房PK对手房间关闭消息
                dispatchCommand(commandCmd, RoomCmdPKRivalExitModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_USER_ORDER_NOTIFY: // 用户点单
                dispatchCommand(commandCmd, RoomCmdUserOrderModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ORDER_OPERATE_NOTIFY: // 主播同意或者拒绝用户点单
                dispatchCommand(commandCmd, RoomCmdOrderOperateModel.fromJson(command), userID);
                break;
        }
    }

    /**
     * 分发信令
     *
     * @param cmd        信令命令
     * @param model      信令内容
     * @param fromUserID 发送者
     */
    private void dispatchCommand(int cmd, RoomCmdBaseModel model, String fromUserID) {
        if (model == null) return;
        for (ICommandListener listener : listenerList) {
            switch (cmd) {
                case RoomCmd.CMD_CHAT_TEXT_NOTIFY: // 发送公屏
                    if (listener instanceof PublicMsgCommandListener) {
                        ((PublicMsgCommandListener) listener).onRecvCommand((RoomCmdChatTextModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_SEND_GIFT_NOTIFY: // 发送礼物
                    if (listener instanceof SendGiftCommandListener) {
                        ((SendGiftCommandListener) listener).onRecvCommand((RoomCmdSendGiftModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_UP_MIC_NOTIFY: // 上麦位
                    if (listener instanceof UpMicCommandListener) {
                        ((UpMicCommandListener) listener).onRecvCommand((RoomCmdUpMicModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_DOWN_MIC_NOTIFY: // 下麦位
                    if (listener instanceof DownMicCommandListener) {
                        ((DownMicCommandListener) listener).onRecvCommand((RoomCmdDownMicModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_CHANGE_GAME_NOTIFY: // 游戏切换
                    if (listener instanceof GameChangeCommandListener) {
                        ((GameChangeCommandListener) listener).onRecvCommand((RoomCmdChangeGameModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ENTER_ROOM_NOTIFY: // 进入房间通知
                    if (listener instanceof EnterRoomCommandListener) {
                        ((EnterRoomCommandListener) listener).onRecvCommand((RoomCmdEnterRoomModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_SEND_INVITE: // 发送跨房PK邀请
                    if (listener instanceof PKSendInviteCommandListener) {
                        ((PKSendInviteCommandListener) listener).onRecvCommand((RoomCmdPKSendInviteModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_ANSWER: // 跨房PK邀请应答
                    if (listener instanceof PKAnswerCommandListener) {
                        ((PKAnswerCommandListener) listener).onRecvCommand((RoomCmdPKAnswerModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_START: // 开始跨房PK
                    if (listener instanceof PKStartCommandListener) {
                        ((PKStartCommandListener) listener).onRecvCommand((RoomCmdPKStartModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_FINISH: // 结束跨房PK
                    if (listener instanceof PKFinishCommandListener) {
                        ((PKFinishCommandListener) listener).onRecvCommand((RoomCmdPKFinishModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_SETTINGS: // 跨房PK设置
                    if (listener instanceof PKSettingsCommandListener) {
                        ((PKSettingsCommandListener) listener).onRecvCommand((RoomCmdPKSettingsModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_OPEN_MATCH: // 开启匹配跨房PK
                    if (listener instanceof PKOpenMatchCommandListener) {
                        ((PKOpenMatchCommandListener) listener).onRecvCommand((RoomCmdPKOpenMatchModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_CHANGE_GAME: // 跨房PK，切换游戏
                    if (listener instanceof PKChangeGameCommandListener) {
                        ((PKChangeGameCommandListener) listener).onRecvCommand((RoomCmdPKChangeGameModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_REMOVE_RIVAL: // 跨房PK，移除对手
                    if (listener instanceof PKRemoveRivalCommandListener) {
                        ((PKRemoveRivalCommandListener) listener).onRecvCommand((RoomCmdPKRemoveRivalModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_AGAIN: // 跨房PK，再来一轮PK
                    if (listener instanceof PKAgainCommandListener) {
                        ((PKAgainCommandListener) listener).onRecvCommand((RoomCmdPKAgainModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_SETTLE: // 跨房pk游戏结算消息通知
                    if (listener instanceof PKSettleCommandListener) {
                        ((PKSettleCommandListener) listener).onRecvCommand((RoomCmdPKSettleModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_RIVAL_EXIT: // 跨房PK对手房间关闭消息
                    if (listener instanceof PKRivalExitCommandListener) {
                        ((PKRivalExitCommandListener) listener).onRecvCommand((RoomCmdPKRivalExitModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_USER_ORDER_NOTIFY: // 用户点单
                    if (listener instanceof UserOrderCommandListener) {
                        ((UserOrderCommandListener) listener).onRecvCommand((RoomCmdUserOrderModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ORDER_OPERATE_NOTIFY: // 主播同意或者拒绝用户点单
                    if (listener instanceof OrderResultCommandListener) {
                        ((OrderResultCommandListener) listener).onRecvCommand((RoomCmdOrderOperateModel) model, fromUserID);
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

    // region 基础信令监听
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
    // endregion 基础信令监听

    // region 跨房pk信令监听
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

    interface PKAgainCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKAgainModel model, String userID);
    }

    interface PKSettleCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKSettleModel model, String userID);
    }

    interface PKRivalExitCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKRivalExitModel model, String userID);
    }
    // endregion 跨房pk信令监听

    // region 点单信令监听
    interface UserOrderCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdUserOrderModel model, String userID);
    }

    interface OrderResultCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdOrderOperateModel model, String userID);
    }
    // endregion 点单信令监听


    @Override
    public void onDestroy() {
        super.onDestroy();
        listenerList.clear();
    }
}
