package tech.sud.mgp.hello.rtc.audio.core;

/**
 * 多媒体语音引擎协议，多引擎实现一下协议
 */
public interface IAudioEngine {

    /**
     * 设置事件处理器
     *
     * @param handler 事件处理实例
     */
    void setEventHandler(IAudioEventHandler handler);

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
    void loginRoom(String roomId, AudioUser user, AudioRoomConfig config);

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
     * 开启拉流
     */
    void startSubscribing();

    /**
     * 停止拉流
     */
    void stopSubscribing();

    /**
     * 发送信令
     *
     * @param command 信令内容
     * @param roomId  房间ID
     * @param result  回调
     */
    void sendCommand(String roomId, String command, SendCommandResult result);

    /**
     * 设置音频监听流
     */
    void setAudioDataHandler();

    /**
     * 开始音频流监听
     */
    void startAudioDataListener();

    /**
     * 关闭音频流监听
     */
    void stopAudioDataListener();

    /**
     * 发送指令回调接口
     */
    interface SendCommandResult {
        void result(int value);
    }

}
