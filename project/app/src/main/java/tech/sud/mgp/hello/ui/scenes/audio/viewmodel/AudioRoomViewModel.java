package tech.sud.mgp.hello.ui.scenes.audio.viewmodel;

import tech.sud.mgp.hello.service.room.repository.AudioRepository;
import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.rx.RxCallback;

public class AudioRoomViewModel extends BaseViewModel {

    /**
     * 退出房间
     */
    public void exitRoom(Long roomId) {
        AudioRepository.exitRoom(null, roomId, new RxCallback<>());
    }

}
