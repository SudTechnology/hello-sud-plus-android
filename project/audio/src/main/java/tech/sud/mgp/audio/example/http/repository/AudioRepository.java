package tech.sud.mgp.audio.example.http.repository;

import androidx.lifecycle.LifecycleOwner;

import tech.sud.mgp.audio.example.http.method.AudioRequestMethodFactory;
import tech.sud.mgp.audio.example.http.req.EnterRoomReq;
import tech.sud.mgp.audio.example.http.response.EnterRoomResp;
import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.http.rx.RxUtil;

public class AudioRepository {

    public static void enterRoom(LifecycleOwner owner, Long roomId, RxCallback<EnterRoomResp> callback) {
        EnterRoomReq req = new EnterRoomReq();
        if (roomId != null) {
            req.roomId = roomId;
        }
        AudioRequestMethodFactory.getMethod()
                .enterRoom(req)
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

}
