package tech.sud.mgp.hello.ui.room.audio.example.manager;

import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.rtc.protocol.MediaUser;
import tech.sud.mgp.hello.ui.room.audio.example.model.RoomTextModel;
import tech.sud.mgp.hello.ui.room.audio.example.model.UserInfo;
import tech.sud.mgp.hello.ui.room.audio.example.model.command.PublicMsgCommand;
import tech.sud.mgp.hello.ui.room.audio.example.service.AudioRoomServiceCallback;
import tech.sud.mgp.hello.ui.room.audio.example.utils.AudioRoomCommandUtils;

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

        // 发送公屏消息信令
        String command = AudioRoomCommandUtils.buildPublicMsgCommand(msg.toString());
        parentManager.audioEngineManager.sendCommand(command, null);
    }

    public void addMsg(Object msg) {
        AudioRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.addPublicMsg(msg);
        }
    }

    private final AudioCommandManager.PublicMsgCommandListener publicMsgCommandListener = new AudioCommandManager.PublicMsgCommandListener() {
        @Override
        public void onRecvCommand(PublicMsgCommand command, MediaUser user, String roomId) {
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