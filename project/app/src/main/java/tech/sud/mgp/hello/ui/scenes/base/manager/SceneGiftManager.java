package tech.sud.mgp.hello.ui.scenes.base.manager;

import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdSendGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftHelper;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftNotifyDetailodel;

/**
 * 房间礼物
 */
public class SceneGiftManager extends BaseServiceManager {
    private SceneRoomServiceManager parentManager;

    public SceneGiftManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.sceneEngineManager.setCommandListener(sendGiftCommandListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        parentManager.sceneEngineManager.removeCommandListener(sendGiftCommandListener);
    }

    public void sendGift(int giftID,
                         int giftCount,
                         UserInfo toUser) {
        String command = RoomCmdModelUtils.buildSendGiftCommand(giftID, giftCount, toUser);

        GiftModel giftModel = GiftHelper.getInstance().getGift(giftID);
        GiftNotifyDetailodel notify = new GiftNotifyDetailodel();
        notify.gift = giftModel;

        UserInfo user = new UserInfo();
        user.userID = HSUserInfo.userId + "";
        user.name = HSUserInfo.nickName;
        user.icon = HSUserInfo.avatar;

        notify.sendUser = user;
        notify.toUser = toUser;
        notify.giftCount = giftCount;
        notify.giftID = giftID;

        parentManager.sceneChatManager.addMsg(notify);

        parentManager.sceneEngineManager.sendCommand(command, null);
    }

    private final SceneCommandManager.SendGiftCommandListener sendGiftCommandListener = new SceneCommandManager.SendGiftCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdSendGiftModel command, String userID) {
            GiftModel giftModel = GiftHelper.getInstance().getGift(command.giftID);
            GiftNotifyDetailodel notify = new GiftNotifyDetailodel();
            notify.gift = giftModel;
            notify.sendUser = command.sendUser;
            notify.toUser = command.toUser;
            notify.giftCount = command.giftCount;
            notify.giftID = command.giftID;
            parentManager.getCallback().sendGiftsNotify(notify);
            parentManager.sceneChatManager.addMsg(notify);
        }
    };
}
