package tech.sud.mgp.hello.ui.scenes.crossapp.viewmodel;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.CrossAppGameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;

/**
 * 跨域 业务
 */
public class CrossAppViewModel extends BaseViewModel {

    public MutableLiveData<GameModel> matchGameModelLiveData = new MutableLiveData<>();

    /** 获取匹配的游戏model */
    public void getMatchGameModel(long matchGameId) {
        HomeRepository.crossAppGameList(null, new RxCallback<CrossAppGameListResp>() {
            @Override
            public void onSuccess(CrossAppGameListResp resp) {
                super.onSuccess(resp);
                if (resp != null) {
                    GameModel gameModel = findGameModel(matchGameId, resp.allGameList);
                    matchGameModelLiveData.setValue(gameModel);
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

}
