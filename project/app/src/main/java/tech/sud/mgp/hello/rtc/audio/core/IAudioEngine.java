package tech.sud.mgp.hello.rtc.audio.core;

import tech.sud.mgp.hello.rtc.audio.model.AudioConfigModel;
import tech.sud.mgp.hello.rtc.audio.model.AudioJoinRoomModel;

/**
 * 多媒体语音引擎协议，多引擎实现一下协议
 */
public interface IAudioEngine {

    /********************* 1. 初始化、销毁SDK， 设置IAudioEventHandler回调 ****************************/

    /**
     * 设置事件处理器
     *
     * @param handler 事件处理实例
     */
    void setEventHandler(IAudioEventHandler handler);

    /**
     * 配置引擎SDK
     *  @param model  APPId
     *
     */
    void config(AudioConfigModel model);

    /**
     * 销毁
     */
    void destroy();

    /****************************    2. 登录房间、退出房间    ****************************************/

    /**
     * 登录房间
     *  @param model roomId
     *
     */
    void loginRoom(AudioJoinRoomModel model);

    /**
     * 退出房间
     */
    void logoutRoom();

    /****************************    3. 开启推流、停止推流    ****************************************/
    /**
     * 开启推流
     *
     */
    void startPublish();

    /**
     * 停止推流
     */
    void stopPublishStream();

    /****************************    4. 开启拉流、停止拉流    ****************************************/

    /**
     * 开启拉流
     */
    void startSubscribing();

    /**
     * 停止拉流
     */
    void stopSubscribing();

    /********************************    5. 发送信令     *******************************************/

    /**
     * 发送信令
     *
     * @param command 信令内容
     * @param roomId  房间ID
     * @param result  回调
     */
    void sendCommand(String roomId, String command, SendCommandResult result);

    /**
     * 发送指令回调接口
     */
    interface SendCommandResult {
        void result(int value);
    }

    /***************************** 6. 开始音频流监听、关闭音频流监听 ************************************/

    /**
     * 开始音频流监听
     */
    void startAudioDataListener();

    /**
     * 关闭音频流监听
     */
    void stopAudioDataListener();

    /******************************* 7. 是否使用扬声器作为音频通道 *************************************/

    void setAudioRouteToSpeaker(boolean enabled);
}
