package tech.sud.mgp.hello.ui.scenes.base.manager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChangeGameModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChatTextModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdDownMicModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdEnterRoomModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdOrderResultModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdSendGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdUpMicModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdUserOrderModel;

/**
 * 房间信令相关
 */
public class SceneCommandManager extends BaseServiceManager {

    private final List<ICommandListener> listenerList = new ArrayList<>();

    /**
     * 设置信令监听
     *
     * @param listener
     */
    public void setCommandListener(ICommandListener listener) {
        listenerList.add(listener);
    }

    /**
     * 移除信令监听
     */
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
        int commandCmd = getCommandCmd(command);
        switch (commandCmd) {
            case RoomCmd.CMD_CHAT_TEXT_NOTIFY: // 公屏消息
                RoomCmdChatTextModel publicMsgCommand = RoomCmdChatTextModel.fromJson(command);
                if (publicMsgCommand != null) {
                    dispatchCommand(commandCmd, publicMsgCommand, userID);
                }
                break;
            case RoomCmd.CMD_SEND_GIFT_NOTIFY: // 发送礼物
                RoomCmdSendGiftModel sendGiftCommand = RoomCmdSendGiftModel.fromJson(command);
                if (sendGiftCommand != null) {
                    dispatchCommand(commandCmd, sendGiftCommand, userID);
                }
                break;
            case RoomCmd.CMD_UP_MIC_NOTIFY: // 上麦位
                RoomCmdUpMicModel upMicCommand = RoomCmdUpMicModel.fromJson(command);
                if (upMicCommand != null) {
                    dispatchCommand(commandCmd, upMicCommand, userID);
                }
                break;
            case RoomCmd.CMD_DOWN_MIC_NOTIFY: // 下麦位
                RoomCmdDownMicModel downMicCommand = RoomCmdDownMicModel.fromJson(command);
                if (downMicCommand != null) {
                    dispatchCommand(commandCmd, downMicCommand, userID);
                }
                break;
            case RoomCmd.CMD_CHANGE_GAME_NOTIFY: // 游戏切换
                RoomCmdChangeGameModel gameChangeCommand = RoomCmdChangeGameModel.fromJson(command);
                if (gameChangeCommand != null) {
                    dispatchCommand(commandCmd, gameChangeCommand, userID);
                }
                break;
            case RoomCmd.CMD_ENTER_ROOM_NOTIFY: // 进入房间通知
                RoomCmdEnterRoomModel enterRoomCommand = RoomCmdEnterRoomModel.fromJson(command);
                if (enterRoomCommand != null) {
                    dispatchCommand(commandCmd, enterRoomCommand, userID);
                }
                break;
            case RoomCmd.CMD_SEND_ROOM_PK_INVITE: // 发送跨房PK邀请
                break;
            case RoomCmd.CMD_ROOM_PK_ANSWER: // 跨房PK邀请应答
                break;
            case RoomCmd.CMD_START_ROOM_PK: // 开始跨房PK
                break;
            case RoomCmd.CMD_FINISH_ROOM_PK: // 结束跨房PK
                break;
            case RoomCmd.CMD_ROOM_PK_SETTINGS: // 跨房PK设置
                break;
            case RoomCmd.CMD_OPEN_MATCH_ROOM_PK: // 开启匹配跨房PK答
                break;
            case RoomCmd.CMD_USER_ORDER_NOTIFY: // 用户点单
                RoomCmdUserOrderModel userOrderModel = RoomCmdUserOrderModel.fromJson(command);
                if (userOrderModel != null) {
                    dispatchCommand(commandCmd, userOrderModel, userID);
                }
                break;
            case RoomCmd.CMD_ORDER_RESULT_NOTIFY: // 主播同意或者拒绝用户点单
                RoomCmdOrderResultModel orderResultModel = RoomCmdOrderResultModel.fromJson(command);
                if (orderResultModel != null) {
                    dispatchCommand(commandCmd, orderResultModel, userID);
                }
                break;
        }
    }

    /**
     * 分发信令
     *
     * @param cmd        信令命令
     * @param command    信令内容
     * @param fromUserID 发送者
     */
    private void dispatchCommand(int cmd, RoomCmdBaseModel command, String fromUserID) {
        for (ICommandListener listener : listenerList) {
            switch (cmd) {
                case RoomCmd.CMD_CHAT_TEXT_NOTIFY: // 发送公屏
                    if (listener instanceof PublicMsgCommandListener) {
                        ((PublicMsgCommandListener) listener).onRecvCommand((RoomCmdChatTextModel) command, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_SEND_GIFT_NOTIFY: // 发送礼物
                    if (listener instanceof SendGiftCommandListener) {
                        ((SendGiftCommandListener) listener).onRecvCommand((RoomCmdSendGiftModel) command, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_UP_MIC_NOTIFY: // 上麦位
                    if (listener instanceof UpMicCommandListener) {
                        ((UpMicCommandListener) listener).onRecvCommand((RoomCmdUpMicModel) command, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_DOWN_MIC_NOTIFY: // 下麦位
                    if (listener instanceof DownMicCommandListener) {
                        ((DownMicCommandListener) listener).onRecvCommand((RoomCmdDownMicModel) command, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_CHANGE_GAME_NOTIFY: // 游戏切换
                    if (listener instanceof GameChangeCommandListener) {
                        ((GameChangeCommandListener) listener).onRecvCommand((RoomCmdChangeGameModel) command, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ENTER_ROOM_NOTIFY: // 进入房间通知
                    if (listener instanceof EnterRoomCommandListener) {
                        ((EnterRoomCommandListener) listener).onRecvCommand((RoomCmdEnterRoomModel) command, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_SEND_ROOM_PK_INVITE: // 发送跨房PK邀请
                    break;
                case RoomCmd.CMD_ROOM_PK_ANSWER: // 跨房PK邀请应答
                    break;
                case RoomCmd.CMD_START_ROOM_PK: // 开始跨房PK
                    break;
                case RoomCmd.CMD_FINISH_ROOM_PK: // 结束跨房PK
                    break;
                case RoomCmd.CMD_ROOM_PK_SETTINGS: // 跨房PK设置
                    break;
                case RoomCmd.CMD_OPEN_MATCH_ROOM_PK: // 开启匹配跨房PK答
                    break;
                case RoomCmd.CMD_USER_ORDER_NOTIFY: // 用户点单
                    if (listener instanceof UserOrderCommandListener) {
                        ((UserOrderCommandListener) listener).onRecvCommand((RoomCmdUserOrderModel) command, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ORDER_RESULT_NOTIFY: // 主播同意或者拒绝用户点单
                    if (listener instanceof OrderResultCommandListener) {
                        ((OrderResultCommandListener) listener).onRecvCommand((RoomCmdOrderResultModel) command, fromUserID);
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

    interface PublicMsgCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdChatTextModel command, String userID);
    }

    interface SendGiftCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdSendGiftModel command, String userID);
    }

    interface UpMicCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdUpMicModel command, String userID);
    }

    interface DownMicCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdDownMicModel command, String userID);
    }

    interface GameChangeCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdChangeGameModel command, String userID);
    }

    interface EnterRoomCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdEnterRoomModel command, String userID);
    }

    interface UserOrderCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdUserOrderModel command, String userID);
    }

    interface OrderResultCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdOrderResultModel command, String userID);
    }

}
