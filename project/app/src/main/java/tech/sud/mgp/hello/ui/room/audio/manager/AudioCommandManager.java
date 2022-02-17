package tech.sud.mgp.hello.ui.room.audio.manager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.rtc.audio.core.AudioUser;
import tech.sud.mgp.hello.ui.room.audio.model.command.BaseCommand;
import tech.sud.mgp.hello.ui.room.audio.model.command.CommandCmd;
import tech.sud.mgp.hello.ui.room.audio.model.command.DownMicCommand;
import tech.sud.mgp.hello.ui.room.audio.model.command.EnterRoomCommand;
import tech.sud.mgp.hello.ui.room.audio.model.command.GameChangeCommand;
import tech.sud.mgp.hello.ui.room.audio.model.command.PublicMsgCommand;
import tech.sud.mgp.hello.ui.room.audio.model.command.SendGiftCommand;
import tech.sud.mgp.hello.ui.room.audio.model.command.UpMicCommand;
import tech.sud.mgp.hello.ui.room.audio.utils.HSJsonUtils;

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
            case CommandCmd.CMD_PUBLIC_MSG_NTF: // 公屏消息
                PublicMsgCommand publicMsgCommand = HSJsonUtils.fromJson(command, PublicMsgCommand.class);
                if (publicMsgCommand != null) {
                    dispatchCommand(commandCmd, publicMsgCommand, fromUser, roomId);
                }
                break;
            case CommandCmd.CMD_PUBLIC_SEND_GIFT_NTF: // 发送礼物
                SendGiftCommand sendGiftCommand = HSJsonUtils.fromJson(command, SendGiftCommand.class);
                if (sendGiftCommand != null) {
                    dispatchCommand(commandCmd, sendGiftCommand, fromUser, roomId);
                }
                break;
            case CommandCmd.CMD_UP_MIC_NTF: // 上麦位
                UpMicCommand upMicCommand = HSJsonUtils.fromJson(command, UpMicCommand.class);
                if (upMicCommand != null) {
                    dispatchCommand(commandCmd, upMicCommand, fromUser, roomId);
                }
                break;
            case CommandCmd.CMD_DOWN_MIC_NTF: // 下麦位
                DownMicCommand downMicCommand = HSJsonUtils.fromJson(command, DownMicCommand.class);
                if (downMicCommand != null) {
                    dispatchCommand(commandCmd, downMicCommand, fromUser, roomId);
                }
                break;
            case CommandCmd.CMD_GAME_CHANGE: // 游戏切换
                GameChangeCommand gameChangeCommand = HSJsonUtils.fromJson(command, GameChangeCommand.class);
                if (gameChangeCommand != null) {
                    dispatchCommand(commandCmd, gameChangeCommand, fromUser, roomId);
                }
                break;
            case CommandCmd.CMD_ENTER_ROOM_NTF: // 进入房间通知
                EnterRoomCommand enterRoomCommand = HSJsonUtils.fromJson(command, EnterRoomCommand.class);
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
    private void dispatchCommand(int cmd, BaseCommand command, AudioUser fromUser, String roomId) {
        for (ICommandListener listener : listenerList) {
            switch (cmd) {
                case CommandCmd.CMD_PUBLIC_MSG_NTF: // 发送公屏
                    if (listener instanceof PublicMsgCommandListener) {
                        ((PublicMsgCommandListener) listener).onRecvCommand((PublicMsgCommand) command, fromUser, roomId);
                    }
                    break;
                case CommandCmd.CMD_PUBLIC_SEND_GIFT_NTF: // 发送礼物
                    if (listener instanceof SendGiftCommandListener) {
                        ((SendGiftCommandListener) listener).onRecvCommand((SendGiftCommand) command, fromUser, roomId);
                    }
                    break;
                case CommandCmd.CMD_UP_MIC_NTF: // 上麦位
                    if (listener instanceof UpMicCommandListener) {
                        ((UpMicCommandListener) listener).onRecvCommand((UpMicCommand) command, fromUser, roomId);
                    }
                    break;
                case CommandCmd.CMD_DOWN_MIC_NTF: // 下麦位
                    if (listener instanceof DownMicCommandListener) {
                        ((DownMicCommandListener) listener).onRecvCommand((DownMicCommand) command, fromUser, roomId);
                    }
                    break;
                case CommandCmd.CMD_GAME_CHANGE: // 游戏切换
                    if (listener instanceof GameChangeCommandListener) {
                        ((GameChangeCommandListener) listener).onRecvCommand((GameChangeCommand) command, fromUser, roomId);
                    }
                    break;
                case CommandCmd.CMD_ENTER_ROOM_NTF: // 进入房间通知
                    if (listener instanceof EnterRoomCommandListener) {
                        ((EnterRoomCommandListener) listener).onRecvCommand((EnterRoomCommand) command, fromUser, roomId);
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
        void onRecvCommand(PublicMsgCommand command, AudioUser user, String roomId);
    }

    interface SendGiftCommandListener extends ICommandListener {
        void onRecvCommand(SendGiftCommand command, AudioUser user, String roomId);
    }

    interface UpMicCommandListener extends ICommandListener {
        void onRecvCommand(UpMicCommand command, AudioUser user, String roomId);
    }

    interface DownMicCommandListener extends ICommandListener {
        void onRecvCommand(DownMicCommand command, AudioUser user, String roomId);
    }

    interface GameChangeCommandListener extends ICommandListener {
        void onRecvCommand(GameChangeCommand command, AudioUser user, String roomId);
    }

    interface EnterRoomCommandListener extends ICommandListener {
        void onRecvCommand(EnterRoomCommand command, AudioUser user, String roomId);
    }

}
