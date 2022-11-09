/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.SudMGPWrapper.decorator;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.SudMGPWrapper.utils.ISudFSMStateHandleUtils;
import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.core.ISudFSMMG;
import tech.sud.mgp.core.ISudFSMStateHandle;

/**
 * ISudFSMMG 游戏调APP回调装饰类
 * 参考文档：https://docs.sud.tech/zh-CN/app/Client/API/ISudFSMMG.html
 */
public class SudFSMMGDecorator implements ISudFSMMG {

    // 回调
    private SudFSMMGListener sudFSMMGListener;

    // 数据状态封装
    private final SudFSMMGCache sudFSMMGCache = new SudFSMMGCache();

    /**
     * 设置回调
     *
     * @param listener 监听器
     */
    public void setSudFSMMGListener(SudFSMMGListener listener) {
        sudFSMMGListener = listener;
    }

    /**
     * 游戏日志
     * 最低版本：v1.1.30.xx
     */
    @Override
    public void onGameLog(String dataJson) {
        SudFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGameLog(dataJson);
        }
    }

    /**
     * 游戏加载进度
     *
     * @param stage    阶段：start=1,loading=2,end=3
     * @param retCode  错误码：0成功
     * @param progress 进度：[0, 100]
     */
    @Override
    public void onGameLoadingProgress(int stage, int retCode, int progress) {
        SudFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGameLoadingProgress(stage, retCode, progress);
        }
    }

    /**
     * 游戏开始
     * 最低版本：v1.1.30.xx
     */
    @Override
    public void onGameStarted() {
        SudFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGameStarted();
        }
    }

    /**
     * 游戏销毁
     * 最低版本：v1.1.30.xx
     */
    @Override
    public void onGameDestroyed() {
        SudFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGameDestroyed();
        }
    }

    /**
     * Code过期，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param dataJson {"code":"value"}
     */
    @Override
    public void onExpireCode(ISudFSMStateHandle handle, String dataJson) {
        SudFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onExpireCode(handle, dataJson);
        }
    }

    /**
     * 获取游戏View信息，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle   操作
     * @param dataJson {}
     */
    @Override
    public void onGetGameViewInfo(ISudFSMStateHandle handle, String dataJson) {
        SudFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGetGameViewInfo(handle, dataJson);
        }
    }

    /**
     * 获取游戏Config，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle   操作
     * @param dataJson {}
     *                 最低版本：v1.1.30.xx
     */
    @Override
    public void onGetGameCfg(ISudFSMStateHandle handle, String dataJson) {
        SudFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGetGameCfg(handle, dataJson);
        }
    }

    /**
     * 游戏状态变化
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle   操作
     * @param state    状态命令
     * @param dataJson 状态值
     */
    @Override
    public void onGameStateChange(ISudFSMStateHandle handle, String state, String dataJson) {
        SudFSMMGListener listener = sudFSMMGListener;
        if (listener != null && listener.onGameStateChange(handle, state, dataJson)) {
            return;
        }
        switch (state) {
            case SudMGPMGState.MG_COMMON_PUBLIC_MESSAGE: // 1. 公屏消息
                SudMGPMGState.MGCommonPublicMessage mgCommonPublicMessage = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonPublicMessage.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonPublicMessage(handle, mgCommonPublicMessage);
                }
                break;
            case SudMGPMGState.MG_COMMON_KEY_WORD_TO_HIT: // 2. 关键词状态
                SudMGPMGState.MGCommonKeyWordToHit mgCommonKeyWordToHit = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonKeyWordToHit.class);
                sudFSMMGCache.onGameMGCommonKeyWordToHit(mgCommonKeyWordToHit);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonKeyWordToHit(handle, mgCommonKeyWordToHit);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_SETTLE: // 3. 游戏结算状态
                SudMGPMGState.MGCommonGameSettle mgCommonGameSettle = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameSettle.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSettle(handle, mgCommonGameSettle);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_JOIN_BTN: // 4. 加入游戏按钮点击状态
                SudMGPMGState.MGCommonSelfClickJoinBtn mgCommonSelfClickJoinBtn = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickJoinBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickJoinBtn(handle, mgCommonSelfClickJoinBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_CANCEL_JOIN_BTN: // 5. 取消加入(退出)游戏按钮点击状态
                SudMGPMGState.MGCommonSelfClickCancelJoinBtn selfClickCancelJoinBtn = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickCancelJoinBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickCancelJoinBtn(handle, selfClickCancelJoinBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_READY_BTN: // 6. 准备按钮点击状态
                SudMGPMGState.MGCommonSelfClickReadyBtn mgCommonSelfClickReadyBtn = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickReadyBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickReadyBtn(handle, mgCommonSelfClickReadyBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_CANCEL_READY_BTN: // 7. 取消准备按钮点击状态
                SudMGPMGState.MGCommonSelfClickCancelReadyBtn mgCommonSelfClickCancelReadyBtn = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickCancelReadyBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickCancelReadyBtn(handle, mgCommonSelfClickCancelReadyBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_START_BTN: // 8. 开始游戏按钮点击状态
                SudMGPMGState.MGCommonSelfClickStartBtn mgCommonSelfClickStartBtn = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickStartBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickStartBtn(handle, mgCommonSelfClickStartBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_SHARE_BTN: // 9. 分享按钮点击状态
                SudMGPMGState.MGCommonSelfClickShareBtn mgCommonSelfClickShareBtn = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickShareBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickShareBtn(handle, mgCommonSelfClickShareBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_STATE: // 10. 游戏状态
                SudMGPMGState.MGCommonGameState mgCommonGameState = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameState.class);
                sudFSMMGCache.onGameMGCommonGameState(mgCommonGameState);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameState(handle, mgCommonGameState);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_GAME_SETTLE_CLOSE_BTN: // 11. 结算界面关闭按钮点击状态（2021-12-27新增）
                SudMGPMGState.MGCommonSelfClickGameSettleCloseBtn mgCommonSelfClickGameSettleCloseBtn = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickGameSettleCloseBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGameSettleCloseBtn(handle, mgCommonSelfClickGameSettleCloseBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_GAME_SETTLE_AGAIN_BTN: // 12. 结算界面再来一局按钮点击状态（2021-12-27新增）
                SudMGPMGState.MGCommonSelfClickGameSettleAgainBtn mgCommonSelfClickGameSettleAgainBtn = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickGameSettleAgainBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGameSettleAgainBtn(handle, mgCommonSelfClickGameSettleAgainBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_SOUND_LIST: // 13. 游戏上报游戏中的声音列表（2021-12-30新增，现在只支持碰碰我最强）
                SudMGPMGState.MGCommonGameSoundList mgCommonGameSoundList = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameSoundList.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSoundList(handle, mgCommonGameSoundList);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_SOUND: // 14. 游通知app层播放声音（2021-12-30新增，现在只支持碰碰我最强）
                SudMGPMGState.MGCommonGameSound mgCommonGameSound = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameSound.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSound(handle, mgCommonGameSound);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_BG_MUSIC_STATE: // 15. 游戏通知app层播放背景音乐状态（2022-01-07新增，现在只支持碰碰我最强）
                SudMGPMGState.MGCommonGameBgMusicState mgCommonGameBgMusicState = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameBgMusicState.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameBgMusicState(handle, mgCommonGameBgMusicState);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_SOUND_STATE: // 16. 游戏通知app层播放音效的状态（2022-01-07新增，现在只支持碰碰我最强）
                SudMGPMGState.MGCommonGameSoundState mgCommonGameSoundState = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameSoundState.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSoundState(handle, mgCommonGameSoundState);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_ASR: // 17. ASR状态(开启和关闭语音识别状态，v1.1.45.xx 版本新增)
                SudMGPMGState.MGCommonGameASR mgCommonGameASR = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameASR.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameASR(handle, mgCommonGameASR);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_MICROPHONE: // 18. 麦克风状态（2022-02-08新增）
                SudMGPMGState.MGCommonSelfMicrophone mgCommonSelfMicrophone = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfMicrophone.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfMicrophone(handle, mgCommonSelfMicrophone);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_HEADPHONE: // 19. 耳机（听筒，扬声器）状态（2022-02-08新增）
                SudMGPMGState.MGCommonSelfHeadphone mgCommonSelfHeadphone = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfHeadphone.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfHeadphone(handle, mgCommonSelfHeadphone);
                }
                break;
            case SudMGPMGState.MG_COMMON_APP_COMMON_SELF_X_RESP: // 20. App通用状态操作结果错误码（2022-05-10新增）
                SudMGPMGState.MGCommonAPPCommonSelfXResp mgCommonAPPCommonSelfXResp = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonAPPCommonSelfXResp.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAPPCommonSelfXResp(handle, mgCommonAPPCommonSelfXResp);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_ADD_AI_PLAYERS: // 21. 游戏通知app层添加陪玩机器人是否成功（2022-05-17新增）
                SudMGPMGState.MGCommonGameAddAIPlayers mgCommonGameAddAIPlayers = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameAddAIPlayers.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameAddAIPlayers(handle, mgCommonGameAddAIPlayers);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_NETWORK_STATE: // 22. 游戏通知app层添当前网络连接状态（2022-06-21新增）
                SudMGPMGState.MGCommonGameNetworkState mgCommonGameNetworkState = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameNetworkState.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameNetworkState(handle, mgCommonGameNetworkState);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_GET_SCORE: // 23. 游戏通知app获取积分
                SudMGPMGState.MGCommonGameGetScore mgCommonGameScore = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameGetScore.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameGetScore(handle, mgCommonGameScore);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_SET_SCORE: // 24. 游戏通知app带入积分
                SudMGPMGState.MGCommonGameSetScore mgCommonGameSetScore = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameSetScore.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSetScore(handle, mgCommonGameSetScore);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_DISCO_ACTION: // 1. 元宇宙砂砂舞指令回调
                SudMGPMGState.MGCommonGameDiscoAction mgCommonGameDiscoAction = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameDiscoAction.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameDiscoAction(handle, mgCommonGameDiscoAction);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_DISCO_ACTION_END: // 2. 元宇宙砂砂舞指令动作结束通知
                SudMGPMGState.MGCommonGameDiscoActionEnd mgCommonGameDiscoActionEnd = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameDiscoActionEnd.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameDiscoActionEnd(handle, mgCommonGameDiscoActionEnd);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_CONFIG: // 1. 礼物配置文件(火箭)
                SudMGPMGState.MGCustomRocketConfig mgCustomRocketConfig = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketConfig.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketConfig(handle, mgCustomRocketConfig);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_MODEL_LIST: // 2. 拥有模型列表(火箭)
                SudMGPMGState.MGCustomRocketModelList mgCustomRocketModelList = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketModelList.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketModelList(handle, mgCustomRocketModelList);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_COMPONENT_LIST: // 3. 拥有组件列表(火箭)
                SudMGPMGState.MGCustomRocketComponentList mgCustomRocketComponentList = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketComponentList.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketComponentList(handle, mgCustomRocketComponentList);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_USER_INFO: // 4. 获取用户信息(火箭)
                SudMGPMGState.MGCustomRocketUserInfo mgCustomRocketUserInfo = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketUserInfo.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketUserInfo(handle, mgCustomRocketUserInfo);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_ORDER_RECORD_LIST: // 5. 订单记录列表(火箭)
                SudMGPMGState.MGCustomRocketOrderRecordList mgCustomRocketOrderRecordList = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketOrderRecordList.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketOrderRecordList(handle, mgCustomRocketOrderRecordList);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_ROOM_RECORD_LIST: // 6. 展馆内列表(火箭)
                SudMGPMGState.MGCustomRocketRoomRecordList mgCustomRocketRoomRecordList = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketRoomRecordList.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketRoomRecordList(handle, mgCustomRocketRoomRecordList);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_USER_RECORD_LIST: // 7. 展馆内玩家送出记录(火箭)
                SudMGPMGState.MGCustomRocketUserRecordList mgCustomRocketUserRecordList = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketUserRecordList.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketUserRecordList(handle, mgCustomRocketUserRecordList);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_SET_DEFAULT_MODEL: // 8. 设置默认模型(火箭)
                SudMGPMGState.MGCustomRocketSetDefaultModel mgCustomRocketSetDefaultSeat = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketSetDefaultModel.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketSetDefaultModel(handle, mgCustomRocketSetDefaultSeat);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_DYNAMIC_FIRE_PRICE: // 9. 动态计算一键发送价格(火箭)
                SudMGPMGState.MGCustomRocketDynamicFirePrice mgCustomRocketDynamicFirePrice = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketDynamicFirePrice.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketDynamicFirePrice(handle, mgCustomRocketDynamicFirePrice);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_FIRE_MODEL: // 10. 一键发送(火箭)
                SudMGPMGState.MGCustomRocketFireModel mGCustomRocketFireModel = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketFireModel.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketFireModel(handle, mGCustomRocketFireModel);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_CREATE_MODEL: // 11. 新组装模型(火箭)
                SudMGPMGState.MGCustomRocketCreateModel mgCustomRocketCreateModel = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketCreateModel.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketCreateModel(handle, mgCustomRocketCreateModel);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_REPLACE_COMPONENT: // 12. 模型更换组件(火箭)
                SudMGPMGState.MGCustomRocketReplaceComponent mgCustomRocketReplaceComponent = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketReplaceComponent.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketReplaceComponent(handle, mgCustomRocketReplaceComponent);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_BUY_COMPONENT: // 13. 购买组件(火箭)
                SudMGPMGState.MGCustomRocketBuyComponent mgCustomRocketBuyComponent = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketBuyComponent.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketBuyComponent(handle, mgCustomRocketBuyComponent);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_PLAY_EFFECT_START: // 14. 播放效果开始(火箭)
                SudMGPMGState.MGCustomRocketPlayEffectStart mgCustomRocketPlayEffectStart = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketPlayEffectStart.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketPlayEffectStart(handle, mgCustomRocketPlayEffectStart);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_PLAY_EFFECT_FINISH: // 15. 播放效果完成(火箭)
                SudMGPMGState.MGCustomRocketPlayEffectFinish mgCustomRocketPlayEffectFinish = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketPlayEffectFinish.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketPlayEffectFinish(handle, mgCustomRocketPlayEffectFinish);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_VERIFY_SIGN: // 16. 验证签名合规(火箭)
                SudMGPMGState.MGCustomRocketVerifySign mgCustomRocketVerifySign = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketVerifySign.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketVerifySign(handle, mgCustomRocketVerifySign);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_UPLOAD_MODEL_ICON: // 17. 上传icon(火箭)
                SudMGPMGState.MGCustomRocketUploadModelIcon mgCustomRocketUploadModelIcon = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketUploadModelIcon.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketUploadModelIcon(handle, mgCustomRocketUploadModelIcon);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_PREPARE_FINISH: // 18. 前期准备完成(火箭)
                SudMGPMGState.MGCustomRocketPrepareFinish mgCustomRocketPrepareFinish = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketPrepareFinish.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketPrepareFinish(handle, mgCustomRocketPrepareFinish);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_HIDE_GAME_SCENE: // 19. 隐藏了火箭主界面(火箭)
                SudMGPMGState.MGCustomRocketHideGameScene mgCustomRocketHideGameScene = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketHideGameScene.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketHideGameScene(handle, mgCustomRocketHideGameScene);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_CLICK_LOCK_COMPONENT: // 20. 点击锁住组件(火箭)
                SudMGPMGState.MGCustomRocketClickLockComponent mgCustomRocketClickLockComponent = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketClickLockComponent.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketClickLockComponent(handle, mgCustomRocketClickLockComponent);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_FLY_CLICK: // 21. 火箭效果飞行点击(火箭)
                SudMGPMGState.MGCustomRocketFlyClick mgCustomRocketFlyClick = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketFlyClick.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketFlyClick(handle, mgCustomRocketFlyClick);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_FLY_END: // 22. 火箭效果飞行结束(火箭)
                SudMGPMGState.MGCustomRocketFlyEnd mgCustomRocketFlyEnd = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketFlyEnd.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketFlyEnd(handle, mgCustomRocketFlyEnd);
                }
                break;
            case SudMGPMGState.MG_CUSTOM_ROCKET_SET_CLICK_RECT: // 23. 设置点击区域(火箭)
                SudMGPMGState.MGCustomRocketSetClickRect mgCustomRocketSetClickRect = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCustomRocketSetClickRect.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketSetClickRect(handle, mgCustomRocketSetClickRect);
                }
                break;
            default:
                ISudFSMStateHandleUtils.handleSuccess(handle);
                break;
        }
    }

    /**
     * 游戏玩家状态变化
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle   操作
     * @param userId   用户id
     * @param state    状态命令
     * @param dataJson 状态值
     */
    @Override
    public void onPlayerStateChange(ISudFSMStateHandle handle, String userId, String state, String dataJson) {
        SudFSMMGListener listener = sudFSMMGListener;
        if (listener != null && listener.onPlayerStateChange(handle, userId, state, dataJson)) {
            return;
        }
        switch (state) {
            case SudMGPMGState.MG_COMMON_PLAYER_IN: // 1.加入状态（已修改）
                SudMGPMGState.MGCommonPlayerIn mgCommonPlayerIn = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonPlayerIn.class);
                sudFSMMGCache.onPlayerMGCommonPlayerIn(userId, mgCommonPlayerIn);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerIn(handle, userId, mgCommonPlayerIn);
                }
                break;
            case SudMGPMGState.MG_COMMON_PLAYER_READY: // 2.准备状态（已修改）
                SudMGPMGState.MGCommonPlayerReady mgCommonPlayerReady = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonPlayerReady.class);
                sudFSMMGCache.onPlayerMGCommonPlayerReady(userId, mgCommonPlayerReady);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerReady(handle, userId, mgCommonPlayerReady);
                }
                break;
            case SudMGPMGState.MG_COMMON_PLAYER_CAPTAIN: // 3.队长状态（已修改）
                SudMGPMGState.MGCommonPlayerCaptain mgCommonPlayerCaptain = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonPlayerCaptain.class);
                sudFSMMGCache.onPlayerMGCommonPlayerCaptain(userId, mgCommonPlayerCaptain);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerCaptain(handle, userId, mgCommonPlayerCaptain);
                }
                break;
            case SudMGPMGState.MG_COMMON_PLAYER_PLAYING: // 4.游戏状态（已修改）
                SudMGPMGState.MGCommonPlayerPlaying mgCommonPlayerPlaying = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonPlayerPlaying.class);
                sudFSMMGCache.onPlayerMGCommonPlayerPlaying(userId, mgCommonPlayerPlaying);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerPlaying(handle, userId, mgCommonPlayerPlaying);
                }
                break;
            case SudMGPMGState.MG_COMMON_PLAYER_ONLINE: // 5.玩家在线状态
                SudMGPMGState.MGCommonPlayerOnline mgCommonPlayerOnline = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonPlayerOnline.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerOnline(handle, userId, mgCommonPlayerOnline);
                }
                break;
            case SudMGPMGState.MG_COMMON_PLAYER_CHANGE_SEAT: // 6.玩家换游戏位状态
                SudMGPMGState.MGCommonPlayerChangeSeat mgCommonPlayerChangeSeat = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonPlayerChangeSeat.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerChangeSeat(handle, userId, mgCommonPlayerChangeSeat);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_GAME_PLAYER_ICON: // 7. 游戏通知app点击玩家头像
                SudMGPMGState.MGCommonSelfClickGamePlayerIcon mgCommonSelfClickGamePlayerIcon = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickGamePlayerIcon.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfClickGamePlayerIcon(handle, userId, mgCommonSelfClickGamePlayerIcon);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_DIE_STATUS: // 8. 游戏通知app玩家死亡状态（2022-04-24新增）
                SudMGPMGState.MGCommonSelfDieStatus mgCommonSelfDieStatus = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfDieStatus.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfDieStatus(handle, userId, mgCommonSelfDieStatus);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_TURN_STATUS: // 9. 游戏通知app轮到玩家出手状态（2022-04-24新增）
                SudMGPMGState.MGCommonSelfTurnStatus mgCommonSelfTurnStatus = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfTurnStatus.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfTurnStatus(handle, userId, mgCommonSelfTurnStatus);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_SELECT_STATUS: // 10. 游戏通知app玩家选择状态（2022-04-24新增）
                SudMGPMGState.MGCommonSelfSelectStatus mgCommonSelfSelectStatus = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfSelectStatus.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfSelectStatus(handle, userId, mgCommonSelfSelectStatus);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_COUNTDOWN_TIME: // 11. 游戏通知app层当前游戏剩余时间（2022-05-23新增，目前UMO生效）
                SudMGPMGState.MGCommonGameCountdownTime mgCommonGameCountdownTime = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameCountdownTime.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonGameCountdownTime(handle, userId, mgCommonGameCountdownTime);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_OB_STATUS: // 12. 游戏通知app层当前玩家死亡后变成ob视角（2022-08-23新增，目前狼人杀生效）
                SudMGPMGState.MGCommonSelfObStatus mgCommonSelfObStatus = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfObStatus.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfObStatus(handle, userId, mgCommonSelfObStatus);
                }
                break;
            case SudMGPMGState.MG_DG_SELECTING: // 1. 选词中状态（已修改）
                SudMGPMGState.MGDGSelecting mgdgSelecting = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGDGSelecting.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGSelecting(handle, userId, mgdgSelecting);
                }
                break;
            case SudMGPMGState.MG_DG_PAINTING: // 2. 作画中状态（已修改）
                SudMGPMGState.MGDGPainting mgdgPainting = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGDGPainting.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGPainting(handle, userId, mgdgPainting);
                }
                break;
            case SudMGPMGState.MG_DG_ERRORANSWER: // 3. 显示错误答案状态（已修改）
                SudMGPMGState.MGDGErroranswer mgdgErroranswer = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGDGErroranswer.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGErroranswer(handle, userId, mgdgErroranswer);
                }
                break;
            case SudMGPMGState.MG_DG_TOTALSCORE: // 4. 显示总积分状态（已修改）
                SudMGPMGState.MGDGTotalscore mgdgTotalscore = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGDGTotalscore.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGTotalscore(handle, userId, mgdgTotalscore);
                }
                break;
            case SudMGPMGState.MG_DG_SCORE: // 5. 本次获得积分状态（已修改）
                SudMGPMGState.MGDGScore mgdgScore = SudJsonUtils.fromJson(dataJson, SudMGPMGState.MGDGScore.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGScore(handle, userId, mgdgScore);
                }
                break;
            default:
                ISudFSMStateHandleUtils.handleSuccess(handle);
                break;
        }
    }

    /** 获取队长userId */
    public String getCaptainUserId() {
        return sudFSMMGCache.getCaptainUserId();
    }

    // 返回该玩家是否正在游戏中
    public boolean playerIsPlaying(String userId) {
        return sudFSMMGCache.playerIsPlaying(userId);
    }

    // 返回该玩家是否已准备
    public boolean playerIsReady(String userId) {
        return sudFSMMGCache.playerIsReady(userId);
    }

    // 返回该玩家是否已加入了游戏
    public boolean playerIsIn(String userId) {
        return sudFSMMGCache.playerIsIn(userId);
    }

    // 获取当前游戏中的人数
    public int getPlayerInNumber() {
        return sudFSMMGCache.getPlayerInNumber();
    }

    // 是否数字炸弹
    public boolean isHitBomb() {
        return sudFSMMGCache.isHitBomb();
    }

    // 销毁游戏
    public void destroyMG() {
        sudFSMMGCache.destroyMG();
    }

    /**
     * 返回当前游戏的状态，数值参数{@link SudMGPMGState.MGCommonGameState}
     */
    public int getGameState() {
        return sudFSMMGCache.getGameState();
    }

    /** 获取缓存的状态 */
    public SudFSMMGCache getSudFSMMGCache() {
        return sudFSMMGCache;
    }

}
