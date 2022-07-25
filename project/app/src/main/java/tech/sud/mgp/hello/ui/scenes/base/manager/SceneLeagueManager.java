package tech.sud.mgp.hello.ui.scenes.base.manager;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.ui.scenes.base.model.LeagueModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdEnterRoomModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.league.RoomCmdLeagueInfoRespModel;

/**
 * 联赛场景内的业务逻辑
 */
public class SceneLeagueManager extends BaseServiceManager {

    private final LeagueModel leagueModel = new LeagueModel();
    private final SceneRoomServiceManager parentManager;
    private boolean leagueInfoCompleted; // 接收联赛信息是否已完成

    public SceneLeagueManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.sceneEngineManager.setCommandListener(enterRoomCommandListener);
        parentManager.sceneEngineManager.setCommandListener(leagueInfoRespListener);
    }

    public LeagueModel getLeagueManager() {
        return leagueModel;
    }

    public void onGameSettle(SudMGPMGState.MGCommonGameSettle gameSettle) {
        // 整理获胜者
        leagueModel.winner.clear();
        if (gameSettle.results != null) {
            for (SudMGPMGState.MGCommonGameSettle.PlayerResult result : gameSettle.results) {
                if (leagueModel.schedule == 0) {
                    if (result.rank <= 3) {
                        leagueModel.winner.add(result.uid);
                    }
                } else {
                    if (result.rank == 1) {
                        leagueModel.winner.add(result.uid);
                    }
                }
            }
        }

        // 局数加1
        leagueModel.schedule++;
    }

    // 进房信令通知
    public SceneCommandManager.EnterRoomCommandListener enterRoomCommandListener = new SceneCommandManager.EnterRoomCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdEnterRoomModel model, String userID) {
            String cmd = RoomCmdModelUtils.buildCmdLeagueInfoResp(leagueModel.schedule, leagueModel.winner);
            parentManager.sceneEngineManager.sendCommand(cmd);
        }
    };

    // 响应联赛信息信令通知
    public SceneCommandManager.LeagueInfoRespListener leagueInfoRespListener = new SceneCommandManager.LeagueInfoRespListener() {
        @Override
        public void onRecvCommand(RoomCmdLeagueInfoRespModel model, String userID) {
            if (leagueInfoCompleted) {
                return;
            }
            leagueInfoCompleted = true;
            leagueModel.schedule = model.schedule;
            leagueModel.winner = model.winner;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        parentManager.sceneEngineManager.removeCommandListener(enterRoomCommandListener);
        parentManager.sceneEngineManager.removeCommandListener(leagueInfoRespListener);
    }

}
