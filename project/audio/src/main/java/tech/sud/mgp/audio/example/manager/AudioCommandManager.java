package tech.sud.mgp.audio.example.manager;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.GsonUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.audio.example.model.command.BaseCommand;
import tech.sud.mgp.audio.example.model.command.CommandCmd;
import tech.sud.mgp.audio.example.model.command.DownMicCommand;
import tech.sud.mgp.audio.example.model.command.PublicMsgCommand;
import tech.sud.mgp.audio.example.model.command.SendGiftCommand;
import tech.sud.mgp.audio.example.model.command.UpMicCommand;
import tech.sud.mgp.audio.middle.MediaUser;

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

    public void onIMRecvCustomCommand(String roomId, MediaUser fromUser, String command) {
        int commandCmd = getCommandCmd(command);
        switch (commandCmd) {
            case CommandCmd.CMD_PUBLIC_MSG_NTF:
                PublicMsgCommand publicMsgCommand = fromJson(command, PublicMsgCommand.class);
                if (publicMsgCommand != null) {
                    dispatchCommand(commandCmd, publicMsgCommand, fromUser, roomId);
                }
                break;
            case CommandCmd.CMD_PUBLIC_SEND_GIFT_NTF:
                SendGiftCommand sendGiftCommand = fromJson(command, SendGiftCommand.class);
                if (sendGiftCommand != null) {
                    dispatchCommand(commandCmd, sendGiftCommand, fromUser, roomId);
                }
                break;
            case CommandCmd.CMD_UP_MIC_NTF:
                UpMicCommand upMicCommand = fromJson(command, UpMicCommand.class);
                if (upMicCommand != null) {
                    dispatchCommand(commandCmd, upMicCommand, fromUser, roomId);
                }
                break;
            case CommandCmd.CMD_DOWN_MIC_NTF:
                DownMicCommand downMicCommand = fromJson(command, DownMicCommand.class);
                if (downMicCommand != null) {
                    dispatchCommand(commandCmd, downMicCommand, fromUser, roomId);
                }
                break;
        }
    }

    private void dispatchCommand(int cmd, BaseCommand command, MediaUser fromUser, String roomId) {
        for (ICommandListener listener : listenerList) {
            switch (cmd) {
                case CommandCmd.CMD_PUBLIC_MSG_NTF:
                    if (listener instanceof PublicMsgCommandListener) {
                        ((PublicMsgCommandListener) listener).onRecvCommand((PublicMsgCommand) command, fromUser, roomId);
                    }
                    break;
                case CommandCmd.CMD_PUBLIC_SEND_GIFT_NTF:
                    if (listener instanceof SendGiftCommandListener) {
                        ((SendGiftCommandListener) listener).onRecvCommand((SendGiftCommand) command, fromUser, roomId);
                    }
                    break;
                case CommandCmd.CMD_UP_MIC_NTF:
                    if (listener instanceof UpMicCommandListener) {
                        ((UpMicCommandListener) listener).onRecvCommand((UpMicCommand) command, fromUser, roomId);
                    }
                    break;
                case CommandCmd.CMD_DOWN_MIC_NTF:
                    if (listener instanceof DownMicCommandListener) {
                        ((DownMicCommandListener) listener).onRecvCommand((DownMicCommand) command, fromUser, roomId);
                    }
                    break;
            }
        }
    }

    private <T> T fromJson(final String json, @NonNull final Class<T> type) {
        try {
            return GsonUtils.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        void onRecvCommand(PublicMsgCommand command, MediaUser user, String roomId);
    }

    interface SendGiftCommandListener extends ICommandListener {
        void onRecvCommand(SendGiftCommand command, MediaUser user, String roomId);
    }

    interface UpMicCommandListener extends ICommandListener {
        void onRecvCommand(UpMicCommand command, MediaUser user, String roomId);
    }

    interface DownMicCommandListener extends ICommandListener {
        void onRecvCommand(DownMicCommand command, MediaUser user, String roomId);
    }

}
