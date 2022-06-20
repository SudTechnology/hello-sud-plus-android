package tech.sud.mgp.hello.common.model;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.HelloSudApplication;
import tech.sud.mgp.hello.common.utils.GlobalCache;
import tech.sud.mgp.hello.service.main.config.BaseRtcConfig;
import tech.sud.mgp.hello.service.main.config.SudConfig;
import tech.sud.mgp.hello.service.main.resp.BaseConfigResp;
import tech.sud.mgp.hello.ui.scenes.quiz.model.QuizRoomPkModel;

/**
 * 全局使用的数据
 */
public class AppData {

    private static final AppData instance = new AppData();

    private BaseConfigResp baseConfigResp; // 基础配置
    private BaseRtcConfig selectRtcConfig; // 当前所使用的rtc配置
    private boolean joinTicketNoRemind = false; // 加入门票场景不再提醒
    private List<QuizRoomPkModel> quizRoomPkModelList; // 竞猜场景更多活动页下的跨房玩游戏列表数据
    private boolean quizAutoGuessIWin; // 竞猜场景是否自动猜自己赢
    private boolean danmakuLandHintCompleted; // 弹幕场景是否已经进行了横屏提示

    private AppData() {
    }

    public static AppData getInstance() {
        return instance;
    }

    public void setBaseConfigResp(BaseConfigResp resp) {
        baseConfigResp = resp;
    }

    public SudConfig getSudConfig() {
        if (baseConfigResp != null) {
            return baseConfigResp.sudCfg;
        }
        return null;
    }

    // 保存数据
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (baseConfigResp != null) {
            outState.putSerializable("BaseConfigResp", baseConfigResp);
        }
        if (selectRtcConfig != null) {
            outState.putSerializable("BaseRtcConfig", selectRtcConfig);
        }
    }

    // 恢复数据
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Serializable baseConfigResp = savedInstanceState.getSerializable("BaseConfigResp");
        if (baseConfigResp instanceof BaseConfigResp) {
            this.baseConfigResp = (BaseConfigResp) baseConfigResp;
        }
        Serializable baseRtcConfig = savedInstanceState.getSerializable("BaseRtcConfig");
        if (baseRtcConfig instanceof BaseRtcConfig) {
            this.selectRtcConfig = (BaseRtcConfig) baseRtcConfig;
        }
    }

    /**
     * 获取当前所使用的rtc配置
     */
    public BaseRtcConfig getSelectRtcConfig() {
        if (selectRtcConfig == null) {
            Object rtcConfigSerializable = GlobalCache.getInstance().getSerializable(GlobalCache.RTC_CONFIG_KEY);
            if (rtcConfigSerializable instanceof BaseRtcConfig) {
                selectRtcConfig = (BaseRtcConfig) rtcConfigSerializable;
            }
        }
        return selectRtcConfig;
    }

    /**
     * 设置当前所使用的rtc配置
     */
    public void setSelectRtcConfig(BaseRtcConfig config) {
        selectRtcConfig = config;
    }

    /**
     * 获取当前所用的rtc类型
     */
    public String getRtcType() {
        if (selectRtcConfig != null) {
            return selectRtcConfig.rtcType;
        }
        return null;
    }

    /**
     * 获取当前所用的rtc名称
     */
    public String getRtcName() {
        BaseRtcConfig config = selectRtcConfig;
        if (config != null) {
            return getRtcNameByRtcType(config.rtcType);
        }
        return null;
    }

    /**
     * 根据rtcType获取名称
     */
    public String getRtcNameByRtcType(String rtcType) {
        BaseConfigResp config = baseConfigResp;
        if (config == null || rtcType == null) {
            return null;
        }
        Context context = HelloSudApplication.instance;
        if (config.zegoCfg != null && config.zegoCfg.rtcType.equals(rtcType)) {
            return context.getString(R.string.rtc_name_zego);
        }
        if (config.agoraCfg != null && config.agoraCfg.rtcType.equals(rtcType)) {
            return context.getString(R.string.rtc_name_agora);
        }
        if (config.rongCloudCfg != null && config.rongCloudCfg.rtcType.equals(rtcType)) {
            return context.getString(R.string.rtc_name_rong_cloud);
        }
        if (config.commsEaseCfg != null && config.commsEaseCfg.rtcType.equals(rtcType)) {
            return context.getString(R.string.rtc_name_comms_ease);
        }
        if (config.volcEngineCfg != null && config.volcEngineCfg.rtcType.equals(rtcType)) {
            return context.getString(R.string.rtc_name_volc_engine);
        }
        if (config.alibabaCloudCfg != null && config.alibabaCloudCfg.rtcType.equals(rtcType)) {
            return context.getString(R.string.rtc_name_alibaba_cloud);
        }
        if (config.tencentCloudCfg != null && config.tencentCloudCfg.rtcType.equals(rtcType)) {
            return context.getString(R.string.rtc_name_tencent_cloud);
        }
        return null;
    }

    // 加入门票场景是否不再提醒扣费
    public boolean isJoinTicketNoRemind() {
        return joinTicketNoRemind;
    }

    // 设置加入门票场景是否不再提醒扣费
    public void setJoinTicketNoRemind(boolean joinTicketNoRemind) {
        this.joinTicketNoRemind = joinTicketNoRemind;
    }

    // 竞猜场景更多活动页下的跨房玩游戏列表数据
    public List<QuizRoomPkModel> getQuizRoomPkModelList() {
        return quizRoomPkModelList;
    }

    // 竞猜场景更多活动页下的跨房玩游戏列表数据
    public void setQuizRoomPkModelList(List<QuizRoomPkModel> quizRoomPkModelList) {
        this.quizRoomPkModelList = quizRoomPkModelList;
    }

    // 竞猜场景是否自动猜自己赢
    public boolean isQuizAutoGuessIWin() {
        return quizAutoGuessIWin;
    }

    // 竞猜场景是否自动猜自己赢
    public void setQuizAutoGuessIWin(boolean quizAutoGuessIWin) {
        this.quizAutoGuessIWin = quizAutoGuessIWin;
    }

    // 弹幕场景是否已经进行了横屏提示
    public boolean isDanmakuLandHintCompleted() {
        return danmakuLandHintCompleted;
    }

    // 弹幕场景是否已经进行了横屏提示
    public void setDanmakuLandHintCompleted(boolean danmakuLandHintCompleted) {
        this.danmakuLandHintCompleted = danmakuLandHintCompleted;
    }
}
