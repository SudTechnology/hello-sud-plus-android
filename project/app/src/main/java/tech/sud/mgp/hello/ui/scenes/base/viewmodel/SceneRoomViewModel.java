package tech.sud.mgp.hello.ui.scenes.base.viewmodel;

import com.blankj.utilcode.util.ThreadUtils;

import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.req.GameListReq;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;

/**
 * 房间业务
 */
public class SceneRoomViewModel extends BaseViewModel {

    private int getGameListTabSceneErrCount;

    private int getGameListTabGameErrCount;

    /** 初始化数据 */
    public void initData() {
        getGameListTabScene();
        getGameListTabGame();
    }

    /** 获取游戏列表 */
    private void getGameListTabScene() {
        if (HomeManager.getInstance().mGameListRespTabScene != null) {
            return;
        }

        HomeRepository.gameListV2(null, GameListReq.TAB_SCENE, new RxCallback<GameListResp>() {
            @Override
            public void onNext(BaseResponse<GameListResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    HomeManager.getInstance().mGameListRespTabScene = t.getData();
                } else {
                    delayGetGameListTabScene();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                delayGetGameListTabScene();
            }
        });
    }

    private void delayGetGameListTabScene() {
        ThreadUtils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                getGameListTabSceneErrCount++;
                if (getGameListTabSceneErrCount > 3) {
                    return;
                }
                getGameListTabScene();
            }
        }, 3000);
    }

    /** 获取游戏列表 */
    private void getGameListTabGame() {
        if (HomeManager.getInstance().mGameListRespTabGame != null) {
            return;
        }

        HomeRepository.gameListV2(null, GameListReq.TAB_GAME, new RxCallback<GameListResp>() {
            @Override
            public void onNext(BaseResponse<GameListResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    HomeManager.getInstance().mGameListRespTabGame = t.getData();
                } else {
                    delayGetGameListTabGame();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                delayGetGameListTabGame();
            }
        });
    }

    private void delayGetGameListTabGame() {
        ThreadUtils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                getGameListTabGameErrCount++;
                if (getGameListTabGameErrCount > 3) {
                    return;
                }
                getGameListTabGame();
            }
        }, 3000);
    }

    /**
     * 获取游戏最大可支持人数
     *
     * @param gameId
     * @return
     */
    public int getGameMaxNumber(long gameId) {
        GameModel gameModel = getGameModel(gameId);
        if (gameModel != null) {
            return gameModel.getGameMaxNumber();
        }
        return 0;
    }

    public GameModel getGameModel(long gameId) {
        return HomeManager.getInstance().getGameModel(gameId);
    }

}
