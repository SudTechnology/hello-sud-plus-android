package tech.sud.mgp.audio.example.http.repository;

import androidx.lifecycle.LifecycleOwner;

import tech.sud.mgp.audio.example.http.method.AudioRequestMethodFactory;
import tech.sud.mgp.audio.example.http.req.EnterRoomReq;
import tech.sud.mgp.audio.example.http.req.RoomMicListReq;
import tech.sud.mgp.audio.example.http.response.EnterRoomResp;
import tech.sud.mgp.audio.example.http.response.RoomMicListResp;
import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.http.rx.RxUtil;

public class AudioRepository {

    /**
     * 进入房间
     *
     * @param roomId 房间id，传空时，相当于创建一个新的房间
     */
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

    /**
     * 查询麦位列表接口
     *
     * @param roomId 房间id
     */
    public static void getRoomMicList(LifecycleOwner owner, long roomId, RxCallback<RoomMicListResp> callback) {
        RoomMicListReq req = new RoomMicListReq();
        req.roomId = roomId;
        AudioRequestMethodFactory.getMethod()
                .roomMicList(req)
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

}
