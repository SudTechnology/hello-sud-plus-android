package tech.sud.mgp.hello.ui.scenes.base.service;

import java.util.List;

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.gip.core.ISudListenerNotifyStateChange;
import tech.sud.mgp.hello.service.room.resp.CrossAppModel;
import tech.sud.mgp.hello.ui.scenes.base.constant.OperateMicType;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.GiftNotifyModel;
import tech.sud.mgp.hello.ui.scenes.base.model.OrderInviteModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.audio3d.Audio3DCmdFaceNotifyModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.danmaku.RoomCmdDanmakuTeamChangeModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.ContributionModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.DanceModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.game.RoomCmdPropsCardGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.monopoly.RoomCmdMonopolyCardGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.order.RoomCmdUserOrderModel;
import tech.sud.mgp.rtc.audio.core.AudioPCMData;

public interface SceneRoomServiceCallback {

    /** 进入房间成功 */
    void onEnterRoomSuccess();

    /**
     * 麦位数据
     *
     * @param list 麦位列表
     */
    void onMicList(List<AudioRoomMicModel> list);

    /**
     * 麦位变动
     *
     * @param micIndex 麦位
     * @param userInfo 用户信息
     * @param isUp     true为上麦 false为下麦
     */
    void onMicChange(int micIndex, UserInfo userInfo, boolean isUp);

    /**
     * 更新某个麦位
     *
     * @param micIndex
     * @param model
     */
    void notifyMicItemChange(int micIndex, AudioRoomMicModel model);

    /** 自己在哪个麦位上，-1表示不在麦位上 */
    void selfMicIndex(int micIndex);

    /** 增加一条公屏消息 */
    void addPublicMsg(Object msg);

    /** 刷新公屏消息 */
    void onChatList(List<Object> list);

    /** 礼物通知 */
    void sendGiftsNotify(GiftNotifyModel notify);

    /** 麦克风开关状态变化 */
    void onMicStateChanged(boolean isOpened);

    /**
     * 某个麦位下产生的声浪
     *
     * @param micIndex   麦位索引
     * @param soundLevel 音量级别，取值范围[0, 100]
     */
    void onSoundLevel(String userId, int micIndex, float soundLevel);

    /**
     * 房间内当前在线用户数量回调
     *
     * @param count 人数
     */
    void onRoomOnlineUserCountUpdate(int count);

    /**
     * 游戏被房主切换了
     *
     * @param gameId 游戏id
     */
    void onGameChange(long gameId);

    /** 对麦位数据进行赋值 */
    void onWrapMicModel(AudioRoomMicModel model);

    /** 对音频流监听回调 */
    void onCapturedAudioData(AudioPCMData audioPCMData);

    /**
     * 标记自己发送的公屏消息
     * 用于处理你画我猜关键字是够命中
     *
     * @param msg 发送内容
     */
    void onSelfSendMsg(String msg);

    /**
     * 麦位切换完成
     *
     * @param micIndex 麦位序号
     * @param operate  true上麦 false下麦
     * @param type     操作类型
     */
    void onMicLocationSwitchCompleted(int micIndex, boolean operate, OperateMicType type);

    /** 收到了点单邀请 */
    void onOrderInvite(RoomCmdUserOrderModel model);

    /** 主播处理用户点单邀请 */
    void onOrderInviteAnswered(OrderInviteModel model);

    /**
     * 用户接收到点单结果
     *
     * @param orderId  订单id
     * @param gameId   游戏id
     * @param gameName 游戏名字
     * @param userId   主播id
     * @param userName 主播名字
     * @param operate  true同意false拒绝
     */
    void onOrderOperate(long orderId, long gameId, String gameName, String userId, String userName, boolean operate);

    /** 主播接收用户点单邀请 */
    void onReceiveInvite(boolean agreeState);

    /** 更新跨房pk信息显示 */
    void onRoomPkUpdate();

    /** 跨房切换游戏 */
    void onRoomPkChangeGame(long gameId);

    /** 跨房移除了对手 */
    void onRoomPkRemoveRival();

    /** 更新倒计时 */
    void onRoomPkCoutndown();

    /** 挂起，重新唤起页面完成的回调，同一房间才有此回调 */
    void onRecoverCompleted();

    /**
     * APP状态通知给小游戏
     *
     * @param state    状态标识
     * @param dataJson 数据
     * @param listener 回调监听
     */
    void notifyStateChange(String state, String dataJson, ISudListenerNotifyStateChange listener);

    // region 蹦迪

    /** 回调整个跳舞队列 */
    void onDanceList(List<DanceModel> list);

    /** 回调页面更新某一个跳舞数据 */
    void onUpdateDance(int index);

    /** 发出跳请时，主播正在跟其他人在跳舞，请等待 */
    void onDanceWait();

    /** 回调蹦迪排行榜 */
    void onDiscoContribution(List<ContributionModel> list);

    /** 倒计时刷新dj */
    void onDJCountdown(int countdown);

    /** 踢出房间 */
    void onKickOutRoom(String userId);
    // endregion 蹦迪

    // region 跨域

    /** 更新跨域信息 */
    void onUpdateCrossApp(CrossAppModel model);
    // endregion 跨域

    // region 弹幕

    /** 弹幕匹配变更 */
    void onDanmakuMatch(RoomCmdDanmakuTeamChangeModel model);

    /** 拉流分辨率变更通知 */
    void onPlayerVideoSizeChanged(String streamID, int width, int height);
    // endregion 弹幕

    // region 3D语聊房

    /** 3D语聊房麦位 */
    void onAudio3DConfig(SudGIPAPPState.AppCustomCrSetRoomConfig model);

    void onAudio3DSeats(SudGIPAPPState.AppCustomCrSetSeats model);

    void onAudio3DFaceNotify(Audio3DCmdFaceNotifyModel model);
    // endregion 3D语聊房

    // region 大富翁
    void onMonopolyCardGiftNotify(RoomCmdMonopolyCardGiftModel model);
    // endregion 大富翁

    /** 游戏道具卡送礼通知 */
    void onGamePropsCardGift(RoomCmdPropsCardGiftModel model);

}
