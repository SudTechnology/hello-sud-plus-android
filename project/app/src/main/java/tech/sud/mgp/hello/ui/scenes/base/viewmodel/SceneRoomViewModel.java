package tech.sud.mgp.hello.ui.scenes.base.viewmodel;

import com.blankj.utilcode.util.ThreadUtils;

import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.main.manager.HomeManager;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;

/**
 * 房间业务
 */
public class SceneRoomViewModel extends BaseViewModel {

    private int getGameListErrCount;
    public GameListResp mGameListResp;

    /** 初始化数据 */
    public void initData() {
        getGameList();
    }

    /** 获取游戏列表 */
    private void getGameList() {
        if (mGameListResp != null) {
            return;
        }

        HomeRepository.gameList(null, new RxCallback<GameListResp>() {
            @Override
            public void onNext(BaseResponse<GameListResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    mGameListResp = t.getData();
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
     * 获取游戏最大可支持人数
     *
     * @param gameId
     * @return
     */
    public int getGameMaxNumber(long gameId) {
        GameModel gameModel = HomeManager.getInstance().getGameModel(mGameListResp, gameId);
        if (gameModel != null) {
            return gameModel.getGameMaxNumber();
        }
        return 0;
    }

}
