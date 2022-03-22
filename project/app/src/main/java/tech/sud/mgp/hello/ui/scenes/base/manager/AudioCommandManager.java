package tech.sud.mgp.hello.ui.scenes.base.manager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.rtc.audio.core.AudioUser;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChangeGameModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChatTextModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdDownMicModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdEnterRoomModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdSendGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdUpMicModel;

/**
 * 房间信令相关
 */
public class AudioCommandManager extends BaseServiceManager {

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
     * @param roomId   房间id
     * @param fromUser 发送者
     * @param command  信令字符
     */
    public void onIMRecvCustomCommand(String roomId, AudioUser fromUser, String command) {
        int commandCmd = getCommandCmd(command);
        switch (commandCmd) {
            case RoomCmd.CMD_CHAT_TEXT_NOTIFY: // 公屏消息
                RoomCmdChatTextModel publicMsgCommand = RoomCmdChatTextModel.fromJson(command);
                if (publicMsgCommand != null) {
                    dispatchCommand(commandCmd, publicMsgCommand, fromUser, roomId);
                }
                break;
            case RoomCmd.CMD_SEND_GIFT_NOTIFY: // 发送礼物
                RoomCmdSendGiftModel sendGiftCommand = RoomCmdSendGiftModel.fromJson(command);
                if (sendGiftCommand != null) {
                    dispatchCommand(commandCmd, sendGiftCommand, fromUser, roomId);
                }
                break;
            case RoomCmd.CMD_UP_MIC_NOTIFY: // 上麦位
                RoomCmdUpMicModel upMicCommand = RoomCmdUpMicModel.fromJson(command);
                if (upMicCommand != null) {
                    dispatchCommand(commandCmd, upMicCommand, fromUser, roomId);
                }
                break;
            case RoomCmd.CMD_DOWN_MIC_NOTIFY: // 下麦位
                RoomCmdDownMicModel downMicCommand = RoomCmdDownMicModel.fromJson(command);
                if (downMicCommand != null) {
                    dispatchCommand(commandCmd, downMicCommand, fromUser, roomId);
                }
                break;
            case RoomCmd.CMD_CHANGE_GAME_NOTIFY: // 游戏切换
                RoomCmdChangeGameModel gameChangeCommand = RoomCmdChangeGameModel.fromJson(command);
                if (gameChangeCommand != null) {
                    dispatchCommand(commandCmd, gameChangeCommand, fromUser, roomId);
                }
                break;
            case RoomCmd.CMD_ENTER_ROOM_NOTIFY: // 进入房间通知
                RoomCmdEnterRoomModel enterRoomCommand = RoomCmdEnterRoomModel.fromJson(command);
                if (enterRoomCommand != null) {
                    dispatchCommand(commandCmd, enterRoomCommand, fromUser, roomId);
                }
                break;
        }
    }

    /**
     * 分发信令
     *
     * @param cmd      信令命令
     * @param command  信令内容
     * @param fromUser 发送者
     * @param roomId   房间id
     */
    private void dispatchCommand(int cmd, RoomCmdBaseModel command, AudioUser fromUser, String roomId) {
        for (ICommandListener listener : listenerList) {
            switch (cmd) {
                case RoomCmd.CMD_CHAT_TEXT_NOTIFY: // 发送公屏
                    if (listener instanceof PublicMsgCommandListener) {
                        ((PublicMsgCommandListener) listener).onRecvCommand((RoomCmdChatTextModel) command, fromUser, roomId);
                    }
                    break;
                case RoomCmd.CMD_SEND_GIFT_NOTIFY: // 发送礼物
                    if (listener instanceof SendGiftCommandListener) {
                        ((SendGiftCommandListener) listener).onRecvCommand((RoomCmdSendGiftModel) command, fromUser, roomId);
                    }
                    break;
                case RoomCmd.CMD_UP_MIC_NOTIFY: // 上麦位
                    if (listener instanceof UpMicCommandListener) {
                        ((UpMicCommandListener) listener).onRecvCommand((RoomCmdUpMicModel) command, fromUser, roomId);
                    }
                    break;
                case RoomCmd.CMD_DOWN_MIC_NOTIFY: // 下麦位
                    if (listener instanceof DownMicCommandListener) {
                        ((DownMicCommandListener) listener).onRecvCommand((RoomCmdDownMicModel) command, fromUser, roomId);
                    }
                    break;
                case RoomCmd.CMD_CHANGE_GAME_NOTIFY: // 游戏切换
                    if (listener instanceof GameChangeCommandListener) {
                        ((GameChangeCommandListener) listener).onRecvCommand((RoomCmdChangeGameModel) command, fromUser, roomId);
                    }
                    break;
                case RoomCmd.CMD_ENTER_ROOM_NOTIFY: // 进入房间通知
                    if (listener instanceof EnterRoomCommandListener) {
                        ((EnterRoomCommandListener) listener).onRecvCommand((RoomCmdEnterRoomModel) command, fromUser, roomId);
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
        void onRecvCommand(RoomCmdChatTextModel command, AudioUser user, String roomId);
    }

    interface SendGiftCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdSendGiftModel command, AudioUser user, String roomId);
    }

    interface UpMicCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdUpMicModel command, AudioUser user, String roomId);
    }

    interface DownMicCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdDownMicModel command, AudioUser user, String roomId);
    }

    interface GameChangeCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdChangeGameModel command, AudioUser user, String roomId);
    }

    interface EnterRoomCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdEnterRoomModel command, AudioUser user, String roomId);
    }

}
