package tech.sud.mgp.hello.ui.scenes.base.manager;

import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdSendGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftHelper;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftId;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftNotifyDetailModel;

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

    /** 发送礼物 */
    public void sendGift(GiftModel giftModel, int giftCount, UserInfo toUser) {
        long giftID = giftModel.giftId;
        int type = giftModel.type;
        String giftName = giftModel.giftName;
        String giftUrl = giftModel.giftUrl;
        String animationUrl = giftModel.animationUrl;
        String extData = giftModel.extData;

        String command = RoomCmdModelUtils.buildSendGiftCommand(giftID, giftCount, toUser, type, giftName, giftUrl, animationUrl, extData);

        GiftNotifyDetailModel notify = new GiftNotifyDetailModel();
        notify.gift = giftModel;

        UserInfo user = new UserInfo();
        user.userID = HSUserInfo.userId + "";
        user.name = HSUserInfo.nickName;
        user.icon = HSUserInfo.getUseAvatar();

        notify.sendUser = user;
        notify.toUser = toUser;
        notify.giftCount = giftCount;
        notify.giftID = giftID;

        parentManager.sceneChatManager.addMsg(notify);

        if (giftID == GiftId.ROCKET) {
            parentManager.sceneEngineManager.sendXRoomCommand(parentManager.getRoomId() + "", command, null);
        } else {
            parentManager.sceneEngineManager.sendCommand(command, null);
        }
    }

    /** 收到送礼消息 */
    private final SceneCommandManager.SendGiftCommandListener sendGiftCommandListener = new SceneCommandManager.SendGiftCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdSendGiftModel command, String userID) {
            GiftModel giftModel;
            if (command.type == 0) {
                giftModel = GiftHelper.getInstance().getGift(command.giftID);
            } else {
                giftModel = new GiftModel();
                giftModel.type = command.type;
                giftModel.giftName = command.giftName;
                giftModel.giftUrl = command.giftUrl;
                giftModel.animationUrl = command.animationUrl;
            }
            if (giftModel == null) {
                return;
            }
            giftModel.extData = command.extData;
            GiftNotifyDetailModel notify = new GiftNotifyDetailModel();
            notify.gift = giftModel;
            notify.sendUser = command.sendUser;
            notify.toUser = command.toUser;
            notify.giftCount = command.giftCount;
            notify.giftID = command.giftID;
            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
                callback.sendGiftsNotify(notify);
            }
            parentManager.sceneChatManager.addMsg(notify);
        }
    };
}
