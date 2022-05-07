package tech.sud.mgp.hello.service.room.repository;

import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtils;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.service.room.method.AudioRequestMethodFactory;
import tech.sud.mgp.hello.service.room.req.EnterRoomReq;
import tech.sud.mgp.hello.service.room.req.ExitRoomReq;
import tech.sud.mgp.hello.service.room.req.RoomMicListReq;
import tech.sud.mgp.hello.service.room.req.RoomMicSwitchReq;
import tech.sud.mgp.hello.service.room.req.RoomOrderCreateReq;
import tech.sud.mgp.hello.service.room.req.RoomOrderReceiveReq;
import tech.sud.mgp.hello.service.room.req.RoomPkAgreeReq;
import tech.sud.mgp.hello.service.room.req.RoomPkDurationReq;
import tech.sud.mgp.hello.service.room.req.RoomPkRemoveRivalReq;
import tech.sud.mgp.hello.service.room.req.RoomPkStartReq;
import tech.sud.mgp.hello.service.room.req.RoomPkSwitchReq;
import tech.sud.mgp.hello.service.room.response.EnterRoomResp;
import tech.sud.mgp.hello.service.room.response.RoomMicListResp;
import tech.sud.mgp.hello.service.room.response.RoomMicSwitchResp;
import tech.sud.mgp.hello.service.room.response.RoomOrderCreateResp;
import tech.sud.mgp.hello.service.room.response.RoomPkAgreeResp;
import tech.sud.mgp.hello.service.room.response.RoomPkStartResp;

public class RoomRepository {

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
                .compose(RxUtils.schedulers(owner))
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
                .compose(RxUtils.schedulers(owner))
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
                .compose(RxUtils.schedulers(owner))
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
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 房间用户点单
     *
     * @param roomId     房间id
     * @param userIdList 受邀主播用户id
     * @param gameId     游戏id
     */
    public static void roomOrderCreate(LifecycleOwner owner,
                                       long roomId,
                                       List<Long> userIdList,
                                       long gameId,
                                       RxCallback<RoomOrderCreateResp> callback) {
        RoomOrderCreateReq req = new RoomOrderCreateReq();
        req.roomId = roomId;
        req.userIdList = userIdList;
        req.gameId = gameId;
        AudioRequestMethodFactory.getMethod()
                .roomOrderCreate(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 房间主播接单
     *
     * @param orderId 订单id
     */
    public static void roomOrderReceive(LifecycleOwner owner, long orderId, RxCallback<Object> callback) {
        RoomOrderReceiveReq req = new RoomOrderReceiveReq();
        req.orderId = orderId;
        AudioRequestMethodFactory.getMethod()
                .roomOrderReceive(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 设置跨房pk时长
     *
     * @param roomId 房间Id
     * @param minute 时长分钟数
     */
    public static void roomPkDuration(LifecycleOwner owner, long roomId, int minute, RxCallback<Object> callback) {
        RoomPkDurationReq req = new RoomPkDurationReq();
        req.roomId = roomId;
        req.minute = minute;
        AudioRequestMethodFactory.getMethod()
                .roomPkDuration(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 设置跨房pk开关
     *
     * @param roomId   房间Id
     * @param pkSwitch true: 开启PK  false:关闭PK
     */
    public static void roomPkSwitch(LifecycleOwner owner, long roomId, boolean pkSwitch, RxCallback<Object> callback) {
        RoomPkSwitchReq req = new RoomPkSwitchReq();
        req.roomId = roomId;
        req.pkSwitch = pkSwitch;
        AudioRequestMethodFactory.getMethod()
                .roomPkSwitch(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 开始跨房Pk
     *
     * @param roomId 房间Id
     * @param minute 时长分钟数
     */
    public static void roomPkStart(LifecycleOwner owner, long roomId, int minute, RxCallback<RoomPkStartResp> callback) {
        RoomPkStartReq req = new RoomPkStartReq();
        req.roomId = roomId;
        req.minute = minute;
        AudioRequestMethodFactory.getMethod()
                .roomPkStart(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 同意PK
     *
     * @param srcRoomId  PK发起房间id
     * @param destRoomId PK受邀房间id
     */
    public static void roomPkAgree(LifecycleOwner owner, long srcRoomId, long destRoomId,
                                   RxCallback<RoomPkAgreeResp> callback) {
        RoomPkAgreeReq req = new RoomPkAgreeReq();
        req.srcRoomId = srcRoomId;
        req.destRoomId = destRoomId;
        AudioRequestMethodFactory.getMethod()
                .roomPkAgree(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 跨房pk移除pk对手
     *
     * @param roomId 房间Id
     */
    public static void roomPkRemoveRival(LifecycleOwner owner, long roomId, RxCallback<Object> callback) {
        RoomPkRemoveRivalReq req = new RoomPkRemoveRivalReq();
        req.roomId = roomId;
        AudioRequestMethodFactory.getMethod()
                .roomPkRemoveRival(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

}
