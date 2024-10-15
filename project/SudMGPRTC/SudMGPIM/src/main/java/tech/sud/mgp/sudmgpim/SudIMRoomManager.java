package tech.sud.mgp.sudmgpim;

import tech.sud.mgp.rtc.audio.impl.zego.SudIMZIMImpl;
import tech.sud.mgp.sudmgpimcore.ISudIM;
import tech.sud.mgp.sudmgpimcore.listener.SendXCommandListener;
import tech.sud.mgp.sudmgpimcore.listener.SudIMListener;
import tech.sud.mgp.sudmgpimcore.listener.SudIMRoomLeftCallback;

/**
 * 负责跨房消息
 */
public class SudIMRoomManager {

    private static SudIMRoomManager sudImRoomManager;

    private ISudIM mISudIM = new SudIMZIMImpl();

    public static SudIMRoomManager sharedInstance() {
        if (sudImRoomManager == null) {
            synchronized (SudIMRoomManager.class) {
                if (sudImRoomManager == null) {
                    sudImRoomManager = new SudIMRoomManager();
                }
            }
        }
        return sudImRoomManager;
    }

    public void init(String appId, SudIMListener imListener) {
        mISudIM.init(appId, imListener);
    }

    public void destroy() {
        mISudIM.destroy();
    }

    public void joinRoom(String roomID, String userID, String userName, String token, SudIMListener imListener) {
        mISudIM.joinRoom(roomID, userID, userName, token, imListener);
    }

    public void leaveRoom(String roomId, SudIMRoomLeftCallback callback) {
        mISudIM.leaveRoom(roomId, callback);
    }

    /** 检查连通性，如果未连通，则触发SudIMListener.rejoinRoom() */
    public void checkConnected(String roomId, String userId) {
        mISudIM.checkConnected(roomId, userId);
    }

    public void sendXRoomCommand(String roomID, String command, SendXCommandListener listener) {
        mISudIM.sendXRoomCommand(roomID, command, listener);
    }
}
