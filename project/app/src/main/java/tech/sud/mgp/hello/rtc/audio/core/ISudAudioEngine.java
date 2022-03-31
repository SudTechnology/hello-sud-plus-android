package tech.sud.mgp.hello.rtc.audio.core;

import android.content.Context;

import tech.sud.mgp.hello.rtc.audio.model.AudioConfigModel;
import tech.sud.mgp.hello.rtc.audio.model.AudioJoinRoomModel;

/**
 * 多媒体语音引擎协议，多引擎实现一下协议
 */
public interface ISudAudioEngine {

    // region 1. 初始化、销毁SDK， 设置IAudioEventHandler回调
    /**
     * 设置事件处理器
     *
     * @param listener 事件处理实例
     */
    void setEventListener(ISudAudioEventListener listener);

    /**
     * 配置引擎SDK
     * @param context
     * @param model  APPId
     *
     */
    void initWithConfig(Context context, AudioConfigModel model);

    /**
     * 配置引擎SDK
     * @param context
     * @param model  APPId
     *
     */
    void initWithConfig(Context context, AudioConfigModel model, Runnable success);

    /**
     * 销毁引擎SDK
     */
    void destroy();
    // endregion

    // region 2. 登录房间、退出房间
    /**
     * 加入房间, 登录成功后， 默认不推流， 默认拉流
     *  @param model roomId
     *
     */
    void joinRoom(AudioJoinRoomModel model);

    /**
     * 离开房间
     */
    void leaveRoom();
    // endregion

    // region 3. 开启推流、停止推流
    /**
     * 开启推流
     *
     */
    void startPublishStream();

    /**
     * 停止推流
     */
    void stopPublishStream();
    // endregion

    // region 4. 开启拉流、停止拉流
    /**
     * 开启拉流，进入房间，默认订阅拉流
     */
    void startSubscribingStream();

    /**
     * 停止拉流
     */
    void stopSubscribingStream();
    // endregion

    // region 5. 开始音频流监听、关闭音频流监听
    /**
     * 开始音频流监听
     */
    void startPCMCapture();

    /**
     * 关闭音频流监听
     */
    void stopPCMCapture();
    // endregion

    // region 6. 是否使用扬声器作为音频通道
    /**
     * 切换扬声器作为音频通道
     */
    void setAudioRouteToSpeaker(boolean enabled);
    // endregion

    // region 7. 发送信令
    /**
     * 发送信令
     *  @param command 信令内容
     * @param listener  回调*/
    void sendCommand(String command, SendCommandListener listener);

    /**
     * 发送指令回调接口
     */
    interface SendCommandListener {
        void onResult(int value);
    }
    // endregion
}
