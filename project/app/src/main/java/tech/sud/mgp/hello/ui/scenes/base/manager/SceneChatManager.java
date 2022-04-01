package tech.sud.mgp.hello.ui.scenes.base.manager;

import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomTextModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChatTextModel;

/**
 * 房间公屏
 */
public class SceneChatManager extends BaseServiceManager {

    private SceneRoomServiceManager parentManager;

    public SceneChatManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.sceneEngineManager.setCommandListener(publicMsgCommandListener);
    }

    public void sendPublicMsg(CharSequence msg) {
        if (msg == null) return;
        // 往公屏列表当中插入一条数据
        RoomTextModel model = new RoomTextModel();
        model.userId = HSUserInfo.userId;
        model.avatar = HSUserInfo.avatar;
        model.nickName = HSUserInfo.nickName;
        model.text = msg.toString();
        addMsg(model);
        selfSendMsg(model.text);

        // 发送公屏消息信令
        String command = RoomCmdModelUtils.buildPublicMsgCommand(msg.toString());
        parentManager.sceneEngineManager.sendCommand(command, null);
    }

    public void addMsg(Object msg) {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.addPublicMsg(msg);
        }
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
    }

    private final SceneCommandManager.PublicMsgCommandListener publicMsgCommandListener = new SceneCommandManager.PublicMsgCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdChatTextModel command, String userID) {
            UserInfo userInfo = command.sendUser;
            if (userInfo == null) return;
            RoomTextModel model = new RoomTextModel();
            try {
                model.userId = Long.parseLong(userInfo.userID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            model.avatar = userInfo.icon;
            model.nickName = userInfo.name;
            model.text = command.content;
            addMsg(model);
        }
    };

}
