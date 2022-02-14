package tech.sud.mgp.hello.rtc.protocol;

/**
 * 多媒体语音引擎协议，多引擎实现一下协议
 */
public interface MediaAudioEngineProtocol {

    /**
     * 设置事件处理器
     *
     * @param handler 事件处理实例
     */
    void setEventHandler(MediaAudioEventHandler handler);

    /**
     * 配置引擎SDK
     *
     * @param appId  APPId
     * @param appKey APP秘钥
     */
    void config(String appId, String appKey);

    /**
     * 销毁
     */
    void destroy();

    /**
     * 登录房间
     *
     * @param roomId roomId
     * @param user   user
     * @param config config
     */
    void loginRoom(String roomId, MediaUser user, MediaRoomConfig config);

    /**
     * 退出房间
     */
    void logoutRoom();

    /**
     * 开启推流
     *
     * @param streamId
     */
    void startPublish(String streamId);

    /**
     * 停止推流
     */
    void stopPublishStream();

    /**
     * 是否处于推流状态
     */
    boolean isPublishing();

    /**
     * 播放流
     *
     * @param streamId 流ID
     */
    void startPlayingStream(String streamId);

    /**
     * 停止播放流
     *
     * @param streamId 流ID
     */
    void stopPlayingStream(String streamId);

    /**
     * 开、关指定流声音
     *
     * @param isMute   是否静音 true静音 false开启声音
     * @param streamId 流ID
     */
    void mutePlayStreamAudio(String streamId, boolean isMute);

    /**
     * 开、关所有流声音
     *
     * @param isMute 是否静音 true静音 false开启声音
     */
    void muteAllPlayStreamAudio(boolean isMute);

    /**
     * 是否静音了所有流
     */
    boolean isMuteAllPlayStreamAudio();

    /**
     * 设置指定流音量
     *
     * @param volume   音量值 [0, 200]
     * @param streamId 流ID
     */
    void setPlayVolume(String streamId, int volume);

    /**
     * 设置所有流音量
     *
     * @param volume 音量值 [0, 200]
     */
    void setAllPlayStreamVolume(int volume);

    /**
     * 静音麦克风
     *
     * @param isMute 是否静音 true静音 false开启声音
     */
    void muteMicrophone(boolean isMute);

    /**
     * 发送指令
     *
     * @param command 指令内容
     * @param roomId  房间ID
     * @param result  回调
     */
    void sendCommand(String roomId, String command, SendCommandResult result);

    /**
     * 启动声浪监控器
     */
    void startSoundLevelMonitor();

    /**
     * 发送指令回调接口
     */
    interface SendCommandResult {
        void result(int value);
    }

}
