package tech.sud.mgp.hello.rtc.audio.core;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 多媒体语音事件处理协议,SDK回调事件，用户根据业务需求选择实现自己业务逻辑
 */
public interface IAudioEventHandler {

    /**
     * 捕获本地音量变化
     *
     * @param soundLevel 本地音量级别，取值范围[0, 100]
     */
    void onCapturedSoundLevelUpdate(float soundLevel);

    /**
     * 捕获远程音流音量变化
     *
     * @param soundLevels [userId: 音量]，音量取值范围[0, 100]
     */
    void onRemoteSoundLevelUpdate(HashMap<String, Float> soundLevels);

    /**
     * 房间流更新 增、减。收到此事件后播放对应流
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
     * @param roomId   房间ID
     * @param fromUser 用户
     * @param command  指令内容
     */
    void onIMRecvCustomCommand(String roomId, AudioUser fromUser, String command);

    /**
     * 房间内当前在线用户数量回调
     *
     * @param roomID 房间ID
     * @param count  人数
     */
    void onRoomOnlineUserCountUpdate(String roomID, int count);

    /**
     * 房间状态变化
     *
     * @param roomID       房间id
     * @param state        状态
     * @param errorCode    错误码
     * @param extendedData 扩展信息
     */
    void onRoomStateUpdate(String roomID, AudioRoomState state, int errorCode, JSONObject extendedData);

    /**
     * 监听音频流回调
     *
     * @param audioData 音频流数据
     */
    void onCapturedAudioData(AudioData audioData);
}