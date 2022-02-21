/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.hello.SudMGPWrapper.decorator;

import com.blankj.utilcode.util.GsonUtils;

import java.nio.ByteBuffer;
import java.util.List;

import tech.sud.mgp.core.ISudFSTAPP;
import tech.sud.mgp.core.ISudListenerNotifyStateChange;
import tech.sud.mgp.hello.SudMGPWrapper.state.SudMGPAPPState;

/**
 * ISudFSTAPP的装饰类，接近于业务
 */
public class SudFSTAPPDecorator {

    /**
     * APP调用游戏的接口
     */
    private ISudFSTAPP iSudFSTAPP;

    /**
     * 设置app调用sdk的对象
     *
     * @param iSudFSTAPP
     */
    public void setISudFSTAPP(ISudFSTAPP iSudFSTAPP) {
        this.iSudFSTAPP = iSudFSTAPP;
    }

    // region 状态通知，ISudFSTAPP.notifyStateChange

    /**
     * 发送
     * 1. 加入状态
     *
     * @param isIn         true 加入游戏，false 退出游戏
     * @param seatIndex    加入的游戏位(座位号) 默认传seatIndex = -1 随机加入，seatIndex 从0开始，不可大于座位数
     * @param isSeatRandom 默认为ture, 带有游戏位(座位号)的时候，如果游戏位(座位号)已经被占用，是否随机分配一个空位坐下 isSeatRandom=true 随机分配空位坐下，isSeatRandom=false 不随机分配
     * @param teamId       不支持分队的游戏：数值填1；支持分队的游戏：数值填1或2（两支队伍）；
     */
    public void notifyAPPCommonSelfIn(boolean isIn, int seatIndex, boolean isSeatRandom, int teamId) {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            SudMGPAPPState.APPCommonSelfIn state = new SudMGPAPPState.APPCommonSelfIn();
            state.isIn = isIn;
            state.seatIndex = seatIndex;
            state.isSeatRandom = isSeatRandom;
            state.teamId = teamId;
            iSudFSTAPP.notifyStateChange(SudMGPAPPState.APP_COMMON_SELF_IN, GsonUtils.toJson(state), null);
        }
    }

    /**
     * 发送
     * 2. 准备状态
     * 用户（本人）准备/取消准备
     *
     * @param isReady true 准备，false 取消准备
     */
    public void notifyAPPCommonSelfReady(boolean isReady) {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            SudMGPAPPState.APPCommonSelfReady state = new SudMGPAPPState.APPCommonSelfReady();
            state.isReady = isReady;
            iSudFSTAPP.notifyStateChange(SudMGPAPPState.APP_COMMON_SELF_READY, GsonUtils.toJson(state), null);
        }
    }

    /**
     * 发送
     * 3. 游戏状态 模型
     * 用户游戏状态，如果用户在游戏中，建议：
     * a.空出屏幕中心区：
     * 关闭全屏礼物特效；
     * b.部分强操作类小游戏（spaceMax为true），尽量收缩原生UI，给游戏留出尽量大的操作空间：
     * 收缩公屏；
     * 收缩麦位；
     * 如果不在游戏中，则恢复。
     *
     * @param isPlaying            true 开始游戏，false 结束游戏
     * @param reportGameInfoExtras string类型，Https服务回调report_game_info参数，最大长度1024字节，超过则截断（2022-01-21）
     */
    public void notifyAPPCommonSelfPlaying(boolean isPlaying, String reportGameInfoExtras) {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            SudMGPAPPState.APPCommonSelfPlaying state = new SudMGPAPPState.APPCommonSelfPlaying();
            state.isPlaying = isPlaying;
            state.reportGameInfoExtras = reportGameInfoExtras;
            iSudFSTAPP.notifyStateChange(SudMGPAPPState.APP_COMMON_SELF_PLAYING, GsonUtils.toJson(state), null);
        }
    }

    /**
     * 发送
     * 4. 队长状态
     * 用户是否为队长，队长在游戏中会有开始游戏的权利。
     *
     * @param curCaptainUID 必填，指定队长uid
     */
    public void notifyAPPCommonSelfCaptain(String curCaptainUID) {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            SudMGPAPPState.APPCommonSelfCaptain state = new SudMGPAPPState.APPCommonSelfCaptain();
            state.curCaptainUID = curCaptainUID;
            iSudFSTAPP.notifyStateChange(SudMGPAPPState.APP_COMMON_SELF_CAPTAIN, GsonUtils.toJson(state), null);
        }
    }

    /**
     * 发送
     * 5. 踢人
     * 用户（本人，队长）踢其他玩家；
     * 队长才能踢人；
     *
     * @param kickedUID 被踢用户uid
     */
    public void notifyAPPCommonSelfKick(String kickedUID) {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            SudMGPAPPState.APPCommonSelfKick state = new SudMGPAPPState.APPCommonSelfKick();
            state.kickedUID = kickedUID;
            iSudFSTAPP.notifyStateChange(SudMGPAPPState.APP_COMMON_SELF_KICK, GsonUtils.toJson(state), null);
        }
    }

    /**
     * 发送
     * 6. 结束游戏
     * 用户（本人，队长）结束（本局）游戏
     */
    public void notifyAPPCommonSelfEnd() {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            SudMGPAPPState.APPCommonSelfEnd state = new SudMGPAPPState.APPCommonSelfEnd();
            iSudFSTAPP.notifyStateChange(SudMGPAPPState.APP_COMMON_SELF_END, GsonUtils.toJson(state), null);
        }
    }

    /**
     * 发送
     * 9. 麦克风状态
     * 用户（本人）麦克风状态，建议：
     * 进入房间后初始通知一次；
     * 每次变更（开麦/闭麦/禁麦/解麦）通知一次；
     *
     * @param isOn       true 开麦，false 闭麦
     * @param isDisabled true 被禁麦，false 未被禁麦
     */
    public void notifyAPPCommonSelfMicrophone(boolean isOn, boolean isDisabled) {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            SudMGPAPPState.APPCommonSelfMicrophone state = new SudMGPAPPState.APPCommonSelfMicrophone();
            state.isOn = isOn;
            state.isDisabled = isDisabled;
            iSudFSTAPP.notifyStateChange(SudMGPAPPState.APP_COMMON_SELF_MICROPHONE, GsonUtils.toJson(state), null);
        }
    }

    /**
     * 发送
     * 10. 文字命中状态
     * 用户（本人）聊天信息命中关键词状态，建议：
     * 精确匹配；
     * 首次聊天内容命中关键词之后，后续聊天内容不翻转成未命中；
     * 直至小游戏侧关键词更新，再将状态翻转为未命中；
     *
     * @param isHit       true 命中，false 未命中
     * @param keyWord     单个关键词， 兼容老版本
     * @param text        返回转写文本
     * @param wordType    text:文本包含匹配; number:数字等于匹配
     * @param keyWordList 命中关键词，可以包含多个关键词
     * @param numberList  在number模式下才有，返回转写的多个数字
     */
    public void notifyAPPCommonSelfTextHitState(boolean isHit, String keyWord, String text,
                                                String wordType, List<String> keyWordList, List<Integer> numberList) {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            SudMGPAPPState.APPCommonSelfTextHitState state = new SudMGPAPPState.APPCommonSelfTextHitState();
            state.isHit = isHit;
            state.keyWord = keyWord;
            state.text = text;
            state.wordType = wordType;
            state.keyWordList = keyWordList;
            state.numberList = numberList;
            iSudFSTAPP.notifyStateChange(SudMGPAPPState.APP_COMMON_SELF_TEXT_HIT, GsonUtils.toJson(state), null);
        }
    }

    /**
     * 发送
     * 11. 打开或关闭背景音乐（2021-12-27新增）
     *
     * @param isOpen true 打开背景音乐，false 关闭背景音乐
     */
    public void notifyAPPCommonOpenBgMusic(boolean isOpen) {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            SudMGPAPPState.APPCommonOpenBgMusic state = new SudMGPAPPState.APPCommonOpenBgMusic();
            state.isOpen = isOpen;
            iSudFSTAPP.notifyStateChange(SudMGPAPPState.APP_COMMON_OPEN_BG_MUSIC, GsonUtils.toJson(state), null);
        }
    }

    /**
     * 发送
     * 12. 打开或关闭音效（2021-12-27新增）
     *
     * @param isOpen true 打开音效，false 关闭音效
     */
    public void notifyAPPCommonOpenSound(boolean isOpen) {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            SudMGPAPPState.APPCommonOpenSound state = new SudMGPAPPState.APPCommonOpenSound();
            state.isOpen = isOpen;
            iSudFSTAPP.notifyStateChange(SudMGPAPPState.APP_COMMON_OPEN_SOUND, GsonUtils.toJson(state), null);
        }
    }

    /**
     * 发送
     * 13. 打开或关闭游戏中的振动效果（2021-12-27新增）
     *
     * @param isOpen 打开振动效果，false 关闭振动效果
     */
    public void notifyAPPCommonOpenVibrate(boolean isOpen) {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            SudMGPAPPState.APPCommonOpenVibrate state = new SudMGPAPPState.APPCommonOpenVibrate();
            state.isOpen = isOpen;
            iSudFSTAPP.notifyStateChange(SudMGPAPPState.APP_COMMON_OPEN_VIBRATE, GsonUtils.toJson(state), null);
        }
    }

    /**
     * 发送
     * 14. 设置游戏的音量大小（2021-12-31新增）
     *
     * @param volume 音量大小 0 到 100
     */
    public void notifyAPPCommonGameSoundVolume(int volume) {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            SudMGPAPPState.APPCommonGameSoundVolume state = new SudMGPAPPState.APPCommonGameSoundVolume();
            state.volume = volume;
            iSudFSTAPP.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_SOUND_VOLUME, GsonUtils.toJson(state), null);
        }
    }

//    /**
//     * 发送
//     * 15. 设置游戏上报信息扩展参数（透传）（2022-01-21新增）
//     * 接口暂未实现
//     *
//     * @param reportGameInfoExtras string类型，Https服务回调report_game_info参数，最大长度1024字节，超过则截断（2022-01-21）
//     */
//    public void notifyAPPCommonReportGameInfoExtras(String reportGameInfoExtras) {
//        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
//        if (iSudFSTAPP != null) {
//            SudMGPAPPState.APPCommonReportGameInfoExtras state = new SudMGPAPPState.APPCommonReportGameInfoExtras();
//            state.reportGameInfoExtras = reportGameInfoExtras;
//            iSudFSTAPP.notifyStateChange(SudMGPAPPState.APP_COMMON_REPORT_GAME_INFO_EXTRAS, GsonUtils.toJson(state), null);
//        }
//    }

    // endregion 状态通知，ISudFSTAPP.notifyStateChange

    // region 生命周期
    public void startMG() {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            iSudFSTAPP.startMG();
        }
    }

    public void pauseMG() {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            iSudFSTAPP.pauseMG();
        }
    }

    public void playMG() {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            iSudFSTAPP.playMG();
        }
    }

    public void stopMG() {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            iSudFSTAPP.stopMG();
        }
    }

    public void destroyMG() {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            iSudFSTAPP.destroyMG();
        }
    }

    // endregion 生命周期

    /**
     * 更新code
     *
     * @param code
     * @param listener
     */
    public void updateCode(String code, ISudListenerNotifyStateChange listener) {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            iSudFSTAPP.updateCode(code, listener);
        }
    }

    /**
     * 音频流数据
     */
    public void pushAudio(ByteBuffer buffer, int bufferLength) {
        ISudFSTAPP iSudFSTAPP = this.iSudFSTAPP;
        if (iSudFSTAPP != null) {
            iSudFSTAPP.pushAudio(buffer, bufferLength);
        }
    }

}
