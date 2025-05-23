package tech.sud.mgp.hello.ui.scenes.base.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInactiveAudioModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomTextModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChatMediaModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChatTextModel;

/**
 * 房间公屏
 */
public class SceneChatManager extends BaseServiceManager {

    private SceneRoomServiceManager parentManager;
    private List<Object> datas = new ArrayList<>();
    private HashSet<SendMsgListener> sendMsgListeners = new HashSet<>();

    public SceneChatManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.sceneEngineManager.setCommandListener(publicMsgCommandListener);
        parentManager.sceneEngineManager.setCommandListener(mediaMsgCommandListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        parentManager.sceneEngineManager.removeCommandListener(publicMsgCommandListener);
        parentManager.sceneEngineManager.removeCommandListener(mediaMsgCommandListener);
    }

    public void sendPublicMsg(CharSequence msg) {
        if (msg == null) return;
        // 往公屏列表当中插入一条数据
        RoomTextModel model = new RoomTextModel();
        model.userId = HSUserInfo.userId + "";
        model.avatar = HSUserInfo.getUseAvatar();
        model.nickName = HSUserInfo.nickName;
        model.text = msg.toString();
        addMsg(model);
        selfSendMsg(model.text);

        // 发送公屏消息信令
        String command = RoomCmdModelUtils.buildPublicMsgCommand(msg.toString());
        parentManager.sceneEngineManager.sendCommand(command, null);
    }

    public void assignUserSendPublicMsg(String userId, String icon, String nickname, CharSequence msg) {
        if (msg == null) return;
        // 往公屏列表当中插入一条数据
        RoomTextModel model = new RoomTextModel();
        model.userId = userId;
        model.avatar = icon;
        model.nickName = nickname;
        model.text = msg.toString();
        addMsg(model);

        // 发送公屏消息信令
        UserInfo user = new UserInfo();
        user.userID = userId;
        user.icon = icon;
        user.name = nickname;
        user.sex = 1;
        user.roomID = parentManager.getRoomId() + "";
        String command = RoomCmdModelUtils.buildPublicMsgCommand(user, msg.toString());
        parentManager.sceneEngineManager.sendCommand(command, null);
    }

    public void sendMediaMsg(int msgType, String content) {
        // 往公屏列表当中插入一条数据
        switch (msgType) {
            case RoomCmdChatMediaModel.MSG_TYPE_INACTIVE_TYPE:
                RoomInactiveAudioModel model = new RoomInactiveAudioModel();
                model.userId = HSUserInfo.userId;
                model.avatar = HSUserInfo.getUseAvatar();
                model.nickName = HSUserInfo.nickName;
                addMsg(model);
                break;
        }

        // 发送公屏消息信令
        String command = RoomCmdModelUtils.buildMediaMsgCommand(msgType, content);
        parentManager.sceneEngineManager.sendCommand(command, null);
    }

    /** 添加一条公屏消息 */
    public void addMsg(Object msg) {
        if (msg == null) return;
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.addPublicMsg(msg);
        }
        datas.add(msg);
    }

    /**
     * 标记自己发送的公屏消息
     * 用于处理你画我猜关键字是够命中
     */
    public void selfSendMsg(String msg) {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onSelfSendMsg(msg);
        }
        for (SendMsgListener listener : sendMsgListeners) {
            if (listener != null) {
                listener.onSendMsgCompleted(msg);
            }
        }
    }

    /** 添加发送公屏消息的监听 */
    public void addSendMsgListener(SendMsgListener listener) {
        sendMsgListeners.add(listener);
    }

    /** 移除发送公屏消息的监听 */
    public void removeSendMsgListener(SendMsgListener listener) {
        sendMsgListeners.remove(listener);
    }

    /** 发送公屏消息的监听 */
    public interface SendMsgListener {
        void onSendMsgCompleted(String msg);
    }

    private final SceneCommandManager.PublicMsgCommandListener publicMsgCommandListener = new SceneCommandManager.PublicMsgCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdChatTextModel command, String userID) {
            UserInfo userInfo = command.sendUser;
            if (userInfo == null) return;
            RoomTextModel model = new RoomTextModel();
            model.userId = userInfo.userID;
            model.avatar = userInfo.icon;
            model.nickName = userInfo.name;
            model.text = command.content;
            addMsg(model);
        }
    };
    private final SceneCommandManager.MediaMsgCommandListener mediaMsgCommandListener = new SceneCommandManager.MediaMsgCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdChatMediaModel command, String userID) {
            UserInfo userInfo = command.sendUser;
            if (userInfo == null) return;
            switch (command.msgType) {
                case RoomCmdChatMediaModel.MSG_TYPE_INACTIVE_TYPE:
                    RoomInactiveAudioModel model = new RoomInactiveAudioModel();
                    try {
                        model.userId = Long.parseLong(userInfo.userID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    model.avatar = userInfo.icon;
                    model.nickName = userInfo.name;
                    addMsg(model);
                    break;
            }
        }
    };

    /** 回调页面数据 */
    public void callbackPageData() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onChatList(new ArrayList<>(datas));
        }
    }

}
