package tech.sud.mgp.audio.middle;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 多媒体语音事件处理协议,SDK回调事件，用户根据业务需求选择实现自己业务逻辑
 */
public interface MediaAudioEventHandler {

    /**
     * 捕获本地音量变化
     *
     * @param soundLevel 本地音量级别，取值范围[0, 100]
     */
    void onCapturedSoundLevelUpdate(float soundLevel);

    /**
     * 捕获远程音流音量变化
     *
     * @param soundLevels [流ID: 音量]，音量取值范围[0, 100]
     */
    void onRemoteSoundLevelUpdate(HashMap<String, Float> soundLevels);

    /**
     * 房间流更新 增、减，需要收到此事件后播放对应流
     *
     * @param roomId       房间id
     * @param type         流更新类型 增，减
     * @param streamList   变动流列表
     * @param extendedData 扩展信息
     */
    void onRoomStreamUpdate(String roomId, MediaAudioEngineUpdateType type, List<MediaStream> streamList, JSONObject extendedData);

    /**
     * 房间推流状态更新
     *
     * @param streamID     流ID
     * @param state        推流状态变化：推送请求，正在推送，停止推流
     * @param errorCode    错误码
     * @param extendedData 扩展信息
     */
    void onPublisherStateUpdate(String streamID, MediaAudioEnginePublisherSateType state, int errorCode, JSONObject extendedData);

    /**
     * 播放状态更新 拉取，播放请求，播放中
     *
     * @param streamID     流ID
     * @param state        拉取，播放请求，播放中
     * @param errorCode    错误码
     * @param extendedData 扩展信息
     */
    void onPlayerStateUpdate(String streamID, MediaAudioEnginePlayerStateType state, int errorCode, JSONObject extendedData);

    /**
     * SDK网络状态变化
     *
     * @param mode
     */
    void onNetworkModeChanged(MediaAudioEngineNetworkStateType mode);

    /**
     * 接收自定义指令信息回调
     *
     * @param roomId   房间ID
     * @param fromUser 用户
     * @param command  指令内容
     */
    void onIMRecvCustomCommand(String roomId, MediaUser fromUser, String command);

    /**
     * 房间内当前在线用户数量回调
     *
     * @param roomID 房间ID
     * @param count  人数
     */
    void onRoomOnlineUserCountUpdate(String roomID, int count);

}
