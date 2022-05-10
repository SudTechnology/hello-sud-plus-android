package tech.sud.mgp.hello.ui.scenes.base.manager;

import android.app.Application;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.service.room.model.PkStatus;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.response.RoomPkAgainResp;
import tech.sud.mgp.hello.service.room.response.RoomPkAgreeResp;
import tech.sud.mgp.hello.service.room.response.RoomPkModel;
import tech.sud.mgp.hello.service.room.response.RoomPkRoomInfo;
import tech.sud.mgp.hello.service.room.response.RoomPkStartResp;
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
import tech.sud.mgp.hello.ui.scenes.base.utils.HSJsonUtils;
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
                        roomPkModel.pkStatus = PkStatus.MATCHING;
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
                }
            }
        });
    }

    /** 更新本地的pk状态为关闭了跨房pk */
    private void setPkStatusClosed() {
        roomPkModel.clear();
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
            if (roomPkModel.srcRoomInfo == null || roomPkModel.destRoomInfo == null) return;
            RoomRepository.roomPkAgain(parentManager, roomPkModel.srcRoomInfo.roomId, roomPkModel.destRoomInfo.roomId, minute,
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
        roomPkModel.pkStatus = PkStatus.STARTED;
        roomPkModel.remainSecond = roomPkModel.totalMinute * 60;
        callbackUpdateRoomPk();
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

        // TODO: 2022/5/7 等待后端接口
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

    /** 跨房pk，发送邀请 */
    public void roomPkInvite(RoomItemModel model) {
        if (model == null || getPkStatus() != PkStatus.MATCHING) return;
        initRoomPkModel();
        sendXRoomCommand(model.getRoomId() + "", RoomCmdModelUtils.buildCmdPkSendInvite(roomPkModel.totalMinute));
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
                            acceptRoomPk(HSJsonUtils.fromJson(command, RoomCmdPKAnswerModel.class));
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

    /** 同意pk之后的处理 */
    private void acceptRoomPk(RoomCmdPKAnswerModel model) {
        if (model == null || model.sendUser == null || model.otherUser == null || !model.isAccept) return;
        if (getPkStatus() != PkStatus.MATCHING) {
            return;
        }
        initRoomPkModel();
        roomPkModel.setPkId(model.pkId);
        roomPkModel.pkStatus = PkStatus.MATCHED;

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
        RoomPkRoomInfo pkRival = getPkRival();
        if (pkRival != null) {
            long gameId = parentManager.getRoomInfoModel().gameId;
            sendXRoomCommand(pkRival.roomId + "", RoomCmdModelUtils.buildCmdPkChangeGame(gameId));
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
            roomPkModel.pkStatus = PkStatus.MATCHING;
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

    // region 信令监听
    /** 跨房PK邀请 监听 */
    private final PKSendInviteCommandListener inviteCommandListener = new PKSendInviteCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKSendInviteModel model, String userID) {
            if (getPkStatus() != PkStatus.MATCHING) return;
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
            roomPkModel.pkStatus = PkStatus.STARTED;
            roomPkModel.totalMinute = model.minuteDuration;
            roomPkModel.remainSecond = model.minuteDuration * 60;
            callbackUpdateRoomPk();
        }
    };

    /** 结束跨房PK 监听 */
    private final PKFinishCommandListener finishCommandListener = new PKFinishCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKFinishModel model, String userID) {
            if (model.sendUser == null || roomPkModel == null) return;
            String fromRoomId = model.sendUser.roomID;
            if ((parentManager.getRoomId() + "").equals(fromRoomId)) {
                // 关闭自己房间的pk
                setPkStatusClosed();
            } else {
                RoomPkRoomInfo pkRival = getPkRival();
                if (pkRival != null && (pkRival.roomId + "").equals(fromRoomId)) {
                    // 对方房间关闭了跨房pk，回到待匹配状态
                    localRemovePkRival();
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
            roomPkModel.pkStatus = PkStatus.MATCHING;
            callbackUpdateRoomPk();
        }
    };

    /** 跨房PK，切换游戏 监听 */
    private final PKChangeGameCommandListener pkChangeGameCommandListener = new PKChangeGameCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdPKChangeGameModel model, String userID) {
            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
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
            }
        }
    };
    // endregion 信令监听

}
