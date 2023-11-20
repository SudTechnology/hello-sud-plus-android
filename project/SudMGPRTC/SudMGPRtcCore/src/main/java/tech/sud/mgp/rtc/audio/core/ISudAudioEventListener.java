package tech.sud.mgp.rtc.audio.core;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 多媒体语音事件处理协议,SDK回调事件，用户根据业务需求选择实现自己业务逻辑
 */
public interface ISudAudioEventListener {

    /**
     * 捕获本地音量变化, 可用于展示自己说话音浪大小
     *
     * @param soundLevel 本地音量级别，取值范围[0, 100]
     */
    void onCapturedSoundLevelUpdate(float soundLevel);

    /**
     * 捕获远程音流音量变化, 可用于展示远端说话音浪大小
     *
     * @param soundLevels [userId: 音量]，音量取值范围[0, 100]
     */
    void onRemoteSoundLevelUpdate(HashMap<String, Float> soundLevels);

    /**
     * 房间流更新 增、减。可用于知道当前推流人数
     *
     * @param roomId       房间id
     * @param type         流更新类型 增，减
     * @param streamList   变动流列表
     * @param extendedData 扩展信息
     */
    void onRoomStreamUpdate(String roomId, AudioEngineUpdateType type, List<AudioStream> streamList, JSONObject extendedData);

    /**
     * 接收自定义指令信息回调
     *
     * @param fromUserID 用户
     * @param command    指令内容
     */
    void onRecvCommand(String fromUserID, String command);

    /**
     * 接收跨房指令信息回调
     *
     * @param fromRoomID 消息的房间 ID
     * @param fromUserID 消息的用户 ID
     * @param command    指令内容
     */
    void onRecvXRoomCommand(String fromRoomID, String fromUserID, String command);

    /**
     * 房间内当前在线用户数量回调
     *
     * @param count 人数
     */
    void onRoomOnlineUserCountUpdate(int count);

    /**
     * 房间状态变化
     *
     * @param state        状态
     * @param errorCode    错误码
     * @param extendedData 扩展信息
     */
    void onRoomStateUpdate(AudioRoomState state, int errorCode, JSONObject extendedData);

    /**
     * 监听音频PCM流回调
     *
     * @param audioPCMData 音频流数据
     */
    void onCapturedPCMData(AudioPCMData audioPCMData);

    /**
     * 观众拉流成功通知
     *
     * @param streamID
     */
    void onPlayingStreamingAdd(String streamID);

    /**
     * 观众拉流结束通知
     *
     * @param streamID
     */
    void onPlayingStreamingDelete(String streamID);

    /**
     * 拉流分辨率变更通知。
     *
     * @param streamID 流id
     * @param width    宽
     * @param height   高
     */
    void onPlayerVideoSizeChanged(String streamID, int width, int height);
}
