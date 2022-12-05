package tech.sud.mgp.hello.ui.scenes.base.manager;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.CrossAppGameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.CrossAppModel;
import tech.sud.mgp.hello.service.room.resp.CrossAppStartMatchResp;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.ResultListener;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.base.utils.GameUtils;
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

    public void onEnterRoomSuccess() {
        crossAppModel = parentManager.getRoomInfoModel().crossAppModel;
        if (crossAppModel == null) {
            crossAppModel = new CrossAppModel();
        }
        initData();
    }

    private void initData() {
        getMatchGameModel(crossAppModel.matchGameId, () -> {
            initStallList();
            callbackUpdateCrossApp();
            if (crossAppModel.isFastMatch) {
                fastMatch();
            }
        });
    }

    private void initStallList() {
        List<UserInfoResp> oldList = new ArrayList<>();
        if (crossAppModel.userList != null) {
            oldList.addAll(crossAppModel.userList);
        }
        crossAppModel.userList = new ArrayList<>();
        int gameMaxNumber = GameUtils.getGameMaxNumber(crossAppModel.gameModel);
        for (int i = 0; i < gameMaxNumber; i++) {
            UserInfoResp model = findUserInfo(oldList, i);
            if (model == null) {
                model = new UserInfoResp();
            }
            model.index = i;
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
    private void joinTeam(Integer intentIndex, ResultListener listener) {
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
    private void startMatch() {
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

    /** 获取匹配的游戏model */
    private void getMatchGameModel(long matchGameId, CompletedListener completedListener) {
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

        }
    };

    /** 跨域匹配状态变更通知 */
    public SceneCommandManager.CrossAppCmdStatusChangeListener crossAppCmdStatusChangeListener = new SceneCommandManager.CrossAppCmdStatusChangeListener() {
        @Override
        public void onRecvCommand(CrossAppCmdStatusChangeModel model, String userID) {

        }
    };

    /** 跨域匹配队伍变更通知 */
    public SceneCommandManager.CrossAppCmdTeamChangeListener crossAppCmdTeamChangeListener = new SceneCommandManager.CrossAppCmdTeamChangeListener() {
        @Override
        public void onRecvCommand(CrossAppCmdTeamChangeModel model, String userID) {

        }
    };

    /** 跨域匹配游戏切换通知 */
    public SceneCommandManager.CrossAppCmdGameSwitchListener crossAppCmdGameSwitchListener = new SceneCommandManager.CrossAppCmdGameSwitchListener() {
        @Override
        public void onRecvCommand(CrossAppCmdGameSwitchModel model, String userID) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        parentManager.sceneEngineManager.removeCommandListener(crossAppCmdUsersChangeListener);
        parentManager.sceneEngineManager.removeCommandListener(crossAppCmdStatusChangeListener);
        parentManager.sceneEngineManager.removeCommandListener(crossAppCmdTeamChangeListener);
        parentManager.sceneEngineManager.removeCommandListener(crossAppCmdGameSwitchListener);
    }

}
