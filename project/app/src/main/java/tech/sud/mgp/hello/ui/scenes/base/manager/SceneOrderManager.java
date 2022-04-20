package tech.sud.mgp.hello.ui.scenes.base.manager;

import java.util.List;

import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdOrderResultModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdUserOrderModel;

/**
 * 点单场景
 */
public class SceneOrderManager extends BaseServiceManager {
    private SceneRoomServiceManager parentManager;

    public SceneOrderManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.sceneEngineManager.setCommandListener(userOrderCommandListener);
        parentManager.sceneEngineManager.setCommandListener(orderResultCommandListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        parentManager.sceneEngineManager.removeCommandListener(userOrderCommandListener);
        parentManager.sceneEngineManager.setCommandListener(orderResultCommandListener);
    }

    public void broadcastOrder(long orderId, long gameId, String gameName, List<Integer> toUsers) {
//        String command = RoomCmdModelUtils.buildSendGiftCommand(giftID, giftCount, toUser);
//
//        GiftModel giftModel = GiftHelper.getInstance().getGift(giftID);
//        GiftNotifyDetailodel notify = new GiftNotifyDetailodel();
//        notify.gift = giftModel;
//
//        UserInfo user = new UserInfo();
//        user.userID = HSUserInfo.userId + "";
//        user.name = HSUserInfo.nickName;
//        user.icon = HSUserInfo.avatar;
//
//        notify.sendUser = user;
//        notify.toUser = toUser;
//        notify.giftCount = giftCount;
//        notify.giftID = giftID;
//
//        parentManager.sceneChatManager.addMsg(notify);
//
//        parentManager.sceneEngineManager.sendCommand(command, null);
    }

    private final SceneCommandManager.UserOrderCommandListener userOrderCommandListener = new SceneCommandManager.UserOrderCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdUserOrderModel command, String userID) {
//            GiftModel giftModel = GiftHelper.getInstance().getGift(command.giftID);
//            GiftNotifyDetailodel notify = new GiftNotifyDetailodel();
//            notify.gift = giftModel;
//            notify.sendUser = command.sendUser;
//            notify.toUser = command.toUser;
//            notify.giftCount = command.giftCount;
//            notify.giftID = command.giftID;
//            parentManager.getCallback().sendGiftsNotify(notify);
//            parentManager.sceneChatManager.addMsg(notify);
        }
    };

    private final SceneCommandManager.OrderResultCommandListener orderResultCommandListener = new SceneCommandManager.OrderResultCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdOrderResultModel command, String userID) {

        }
    };
}
