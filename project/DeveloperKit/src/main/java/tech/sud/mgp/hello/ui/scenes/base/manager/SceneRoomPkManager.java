package tech.sud.mgp.hello.ui.scenes.base.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import java.util.concurrent.Future;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.CustomCountdownTimer;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.room.model.PkStatus;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.EnterRoomResp;
import tech.sud.mgp.hello.service.room.resp.RoomPkAgainResp;
import tech.sud.mgp.hello.service.room.resp.RoomPkAgreeResp;
import tech.sud.mgp.hello.service.room.resp.RoomPkModel;
import tech.sud.mgp.hello.service.room.resp.RoomPkRoomInfo;
import tech.sud.mgp.hello.service.room.resp.RoomPkStartResp;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.main.constant.GameIdCons;
import tech.sud.mgp.hello.ui.main.home.model.RoomItemModel;
import tech.sud.mgp.hello.ui.scenes.base.activity.RoomConfig;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKAnswerCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKChangeGameCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKFinishCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKOpenMatchCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKRemoveRivalCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKRivalExitCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKSendInviteCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKSettingsCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKSettleCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKStartCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.model.GameTextModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKAnswerModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKChangeGameModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKFinishModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKOpenMatchModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKRemoveRivalModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKRivalExitModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKSendInviteModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKSettingsModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKSettleModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKStartModel;

/**
 * 跨房pk相关业务
 */
public class SceneRoomPkManager extends BaseServiceManager {

    private final SceneRoomServiceManager parentManager;
    private RoomPkModel roomPkModel;
    private SimpleChooseDialog inviteAnswerDialog;
    private Future<Object> showInviteDialogFuture;
    private CustomCountdownTimer countdownTimer;

    public SceneRoomPkManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.sceneEngineManager.setCommandListener(inviteCommandListener);
        parentManager.sceneEngineManager.setCommandListener(answerCommandListener);
        parentManager.sceneEngineManager.setCommandListener(startCommandListener);
        parentManager.sceneEngineManager.setCommandListener(finishCommandListener);
        parentManager.sceneEngineManager.setCommandListener(settingsCommandListener);
        parentManager.sceneEngineManager.setCommandListener(openMatchCommandListener);
        parentManager.sceneEngineManager.setCommandListener(pkChangeGameCommandListener);
        parentManager.sceneEngineManager.setCommandListener(pkRemoveRivalCommandListener);
        parentManager.sceneEngineManager.setCommandListener(pkSettleCommandListener);
        parentManager.sceneEngineManager.setCommandListener(pkRivalExitCommandListener);
    }

    public void enterRoom(RoomConfig config, RoomInfoModel model) {
        roomPkModel = model.roomPkModel;
        checkCountdown();
    }

    public void callbackPageData() {
        callbackUpdateRoomPk();
    }

    public void initRoomPkModel() {
        if (roomPkModel == null) {
            roomPkModel = new RoomPkModel();
            setSelfSrcRoomInfo();
            RoomInfoModel roomInfoModel = parentManager.getRoomInfoModel();
            if (roomInfoModel != null) {
                roomInfoModel.roomPkModel = roomPkModel;
            }
        }
    }

    /** 设置自己为发起方 */
    private void setSelfSrcRoomInfo() {
        if (roomPkModel == null) return;
        RoomPkRoomInfo roomPkRoomInfo = new RoomPkRoomInfo();
        roomPkRoomInfo.roomId = parentManager.getRoomId();
        roomPkRoomInfo.score = 0;
        if (parentManager.getRoleType() == RoleType.OWNER) {
            roomPkRoomInfo.roomOwnerHeader = HSUserInfo.avatar;
            roomPkRoomInfo.roomOwnerNickname = HSUserInfo.nickName;
        }
        roomPkRoomInfo.isInitiator = true;
        roomPkRoomInfo.isSelfRoom = true;
        roomPkModel.srcRoomInfo = roomPkRoomInfo;
    }

    /** 跨房pk，开启匹配或者关闭Pk了 */
    public void roomPkSwitch(boolean pkSwitch) {
        // 发送http请求
        RoomRepository.roomPkSwitch(parentManager, parentManager.getRoomId(), pkSwitch, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                initRoomPkModel();
                if (pkSwitch) { // 开启匹配
                    if (roomPkModel.pkStatus == PkStatus.MATCH_CLOSED) {
                        setSelfSrcRoomInfo();
                        sendCommand(RoomCmdModelUtils.buildCmdPkOpenMatch());
                        setPkStatus(PkStatus.MATCHING);
                        callbackUpdateRoomPk();
                    }
                } else { // 关闭跨房pk
                    String command = RoomCmdModelUtils.buildCmdPkFinish();
                    sendCommand(command);
                    RoomPkRoomInfo pkRival = getPkRival();
                    if (pkRival != null) {
                        sendXRoomCommand(pkRival.roomId + "", command);
                    }

                    // 更新本地状态
                    setPkStatusClosed();

                    // 发送http协议，通知后端关闭游戏
                    GameRepository.switchGame(parentManager, parentManager.getRoomId(), GameIdCons.NONE, new RxCallback<>());

                    // 发送信令
                    parentManager.switchGame(GameIdCons.NONE);
                }
            }
        });
    }

    /** 设置pk状态 */
    private void setPkStatus(int pkStatus) {
        roomPkModel.pkStatus = pkStatus;
        checkCountdown();
    }

    private void checkCountdown() {
        if (getPkStatus() == PkStatus.STARTED && roomPkModel != null) {
            startCountdown(roomPkModel.remainSecond);
        } else {
            cancelCountdown();
        }
    }

    /** 更新本地的pk状态为关闭了跨房pk */
    private void setPkStatusClosed() {
        roomPkModel.clear();
        checkCountdown();
        callbackUpdateRoomPk();
        parentManager.callbackOnGameChange(GameIdCons.NONE);
    }

    /** 跨房pk，开始 */
    public void roomPkStart(int minute) {
        if (roomPkModel == null) return;
        RoomPkRoomInfo pkRival = getPkRival();
        if (pkRival == null) return;

        long pkRivalRoomId = pkRival.roomId;
        // 发送http请求，已匹配跟已结束，发送不一样的接口
        if (getPkStatus() == PkStatus.MATCHED) {
            RoomRepository.roomPkStart(parentManager, parentManager.getRoomId(), minute,
                    new RxCallback<RoomPkStartResp>() {
                        @Override
                        public void onSuccess(RoomPkStartResp resp) {
                            super.onSuccess(resp);
                            if (resp == null) return;
                            onStartSuccess(resp.pkId, minute, pkRivalRoomId);
                        }
                    }
            );
        } else if (getPkStatus() == PkStatus.PK_END) {
            RoomRepository.roomPkAgain(parentManager, parentManager.getRoomId(), minute,
                    new RxCallback<RoomPkAgainResp>() {
                        @Override
                        public void onSuccess(RoomPkAgainResp resp) {
                            super.onSuccess(resp);
                            if (resp == null) return;
                            onStartSuccess(resp.pkId, minute, pkRivalRoomId);
                        }
                    }
            );
        }
    }

    private void onStartSuccess(long pkId, int minute, long pkRivalRoomId) {
        if ((getPkStatus() != PkStatus.MATCHED && getPkStatus() != PkStatus.PK_END)
                || roomPkModel.getPkRival() == null
                || roomPkModel.getPkRival().roomId != pkRivalRoomId) {
            return;
        }

        roomPkModel.pkId = pkId;
        roomPkModel.totalMinute = minute;
        // 发送信令
        String command = RoomCmdModelUtils.buildCmdPkStart(roomPkModel.totalMinute, roomPkModel.pkId + "");
        sendCommand(command);
        sendXRoomCommand(pkRivalRoomId + "", command);

        // 本地开始
        roomPkModel.remainSecond = roomPkModel.totalMinute * 60;
        setPkStatus(PkStatus.STARTED);
        clearScore();
        callbackUpdateRoomPk();
    }

    /** 清除双方分数 */
    private void clearScore() {
        if (roomPkModel != null) {
            if (roomPkModel.srcRoomInfo != null) {
                roomPkModel.srcRoomInfo.score = 0;
            }
            if (roomPkModel.destRoomInfo != null) {
                roomPkModel.destRoomInfo.score = 0;
            }
        }
    }

    /**
     * 跨房pk，重新设置时长
     *
     * @param minute 分钟数
     */
    public void roomPkSettings(int minute) {
        if (roomPkModel == null || getPkStatus() != PkStatus.STARTED) return;
        RoomPkRoomInfo pkRival = getPkRival();
        if (pkRival == null) return;

        // 发送http请求
        RoomRepository.roomPkDuration(parentManager, parentManager.getRoomId(), minute,
                new RxCallback<Object>() {
                    @Override
                    public void onSuccess(Object resp) {
                        super.onSuccess(resp);
                        if (roomPkModel == null || roomPkModel.pkStatus != PkStatus.STARTED) return;

                        roomPkModel.totalMinute = minute;

                        // 发送信令
                        String command = RoomCmdModelUtils.buildCmdPkSettings(minute);
                        sendCommand(command);
                        sendXRoomCommand(pkRival.roomId + "", command);

                        // 本地数据刷新
                        roomPkModel.remainSecond = minute * 60;
                        checkCountdown();
                        callbackUpdateRoomPk();
                    }
                }
        );
    }

    /** 移除pk对手 */
    public void removePkRival() {
        // pk正在进行中，不可以移除
        int pkStatus = roomPkModel.pkStatus;
        if (pkStatus == PkStatus.STARTED) {
            ToastUtils.showShort(R.string.pk_running_remove_warn);
            return;
        }
        RoomPkRoomInfo pkRival = roomPkModel.getPkRival();
        if (pkRival == null) return;

        // 发送http请求
        RoomRepository.roomPkRemoveRival(parentManager, parentManager.getRoomId(), new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);

                // 发送信令
                String command = RoomCmdModelUtils.buildCmdPkRemoveRival();
                sendCommand(command);
                sendXRoomCommand(pkRival.roomId + "", command);

                // 本地移除
                localRemovePkRival();
            }
        });
    }

    /** 刷新房间pk信息 */
    public void refreshRoomPkInfo() {
        long roomId = parentManager.getRoomId();
        RoomRepository.enterRoom(parentManager, roomId, new RxCallback<EnterRoomResp>() {
            @Override
            public void onSuccess(EnterRoomResp resp) {
                super.onSuccess(resp);
                if (resp != null && resp.pkResultVO != null) {
                    resp.pkResultVO.initInfo(roomId);
                    RoomInfoModel roomInfoModel = parentManager.getRoomInfoModel();
                    if (roomInfoModel != null) {
                        roomInfoModel.roomPkModel = resp.pkResultVO;
                    }
                    roomPkModel = resp.pkResultVO;
                    checkCountdown();
                    callbackUpdateRoomPk();
                }
            }
        });
    }

    /** 跨房pk，发送邀请 */
    public void roomPkInvite(RoomItemModel model) {
        if (model == null || getPkStatus() != PkStatus.MATCHING) return;
        initRoomPkModel();
        sendXRoomCommand(model.getRoomId() + "", RoomCmdModelUtils.buildCmdPkSendInvite(roomPkModel.totalMinute));
        ToastUtils.showShort(R.string.room_invitation_send);
    }

    public void sendCommand(String command) {
        parentManager.sceneEngineManager.sendCommand(command);
    }

    private void sendXRoomCommand(String roomId, String command) {
        parentManager.sceneEngineManager.sendXRoomCommand(roomId, command);
    }

    /** 跨房pk，应答 */
    public void roomPkAnswer(RoomCmdPKSendInviteModel model, boolean isAccept) {
        if (model == null || model.sendUser == null) return;
        if (isAccept) {
            if (getPkStatus() != PkStatus.MATCHING) {
                return;
            }
            // 发送http请求
            RoomRepository.roomPkAgree(parentManager, model.sendUser.getRoomId(), parentManager.getRoomId(),
                    new RxCallback<RoomPkAgreeResp>() {
                        @Override
                        public void onSuccess(RoomPkAgreeResp resp) {
                            super.onSuccess(resp);
                            if (getPkStatus() != PkStatus.MATCHING) {
                                return;
                            }

                            // 发送信令
                            String command = RoomCmdModelUtils.buildCmdPkAnswer(model.sendUser, isAccept, resp.pkId + "");
                            sendCommand(command);
                            sendXRoomCommand(model.sendUser.roomID, command);

                            // 修改本地状态，更新页面显示
                            acceptRoomPk(SudJsonUtils.fromJson(command, RoomCmdPKAnswerModel.class));
                            roomPkModel.totalMinute = model.minuteDuration;
                        }
                    }
            );
        } else {
            // 发送信令
            String command = RoomCmdModelUtils.buildCmdPkAnswer(model.sendUser, isAccept, null);
            sendXRoomCommand(model.sendUser.roomID, command);
        }
    }

    public int getPkStatus() {
        if (roomPkModel != null) {
            return roomPkModel.pkStatus;
        }
        return PkStatus.UNDEFINED;
    }

    private void callbackUpdateRoomPk() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onRoomPkUpdate();
        }
    }

    private void callbackUpdateCountdown() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onRoomPkCoutndown();
        }
    }

    /** 同意pk之后的处理 */
    private void acceptRoomPk(RoomCmdPKAnswerModel model) {
        if (model == null || model.sendUser == null || model.otherUser == null || !model.isAccept) return;
        if (getPkStatus() != PkStatus.MATCHING) {
            return;
        }
        initRoomPkModel();
        roomPkModel.setPkId(model.pkId);
        setPkStatus(PkStatus.MATCHED);

        roomPkModel.srcRoomInfo = new RoomPkRoomInfo();
        roomPkModel.srcRoomInfo.setRoomId(model.otherUser.roomID);
        roomPkModel.srcRoomInfo.roomOwnerHeader = model.otherUser.icon;
        roomPkModel.srcRoomInfo.roomOwnerNickname = model.otherUser.name;
        roomPkModel.srcRoomInfo.isInitiator = true;

        roomPkModel.destRoomInfo = new RoomPkRoomInfo();
        roomPkModel.destRoomInfo.setRoomId(model.sendUser.roomID);
        roomPkModel.destRoomInfo.roomOwnerHeader = model.sendUser.icon;
        roomPkModel.destRoomInfo.roomOwnerNickname = model.sendUser.name;
        roomPkModel.destRoomInfo.isInitiator = false;

        if ((parentManager.getRoomId() + "").equals(model.sendUser.roomID)) {// 本房间现在是被邀请者
            roomPkModel.srcRoomInfo.isSelfRoom = false;
            roomPkModel.destRoomInfo.isSelfRoom = true;
        } else { // 本房间是邀请者
            roomPkModel.srcRoomInfo.isSelfRoom = true;
            roomPkModel.destRoomInfo.isSelfRoom = false;
            syncGame();
        }

        callbackUpdateRoomPk();
    }

    /** 同步自己的游戏给对方房间 */
    public void syncGame() {
        if (parentManager.getRoleType() == RoleType.OWNER) {
            RoomPkRoomInfo pkRival = getPkRival();
            if (pkRival != null) {
                long gameId = parentManager.getRoomInfoModel().gameId;
                sendXRoomCommand(pkRival.roomId + "", RoomCmdModelUtils.buildCmdPkChangeGame(gameId));
            }
        }
    }

    /** 获取pk对手 */
    private RoomPkRoomInfo getPkRival() {
        if (roomPkModel != null) {
            return roomPkModel.getPkRival();
        }
        return null;
    }

    /** 移除pk对手 */
    private void localRemovePkRival() {
        if (roomPkModel != null) {
            roomPkModel.removePkRival();
            setPkStatus(PkStatus.MATCHING);
            callbackUpdateRoomPk();
            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
                callback.onRoomPkRemoveRival();
            }
        }
    }

    /** 是否是发起方 */
    private boolean isSrcRoom(long roomId) {
        if (roomPkModel != null && roomPkModel.srcRoomInfo != null) {
            return roomPkModel.srcRoomInfo.roomId == roomId;
        }
        return false;
    }

    /** 是否是被邀请方 */
    private boolean isDestRoom(long roomId) {
        if (roomPkModel != null && roomPkModel.destRoomInfo != null) {
            return roomPkModel.destRoomInfo.roomId == roomId;
        }
        return false;
    }

    /**
     * 处理跨房pk邀请
     *
     * @param model 邀请信息
     */
    private void processInvite(RoomCmdPKSendInviteModel model) {
        if (getPkStatus() != PkStatus.MATCHING) return;
        if (parentManager.getRoleType() != RoleType.OWNER) return;
        if (showInviteDialogFuture != null && !showInviteDialogFuture.isDone()) return;
        // 房主才处理
        Activity topActivity = ActivityUtils.getTopActivity();
        if (topActivity instanceof LifecycleOwner) {
            LifecycleOwner owner = (LifecycleOwner) topActivity;
            showInviteDialogFuture = LifecycleUtils.safeLifecycle(owner, 8000, new CompletedListener() {
                @Override
                public void onCompleted() {
                    if (getPkStatus() != PkStatus.MATCHING) return;
                    showInviteAnswerDialog(topActivity, model);
                }
            });
        }
    }

    /** 展示邀请应答弹窗 */
    private void showInviteAnswerDialog(Context context, RoomCmdPKSendInviteModel model) {
        if (inviteAnswerDialog != null && inviteAnswerDialog.isShowing()) return;
        // 显示dialog
        SimpleChooseDialog dialog = new SimpleChooseDialog(context, context.getString(R.string.invite_pk_answer_title, model.sendUser.name)
                , "", context.getString(R.string.accept));
        dialog.setCustomCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        // 倒计时
        CustomCountdownTimer countdownTimer = new CustomCountdownTimer(8) {
            @Override
            protected void onTick(int count) {
                if (dialog.isShowing()) {
                    String text = context.getString(R.string.refuse) + "(" + count + "s)";
                    dialog.setLeftText(text);
                }
            }

            @Override
            protected void onFinish() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };
        countdownTimer.start();
        // 监听
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 0) { // 拒绝
                    roomPkAnswer(model, false);
                } else if (index == 1) { // 接受
                    parentManager.startRoomActivity();
                    roomPkAnswer(model, true);
                }
                dialog.dismiss();
            }
        });
        inviteAnswerDialog = dialog;
        inviteAnswerDialog.setOnDestroyListener(() -> {
            inviteAnswerDialog = null;
            countdownTimer.cancel();
        });
    }

    /** 添加一条对方结束pk的公屏消息 */
    private void addInterceptChatMsg() {
        String msg = Utils.getApp().getString(R.string.intercept_room_pk);
        parentManager.sceneChatManager.addMsg(msg);
    }

    /** 取消时长倒计时 */
    private void cancelCountdown() {
        if (countdownTimer == null) return;
        countdownTimer.cancel();
        countdownTimer = null;
    }

    /** 开启倒计时 */
    private void startCountdown(int count) {
        cancelCountdown();
        countdownTimer = new CustomCountdownTimer(count) {
            @Override
            protected void onTick(int count) {
                if (getPkStatus() == PkStatus.STARTED) {
                    roomPkModel.remainSecond = count;
                    callbackUpdateCountdown();
                }
            }

            @Override
            protected void onFinish() {
                if (roomPkModel == null || roomPkModel.pkStatus != PkStatus.STARTED) return;
                setPkStatus(PkStatus.PK_END);
                callbackUpdateRoomPk();
            }
        };
        countdownTimer.start();
    }

    // region 信令监听
    /** 跨房PK邀请 监听 */
    private final PKSendInviteCommandListener inviteCommandListener = new PKSendInviteCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKSendInviteModel model, String userID) {
            processInvite(model);
        }
    };

    /** 跨房PK邀请应答 监听 */
    private final PKAnswerCommandListener answerCommandListener = new PKAnswerCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKAnswerModel model, String userID) {
            if (model.isAccept) {
                acceptRoomPk(model);
            }
        }
    };

    /** 开始跨房PK 监听 */
    private final PKStartCommandListener startCommandListener = new PKStartCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKStartModel model, String userID) {
            roomPkModel.setPkId(model.pkId);
            roomPkModel.totalMinute = model.minuteDuration;
            roomPkModel.remainSecond = model.minuteDuration * 60;
            clearScore();
            setPkStatus(PkStatus.STARTED);
            callbackUpdateRoomPk();
        }
    };

    /** 结束跨房PK 监听 */
    private final PKFinishCommandListener finishCommandListener = new PKFinishCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKFinishModel model, String userID) {
            if (model.sendUser == null || roomPkModel == null) return;
            String fromRoomId = model.sendUser.roomID;
            if ((parentManager.getRoomId() + "").equals(fromRoomId)) { // 自己的房间结束了跨房pk
                setPkStatusClosed();
            } else { // 对方房间结束跨房pk
                RoomPkRoomInfo pkRival = getPkRival();
                if (pkRival != null && (pkRival.roomId + "").equals(fromRoomId)) {
                    // 对方房间关闭了跨房pk，回到待匹配状态
                    localRemovePkRival();
                }
                addInterceptChatMsg();

                if (parentManager.getRoleType() == RoleType.OWNER) {
                    // 发送http协议，通知后端关闭游戏
                    GameRepository.switchGame(parentManager, parentManager.getRoomId(), GameIdCons.NONE, new RxCallback<>());
                    parentManager.switchGame(GameIdCons.NONE);
                    parentManager.callbackOnGameChange(GameIdCons.NONE);
                }
            }
        }
    };

    /** 跨房PK设置 监听 */
    private final PKSettingsCommandListener settingsCommandListener = new PKSettingsCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKSettingsModel model, String userID) {
            if (roomPkModel == null || getPkStatus() != PkStatus.STARTED) return;
            roomPkModel.totalMinute = model.minuteDuration;
            roomPkModel.remainSecond = model.minuteDuration * 60;
            checkCountdown();
            callbackUpdateRoomPk();
        }
    };

    /** 开启匹配跨房PK 监听 */
    private final PKOpenMatchCommandListener openMatchCommandListener = new PKOpenMatchCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKOpenMatchModel model, String userID) {
            initRoomPkModel();
            setSelfSrcRoomInfo();
            roomPkModel.srcRoomInfo.roomOwnerNickname = model.sendUser.name;
            roomPkModel.srcRoomInfo.roomOwnerHeader = model.sendUser.icon;
            roomPkModel.srcRoomInfo.roomId = model.sendUser.getRoomId();
            roomPkModel.srcRoomInfo.score = 0;
            roomPkModel.srcRoomInfo.isInitiator = true;
            roomPkModel.srcRoomInfo.isSelfRoom = parentManager.getRoomId() == roomPkModel.srcRoomInfo.roomId;
            setPkStatus(PkStatus.MATCHING);
            callbackUpdateRoomPk();
        }
    };

    /** 跨房PK，切换游戏 监听 */
    private final PKChangeGameCommandListener pkChangeGameCommandListener = new PKChangeGameCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKChangeGameModel model, String userID) {
            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback == null) {
                // 没有回调的情况下，可能页面已经挂起了，直接处理数据
                if (parentManager.getRoleType() == RoleType.OWNER) {
                    // 发送http通知后台
                    GameRepository.switchGame(parentManager, parentManager.getRoomId(), model.gameID, new RxCallback<Object>() {
                        @Override
                        public void onSuccess(Object o) {
                            super.onSuccess(o);
                            RoomInfoModel roomInfoModel = parentManager.getRoomInfoModel();
                            if (roomInfoModel != null) {
                                roomInfoModel.gameId = model.gameID;
                            }
                            parentManager.switchGame(model.gameID);
                        }
                    });
                }
            } else {
                callback.onRoomPkChangeGame(model.gameID);
            }
        }
    };

    /** 跨房PK，移除对手 监听 */
    private final PKRemoveRivalCommandListener pkRemoveRivalCommandListener = new PKRemoveRivalCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKRemoveRivalModel command, String userID) {
            localRemovePkRival();
        }
    };

    /** 跨房pk游戏结算消息通知 监听 */
    private final PKSettleCommandListener pkSettleCommandListener = new PKSettleCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKSettleModel model, String userID) {
            RoomCmdPKSettleModel.Content content = model.content;
            if (roomPkModel == null || content == null) return;
            // 给两个队伍设置分数
            if (roomPkModel.srcRoomInfo != null && content.srcPkGameSettleInfo != null) {
                roomPkModel.srcRoomInfo.score = content.srcPkGameSettleInfo.totalScore;
            }
            if (roomPkModel.destRoomInfo != null && content.destPkGameSettleInfo != null) {
                roomPkModel.destRoomInfo.score = content.destPkGameSettleInfo.totalScore;
            }
            callbackUpdateRoomPk();

            // 前三名，发送公屏
            Application app = Utils.getApp();
            if (content.userRankInfoList != null) {
                for (RoomCmdPKSettleModel.RankInfo rankInfo : content.userRankInfoList) {
                    GameTextModel gameTextModel = new GameTextModel();
                    String teamText;
                    if (isSrcRoom(rankInfo.roomId)) {
                        teamText = app.getString(R.string.team_red);
                    } else if (isDestRoom(rankInfo.roomId)) {
                        teamText = app.getString(R.string.team_blue);
                    } else {
                        continue;
                    }
                    int textResId;
                    switch (rankInfo.rank) {
                        case 1:
                            textResId = R.string.win_info_first;
                            break;
                        case 2:
                            textResId = R.string.win_info_second;
                            break;
                        case 3:
                            textResId = R.string.win_info_thirdly;
                            break;
                        default:
                            continue;
                    }
                    gameTextModel.message = app.getString(textResId, rankInfo.nickname, teamText, rankInfo.winScore + "");
                    parentManager.sceneChatManager.addMsg(gameTextModel);
                }
            }
        }
    };

    /** 跨房PK对手房间关闭消息 监听 */
    private final PKRivalExitCommandListener pkRivalExitCommandListener = new PKRivalExitCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKRivalExitModel model, String userID) {
            RoomPkRoomInfo pkRival = getPkRival();
            if (pkRival != null) {
                localRemovePkRival();
                addInterceptChatMsg();
            }
        }
    };
    // endregion 信令监听


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (showInviteDialogFuture != null) {
            showInviteDialogFuture.cancel(false);
            showInviteDialogFuture = null;
        }
        if (inviteAnswerDialog != null) {
            inviteAnswerDialog.dismiss();
            inviteAnswerDialog = null;
        }
    }
}
