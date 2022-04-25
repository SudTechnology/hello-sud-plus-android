package tech.sud.mgp.hello.ui.scenes.base.manager;

import java.util.List;

import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.order.RoomCmdOrderOperateModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.order.RoomCmdUserOrderModel;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.model.ReceiveInviteMsgModel;

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

    public void broadcastOrder(long orderId, long gameId, String gameName, List<String> toUsers) {
        String command = RoomCmdModelUtils.buildCmdUserOrder(orderId, gameId, gameName, toUsers);
        parentManager.sceneEngineManager.sendCommand(command, null);
    }

    public void operateOrder(long orderId, long gameId, String gameName, String toUser, boolean state) {
        String command = RoomCmdModelUtils.buildCmdOrderResult(orderId, gameId, gameName, toUser, state);
        parentManager.sceneEngineManager.sendCommand(command, null);
    }

    private final SceneCommandManager.UserOrderCommandListener userOrderCommandListener = new SceneCommandManager.UserOrderCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdUserOrderModel command, String userID) {
            if (command.toUsers != null && command.toUsers.size() > 0) {
                boolean needDispatch = false;
                for (String toUser : command.toUsers) {
                    if (toUser.equals(HSUserInfo.userId + "")) {
                        needDispatch = true;
                    }
                }
                if (needDispatch) {
                    parentManager.getCallback().onOrderInvite(command.orderId, command.gameId, command.gameName, command.sendUser.userID, command.sendUser.name, command.toUsers);
                }
            }
        }
    };

    private final SceneCommandManager.OrderResultCommandListener orderResultCommandListener = new SceneCommandManager.OrderResultCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdOrderOperateModel command, String userID) {
            if (command.toUser.equals(HSUserInfo.userId+"")){
                if (command.operate){
                    ReceiveInviteMsgModel msgModel = new ReceiveInviteMsgModel();
                    msgModel.gameId = command.gameId;
                    msgModel.gameName = command.gameName;
                    msgModel.userId  = command.sendUser.userID;
                    msgModel.userName = command.sendUser.name;
                    parentManager.sceneChatManager.addMsg(msgModel);
                }else {
                    parentManager.getCallback().onOrderOperate(command.orderId, command.gameId, command.gameName, command.sendUser.userID, command.sendUser.name, command.operate);
                }
            }
        }
    };
}
