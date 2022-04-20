package tech.sud.mgp.hello.ui.scenes.base.manager;

import java.util.List;

import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdOrderOperateModel;
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

    public void broadcastOrder(long orderId, long gameId, String gameName, List<String> toUsers) {
        String command = RoomCmdModelUtils.buildUserOrderCommand(orderId, gameId, gameName, toUsers);
        parentManager.sceneEngineManager.sendCommand(command, null);
    }

    public void operateOrder(long orderId, long gameId, String gameName, String toUser, boolean state) {
        String command = RoomCmdModelUtils.buildOrderResultCommand(orderId, gameId, gameName, toUser, state);
        parentManager.sceneEngineManager.sendCommand(command, null);
    }

    private final SceneCommandManager.UserOrderCommandListener userOrderCommandListener = new SceneCommandManager.UserOrderCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdUserOrderModel command, String userID) {
            parentManager.getCallback().onOrderInvite(command.orderId, command.gameId, command.gameName, command.sendUser.name, command.toUsers);
        }
    };

    private final SceneCommandManager.OrderResultCommandListener orderResultCommandListener = new SceneCommandManager.OrderResultCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdOrderOperateModel command, String userID) {
            parentManager.getCallback().onOrderOperate(command.orderId, command.gameId, command.gameName, command.sendUser.userID, command.sendUser.name, command.operate);
        }
    };
}
