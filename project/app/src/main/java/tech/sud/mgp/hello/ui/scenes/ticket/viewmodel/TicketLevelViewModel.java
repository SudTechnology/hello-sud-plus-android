package tech.sud.mgp.hello.ui.scenes.ticket.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.ui.main.home.model.MatchRoomModel;

/**
 * 选择门票等级页面的业务逻辑
 */
public class TicketLevelViewModel extends BaseViewModel {

    public MutableLiveData<MatchRoomModel> matchRoomLiveData = new MutableLiveData<>();
    private boolean matching = false;

    // 匹配房间
    public void matchRoom(int sceneType, long gameId, Integer gameLevel, LifecycleOwner owner) {
        if (matching) return;
        matching = true;
        HomeRepository.matchGame(sceneType, gameId, gameLevel, owner, new RxCallback<MatchRoomModel>() {
            @Override
            public void onSuccess(MatchRoomModel matchRoomModel) {
                super.onSuccess(matchRoomModel);
                if (matchRoomModel != null) {
                    matchRoomModel.gameLevel = gameLevel;
                    matchRoomLiveData.setValue(matchRoomModel);
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                matching = false;
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                matching = false;
            }
        });
    }

}
