package tech.sud.mgp.hello.ui.scenes.audio.manager;

import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.rtc.audio.core.AudioUser;
import tech.sud.mgp.hello.ui.scenes.audio.model.RoomTextModel;
import tech.sud.mgp.hello.ui.scenes.audio.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.audio.service.AudioRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChatTextModel;

/**
 * 房间公屏
 */
public class AudioChatManager extends BaseServiceManager {

    private AudioRoomServiceManager parentManager;

    public AudioChatManager(AudioRoomServiceManager audioRoomServiceManager) {
        super();
        this.parentManager = audioRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.audioEngineManager.setCommandListener(publicMsgCommandListener);
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
        parentManager.audioEngineManager.sendCommand(command, null);
    }

    public void addMsg(Object msg) {
        AudioRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.addPublicMsg(msg);
        }
    }

    /**
     * 标记自己发送的公屏消息
     * 用于处理你画我猜关键字是够命中
     */
    public void selfSendMsg(String msg) {
        AudioRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onSelfSendMsg(msg);
        }
    }

    private final AudioCommandManager.PublicMsgCommandListener publicMsgCommandListener = new AudioCommandManager.PublicMsgCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdChatTextModel command, AudioUser user) {
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

}
