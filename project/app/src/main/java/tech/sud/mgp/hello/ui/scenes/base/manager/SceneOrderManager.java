package tech.sud.mgp.hello.ui.scenes.base.manager;

import android.app.Activity;
import android.content.Context;

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
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.ui.common.utils.DialogUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.OrderInviteModel;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.order.RoomCmdOrderOperateModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.order.RoomCmdUserOrderModel;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.viewmodel.OrderViewModel;

/**
 * 点单场景
 */
public class SceneOrderManager extends BaseServiceManager {

    private SimpleChooseDialog inviteDialog;//邀请弹窗
    private SimpleChooseDialog operateDialog;//拒绝弹窗
    private final SceneRoomServiceManager parentManager;

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

    public void operateOrder(long orderId, long gameId, String gameName, String toUser, boolean state) {
        String command = RoomCmdModelUtils.buildCmdOrderResult(orderId, gameId, gameName, toUser, state);
        parentManager.sceneEngineManager.sendCommand(command, null);

        if (state) {
            //接受邀请后，发送切换游戏信令
            parentManager.switchGame(gameId);
        }
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
        if (activity == null) return;
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
                        parentManager.startRoomActivity();
                        //用户接受了邀请点单
                        roomOrderReceive((LifecycleOwner) activity, orderId);
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

    public void roomOrderReceive(LifecycleOwner owner, long orderId) {
        RoomRepository.roomOrderReceive(owner, orderId, new RxCallback<Object>() {
            @Override
            public void onNext(BaseResponse<Object> t) {
                super.onNext(t);
                SceneRoomServiceCallback callback = parentManager.getCallback();
                if (callback != null) {
                    if (t.getRetCode() == RetCode.SUCCESS) {
                        callback.onReceiveInvite(true);
                    } else {
                        callback.onReceiveInvite(false);
                    }
                }
            }
        });
    }

}
