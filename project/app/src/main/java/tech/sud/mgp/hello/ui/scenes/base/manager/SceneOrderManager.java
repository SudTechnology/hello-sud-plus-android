package tech.sud.mgp.hello.ui.scenes.base.manager;

import android.app.Activity;

import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.ui.common.utils.DialogUtils;
import tech.sud.mgp.hello.ui.main.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.model.OrderInviteModel;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.order.RoomCmdOrderOperateModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.order.RoomCmdUserOrderModel;

/**
 * 点单场景
 */
public class SceneOrderManager extends BaseServiceManager {

    private SimpleChooseDialog inviteDialog;//邀请弹窗
    private SimpleChooseDialog operateDialog;//拒绝弹窗
    private final SceneRoomServiceManager parentManager;
    private OrderInviteModel orderModel;
    private int receiveSate = 0; // 1已成功接受点单 2接受点单不成功

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
        parentManager.sceneEngineManager.removeCommandListener(orderResultCommandListener);
    }

    public void broadcastOrder(long orderId, long gameId, String gameName, List<String> toUsers) {
        String command = RoomCmdModelUtils.buildCmdUserOrder(orderId, gameId, gameName, toUsers);
        parentManager.sceneEngineManager.sendCommand(command, null);
    }

    public void operateOrder(long orderId, long gameId, String gameName, String toUser, boolean isAccept) {
        String command = RoomCmdModelUtils.buildCmdOrderResult(orderId, gameId, gameName, toUser, isAccept);
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
                    inviteDialog(command.orderId, command.gameId, command.gameName, command.sendUser.userID, command.sendUser.name, command.toUsers);
                }
            }
        }
    };

    private final SceneCommandManager.OrderResultCommandListener orderResultCommandListener = new SceneCommandManager.OrderResultCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdOrderOperateModel command, String userID) {
            if (command.toUser.equals(HSUserInfo.userId + "")) {
                if (command.operate) {
                    String msg = Utils.getApp().getString(R.string.user_receive_invite_msg, command.sendUser.name, command.gameName);
                    parentManager.sceneChatManager.addMsg(msg);
                }
                SceneRoomServiceCallback callback = parentManager.getCallback();
                if (callback != null) {
                    callback.onOrderOperate(command.orderId, command.gameId, command.gameName, command.sendUser.userID, command.sendUser.name, command.operate);
                    if (!command.operate) {
                        operateDialog(command.sendUser.name);
                    }
                }
            }
        }
    };

    /** 邀请弹窗 */
    public void inviteDialog(long orderId, long gameId, String gameName,
                             String userID, String nickname, List<String> toUsers) {
        Activity activity = ActivityUtils.getTopActivity();
        if (activity == null || activity.isDestroyed()) return;
        if (activity instanceof LifecycleOwner) {
            if (inviteDialog == null || !inviteDialog.isShowing()) {
                OrderInviteModel orderModel = new OrderInviteModel();
                orderModel.orderId = orderId;
                orderModel.gameId = gameId;
                orderModel.gameName = gameName;
                orderModel.sendUserId = userID;
                orderModel.sendUserName = nickname;
                orderModel.toUsers = toUsers;
                inviteDialog = new SimpleChooseDialog(
                        activity,
                        activity.getString(R.string.order_invite_conent, nickname, gameName),
                        activity.getString(R.string.order_invite_left_btn),
                        activity.getString(R.string.order_invite_right_btn));
                inviteDialog.setOnChooseListener(index -> {
                    if (index == 1) {
                        orderModel.agreeState = 1;
                        SceneRoomServiceCallback callback = parentManager.getCallback();
                        if (callback != null) {
                            callback.onOrderInvite(orderModel);
                        }
                        //用户接受了邀请点单
                        roomOrderReceive(orderId, orderModel);
                    } else {
                        orderModel.agreeState = 2;
                        SceneRoomServiceCallback callback = parentManager.getCallback();
                        if (callback != null) {
                            callback.onOrderInvite(orderModel);
                        }
                        operateOrder(orderId, gameId, gameName, orderModel.sendUserId, false);
                    }
                    inviteDialog.dismiss();
                    inviteDialog = null;
                });
                DialogUtils.safeShowDialog((LifecycleOwner) activity, inviteDialog);
                this.orderModel = orderModel;
            }
        }
    }

    /** 被拒绝弹窗 */
    public void operateDialog(String nickName) {
        Activity activity = ActivityUtils.getTopActivity();
        if (activity == null) return;
        if (activity instanceof LifecycleOwner) {
            if (operateDialog == null || !operateDialog.isShowing()) {
                operateDialog = new SimpleChooseDialog(
                        activity,
                        activity.getString(R.string.order_result_conent, nickName),
                        activity.getString(R.string.cancel),
                        activity.getString(R.string.confirm));
                operateDialog.setOnChooseListener(index -> {
                    operateDialog.dismiss();
                    operateDialog = null;
                });
                DialogUtils.safeShowDialog((LifecycleOwner) activity, operateDialog);
            }
        }
    }

    public void roomOrderReceive(long orderId, OrderInviteModel orderModel) {
        RoomRepository.roomOrderReceive(parentManager, orderId, new RxCallback<Object>() {
            @Override
            public void onNext(BaseResponse<Object> t) {
                super.onNext(t);
                SceneRoomServiceCallback callback = parentManager.getCallback();
                if (t.getRetCode() == RetCode.SUCCESS) {
                    receiveSate = 1;
                    parentManager.startRoomActivity();
                    
                    // 发送信令
                    operateOrder(orderModel.orderId, orderModel.gameId, orderModel.gameName, orderModel.sendUserId, true);

                    if (callback == null) {
                        long gameId = orderModel.gameId;
                        // 发送http协议，通知后端
                        GameRepository.switchGame(parentManager, parentManager.getRoomId(), GameIdCons.NONE, new RxCallback<Object>() {
                            @Override
                            public void onSuccess(Object o) {
                                super.onSuccess(o);
                                // 发送切换游戏信令
                                parentManager.switchGame(gameId);
                                parentManager.callbackOnGameChange(gameId);
                            }
                        });
                    } else {
                        callback.onReceiveInvite(t.getRetCode() == RetCode.SUCCESS);
                    }
                } else {
                    receiveSate = 2;
                }
            }
        });
    }

    public void callbackPageData() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            if (orderModel != null) {
                callback.onOrderInvite(orderModel);
            }
            if (receiveSate != 0) {
                callback.onReceiveInvite(receiveSate == 1);
            }
        }
    }

}
