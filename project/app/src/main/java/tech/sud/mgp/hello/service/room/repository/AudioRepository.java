package tech.sud.mgp.hello.service.room.repository;

import androidx.lifecycle.LifecycleOwner;

import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtil;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.service.room.method.AudioRequestMethodFactory;
import tech.sud.mgp.hello.service.room.req.EnterRoomReq;
import tech.sud.mgp.hello.service.room.req.ExitRoomReq;
import tech.sud.mgp.hello.service.room.req.RoomMicListReq;
import tech.sud.mgp.hello.service.room.req.RoomMicSwitchReq;
import tech.sud.mgp.hello.service.room.response.EnterRoomResp;
import tech.sud.mgp.hello.service.room.response.RoomMicListResp;
import tech.sud.mgp.hello.service.room.response.RoomMicSwitchResp;

public class AudioRepository {

    /**
     * 进入房间
     */
    public static void enterRoom(LifecycleOwner owner, Long roomId, RxCallback<EnterRoomResp> callback) {
        EnterRoomReq req = new EnterRoomReq();
        if (roomId != null) {
            req.roomId = roomId;
        }
        req.rtcType = AppData.getInstance().getRtcType();
        AudioRequestMethodFactory.getMethod()
                .enterRoom(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 退出房间
     *
     * @param roomId 房间id
     */
    public static void exitRoom(LifecycleOwner owner, Long roomId, RxCallback<Object> callback) {
        ExitRoomReq req = new ExitRoomReq();
        if (roomId != null) {
            req.roomId = roomId;
        }
        AudioRequestMethodFactory.getMethod()
                .exitRoom(BaseUrlManager.getInteractBaseUrl(), req)
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
                .roomMicList(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 房间上下麦接口
     *
     * @param roomId   房间id
     * @param micIndex 麦位索引
     * @param operate  true上麦 false下麦
     */
    public static void roomMicLocationSwitch(LifecycleOwner owner, long roomId, int micIndex, boolean operate, RxCallback<RoomMicSwitchResp> callback) {
        RoomMicSwitchReq req = new RoomMicSwitchReq();
        req.roomId = roomId;
        req.micIndex = micIndex;
        if (operate) {
            req.handleType = 0;
        } else {
            req.handleType = 1;
        }
        AudioRequestMethodFactory.getMethod()
                .roomMicSwitch(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

}
