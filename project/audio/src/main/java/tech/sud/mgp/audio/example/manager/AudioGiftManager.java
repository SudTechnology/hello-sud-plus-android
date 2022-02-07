package tech.sud.mgp.audio.example.manager;

import tech.sud.mgp.audio.example.model.UserInfo;
import tech.sud.mgp.audio.example.model.command.SendGiftCommand;
import tech.sud.mgp.audio.example.utils.AudioRoomCommandUtils;
import tech.sud.mgp.audio.middle.MediaUser;

/**
 * 房间礼物
 */
public class AudioGiftManager extends BaseServiceManager {
    private AudioRoomServiceManager parentManager;

    public AudioGiftManager(AudioRoomServiceManager audioRoomServiceManager) {
        super();
        this.parentManager = audioRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.audioEngineManager.setCommandListener(sendGiftCommandListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        parentManager.audioEngineManager.removeCommandListener(sendGiftCommandListener);
    }

    public void sendGift(int giftID,
                         int giftCount,
                         UserInfo toUser) {
        String command = AudioRoomCommandUtils.buildSendGiftCommand(giftID, giftCount,toUser);
        parentManager.audioEngineManager.sendCommand(command, null);
    }

    private final AudioCommandManager.SendGiftCommandListener sendGiftCommandListener = new AudioCommandManager.SendGiftCommandListener() {
        @Override
        public void onRecvCommand(SendGiftCommand command, MediaUser user, String roomId) {
            parentManager.getCallback().sendGiftsNotify(command.giftID,command.giftCount);
        }
    };
}
