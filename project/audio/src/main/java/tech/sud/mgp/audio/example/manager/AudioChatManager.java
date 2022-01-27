package tech.sud.mgp.audio.example.manager;

import tech.sud.mgp.audio.example.model.RoomTextModel;
import tech.sud.mgp.audio.example.model.UserInfo;
import tech.sud.mgp.audio.example.model.command.PublicMsgCommand;
import tech.sud.mgp.audio.example.service.AudioRoomServiceCallback;
import tech.sud.mgp.audio.example.utils.AudioRoomCommandUtils;
import tech.sud.mgp.audio.middle.MediaUser;
import tech.sud.mgp.common.model.HSUserInfo;

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
        RoomTextModel model = new RoomTextModel();
        model.userId = HSUserInfo.userId;
        model.avatar = HSUserInfo.avatar;
        model.nickName = HSUserInfo.nickName;
        model.text = msg.toString();
        addMsg(model);

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
