package tech.sud.mgp.hello.ui.scenes.base.manager;

import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.repository.UserInfoRepository;
import tech.sud.mgp.hello.service.main.resp.CrossAppGameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.service.room.model.CrossAppMatchStatus;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.CrossAppModel;
import tech.sud.mgp.hello.service.room.resp.CrossAppStartMatchResp;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.ResultListener;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.crossapp.CrossAppCmdGameSwitchModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.crossapp.CrossAppCmdStatusChangeModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.crossapp.CrossAppCmdTeamChangeModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.crossapp.CrossAppCmdUsersChangeModel;

/**
 * 跨域场景内的业务逻辑
 */
public class SceneCrossAppManager extends BaseServiceManager {

    private CrossAppModel crossAppModel;
    private final SceneRoomServiceManager parentManager;

    public SceneCrossAppManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.sceneEngineManager.setCommandListener(crossAppCmdUsersChangeListener);
        parentManager.sceneEngineManager.setCommandListener(crossAppCmdStatusChangeListener);
        parentManager.sceneEngineManager.setCommandListener(crossAppCmdTeamChangeListener);
        parentManager.sceneEngineManager.setCommandListener(crossAppCmdGameSwitchListener);
    }

    public void crossAppExitTeam(boolean inLifecycle) {
        LifecycleOwner lifecycleOwner;
        if (inLifecycle) {
            lifecycleOwner = parentManager;
        } else {
            lifecycleOwner = null;
        }
        RoomRepository.crossAppQuitTeam(lifecycleOwner, parentManager.getRoomId(), new RxCallback<>());
    }

    public void onEnterRoomSuccess() {
        crossAppModel = parentManager.getRoomInfoModel().crossAppModel;
        if (crossAppModel == null) {
            crossAppModel = new CrossAppModel();
            parentManager.getRoomInfoModel().crossAppModel = crossAppModel;
        }
        initData();
    }

    private void initData() {
        updateMatchGameModel(crossAppModel.matchGameId, () -> {
            initStallList();
            callbackUpdateCrossApp();
            updateUserinfo();
            switch (crossAppModel.enterType) {
                case CrossAppModel.SINGLE_MATCH:
                    fastMatch();
                    break;
                case CrossAppModel.TEAM_MATCH:
                    joinTeam(null, null);
                    break;
            }
        });
    }

    private void initStallList() {
        List<UserInfoResp> oldList = new ArrayList<>();
        if (crossAppModel.userList != null) {
            oldList.addAll(crossAppModel.userList);
        }
        crossAppModel.userList = new ArrayList<>();
        int gameMaxNumber = crossAppModel.gameModel.getGameMaxNumber();
        for (int i = 0; i < gameMaxNumber; i++) {
            UserInfoResp model = findUserInfo(oldList, i);
            if (model == null) {
                model = new UserInfoResp();
            }
            model.index = i;
            model.isCaptain = model.userId > 0 && crossAppModel.captain == model.userId;
            crossAppModel.userList.add(model);
        }
    }

    private UserInfoResp findUserInfo(List<UserInfoResp> list, int index) {
        if (list != null) {
            for (UserInfoResp userInfoResp : list) {
                if (userInfoResp != null && userInfoResp.index == index) {
                    return userInfoResp;
                }
            }
        }
        return null;
    }

    /** 快速匹配 */
    private void fastMatch() {
        joinTeam(null, new ResultListener() {
            @Override
            public void onSuccess() {
                startMatch();
            }

            @Override
            public void onFailed() {
            }
        });
    }

    /** 加入组队 */
    public void joinTeam(Integer intentIndex, ResultListener listener) {
        int availableIndex;
        if (intentIndex == null) {
            availableIndex = findAvailableIndex();
        } else {
            availableIndex = intentIndex;
        }
        if (availableIndex == -1) {
            return;
        }
        RoomRepository.crossAppJoinTeam(parentManager, parentManager.getRoomId(), crossAppModel.matchGameId, availableIndex, new RxCallback<Object>() {
            @Override
            public void onNext(BaseResponse<Object> t) {
                super.onNext(t);
                if (listener != null) {
                    if (t.getRetCode() == RetCode.SUCCESS) {
                        listener.onSuccess();
                    } else {
                        listener.onFailed();
                    }
                }
            }
        });
    }

    /** 查找可用的座位，如果没有，返回-1 */
    private int findAvailableIndex() {
        if (crossAppModel.userList != null) {
            for (UserInfoResp userInfoResp : crossAppModel.userList) {
                if (userInfoResp.userId <= 0) {
                    return userInfoResp.index;
                }
            }
        }
        return -1;
    }

    /** 开启匹配 */
    public void startMatch() {
        RoomRepository.crossAppStartMatch(parentManager, parentManager.getRoomId(), crossAppModel.matchGameId, new RxCallback<CrossAppStartMatchResp>() {
            @Override
            public void onSuccess(CrossAppStartMatchResp resp) {
                super.onSuccess(resp);
                if (resp != null) {
                    crossAppModel.groupId = resp.groupId;
                }
            }
        });
    }

    /** 取消匹配 */
    public void cancelMatch(boolean inLifecycle, CompletedListener listener) {
        LifecycleOwner lifecycleOwner;
        if (inLifecycle) {
            lifecycleOwner = parentManager;
        } else {
            lifecycleOwner = null;
        }
        RoomRepository.crossAppCancelMatch(lifecycleOwner, getGroupId(), parentManager.getRoomId(), crossAppModel.matchGameId, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (listener != null) {
                    listener.onCompleted();
                }
            }
        });
    }

    /** 更换游戏 */
    public void changeMatchGame(GameModel gameModel) {
        if (gameModel == null || crossAppModel == null) {
            return;
        }
//        if (crossAppModel.matchGameId == gameModel.gameId) {
//            return;
//        }
        RoomRepository.crossAppSwitchGame(parentManager, parentManager.getRoomId(), gameModel.gameId, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                crossAppModel.matchGameId = gameModel.gameId;
                crossAppModel.gameModel = gameModel;
                callbackUpdateCrossApp();
            }
        });
    }

    public CrossAppModel getCrossAppModel() {
        return crossAppModel;
    }

    public void crossAppGameSettle() {
        if (crossAppModel != null && crossAppModel.captain == HSUserInfo.userId) {
            switchGame(GameIdCons.NONE);
            changeMatchGame(crossAppModel.gameModel);
        }
    }

    private String getGroupId() {
        if (crossAppModel != null) {
            return crossAppModel.groupId;
        }
        return null;
    }

    /** 获取匹配的游戏model */
    private void updateMatchGameModel(long matchGameId, CompletedListener completedListener) {
        HomeRepository.crossAppGameList(parentManager, new RxCallback<CrossAppGameListResp>() {
            @Override
            public void onSuccess(CrossAppGameListResp resp) {
                super.onSuccess(resp);
                if (resp != null) {
                    crossAppModel.gameModel = findGameModel(matchGameId, resp.allGameList);
                    if (completedListener != null) {
                        completedListener.onCompleted();
                    }
                }
            }
        });
    }

    private GameModel findGameModel(long gameId, List<GameModel> list) {
        if (list != null) {
            for (GameModel gameModel : list) {
                if (gameModel.gameId == gameId) {
                    return gameModel;
                }
            }
        }
        return null;
    }

    public void callbackPageData() {
        callbackUpdateCrossApp();
    }

    private void callbackUpdateCrossApp() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onUpdateCrossApp(crossAppModel);
        }
    }

    /** 跨域匹配人数变更通知 */
    public SceneCommandManager.CrossAppCmdUsersChangeListener crossAppCmdUsersChangeListener = new SceneCommandManager.CrossAppCmdUsersChangeListener() {
        @Override
        public void onRecvCommand(CrossAppCmdUsersChangeModel model, String userID) {
            if (model == null || model.content == null || crossAppModel == null) {
                return;
            }
            if (!Objects.equals(crossAppModel.groupId, model.content.groupId)) {
                return;
            }
            crossAppModel.curNum = model.content.curNum;
            crossAppModel.totalNum = model.content.totalNum;
            callbackUpdateCrossApp();
        }
    };

    /** 跨域匹配状态变更通知 */
    public SceneCommandManager.CrossAppCmdStatusChangeListener crossAppCmdStatusChangeListener = new SceneCommandManager.CrossAppCmdStatusChangeListener() {
        @Override
        public void onRecvCommand(CrossAppCmdStatusChangeModel model, String userID) {
            if (model == null || model.content == null || crossAppModel == null) {
                return;
            }
            crossAppModel.groupId = model.content.groupId;
            crossAppModel.matchRoomId = model.content.matchRoomId;
            crossAppModel.matchStatus = model.content.matchStatus;
//            checkInitMatchNumber();
            callbackUpdateCrossApp();

            if (crossAppModel.matchStatus == CrossAppMatchStatus.MATCH_SUCCESS) {
                if (crossAppModel.captain == HSUserInfo.userId) { // 匹配成功时，队长触发游戏
                    switchGame(crossAppModel.matchGameId);
                }
                if (parentManager.getCallback() == null) { // 唤醒界面
                    parentManager.startRoomActivity();
                }
            }
        }
    };

    /** 跨域匹配队伍变更通知 */
    public SceneCommandManager.CrossAppCmdTeamChangeListener crossAppCmdTeamChangeListener = new SceneCommandManager.CrossAppCmdTeamChangeListener() {
        @Override
        public void onRecvCommand(CrossAppCmdTeamChangeModel model, String userID) {
            if (crossAppModel == null) {
                return;
            }
            long captainUserId = 0;
            List<UserInfoResp> curList = crossAppModel.userList;
            List<UserInfoResp> anewList = null;
            if (model.content != null) {
                anewList = model.content.userList;
                captainUserId = model.content.captain;
                crossAppModel.captain = captainUserId;
            }
            if (curList == null) {
                return;
            }
            for (UserInfoResp userInfoResp : curList) {
                int index = userInfoResp.index;
                UserInfoResp anewUserInfo = findUserInfo(anewList, index);
                if (anewUserInfo == null) {
                    userInfoResp.clearUser();
                } else {
                    if (userInfoResp.userId != anewUserInfo.userId) {
                        userInfoResp.clearUser();
                        userInfoResp.userId = anewUserInfo.userId;
                    }
                }
                userInfoResp.isCaptain = userInfoResp.userId > 0 && userInfoResp.userId == captainUserId;
            }
            updateUserinfo();
        }
    };

    /** 切换游戏 */
    private void switchGame(long gameId) {
        // 发送http协议，通知后端
        GameRepository.switchGame(parentManager, parentManager.getRoomId(), gameId, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                // 发送切换游戏信令
                parentManager.switchGame(gameId);
                parentManager.callbackOnGameChange(gameId);
            }
        });
    }

    /** 跨域匹配游戏切换通知 */
    public SceneCommandManager.CrossAppCmdGameSwitchListener crossAppCmdGameSwitchListener = new SceneCommandManager.CrossAppCmdGameSwitchListener() {
        @Override
        public void onRecvCommand(CrossAppCmdGameSwitchModel model, String userID) {
            if (model == null || model.content == null || crossAppModel == null) {
                return;
            }
            crossAppModel.matchGameId = model.content.gameId;
            updateMatchGameModel(crossAppModel.matchGameId, () -> {
                callbackUpdateCrossApp();
            });
        }
    };

    private void updateUserinfo() {
        if (crossAppModel == null) {
            return;
        }
        List<UserInfoResp> curList = crossAppModel.userList;
        if (curList == null) {
            return;
        }
        List<Long> userIdList = new ArrayList<>();
        for (UserInfoResp userInfoResp : curList) {
            if (userInfoResp.userId > 0) {
                userIdList.add(userInfoResp.userId);
            }
        }
        if (userIdList.size() == 0) {
            callbackUpdateCrossApp();
            return;
        }
        UserInfoRepository.getUserInfoList(parentManager, userIdList, new UserInfoRepository.UserInfoResultListener() {
            @Override
            public void userInfoList(List<UserInfoResp> userInfos) {
                if (userInfos == null) {
                    return;
                }
                for (UserInfoResp userInfo : userInfos) {
                    for (UserInfoResp curUserInfo : curList) {
                        if (userInfo.userId == curUserInfo.userId) {
                            curUserInfo.setUserInfo(userInfo);
                            break;
                        }
                    }
                }
                callbackUpdateCrossApp();
            }
        });
    }

    private void checkInitMatchNumber() {
        // 开启匹配的时候，这里判断一下要不要为匹配人数赋值
        if (crossAppModel != null && crossAppModel.matchStatus == CrossAppMatchStatus.MATCHING) {
            crossAppModel.curNum = getStallUserSize();
            if (crossAppModel.gameModel != null) {
                crossAppModel.totalNum = crossAppModel.gameModel.getGameMaxNumber();
            }
        }
    }

    private int getStallUserSize() {
        int count = 0;
        if (crossAppModel.userList != null) {
            for (UserInfoResp userInfoResp : crossAppModel.userList) {
                if (userInfoResp.existsUser()) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        parentManager.sceneEngineManager.removeCommandListener(crossAppCmdUsersChangeListener);
        parentManager.sceneEngineManager.removeCommandListener(crossAppCmdStatusChangeListener);
        parentManager.sceneEngineManager.removeCommandListener(crossAppCmdTeamChangeListener);
        parentManager.sceneEngineManager.removeCommandListener(crossAppCmdGameSwitchListener);
        checkExitCrossApp();
    }

    private void checkExitCrossApp() {
        if (crossAppModel == null || !isInTeam()) {
            return;
        }
        switch (crossAppModel.matchStatus) {
            case CrossAppMatchStatus.TEAM:
            case CrossAppMatchStatus.MATCH_SUCCESS:
                crossAppExitTeam(false);
                break;
            case CrossAppMatchStatus.MATCHING:
            case CrossAppMatchStatus.MATCH_FAILED:
                cancelMatch(false, () -> {
                    crossAppExitTeam(false);
                });
                break;
        }
    }

    private boolean isInTeam() {
        if (crossAppModel != null && crossAppModel.userList != null) {
            for (UserInfoResp userInfoResp : crossAppModel.userList) {
                if (userInfoResp.userId == HSUserInfo.userId) {
                    return true;
                }
            }
        }
        return false;
    }

}
