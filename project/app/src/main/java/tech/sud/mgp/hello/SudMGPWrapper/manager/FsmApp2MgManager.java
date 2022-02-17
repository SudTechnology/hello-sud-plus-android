package tech.sud.mgp.hello.SudMGPWrapper.manager;

import com.blankj.utilcode.util.GsonUtils;

import tech.sud.mgp.core.ISudFSTAPP;
import tech.sud.mgp.core.ISudListenerNotifyStateChange;
import tech.sud.mgp.hello.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.hello.SudMGPWrapper.state.app.CommonSelfInState;
import tech.sud.mgp.hello.SudMGPWrapper.state.app.CommonSelfTextHitState;
import tech.sud.mgp.hello.rtc.audio.core.AudioData;

/**
 * app端调用sdk
 */
public class FsmApp2MgManager {

    private ISudFSTAPP iSudFSTAPP;

    /**
     * 设置app调用sdk的对象
     *
     * @param iSudFSTAPP
     */
    public void setISudFSTAPP(ISudFSTAPP iSudFSTAPP) {
        this.iSudFSTAPP = iSudFSTAPP;
    }

    /**
     * 更新code
     *
     * @param code
     * @param listener
     */
    public void updateCode(String code, ISudListenerNotifyStateChange listener) {
        if (iSudFSTAPP != null) {
            iSudFSTAPP.updateCode(code, listener);
        }
    }

    /**
     * 发送加入游戏的状态
     *
     * @param isIn         true 加入游戏，false 退出游戏
     * @param seatIndex    加入的游戏位(座位号) 默认传seatIndex = -1 随机加入，seatIndex 从0开始，不可大于座位数
     * @param isSeatRandom 默认为ture, 带有游戏位(座位号)的时候，如果游戏位(座位号)已经被占用，是否随机分配一个空位坐下 isSeatRandom=true 随机分配空位坐下，isSeatRandom=false 不随机分配
     * @param teamId       不支持分队的游戏：数值填1；支持分队的游戏：数值填1或2（两支队伍）；
     */
    public void sendCommonSelfInState(boolean isIn, int seatIndex, boolean isSeatRandom, int teamId) {
        if (iSudFSTAPP != null) {
            CommonSelfInState state = new CommonSelfInState();
            state.isIn = isIn;
            state.seatIndex = seatIndex;
            state.isSeatRandom = isSeatRandom;
            state.teamId = teamId;
            iSudFSTAPP.notifyStateChange(SudMGPAPPState.APP_COMMON_SELF_IN, GsonUtils.toJson(state), null);
        }
    }

    /**
     * 音频流数据
     */
    public void onAudioPush(AudioData audioData) {
        if (iSudFSTAPP != null) {
            iSudFSTAPP.pushAudio(audioData.data, audioData.dataLength);
        }
    }

    /**
     * 文本数据
     */
    public void sendCommonSelfTextHitState(Boolean isHit, String keyWord, String text) {
        if (iSudFSTAPP != null) {
            CommonSelfTextHitState state = new CommonSelfTextHitState();
            state.isHit = isHit;
            state.keyWord = keyWord;
            state.text = text;
            iSudFSTAPP.notifyStateChange(SudMGPAPPState.APP_COMMON_SELF_TEXT_HIT, GsonUtils.toJson(state), null);
        }
    }

    public void onStart() {
        if (iSudFSTAPP != null) {
            iSudFSTAPP.startMG();
        }
    }

    public void onPause() {
        if (iSudFSTAPP != null) {
            iSudFSTAPP.pauseMG();
        }
    }

    public void onResume() {
        if (iSudFSTAPP != null) {
            iSudFSTAPP.playMG();
        }
    }

    public void onStop() {
        if (iSudFSTAPP != null) {
            iSudFSTAPP.stopMG();
        }
    }

    public void destroyMG() {
        if (iSudFSTAPP != null) {
            iSudFSTAPP.destroyMG();
        }
    }

}
