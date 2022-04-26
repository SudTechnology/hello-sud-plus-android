package tech.sud.mgp.hello.ui.scenes.base.viewmodel;

import com.blankj.utilcode.util.ThreadUtils;

import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModeModel;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;

/**
 * 房间业务
 */
public class SceneRoomViewModel extends BaseViewModel {

    private int getGameListErrCount;

    /** 初始化数据 */
    public void initData() {
        getGameList();
    }

    /** 获取游戏列表 */
    private void getGameList() {
        HomeRepository.gameList(null, new RxCallback<GameListResp>() {
            @Override
            public void onNext(BaseResponse<GameListResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    HomeManager.getInstance().gameListResp = t.getData();
                } else {
                    delayGetGameList();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                delayGetGameList();
            }
        });
    }

    private void delayGetGameList() {
        ThreadUtils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                getGameListErrCount++;
                if (getGameListErrCount > 3) {
                    return;
                }
                getGameList();
            }
        }, 3000);
    }

    /**
     * 退出房间
     */
    public void exitRoom(Long roomId) {
        RoomRepository.exitRoom(null, roomId, new RxCallback<>());
    }

    /**
     * 获取游戏最大可支持人数
     *
     * @param gameId
     * @return
     */
    public int getGameMaxNumber(long gameId) {
        GameModel gameModel = HomeManager.getInstance().getGameModel(gameId);
        if (gameModel != null && gameModel.gameModeList != null && gameModel.gameModeList.size() > 0) {
            GameModeModel gameModeModel = gameModel.gameModeList.get(0);
            if (gameModeModel.mode != 1) {
                for (GameModeModel model : gameModel.gameModeList) {
                    if (model.mode == 1) {
                        gameModeModel = model;
                    }
                }
            }
            if (gameModeModel.count.length >= 2) {
                return gameModeModel.count[1];
            }
        }
        return 0;
    }

}
