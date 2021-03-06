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
 * ??????pk????????????
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

    /** ???????????????????????? */
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

    /** ??????pk???????????????????????????Pk??? */
    public void roomPkSwitch(boolean pkSwitch) {
        // ??????http??????
        RoomRepository.roomPkSwitch(parentManager, parentManager.getRoomId(), pkSwitch, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                initRoomPkModel();
                if (pkSwitch) { // ????????????
                    if (roomPkModel.pkStatus == PkStatus.MATCH_CLOSED) {
                        setSelfSrcRoomInfo();
                        sendCommand(RoomCmdModelUtils.buildCmdPkOpenMatch());
                        setPkStatus(PkStatus.MATCHING);
                        callbackUpdateRoomPk();
                    }
                } else { // ????????????pk
                    String command = RoomCmdModelUtils.buildCmdPkFinish();
                    sendCommand(command);
                    RoomPkRoomInfo pkRival = getPkRival();
                    if (pkRival != null) {
                        sendXRoomCommand(pkRival.roomId + "", command);
                    }

                    // ??????????????????
                    setPkStatusClosed();

                    // ??????http?????????????????????????????????
                    GameRepository.switchGame(parentManager, parentManager.getRoomId(), GameIdCons.NONE, new RxCallback<>());

                    // ????????????
                    parentManager.switchGame(GameIdCons.NONE);
                }
            }
        });
    }

    /** ??????pk?????? */
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

    /** ???????????????pk????????????????????????pk */
    private void setPkStatusClosed() {
        roomPkModel.clear();
        checkCountdown();
        callbackUpdateRoomPk();
        parentManager.callbackOnGameChange(GameIdCons.NONE);
    }

    /** ??????pk????????? */
    public void roomPkStart(int minute) {
        if (roomPkModel == null) return;
        RoomPkRoomInfo pkRival = getPkRival();
        if (pkRival == null) return;

        long pkRivalRoomId = pkRival.roomId;
        // ??????http?????????????????????????????????????????????????????????
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
        // ????????????
        String command = RoomCmdModelUtils.buildCmdPkStart(roomPkModel.totalMinute, roomPkModel.pkId + "");
        sendCommand(command);
        sendXRoomCommand(pkRivalRoomId + "", command);

        // ????????????
        roomPkModel.remainSecond = roomPkModel.totalMinute * 60;
        setPkStatus(PkStatus.STARTED);
        clearScore();
        callbackUpdateRoomPk();
    }

    /** ?????????????????? */
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
     * ??????pk?????????????????????
     *
     * @param minute ?????????
     */
    public void roomPkSettings(int minute) {
        if (roomPkModel == null || getPkStatus() != PkStatus.STARTED) return;
        RoomPkRoomInfo pkRival = getPkRival();
        if (pkRival == null) return;

        // ??????http??????
        RoomRepository.roomPkDuration(parentManager, parentManager.getRoomId(), minute,
                new RxCallback<Object>() {
                    @Override
                    public void onSuccess(Object resp) {
                        super.onSuccess(resp);
                        if (roomPkModel == null || roomPkModel.pkStatus != PkStatus.STARTED) return;

                        roomPkModel.totalMinute = minute;

                        // ????????????
                        String command = RoomCmdModelUtils.buildCmdPkSettings(minute);
                        sendCommand(command);
                        sendXRoomCommand(pkRival.roomId + "", command);

                        // ??????????????????
                        roomPkModel.remainSecond = minute * 60;
                        checkCountdown();
                        callbackUpdateRoomPk();
                    }
                }
        );
    }

    /** ??????pk?????? */
    public void removePkRival() {
        // pk?????????????????????????????????
        int pkStatus = roomPkModel.pkStatus;
        if (pkStatus == PkStatus.STARTED) {
            ToastUtils.showShort(R.string.pk_running_remove_warn);
            return;
        }
        RoomPkRoomInfo pkRival = roomPkModel.getPkRival();
        if (pkRival == null) return;

        // ??????http??????
        RoomRepository.roomPkRemoveRival(parentManager, parentManager.getRoomId(), new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);

                // ????????????
                String command = RoomCmdModelUtils.buildCmdPkRemoveRival();
                sendCommand(command);
                sendXRoomCommand(pkRival.roomId + "", command);

                // ????????????
                localRemovePkRival();
            }
        });
    }

    /** ????????????pk?????? */
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

    /** ??????pk??????????????? */
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

    /** ??????pk????????? */
    public void roomPkAnswer(RoomCmdPKSendInviteModel model, boolean isAccept) {
        if (model == null || model.sendUser == null) return;
        if (isAccept) {
            if (getPkStatus() != PkStatus.MATCHING) {
                return;
            }
            // ??????http??????
            RoomRepository.roomPkAgree(parentManager, model.sendUser.getRoomId(), parentManager.getRoomId(),
                    new RxCallback<RoomPkAgreeResp>() {
                        @Override
                        public void onSuccess(RoomPkAgreeResp resp) {
                            super.onSuccess(resp);
                            if (getPkStatus() != PkStatus.MATCHING) {
                                return;
                            }

                            // ????????????
                            String command = RoomCmdModelUtils.buildCmdPkAnswer(model.sendUser, isAccept, resp.pkId + "");
                            sendCommand(command);
                            sendXRoomCommand(model.sendUser.roomID, command);

                            // ???????????????????????????????????????
                            acceptRoomPk(SudJsonUtils.fromJson(command, RoomCmdPKAnswerModel.class));
                            roomPkModel.totalMinute = model.minuteDuration;
                        }
                    }
            );
        } else {
            // ????????????
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

    /** ??????pk??????????????? */
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

        if ((parentManager.getRoomId() + "").equals(model.sendUser.roomID)) {// ??????????????????????????????
            roomPkModel.srcRoomInfo.isSelfRoom = false;
            roomPkModel.destRoomInfo.isSelfRoom = true;
        } else { // ?????????????????????
            roomPkModel.srcRoomInfo.isSelfRoom = true;
            roomPkModel.destRoomInfo.isSelfRoom = false;
            syncGame();
        }

        callbackUpdateRoomPk();
    }

    /** ???????????????????????????????????? */
    public void syncGame() {
        if (parentManager.getRoleType() == RoleType.OWNER) {
            RoomPkRoomInfo pkRival = getPkRival();
            if (pkRival != null) {
                long gameId = parentManager.getRoomInfoModel().gameId;
                sendXRoomCommand(pkRival.roomId + "", RoomCmdModelUtils.buildCmdPkChangeGame(gameId));
            }
        }
    }

    /** ??????pk?????? */
    private RoomPkRoomInfo getPkRival() {
        if (roomPkModel != null) {
            return roomPkModel.getPkRival();
        }
        return null;
    }

    /** ??????pk?????? */
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

    /** ?????????????????? */
    private boolean isSrcRoom(long roomId) {
        if (roomPkModel != null && roomPkModel.srcRoomInfo != null) {
            return roomPkModel.srcRoomInfo.roomId == roomId;
        }
        return false;
    }

    /** ????????????????????? */
    private boolean isDestRoom(long roomId) {
        if (roomPkModel != null && roomPkModel.destRoomInfo != null) {
            return roomPkModel.destRoomInfo.roomId == roomId;
        }
        return false;
    }

    /**
     * ????????????pk??????
     *
     * @param model ????????????
     */
    private void processInvite(RoomCmdPKSendInviteModel model) {
        if (getPkStatus() != PkStatus.MATCHING) return;
        if (parentManager.getRoleType() != RoleType.OWNER) return;
        if (showInviteDialogFuture != null && !showInviteDialogFuture.isDone()) return;
        // ???????????????
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

    /** ???????????????????????? */
    private void showInviteAnswerDialog(Context context, RoomCmdPKSendInviteModel model) {
        if (inviteAnswerDialog != null && inviteAnswerDialog.isShowing()) return;
        // ??????dialog
        SimpleChooseDialog dialog = new SimpleChooseDialog(context, context.getString(R.string.invite_pk_answer_title, model.sendUser.name)
                , "", context.getString(R.string.accept));
        dialog.setCustomCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        // ?????????
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
        // ??????
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 0) { // ??????
                    roomPkAnswer(model, false);
                } else if (index == 1) { // ??????
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

    /** ????????????????????????pk??????????????? */
    private void addInterceptChatMsg() {
        String msg = Utils.getApp().getString(R.string.intercept_room_pk);
        parentManager.sceneChatManager.addMsg(msg);
    }

    /** ????????????????????? */
    private void cancelCountdown() {
        if (countdownTimer == null) return;
        countdownTimer.cancel();
        countdownTimer = null;
    }

    /** ??????????????? */
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

    // region ????????????
    /** ??????PK?????? ?????? */
    private final PKSendInviteCommandListener inviteCommandListener = new PKSendInviteCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKSendInviteModel model, String userID) {
            processInvite(model);
        }
    };

    /** ??????PK???????????? ?????? */
    private final PKAnswerCommandListener answerCommandListener = new PKAnswerCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKAnswerModel model, String userID) {
            if (model.isAccept) {
                acceptRoomPk(model);
            }
        }
    };

    /** ????????????PK ?????? */
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

    /** ????????????PK ?????? */
    private final PKFinishCommandListener finishCommandListener = new PKFinishCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKFinishModel model, String userID) {
            if (model.sendUser == null || roomPkModel == null) return;
            String fromRoomId = model.sendUser.roomID;
            if ((parentManager.getRoomId() + "").equals(fromRoomId)) { // ??????????????????????????????pk
                setPkStatusClosed();
            } else { // ????????????????????????pk
                RoomPkRoomInfo pkRival = getPkRival();
                if (pkRival != null && (pkRival.roomId + "").equals(fromRoomId)) {
                    // ???????????????????????????pk????????????????????????
                    localRemovePkRival();
                }
                addInterceptChatMsg();

                if (parentManager.getRoleType() == RoleType.OWNER) {
                    // ??????http?????????????????????????????????
                    GameRepository.switchGame(parentManager, parentManager.getRoomId(), GameIdCons.NONE, new RxCallback<>());
                    parentManager.switchGame(GameIdCons.NONE);
                    parentManager.callbackOnGameChange(GameIdCons.NONE);
                }
            }
        }
    };

    /** ??????PK?????? ?????? */
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

    /** ??????????????????PK ?????? */
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

    /** ??????PK??????????????? ?????? */
    private final PKChangeGameCommandListener pkChangeGameCommandListener = new PKChangeGameCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKChangeGameModel model, String userID) {
            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback == null) {
                // ???????????????????????????????????????????????????????????????????????????
                if (parentManager.getRoleType() == RoleType.OWNER) {
                    // ??????http????????????
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

    /** ??????PK??????????????? ?????? */
    private final PKRemoveRivalCommandListener pkRemoveRivalCommandListener = new PKRemoveRivalCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKRemoveRivalModel command, String userID) {
            localRemovePkRival();
        }
    };

    /** ??????pk???????????????????????? ?????? */
    private final PKSettleCommandListener pkSettleCommandListener = new PKSettleCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKSettleModel model, String userID) {
            RoomCmdPKSettleModel.Content content = model.content;
            if (roomPkModel == null || content == null) return;
            // ???????????????????????????
            if (roomPkModel.srcRoomInfo != null && content.srcPkGameSettleInfo != null) {
                roomPkModel.srcRoomInfo.score = content.srcPkGameSettleInfo.totalScore;
            }
            if (roomPkModel.destRoomInfo != null && content.destPkGameSettleInfo != null) {
                roomPkModel.destRoomInfo.score = content.destPkGameSettleInfo.totalScore;
            }
            callbackUpdateRoomPk();

            // ????????????????????????
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

    /** ??????PK???????????????????????? ?????? */
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
    // endregion ????????????


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
