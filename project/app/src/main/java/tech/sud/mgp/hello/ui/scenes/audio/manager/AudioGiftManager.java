package tech.sud.mgp.hello.ui.scenes.audio.manager;

import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.rtc.audio.core.AudioUser;
import tech.sud.mgp.hello.ui.scenes.audio.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdSendGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftHelper;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftNotifyDetailodel;

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
        String command = RoomCmdModelUtils.buildSendGiftCommand(giftID, giftCount, toUser);

        GiftModel giftModel = GiftHelper.getInstance().getGift(giftID);
        GiftNotifyDetailodel notify = new GiftNotifyDetailodel();
        notify.gift = giftModel;

        UserInfo user = new UserInfo();
        user.userID = HSUserInfo.userId;
        user.name = HSUserInfo.nickName;
        user.icon = HSUserInfo.avatar;

        notify.sendUser = user;
        notify.toUser = toUser;
        notify.giftCount = giftCount;
        notify.giftID = giftID;

        parentManager.audioChatManager.addMsg(notify);

        parentManager.audioEngineManager.sendCommand(command, null);
    }

    private final AudioCommandManager.SendGiftCommandListener sendGiftCommandListener = new AudioCommandManager.SendGiftCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdSendGiftModel command, AudioUser user) {
            GiftModel giftModel = GiftHelper.getInstance().getGift(command.giftID);
            GiftNotifyDetailodel notify = new GiftNotifyDetailodel();
            notify.gift = giftModel;
            notify.sendUser = command.sendUser;
            notify.toUser = command.toUser;
            notify.giftCount = command.giftCount;
            notify.giftID = command.giftID;
            parentManager.getCallback().sendGiftsNotify(notify);
            parentManager.audioChatManager.addMsg(notify);
        }
    };
}
