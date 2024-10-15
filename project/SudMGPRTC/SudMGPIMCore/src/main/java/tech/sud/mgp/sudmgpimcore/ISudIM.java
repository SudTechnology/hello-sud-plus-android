package tech.sud.mgp.sudmgpimcore;

import android.content.Context;

import tech.sud.mgp.sudmgpimcore.listener.SendXCommandListener;
import tech.sud.mgp.sudmgpimcore.listener.SudIMListener;
import tech.sud.mgp.sudmgpimcore.listener.SudIMRoomLeftCallback;

/** IM接口 */
public interface ISudIM {

    void init(Context context, String appId, String userId, SudIMListener zimListener);

    void joinRoom(String roomID, String userID, String userName, String token);

    void leaveRoom(String roomId, SudIMRoomLeftCallback callback);

    void sendXRoomCommand(String roomID, String command, SendXCommandListener listener);

    /** 检查连通性，如果未连通，则触发SudIMListener.rejoinRoom() */
    void checkConnected(String roomId, String userId);

    void destroy();

}
