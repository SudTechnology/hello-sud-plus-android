package tech.sud.mgp.audio.example.manager;

import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.audio.example.model.command.EnterRoomCommand;
import tech.sud.mgp.audio.example.model.command.GameChangeCommand;
import tech.sud.mgp.audio.example.service.AudioRoomServiceCallback;
import tech.sud.mgp.audio.example.utils.AudioRoomCommandUtils;
import tech.sud.mgp.audio.middle.MediaUser;
import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.model.HSUserInfo;
import tech.sud.mgp.game.example.http.repository.GameRepository;

/**
 * 房间主要业务逻辑
 */
public class AudioRoomServiceManager extends BaseServiceManager {

    private AudioRoomServiceCallback audioRoomServiceCallback;
    private RoomInfoModel roomInfoModel;

    public final AudioEngineManager audioEngineManager = new AudioEngineManager(this);
    public final AudioChatManager audioChatManager = new AudioChatManager(this);
    public final AudioMicManager audioMicManager = new AudioMicManager(this);
    public final AudioStreamManager audioStreamManager = new AudioStreamManager(this);
    public final AudioGiftManager audioGiftManager = new AudioGiftManager(this);

    @Override
    public void onCreate() {
        super.onCreate();
        audioEngineManager.onCreate();
        audioChatManager.onCreate();
        audioMicManager.onCreate();
        audioStreamManager.onCreate();
        audioGiftManager.onCreate();
        setListener();
    }

    private void setListener() {
        audioEngineManager.setCommandListener(new AudioCommandManager.GameChangeCommandListener() {
            @Override
            public void onRecvCommand(GameChangeCommand command, MediaUser user, String roomId) {
                roomInfoModel.gameId = command.gameID;
                AudioRoomServiceCallback callback = getCallback();
                if (callback != null) {
                    callback.onGameChange(command.gameID);
                }
            }
        });
        audioEngineManager.setCommandListener(new AudioCommandManager.EnterRoomCommandListener() {
            @Override
            public void onRecvCommand(EnterRoomCommand command, MediaUser user, String roomId) {
                if (command == null || command.sendUser == null || command.sendUser.name == null) {
                    return;
                }
                String msg = buildEnterRoomMsg(command.sendUser.name);
                audioChatManager.addMsg(msg);
            }
        });
    }

    private String buildEnterRoomMsg(String nickName) {
        return nickName + " " + Utils.getApp().getString(R.string.audio_enter_the_room);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioEngineManager.onDestroy();
        audioChatManager.onDestroy();
        audioMicManager.onDestroy();
        audioStreamManager.onDestroy();
        audioGiftManager.onDestroy();
    }

    public void setCallback(AudioRoomServiceCallback callback) {
        audioRoomServiceCallback = callback;
    }

    public AudioRoomServiceCallback getCallback() {
        return audioRoomServiceCallback;
    }

    /**
     * 获取当前房间id
     */
    public long getRoomId() {
        RoomInfoModel model = roomInfoModel;
        if (model == null || model.roomId == null) {
            return 0;
        } else {
            return model.roomId;
        }
    }

    /**
     * 获取房间的角色
     */
    public int getRoleType() {
        RoomInfoModel model = roomInfoModel;
        if (model == null) {
            return 0;
        } else {
            return model.roleType;
        }
    }

    public void enterRoom(RoomInfoModel model) {
        roomInfoModel = model;
        audioEngineManager.enterRoom(model);
        audioMicManager.enterRoom(model);
        AudioRoomServiceCallback callback = getCallback();
        if (callback != null) {
            callback.onEnterRoomSuccess();
        }
        audioChatManager.addMsg(buildEnterRoomMsg(HSUserInfo.nickName));
    }

    /**
     * 设置麦克风开关
     *
     * @param isOpen true为开 false为关
     */
    public void setMicState(boolean isOpen) {
        if (isOpen) {
            String streamId = null;
            AudioRoomMicModel selfMicModel = audioMicManager.findMicModel(HSUserInfo.userId);
            if (selfMicModel != null) {
                streamId = selfMicModel.streamId;
            }
            audioStreamManager.openMic(streamId);
        } else {
            audioStreamManager.closeMic();
        }
    }

    /**
     * 游戏切换
     *
     * @param gameId     游戏id
     * @param selfSwitch 标识是否是自己切换的
     */
    public void switchGame(long gameId, boolean selfSwitch) {
        if (selfSwitch) {
            // 发送http通知后台
            GameRepository.switchGame(null, getRoomId(), gameId, new RxCallback<>());

            // 发送信令通知房间内其他人
            String command = AudioRoomCommandUtils.buildGameChangeCommand(gameId);
            audioEngineManager.sendCommand(command, null);
        }
    }

}
