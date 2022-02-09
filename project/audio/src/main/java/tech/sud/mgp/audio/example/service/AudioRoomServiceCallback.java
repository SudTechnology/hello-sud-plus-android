package tech.sud.mgp.audio.example.service;

import java.util.List;

import tech.sud.mgp.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.audio.gift.model.GiftNotifyDetailodel;

public interface AudioRoomServiceCallback {

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

}
