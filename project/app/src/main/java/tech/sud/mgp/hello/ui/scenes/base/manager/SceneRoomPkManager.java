package tech.sud.mgp.hello.ui.scenes.base.manager;

import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.service.room.model.PkStatus;
import tech.sud.mgp.hello.service.room.response.RoomPkModel;
import tech.sud.mgp.hello.service.room.response.RoomPkRoomInfo;
import tech.sud.mgp.hello.ui.main.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.activity.RoomConfig;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.PKSendInviteCommandListener;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
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

    private void callbackUpdateRoomPk() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onUpdateRoomPk();
        }
    }

    // region 信令监听
    /** 跨房PK邀请 监听 */
    private final PKSendInviteCommandListener inviteCommandListener = new PKSendInviteCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKSendInviteModel command, String userID) {
        }
    };

    /** 跨房PK邀请应答 监听 */
    private final SceneCommandManager.PKAnswerCommandListener answerCommandListener = new SceneCommandManager.PKAnswerCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKAnswerModel command, String userID) {

        }
    };

    /** 开始跨房PK 监听 */
    private final SceneCommandManager.PKStartCommandListener startCommandListener = new SceneCommandManager.PKStartCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKStartModel command, String userID) {

        }
    };

    /** 结束跨房PK 监听 */
    private final SceneCommandManager.PKFinishCommandListener finishCommandListener = new SceneCommandManager.PKFinishCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKFinishModel command, String userID) {

        }
    };

    /** 跨房PK设置 监听 */
    private final SceneCommandManager.PKSettingsCommandListener settingsCommandListener = new SceneCommandManager.PKSettingsCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKSettingsModel command, String userID) {

        }
    };

    /** 开启匹配跨房PK 监听 */
    private final SceneCommandManager.PKOpenMatchCommandListener openMatchCommandListener = new SceneCommandManager.PKOpenMatchCommandListener() {
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
    private final SceneCommandManager.PKChangeGameCommandListener pkChangeGameCommandListener = new SceneCommandManager.PKChangeGameCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKChangeGameModel command, String userID) {

        }
    };

    /** 跨房pk游戏结算消息通知 监听 */
    private final SceneCommandManager.PKSettleCommandListener pkSettleCommandListener = new SceneCommandManager.PKSettleCommandListener() {
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
