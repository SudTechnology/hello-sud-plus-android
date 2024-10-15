package tech.sud.mgp.sudmgpimcore.listener;

import org.json.JSONObject;

import tech.sud.mgp.sudmgpimcore.model.SudIMConnectionEvent;
import tech.sud.mgp.sudmgpimcore.model.SudIMConnectionState;
import tech.sud.mgp.sudmgpimcore.model.SudIMError;
import tech.sud.mgp.sudmgpimcore.model.SudIMRoomFullInfo;
import tech.sud.mgp.sudmgpimcore.model.SudIMRoomState;

/** im监听 */
public interface SudIMListener {

    void onConnectionStateChanged(SudIMConnectionState state, SudIMConnectionEvent event, JSONObject extendedData);

    void onError(SudIMError errorInfo);

    void onLoggedIn(SudIMError errorInfo);

    void onRoomEntered(SudIMRoomFullInfo roomInfo, SudIMError errorInfo);

    void onRoomStateChanged(SudIMRoomState state, String roomID);

    /** 需要销毁然后重新进入房间 */
    void onRejoinRoom(String roomId, String userId);

    /**
     * 接收跨房指令信息回调
     *
     * @param fromRoomID 消息的房间 ID
     * @param fromUserID 消息的用户 ID
     * @param command    指令内容
     */
    void onRecvXRoomCommand(String fromRoomID, String fromUserID, String command);

    /** 房间在线人数变更 */
    void onRoomOnlineUserCountUpdate(int count);
}
