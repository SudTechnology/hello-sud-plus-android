package tech.sud.mgp.hello.ui.scenes.base.service;

import java.util.List;

import tech.sud.mgp.hello.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.hello.ui.scenes.base.constant.OperateMicType;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftNotifyDetailodel;

public interface SceneRoomServiceCallback {

    /**
     * 进入房间成功
     */
    void onEnterRoomSuccess();

    /**
     * 麦位数据
     *
     * @param list 麦位列表
     */
    void setMicList(List<AudioRoomMicModel> list);

    /**
     * 更新某个麦位
     *
     * @param micIndex
     * @param model
     */
    void notifyMicItemChange(int micIndex, AudioRoomMicModel model);

    /**
     * 自己在哪个麦位上，-1表示不在麦位上
     */
    void selfMicIndex(int micIndex);

    /**
     * 增加一条公屏消息
     */
    void addPublicMsg(Object msg);

    /**
     * 礼物通知
     */
    void sendGiftsNotify(GiftNotifyDetailodel notify);

    /**
     * 麦克风开关状态变化
     */
    void onMicStateChanged(boolean isOpened);

    /**
     * 某个麦位下产生的声浪
     *
     * @param micIndex 麦位索引
     */
    void onSoundLevel(int micIndex);

    /**
     * 房间内当前在线用户数量回调
     *
     * @param roomID 房间ID
     * @param count  人数
     */
    void onRoomOnlineUserCountUpdate(String roomID, int count);

    /**
     * 游戏被房主切换了
     *
     * @param gameId 游戏id
     */
    void onGameChange(long gameId);

    /**
     * 对麦位数据进行赋值
     */
    void onWrapMicModel(AudioRoomMicModel model);

    /**
     * 对音频流监听回调
     */
    void onCapturedAudioData(AudioPCMData audioPCMData);

    /**
     * 标记自己发送的公屏消息
     * 用于处理你画我猜关键字是够命中
     *
     * @param msg 发送内容
     */
    void onSelfSendMsg(String msg);

    /**
     * 麦克切换完成
     *
     * @param micIndex 麦位序号
     * @param operate  true上麦 false下麦
     * @param type     操作类型
     */
    void onMicLocationSwitchCompleted(int micIndex, boolean operate, OperateMicType type);
}