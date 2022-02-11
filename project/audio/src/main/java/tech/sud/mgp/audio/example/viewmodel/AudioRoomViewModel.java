package tech.sud.mgp.audio.example.viewmodel;

import tech.sud.mgp.audio.example.http.repository.AudioRepository;
import tech.sud.mgp.common.base.BaseViewModel;
import tech.sud.mgp.common.http.rx.RxCallback;

public class AudioRoomViewModel extends BaseViewModel {

    /**
     * 退出房间
     */
    public void exitRoom(Long roomId) {
        AudioRepository.exitRoom(null, roomId, new RxCallback<>());
    }

}
