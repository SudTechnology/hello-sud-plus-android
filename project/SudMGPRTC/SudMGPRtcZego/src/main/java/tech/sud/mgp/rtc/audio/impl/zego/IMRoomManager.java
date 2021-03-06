package tech.sud.mgp.rtc.audio.impl.zego;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.rtc.audio.core.ISudAudioEventListener;

/**
 * 负责跨房消息
 * 目前只集成了zego的IM SDK，负责所有跨房IM消息
 */
public class IMRoomManager {

    private static final String kTag = IMRoomManager.class.toString();

    private ISudAudioEventListener mISudAudioEventListener;

    private static IMRoomManager imRoomManager;

    public static IMRoomManager sharedInstance() {
        if (imRoomManager == null) {
            synchronized (IMRoomManager.class) {
                if (imRoomManager == null) {
                    imRoomManager = new IMRoomManager();
                }
            }
        }

        return imRoomManager;
    }

    public void init(String appId, ISudAudioEventListener listener) {
        long appID = 0;
        try {
            appID = Long.parseLong(appId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mISudAudioEventListener = listener;
        ZIMManager.sharedInstance().create(appID, Utils.getApp());
        ZIMManager.sharedInstance().setReceiveRoomMessageCallback(new ZIMManager.OnReceiveRoomMessage() {
            @Override
            public void onReceiveRoomMessage(String roomID, String senderUserID, String message) {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ISudAudioEventListener listener = mISudAudioEventListener;
                        if (listener != null) {
                            listener.onRecvXRoomCommand(roomID, senderUserID, message);
                        }
                    }
                });
            }
        });
    }

    public void destroy() {
        ZIMManager.sharedInstance().destroy();
        ZIMManager.sharedInstance().setReceiveRoomMessageCallback(null);
        mISudAudioEventListener = null;
    }

    public void joinRoom(String roomID, String userID, String userName, String token) {
        ZIMManager.sharedInstance().joinRoom(roomID, userID, userName, token);
    }

    public void leaveRoom() {
        ZIMManager.sharedInstance().leaveRoom();
    }

    public void sendXRoomCommand(String roomID, String command, ISudAudioEngine.SendCommandListener listener) {
        ZIMManager.sharedInstance().sendXRoomCommand(roomID, command, listener);
    }
}
