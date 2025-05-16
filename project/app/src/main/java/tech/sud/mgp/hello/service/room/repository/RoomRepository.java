package tech.sud.mgp.hello.service.room.repository;

import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtils;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.service.room.method.AudioRequestMethodFactory;
import tech.sud.mgp.hello.service.room.req.Audio3DGetConfigReq;
import tech.sud.mgp.hello.service.room.req.Audio3DLockMicReq;
import tech.sud.mgp.hello.service.room.req.Audio3DMicListReq;
import tech.sud.mgp.hello.service.room.req.Audio3DSwitchMicReq;
import tech.sud.mgp.hello.service.room.req.Audio3DUpdateMicrophoneStateReq;
import tech.sud.mgp.hello.service.room.req.CrossAppCancelMatchReq;
import tech.sud.mgp.hello.service.room.req.CrossAppJoinTeamReq;
import tech.sud.mgp.hello.service.room.req.CrossAppQuitTeamReq;
import tech.sud.mgp.hello.service.room.req.CrossAppStartMatchReq;
import tech.sud.mgp.hello.service.room.req.CrossAppSwitchGameReq;
import tech.sud.mgp.hello.service.room.req.DanmakuListReq;
import tech.sud.mgp.hello.service.room.req.DeductionCoinReq;
import tech.sud.mgp.hello.service.room.req.DiscoAnchorListReq;
import tech.sud.mgp.hello.service.room.req.DiscoSwitchAnchorReq;
import tech.sud.mgp.hello.service.room.req.EnterRoomReq;
import tech.sud.mgp.hello.service.room.req.ExitRoomReq;
import tech.sud.mgp.hello.service.room.req.GetMonopolyCardsReq;
import tech.sud.mgp.hello.service.room.req.GiftListReq;
import tech.sud.mgp.hello.service.room.req.LeaguePlayingReq;
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
import tech.sud.mgp.hello.service.room.req.SetCrRoomConfigReq;
import tech.sud.mgp.hello.service.room.req.WebGameTokenReq;
import tech.sud.mgp.hello.service.room.resp.CrossAppStartMatchResp;
import tech.sud.mgp.hello.service.room.resp.DanmakuListResp;
import tech.sud.mgp.hello.service.room.resp.DiscoAnchorListResp;
import tech.sud.mgp.hello.service.room.resp.EnterRoomResp;
import tech.sud.mgp.hello.service.room.resp.GiftListResp;
import tech.sud.mgp.hello.service.room.resp.LeaguePlayingResp;
import tech.sud.mgp.hello.service.room.resp.MonopolyCardsResp;
import tech.sud.mgp.hello.service.room.resp.QuizGamePlayerResp;
import tech.sud.mgp.hello.service.room.resp.RobotListResp;
import tech.sud.mgp.hello.service.room.resp.RoomMicListResp;
import tech.sud.mgp.hello.service.room.resp.RoomMicSwitchResp;
import tech.sud.mgp.hello.service.room.resp.RoomOrderCreateResp;
import tech.sud.mgp.hello.service.room.resp.RoomPkAgainResp;
import tech.sud.mgp.hello.service.room.resp.RoomPkAgreeResp;
import tech.sud.mgp.hello.service.room.resp.RoomPkStartResp;
import tech.sud.mgp.hello.service.room.resp.WebGameTokenResp;

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
        roomMicLocationSwitch(owner, roomId, micIndex, operate, null, callback);
    }

    /**
     * 房间上下麦接口
     *
     * @param roomId   房间id
     * @param micIndex 麦位索引
     * @param operate  true上麦 false下麦
     * @param userId   上麦的用户id
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
     * 房间用户点单
     *
     * @param roomId     房间id
     * @param userIdList 受邀主播用户id
     * @param gameId     游戏id
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

    /**
     * pk再来一局
     *
     * @param roomId 房间id
     * @param minute 时长分钟数
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
     * 查询竞猜场景游戏玩家列表（房间内）
     *
     * @param roomId     房间id
     * @param playerList 玩家列表
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
     * 弹幕列表
     *
     * @param gameId 游戏id
     */
    public static void danmakuList(LifecycleOwner owner, long gameId, long roomId, RxCallback<DanmakuListResp> callback) {
        DanmakuListReq req = new DanmakuListReq();
        req.gameId = gameId;
        req.roomId = roomId;
        AudioRequestMethodFactory.getMethod()
                .danmakuList(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 发送弹幕
     *
     * @param roomId  房间id
     * @param content 弹幕内容（具体值跟游戏类型相关）
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
     * 发送礼物
     *
     * @param roomId         房间id
     * @param giftId         礼物id
     * @param amount         总数量
     * @param giftConfigType 礼物配置方式（1：客户端，2：服务端）
     * @param giftPrice      礼物价格(金币)
     */
    public static void sendGift(LifecycleOwner owner, long roomId, long giftId, int amount,
                                int giftConfigType, int giftPrice, List<String> receiverList, RxCallback<Object> callback) {
        SendGiftReq req = new SendGiftReq();
        req.roomId = roomId;
        req.giftId = giftId;
        req.amount = amount;
        req.giftConfigType = giftConfigType;
        req.giftPrice = giftPrice;
        req.receiverList = receiverList;
        AudioRequestMethodFactory.getMethod()
                .sendGift(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 礼物列表
     *
     * @param sceneId 场景id
     * @param gameId  游戏id
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
     * 机器人列表
     *
     * @param count 获取数量
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
     * 扣费
     *
     * @param price 数额
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
     * 蹦迪主播列表
     *
     * @param roomId 房间id
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
     * 上/下主播位
     *
     * @param roomId     房间id
     * @param handleType 1上，2下
     * @param userId     用户id
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

    // region 联赛

    /**
     * 查询进入前三的房间
     *
     * @param gameId 游戏id
     */
    public static void leaguePlaying(LifecycleOwner owner, long gameId, RxCallback<LeaguePlayingResp> callback) {
        LeaguePlayingReq req = new LeaguePlayingReq();
        req.gameId = gameId;
        AudioRequestMethodFactory.getMethod()
                .leaguePlaying(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }
    // endregion 联赛

    // region 跨域

    /** 加入组队 */
    public static void crossAppJoinTeam(LifecycleOwner owner, long roomId, long gameId, int index, RxCallback<Object> callback) {
        CrossAppJoinTeamReq req = new CrossAppJoinTeamReq();
        req.roomId = roomId;
        req.gameId = gameId;
        req.index = index;
        AudioRequestMethodFactory.getMethod()
                .crossAppJoinTeam(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /** 开启匹配 */
    public static void crossAppStartMatch(LifecycleOwner owner, long roomId, long gameId, RxCallback<CrossAppStartMatchResp> callback) {
        CrossAppStartMatchReq req = new CrossAppStartMatchReq();
        req.roomId = roomId;
        req.gameId = gameId;
        AudioRequestMethodFactory.getMethod()
                .crossAppStartMatch(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /** 取消匹配 */
    public static void crossAppCancelMatch(LifecycleOwner owner, String groupId, long roomId, long gameId, RxCallback<Object> callback) {
        CrossAppCancelMatchReq req = new CrossAppCancelMatchReq();
        req.groupId = groupId;
        req.roomId = roomId;
        req.gameId = gameId;
        AudioRequestMethodFactory.getMethod()
                .crossAppCancelMatch(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /** 退出组队 */
    public static void crossAppQuitTeam(LifecycleOwner owner, long roomId, RxCallback<Object> callback) {
        CrossAppQuitTeamReq req = new CrossAppQuitTeamReq();
        req.roomId = roomId;
        AudioRequestMethodFactory.getMethod()
                .crossAppQuitTeam(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /** 切换游戏 */
    public static void crossAppSwitchGame(LifecycleOwner owner, long roomId, long gameId, RxCallback<Object> callback) {
        CrossAppSwitchGameReq req = new CrossAppSwitchGameReq();
        req.roomId = roomId;
        req.gameId = gameId;
        AudioRequestMethodFactory.getMethod()
                .crossAppSwitchGame(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }
    // endregion 跨域

    // region 3D语聊房

    /** 上麦或下麦 */
    public static void audio3DSwitchMic(LifecycleOwner owner, Audio3DSwitchMicReq req, RxCallback<Object> callback) {
        AudioRequestMethodFactory.getMethod()
                .audio3DSwitchMic(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /** 更新麦克风信息 */
    public static void audio3DUpdateMicrophoneState(LifecycleOwner owner, Audio3DUpdateMicrophoneStateReq req, RxCallback<Object> callback) {
        AudioRequestMethodFactory.getMethod()
                .audio3DUpdateMicrophoneState(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /** 锁/解锁麦位 */
    public static void audio3DLockMic(LifecycleOwner owner, Audio3DLockMicReq req, RxCallback<Object> callback) {
        AudioRequestMethodFactory.getMethod()
                .audio3DLockMic(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /** 查询麦位列表 */
    public static void audio3DMicList(LifecycleOwner owner, Audio3DMicListReq req, RxCallback<SudGIPAPPState.AppCustomCrSetSeats> callback) {
        AudioRequestMethodFactory.getMethod()
                .audio3DMicList(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /** 获取配置 */
    public static void audio3DGetConfig(LifecycleOwner owner, Audio3DGetConfigReq req, RxCallback<SudGIPAPPState.AppCustomCrSetRoomConfig> callback) {
        AudioRequestMethodFactory.getMethod()
                .audio3DGetConfig(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /** 设置配置 */
    public static void audio3DSetConfig(LifecycleOwner owner, long roomId, SudGIPAPPState.AppCustomCrSetRoomConfig config, RxCallback<Object> callback) {
        SetCrRoomConfigReq req = new SetCrRoomConfigReq();
        req.roomId = roomId;
        req.setConfig(config);
        AudioRequestMethodFactory.getMethod()
                .audio3DSetConfig(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }
    // endregion 3D语聊房

    /** 大富翁道具列表 */
    public static void getMonopolyCards(LifecycleOwner owner, RxCallback<MonopolyCardsResp> callback) {
        GetMonopolyCardsReq req = new GetMonopolyCardsReq();
        AudioRequestMethodFactory.getMethod()
                .getMonopolyCards(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /** web游戏登录token信息 */
    public static void webGameToken(LifecycleOwner owner, String roomId, long gameId, RxCallback<WebGameTokenResp> callback) {
        WebGameTokenReq req = new WebGameTokenReq();
        req.roomId = roomId;
        req.gameId = gameId;
        AudioRequestMethodFactory.getMethod()
                .webGameToken(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /** 获取gameScale */
    public static void getGameScale(LifecycleOwner owner, String roomId, long gameId, RxCallback<WebGameTokenResp> callback) {
        WebGameTokenReq req = new WebGameTokenReq();
        req.roomId = roomId;
        req.gameId = gameId;
        AudioRequestMethodFactory.getMethod()
                .getGameScale(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

}
