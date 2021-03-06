package tech.sud.mgp.hello.service.room.repository;

import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtils;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.service.room.method.AudioRequestMethodFactory;
import tech.sud.mgp.hello.service.room.req.DanmakuListReq;
import tech.sud.mgp.hello.service.room.req.DeductionCoinReq;
import tech.sud.mgp.hello.service.room.req.DiscoAnchorListReq;
import tech.sud.mgp.hello.service.room.req.DiscoSwitchAnchorReq;
import tech.sud.mgp.hello.service.room.req.EnterRoomReq;
import tech.sud.mgp.hello.service.room.req.ExitRoomReq;
import tech.sud.mgp.hello.service.room.req.GiftListReq;
import tech.sud.mgp.hello.service.room.req.QuizGamePlayerReq;
import tech.sud.mgp.hello.service.room.req.RobotListReq;
import tech.sud.mgp.hello.service.room.req.RoomMicListReq;
import tech.sud.mgp.hello.service.room.req.RoomMicSwitchReq;
import tech.sud.mgp.hello.service.room.req.RoomOrderCreateReq;
import tech.sud.mgp.hello.service.room.req.RoomOrderReceiveReq;
import tech.sud.mgp.hello.service.room.req.RoomPkAgainReq;
import tech.sud.mgp.hello.service.room.req.RoomPkAgreeReq;
import tech.sud.mgp.hello.service.room.req.RoomPkDurationReq;
import tech.sud.mgp.hello.service.room.req.RoomPkRemoveRivalReq;
import tech.sud.mgp.hello.service.room.req.RoomPkStartReq;
import tech.sud.mgp.hello.service.room.req.RoomPkSwitchReq;
import tech.sud.mgp.hello.service.room.req.SendDanmakuReq;
import tech.sud.mgp.hello.service.room.req.SendGiftReq;
import tech.sud.mgp.hello.service.room.resp.DanmakuListResp;
import tech.sud.mgp.hello.service.room.resp.DiscoAnchorListResp;
import tech.sud.mgp.hello.service.room.resp.EnterRoomResp;
import tech.sud.mgp.hello.service.room.resp.GiftListResp;
import tech.sud.mgp.hello.service.room.resp.QuizGamePlayerResp;
import tech.sud.mgp.hello.service.room.resp.RobotListResp;
import tech.sud.mgp.hello.service.room.resp.RoomMicListResp;
import tech.sud.mgp.hello.service.room.resp.RoomMicSwitchResp;
import tech.sud.mgp.hello.service.room.resp.RoomOrderCreateResp;
import tech.sud.mgp.hello.service.room.resp.RoomPkAgainResp;
import tech.sud.mgp.hello.service.room.resp.RoomPkAgreeResp;
import tech.sud.mgp.hello.service.room.resp.RoomPkStartResp;

public class RoomRepository {

    /**
     * ????????????
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
     * ????????????
     *
     * @param roomId ??????id
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
     * ????????????????????????
     *
     * @param roomId ??????id
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
     * ?????????????????????
     *
     * @param roomId   ??????id
     * @param micIndex ????????????
     * @param operate  true?????? false??????
     */
    public static void roomMicLocationSwitch(LifecycleOwner owner, long roomId, int micIndex, boolean operate, RxCallback<RoomMicSwitchResp> callback) {
        roomMicLocationSwitch(owner, roomId, micIndex, operate, null, callback);
    }

    /**
     * ?????????????????????
     *
     * @param roomId   ??????id
     * @param micIndex ????????????
     * @param operate  true?????? false??????
     * @param userId   ???????????????id
     */
    public static void roomMicLocationSwitch(LifecycleOwner owner, long roomId, int micIndex, boolean operate, Long userId, RxCallback<RoomMicSwitchResp> callback) {
        RoomMicSwitchReq req = new RoomMicSwitchReq();
        req.roomId = roomId;
        req.micIndex = micIndex;
        if (operate) {
            req.handleType = 0;
        } else {
            req.handleType = 1;
        }
        req.userId = userId;
        AudioRequestMethodFactory.getMethod()
                .roomMicSwitch(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * ??????????????????
     *
     * @param roomId     ??????id
     * @param userIdList ??????????????????id
     * @param gameId     ??????id
     */
    public static void roomOrderCreate(LifecycleOwner owner, long roomId, List<Long> userIdList, long gameId,
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
     * ??????????????????
     *
     * @param orderId ??????id
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
     * ????????????pk??????
     *
     * @param roomId ??????Id
     * @param minute ???????????????
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
     * ????????????pk??????
     *
     * @param roomId   ??????Id
     * @param pkSwitch true: ??????PK  false:??????PK
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
     * ????????????Pk
     *
     * @param roomId ??????Id
     * @param minute ???????????????
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
     * ??????PK
     *
     * @param srcRoomId  PK????????????id
     * @param destRoomId PK????????????id
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
     * ??????pk??????pk??????
     *
     * @param roomId ??????Id
     */
    public static void roomPkRemoveRival(LifecycleOwner owner, long roomId, RxCallback<Object> callback) {
        RoomPkRemoveRivalReq req = new RoomPkRemoveRivalReq();
        req.roomId = roomId;
        AudioRequestMethodFactory.getMethod()
                .roomPkRemoveRival(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * pk????????????
     *
     * @param roomId ??????id
     * @param minute ???????????????
     */
    public static void roomPkAgain(LifecycleOwner owner, long roomId, int minute, RxCallback<RoomPkAgainResp> callback) {
        RoomPkAgainReq req = new RoomPkAgainReq();
        req.roomId = roomId;
        req.minute = minute;
        AudioRequestMethodFactory.getMethod()
                .roomPkAgain(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param roomId     ??????id
     * @param playerList ????????????
     */
    public static void quizGamePlayer(LifecycleOwner owner, long roomId, List<Long> playerList, RxCallback<QuizGamePlayerResp> callback) {
        QuizGamePlayerReq req = new QuizGamePlayerReq();
        req.roomId = roomId;
        req.playerList = playerList;
        AudioRequestMethodFactory.getMethod()
                .quizGamePlayer(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * ????????????
     *
     * @param gameId ??????id
     */
    public static void danmakuList(LifecycleOwner owner, long gameId, RxCallback<DanmakuListResp> callback) {
        DanmakuListReq req = new DanmakuListReq();
        req.gameId = gameId;
        AudioRequestMethodFactory.getMethod()
                .danmakuList(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * ????????????
     *
     * @param roomId  ??????id
     * @param content ????????????????????????????????????????????????
     */
    public static void sendDanmaku(LifecycleOwner owner, long roomId, String content, RxCallback<Object> callback) {
        SendDanmakuReq req = new SendDanmakuReq();
        req.roomId = roomId;
        req.content = content;
        AudioRequestMethodFactory.getMethod()
                .sendDanmaku(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * ????????????
     *
     * @param roomId         ??????id
     * @param giftId         ??????id
     * @param amount         ?????????
     * @param giftConfigType ?????????????????????1???????????????2???????????????
     * @param giftPrice      ????????????(??????)
     */
    public static void sendGift(LifecycleOwner owner, long roomId, long giftId, int amount,
                                int giftConfigType, int giftPrice, RxCallback<Object> callback) {
        SendGiftReq req = new SendGiftReq();
        req.roomId = roomId;
        req.giftId = giftId;
        req.amount = amount;
        req.giftConfigType = giftConfigType;
        req.giftPrice = giftPrice;
        AudioRequestMethodFactory.getMethod()
                .sendGift(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * ????????????
     *
     * @param sceneId ??????id
     * @param gameId  ??????id
     */
    public static void giftList(LifecycleOwner owner, int sceneId, long gameId, RxCallback<GiftListResp> callback) {
        GiftListReq req = new GiftListReq();
        req.sceneId = sceneId;
        req.gameId = gameId;
        AudioRequestMethodFactory.getMethod()
                .giftList(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * ???????????????
     *
     * @param count ????????????
     */
    public static void robotList(LifecycleOwner owner, int count, RxCallback<RobotListResp> callback) {
        RobotListReq req = new RobotListReq();
        req.count = count;
        AudioRequestMethodFactory.getMethod()
                .robotList(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * ??????
     *
     * @param price ??????
     */
    public static void deductionCoin(LifecycleOwner owner, int price, RxCallback<Object> callback) {
        DeductionCoinReq req = new DeductionCoinReq();
        req.price = price;
        AudioRequestMethodFactory.getMethod()
                .deductionCoin(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * ??????????????????
     *
     * @param roomId ??????id
     */
    public static void discoAnchorList(LifecycleOwner owner, long roomId, RxCallback<DiscoAnchorListResp> callback) {
        DiscoAnchorListReq req = new DiscoAnchorListReq();
        req.roomId = roomId;
        AudioRequestMethodFactory.getMethod()
                .discoAnchorList(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * ???/????????????
     *
     * @param roomId     ??????id
     * @param handleType 1??????2???
     * @param userId     ??????id
     */
    public static void discoSwitchAnchor(LifecycleOwner owner, long roomId, int handleType, long userId, RxCallback<Object> callback) {
        DiscoSwitchAnchorReq req = new DiscoSwitchAnchorReq();
        req.roomId = roomId;
        req.handleType = handleType;
        req.userId = userId;
        AudioRequestMethodFactory.getMethod()
                .discoSwitchAnchor(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

}
