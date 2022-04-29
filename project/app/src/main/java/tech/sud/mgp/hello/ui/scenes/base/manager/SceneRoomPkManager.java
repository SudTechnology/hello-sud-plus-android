package tech.sud.mgp.hello.ui.scenes.base.manager;

import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.service.room.model.PkStatus;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.response.RoomPkAgreeResp;
import tech.sud.mgp.hello.service.room.response.RoomPkModel;
import tech.sud.mgp.hello.service.room.response.RoomPkRoomInfo;
import tech.sud.mgp.hello.ui.main.constant.GameIdCons;
import tech.sud.mgp.hello.ui.main.home.model.RoomItemModel;
import tech.sud.mgp.hello.ui.scenes.base.activity.RoomConfig;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKAnswerCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKChangeGameCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKFinishCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKOpenMatchCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKSendInviteCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKSettingsCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKSettleCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKStartCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.base.utils.HSJsonUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKAnswerModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKChangeGameModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKFinishModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKOpenMatchModel;
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
        parentManager.sceneEngineManager.setCommandListener(pkSettleCommandListener);
    }

    public void enterRoom(RoomConfig config, RoomInfoModel model) {
        roomPkModel = model.roomPkModel;
    }

    public void initRoomPkModel() {
        if (roomPkModel == null) {
            roomPkModel = new RoomPkModel();
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
            RoomInfoModel roomInfoModel = parentManager.getRoomInfoModel();
            if (roomInfoModel != null) {
                roomInfoModel.roomPkModel = roomPkModel;
            }
        }
    }

    /** 跨房pk，开启匹配或者关闭Pk了 */
    public void roomPkSwitch(boolean pkSwitch) {
        initRoomPkModel();
        if (pkSwitch) {
            if (roomPkModel.pkStatus == PkStatus.MATCH_CLOSED) {
                parentManager.sceneEngineManager.sendCommand(RoomCmdModelUtils.buildCmdPkOpenMatch(), null);
                roomPkModel.pkStatus = PkStatus.MATCHING;
                callbackUpdateRoomPk();
            }
        } else {
            parentManager.sceneEngineManager.sendCommand(RoomCmdModelUtils.buildCmdPkFinish(), null);
            roomPkModel.pkStatus = PkStatus.MATCH_CLOSED;
            callbackUpdateRoomPk();
            parentManager.callbackOnGameChange(GameIdCons.NONE);
        }
    }

    /** 跨房pk，开始 */
    public void roomPkStart() {

    }

    /** 跨房pk，发送邀请 */
    public void roomPkInvite(RoomItemModel model) {
        if (model == null) return;
        initRoomPkModel();
        parentManager.sceneEngineManager.sendXRoomCommand(model.getRoomId() + "", RoomCmdModelUtils.buildCmdPkSendInvite(roomPkModel.totalMinute), null);
    }

    /** 跨房pk，应答 */
    public void roomPkAnswer(RoomCmdPKSendInviteModel model, boolean isAccept) {
        if (model == null || model.sendUser == null) return;
        if (isAccept) {
            // 发送http请求
            RoomRepository.roomPkAgree(null, model.sendUser.getRoomId(), parentManager.getRoomId(),
                    new RxCallback<RoomPkAgreeResp>() {
                        @Override
                        public void onSuccess(RoomPkAgreeResp roomPkAgreeResp) {
                            super.onSuccess(roomPkAgreeResp);
                            if (getPkStatus() != PkStatus.MATCHING) {
                                return;
                            }

                            // 发送信令
                            String command = RoomCmdModelUtils.buildCmdPkAnswer(model.sendUser, isAccept);
                            parentManager.sceneEngineManager.sendCommand(command, null);
                            parentManager.sceneEngineManager.sendXRoomCommand(model.sendUser.roomID + "", command, null);

                            // 修改本地状态，更新页面显示
                            acceptRoomPk(HSJsonUtils.fromJson(command, RoomCmdPKAnswerModel.class));
                        }
                    }
            );
        } else {
            // 发送信令
            String command = RoomCmdModelUtils.buildCmdPkAnswer(model.sendUser, isAccept);
            parentManager.sceneEngineManager.sendXRoomCommand(model.sendUser.roomID + "", command, null);
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

    /** 同意pk之后的处理 */
    private void acceptRoomPk(RoomCmdPKAnswerModel model) {
        if (model == null || model.sendUser == null || model.otherUser == null || !model.isAccept) return;
        if (getPkStatus() != PkStatus.MATCHING) {
            return;
        }
        initRoomPkModel();
        roomPkModel.pkStatus = PkStatus.MATCHED;
        roomPkModel.srcRoomInfo = new RoomPkRoomInfo();
        roomPkModel.srcRoomInfo.setRoomId(model.otherUser.roomID);
        roomPkModel.srcRoomInfo.roomOwnerHeader = model.otherUser.icon;
        roomPkModel.srcRoomInfo.roomOwnerNickname = model.otherUser.name;
        roomPkModel.srcRoomInfo.isInitiator = true;

        roomPkModel.destRoomInfo = new RoomPkRoomInfo();
        roomPkModel.destRoomInfo.setRoomId(model.sendUser.roomID);
        roomPkModel.srcRoomInfo.roomOwnerHeader = model.sendUser.icon;
        roomPkModel.srcRoomInfo.roomOwnerNickname = model.sendUser.name;
        roomPkModel.destRoomInfo.isInitiator = false;

        if ((parentManager.getRoomId() + "").equals(model.sendUser.roomID)) {// 本房间现在是被邀请者
            roomPkModel.srcRoomInfo.isSelfRoom = false;
            roomPkModel.destRoomInfo.isSelfRoom = true;
        } else { // 本房间是邀请者
            roomPkModel.srcRoomInfo.isSelfRoom = true;
            roomPkModel.destRoomInfo.isSelfRoom = false;
        }

        callbackUpdateRoomPk();
    }

    // region 信令监听
    /** 跨房PK邀请 监听 */
    private final PKSendInviteCommandListener inviteCommandListener = new PKSendInviteCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKSendInviteModel model, String userID) {
            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
                callback.onRoomPkInvite(model);
            }
        }
    };

    /** 跨房PK邀请应答 监听 */
    private final PKAnswerCommandListener answerCommandListener = new PKAnswerCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKAnswerModel model, String userID) {
            if (model != null && model.isAccept) {
                acceptRoomPk(model);
            }
        }
    };

    /** 开始跨房PK 监听 */
    private final PKStartCommandListener startCommandListener = new PKStartCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKStartModel command, String userID) {

        }
    };

    /** 结束跨房PK 监听 */
    private final PKFinishCommandListener finishCommandListener = new PKFinishCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKFinishModel command, String userID) {

        }
    };

    /** 跨房PK设置 监听 */
    private final PKSettingsCommandListener settingsCommandListener = new PKSettingsCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKSettingsModel command, String userID) {

        }
    };

    /** 开启匹配跨房PK 监听 */
    private final PKOpenMatchCommandListener openMatchCommandListener = new PKOpenMatchCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKOpenMatchModel command, String userID) {
            initRoomPkModel();
            roomPkModel.srcRoomInfo.roomOwnerNickname = command.sendUser.name;
            roomPkModel.srcRoomInfo.roomOwnerHeader = command.sendUser.icon;
            roomPkModel.pkStatus = PkStatus.MATCHING;
            callbackUpdateRoomPk();
        }
    };

    /** 跨房PK，切换游戏 监听 */
    private final PKChangeGameCommandListener pkChangeGameCommandListener = new PKChangeGameCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKChangeGameModel command, String userID) {

        }
    };

    /** 跨房pk游戏结算消息通知 监听 */
    private final PKSettleCommandListener pkSettleCommandListener = new PKSettleCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKSettleModel command, String userID) {

        }
    };
    // endregion 信令监听

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
