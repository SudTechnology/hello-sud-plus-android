package tech.sud.mgp.hello.ui.scenes.base.manager;

import java.util.List;

import tech.sud.mgp.hello.ui.main.base.constant.SceneType;
import tech.sud.mgp.hello.ui.scenes.base.model.GiftNotifyModel;
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
    public void sendGift(GiftModel giftModel, int giftCount, List<UserInfo> toUserList, boolean isAllSeat) {
        if (toUserList == null || toUserList.size() == 0) {
            return;
        }

        long giftID = giftModel.giftId;
        int type = giftModel.type;
        String giftName = giftModel.giftName;
        String giftUrl = giftModel.giftUrl;
        String animationUrl = giftModel.animationUrl;
        String extData = giftModel.extData;

        String command = RoomCmdModelUtils.buildSendGiftCommand(giftID, giftCount, toUserList, type, giftName, giftUrl, animationUrl, extData, isAllSeat);

        for (UserInfo userInfo : toUserList) {
            GiftNotifyDetailModel notify = new GiftNotifyDetailModel();
            notify.gift = giftModel;
            notify.sendUser = RoomCmdModelUtils.getSendUser();
            notify.toUser = userInfo;
            notify.giftCount = giftCount;
            notify.giftID = giftID;
            parentManager.sceneChatManager.addMsg(notify);
        }

        if (giftID == GiftId.ROCKET || parentManager.getSceneType() == SceneType.AUDIO_3D || parentManager.getSceneType() == SceneType.CUBE) {
            parentManager.sceneEngineManager.sendXRoomCommand(parentManager.getRoomId() + "", command);
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
            if (command.toUserList == null || command.toUserList.size() == 0) {
                return;
            }
            giftModel.extData = command.extData;

            for (UserInfo userInfo : command.toUserList) {
                GiftNotifyDetailModel notify = new GiftNotifyDetailModel();
                notify.gift = giftModel;
                notify.sendUser = command.sendUser;
                notify.toUser = userInfo;
                notify.giftCount = command.giftCount;
                notify.giftID = command.giftID;
                parentManager.sceneChatManager.addMsg(notify);
            }

            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
                GiftNotifyModel model = new GiftNotifyModel();
                model.gift = giftModel;
                model.sendUser = command.sendUser;
                model.toUserList = command.toUserList;
                model.giftCount = command.giftCount;
                model.giftID = command.giftID;
                model.isAllSeat = command.isAllSeat;
                callback.sendGiftsNotify(model);
            }
        }
    };

}
