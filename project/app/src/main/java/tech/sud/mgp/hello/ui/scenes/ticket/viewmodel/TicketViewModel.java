package tech.sud.mgp.hello.ui.scenes.ticket.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.TicketConfirmJoinResp;
import tech.sud.mgp.hello.ui.main.constant.SceneType;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;

/**
 * 门票场景业务
 */
public class TicketViewModel extends ViewModel {

    // 返回局id
    public MutableLiveData<TicketConfirmJoinResp> ticketConfirmJoinMutableLiveData = new MutableLiveData<>();

    // 发送确认加入的消息到后端
    public void sendJoinMsg(LifecycleOwner owner, RoomInfoModel model) {
        HomeRepository.ticketConfirmJoin(SceneType.TICKET, model.roomId,
                model.gameId, model.gameLevel, owner, new RxCallback<TicketConfirmJoinResp>() {
                    @Override
                    public void onSuccess(TicketConfirmJoinResp ticketConfirmJoinResp) {
                        super.onSuccess(ticketConfirmJoinResp);
                        ticketConfirmJoinMutableLiveData.setValue(ticketConfirmJoinResp);
                    }
                });
    }

}
