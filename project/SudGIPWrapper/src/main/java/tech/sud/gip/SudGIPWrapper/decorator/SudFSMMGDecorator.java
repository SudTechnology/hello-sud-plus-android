/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.gip.SudGIPWrapper.decorator;

import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.gip.SudGIPWrapper.utils.ISudFSMStateHandleUtils;
import tech.sud.gip.SudGIPWrapper.utils.SudJsonUtils;
import tech.sud.gip.core.ISudFSMMG;
import tech.sud.gip.core.ISudFSMStateHandle;

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
            case SudGIPMGState.MG_COMMON_PUBLIC_MESSAGE: // 1. 公屏消息
                SudGIPMGState.MGCommonPublicMessage mgCommonPublicMessage = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonPublicMessage.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonPublicMessage(handle, mgCommonPublicMessage);
                }
                break;
            case SudGIPMGState.MG_COMMON_KEY_WORD_TO_HIT: // 2. 关键词状态
                SudGIPMGState.MGCommonKeyWordToHit mgCommonKeyWordToHit = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonKeyWordToHit.class);
                sudFSMMGCache.onGameMGCommonKeyWordToHit(mgCommonKeyWordToHit);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonKeyWordToHit(handle, mgCommonKeyWordToHit);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_SETTLE: // 3. 游戏结算状态
                SudGIPMGState.MGCommonGameSettle mgCommonGameSettle = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameSettle.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSettle(handle, mgCommonGameSettle);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_CLICK_JOIN_BTN: // 4. 加入游戏按钮点击状态
                SudGIPMGState.MGCommonSelfClickJoinBtn mgCommonSelfClickJoinBtn = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfClickJoinBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickJoinBtn(handle, mgCommonSelfClickJoinBtn);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_CLICK_CANCEL_JOIN_BTN: // 5. 取消加入(退出)游戏按钮点击状态
                SudGIPMGState.MGCommonSelfClickCancelJoinBtn selfClickCancelJoinBtn = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfClickCancelJoinBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickCancelJoinBtn(handle, selfClickCancelJoinBtn);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_CLICK_READY_BTN: // 6. 准备按钮点击状态
                SudGIPMGState.MGCommonSelfClickReadyBtn mgCommonSelfClickReadyBtn = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfClickReadyBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickReadyBtn(handle, mgCommonSelfClickReadyBtn);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_CLICK_CANCEL_READY_BTN: // 7. 取消准备按钮点击状态
                SudGIPMGState.MGCommonSelfClickCancelReadyBtn mgCommonSelfClickCancelReadyBtn = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfClickCancelReadyBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickCancelReadyBtn(handle, mgCommonSelfClickCancelReadyBtn);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_CLICK_START_BTN: // 8. 开始游戏按钮点击状态
                SudGIPMGState.MGCommonSelfClickStartBtn mgCommonSelfClickStartBtn = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfClickStartBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickStartBtn(handle, mgCommonSelfClickStartBtn);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_CLICK_SHARE_BTN: // 9. 分享按钮点击状态
                SudGIPMGState.MGCommonSelfClickShareBtn mgCommonSelfClickShareBtn = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfClickShareBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickShareBtn(handle, mgCommonSelfClickShareBtn);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_STATE: // 10. 游戏状态
                SudGIPMGState.MGCommonGameState mgCommonGameState = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameState.class);
                sudFSMMGCache.onGameMGCommonGameState(mgCommonGameState);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameState(handle, mgCommonGameState);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_CLICK_GAME_SETTLE_CLOSE_BTN: // 11. 结算界面关闭按钮点击状态（2021-12-27新增）
                SudGIPMGState.MGCommonSelfClickGameSettleCloseBtn mgCommonSelfClickGameSettleCloseBtn = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfClickGameSettleCloseBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGameSettleCloseBtn(handle, mgCommonSelfClickGameSettleCloseBtn);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_CLICK_GAME_SETTLE_AGAIN_BTN: // 12. 结算界面再来一局按钮点击状态（2021-12-27新增）
                SudGIPMGState.MGCommonSelfClickGameSettleAgainBtn mgCommonSelfClickGameSettleAgainBtn = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfClickGameSettleAgainBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGameSettleAgainBtn(handle, mgCommonSelfClickGameSettleAgainBtn);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_SOUND_LIST: // 13. 游戏上报游戏中的声音列表（2021-12-30新增，现在只支持碰碰我最强）
                SudGIPMGState.MGCommonGameSoundList mgCommonGameSoundList = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameSoundList.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSoundList(handle, mgCommonGameSoundList);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_SOUND: // 14. 游通知app层播放声音（2021-12-30新增，现在只支持碰碰我最强）
                SudGIPMGState.MGCommonGameSound mgCommonGameSound = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameSound.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSound(handle, mgCommonGameSound);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_BG_MUSIC_STATE: // 15. 游戏通知app层播放背景音乐状态（2022-01-07新增，现在只支持碰碰我最强）
                SudGIPMGState.MGCommonGameBgMusicState mgCommonGameBgMusicState = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameBgMusicState.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameBgMusicState(handle, mgCommonGameBgMusicState);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_SOUND_STATE: // 16. 游戏通知app层播放音效的状态（2022-01-07新增，现在只支持碰碰我最强）
                SudGIPMGState.MGCommonGameSoundState mgCommonGameSoundState = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameSoundState.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSoundState(handle, mgCommonGameSoundState);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_ASR: // 17. ASR状态(开启和关闭语音识别状态，v1.1.45.xx 版本新增)
                SudGIPMGState.MGCommonGameASR mgCommonGameASR = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameASR.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameASR(handle, mgCommonGameASR);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_MICROPHONE: // 18. 麦克风状态（2022-02-08新增）
                SudGIPMGState.MGCommonSelfMicrophone mgCommonSelfMicrophone = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfMicrophone.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfMicrophone(handle, mgCommonSelfMicrophone);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_HEADPHONE: // 19. 耳机（听筒，扬声器）状态（2022-02-08新增）
                SudGIPMGState.MGCommonSelfHeadphone mgCommonSelfHeadphone = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfHeadphone.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfHeadphone(handle, mgCommonSelfHeadphone);
                }
                break;
            case SudGIPMGState.MG_COMMON_APP_COMMON_SELF_X_RESP: // 20. App通用状态操作结果错误码（2022-05-10新增）
                SudGIPMGState.MGCommonAPPCommonSelfXResp mgCommonAPPCommonSelfXResp = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonAPPCommonSelfXResp.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAPPCommonSelfXResp(handle, mgCommonAPPCommonSelfXResp);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_ADD_AI_PLAYERS: // 21. 游戏通知app层添加陪玩机器人是否成功（2022-05-17新增）
                SudGIPMGState.MGCommonGameAddAIPlayers mgCommonGameAddAIPlayers = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameAddAIPlayers.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameAddAIPlayers(handle, mgCommonGameAddAIPlayers);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_NETWORK_STATE: // 22. 游戏通知app层添当前网络连接状态（2022-06-21新增）
                SudGIPMGState.MGCommonGameNetworkState mgCommonGameNetworkState = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameNetworkState.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameNetworkState(handle, mgCommonGameNetworkState);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_GET_SCORE: // 23. 游戏通知app获取积分
                SudGIPMGState.MGCommonGameGetScore mgCommonGameScore = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameGetScore.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameGetScore(handle, mgCommonGameScore);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_SET_SCORE: // 24. 游戏通知app带入积分
                SudGIPMGState.MGCommonGameSetScore mgCommonGameSetScore = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameSetScore.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSetScore(handle, mgCommonGameSetScore);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_CREATE_ORDER: // 25. 创建订单
                SudGIPMGState.MGCommonGameCreateOrder mgCommonGameCreateOrder = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameCreateOrder.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameCreateOrder(handle, mgCommonGameCreateOrder);
                }
                break;
            case SudGIPMGState.MG_COMMON_PLAYER_ROLE_ID: // 26. 游戏通知app玩家角色(仅对狼人杀有效)
                SudGIPMGState.MGCommonPlayerRoleId mgCommonPlayerRoleId = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonPlayerRoleId.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonPlayerRoleId(handle, mgCommonPlayerRoleId);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_CLICK_POOP: // 27. 游戏通知app玩家被扔便便(你画我猜，你说我猜，友尽闯关有效)
                SudGIPMGState.MGCommonSelfClickPoop mgCommonSelfClickPoop = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfClickPoop.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickPoop(handle, mgCommonSelfClickPoop);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_CLICK_GOOD: // 28. 游戏通知app玩家被点赞(你画我猜，你说我猜，友尽闯关有效)
                SudGIPMGState.MGCommonSelfClickGood mgCommonSelfClickGood = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfClickGood.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGood(handle, mgCommonSelfClickGood);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_FPS: // 29. 游戏通知app游戏FPS(仅对碰碰，多米诺骨牌，飞镖达人生效)
                SudGIPMGState.MGCommonGameFps mgCommonGameFps = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameFps.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameFps(handle, mgCommonGameFps);
                }
                break;
            case SudGIPMGState.MG_COMMON_ALERT: // 30. 游戏通知app游戏弹框
                SudGIPMGState.MGCommonAlert mgCommonAlert = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonAlert.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAlert(handle, mgCommonAlert);
                }
                break;
            case SudGIPMGState.MG_COMMON_WORST_TEAMMATE: // 31. 游戏通知app最坑队友（只支持友尽闯关）
                SudGIPMGState.MGCommonWorstTeammate mgCommonWorstTeammate = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonWorstTeammate.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonWorstTeammate(handle, mgCommonWorstTeammate);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_OVER_TIP: // 32. 游戏通知app因玩家逃跑导致游戏结束（只支持友尽闯关）
                SudGIPMGState.MGCommonGameOverTip mgCommonGameOverTip = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameOverTip.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameOverTip(handle, mgCommonGameOverTip);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_PLAYER_COLOR: // 33. 游戏通知app玩家颜色（只支持友尽闯关）
                SudGIPMGState.MGCommonGamePlayerColor mgCommonGamePlayerColor = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGamePlayerColor.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerColor(handle, mgCommonGamePlayerColor);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_PLAYER_ICON_POSITION: // 34. 游戏通知app玩家头像的坐标（只支持ludo）
                SudGIPMGState.MGCommonGamePlayerIconPosition mgCommonGamePlayerIconPosition = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGamePlayerIconPosition.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerIconPosition(handle, mgCommonGamePlayerIconPosition);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_CLICK_EXIT_GAME_BTN: // 35. 游戏通知app退出游戏（只支持teenpattipro 与 德州pro）
                SudGIPMGState.MGCommonSelfClickExitGameBtn mgCommonSelfClickExitGameBtn = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfClickExitGameBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickExitGameBtn(handle, mgCommonSelfClickExitGameBtn);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_IS_APP_CHIP: // 36. 游戏通知app是否要开启带入积分（只支持teenpattipro 与 德州pro）
                SudGIPMGState.MGCommonGameIsAppChip mgCommonGameIsAppChip = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameIsAppChip.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameIsAppChip(handle, mgCommonGameIsAppChip);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_RULE: // 37. 游戏通知app当前游戏的设置信息
                SudGIPMGState.MGCommonGameRule mgCommonGameRule = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameRule.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameRule(handle, mgCommonGameRule);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_SETTINGS: // 38. 游戏通知app进行玩法设置（只支持德州pro，teenpatti pro）
                SudGIPMGState.MGCommonGameSettings mgCommonGameSettings = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameSettings.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSettings(handle, mgCommonGameSettings);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_MONEY_NOT_ENOUGH: // 39. 游戏通知app钱币不足（只支持德州pro，teenpatti pro）
                SudGIPMGState.MGCommonGameMoneyNotEnough mgCommonGameMoneyNotEnough = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameMoneyNotEnough.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameMoneyNotEnough(handle, mgCommonGameMoneyNotEnough);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_UI_CUSTOM_CONFIG: // 40. 游戏通知app下发定制ui配置表（只支持ludo）
                SudGIPMGState.MGCommonGameUiCustomConfig mgCommonGameUiCustomConfig = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameUiCustomConfig.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameUiCustomConfig(handle, mgCommonGameUiCustomConfig);
                }
                break;
            case SudGIPMGState.MG_COMMON_SET_CLICK_RECT: // 41. 设置app提供给游戏可点击区域(赛车)
                SudGIPMGState.MGCommonSetClickRect mgCommonSetClickRect = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSetClickRect.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSetClickRect(handle, mgCommonSetClickRect);
                }
                break;
            case SudGIPMGState.MG_COMMON_USERS_INFO: // 42. 通知app提供对应uids列表玩家的数据(赛车)
                SudGIPMGState.MGCommonUsersInfo mgCommonUsersInfo = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonUsersInfo.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonUsersInfo(handle, mgCommonUsersInfo);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_PREPARE_FINISH: // 43. 通知app游戏前期准备完成(赛车)
                SudGIPMGState.MGCommonGamePrepareFinish mgCommonGamePrepareFinish = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGamePrepareFinish.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePrepareFinish(handle, mgCommonGamePrepareFinish);
                }
                break;
            case SudGIPMGState.MG_COMMON_SHOW_GAME_SCENE: // 44. 通知app游戏主界面已显示(赛车)
                SudGIPMGState.MGCommonShowGameScene mgCommonShowGameScene = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonShowGameScene.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonShowGameScene(handle, mgCommonShowGameScene);
                }
                break;
            case SudGIPMGState.MG_COMMON_HIDE_GAME_SCENE: // 45. 通知app游戏主界面已隐藏(赛车)
                SudGIPMGState.MGCommonHideGameScene mgCommonHideGameScene = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonHideGameScene.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonHideGameScene(handle, mgCommonHideGameScene);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_CLICK_GOLD_BTN: // 46. 通知app点击了游戏的金币按钮(赛车)
                SudGIPMGState.MGCommonSelfClickGoldBtn mgCommonSelfClickGoldBtn = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfClickGoldBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGoldBtn(handle, mgCommonSelfClickGoldBtn);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_PIECE_ARRIVE_END: // 47. 通知app棋子到达终点(ludo)
                SudGIPMGState.MGCommonGamePieceArriveEnd mgCommonGamePieceArriveEnd = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGamePieceArriveEnd.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePieceArriveEnd(handle, mgCommonGamePieceArriveEnd);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_PLAYER_MANAGED_STATE: // 48. 通知app玩家是否托管
                SudGIPMGState.MGCommonGamePlayerManagedState mgCommonGamePlayerManagedState = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGamePlayerManagedState.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerManagedState(handle, mgCommonGamePlayerManagedState);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_SEND_BURST_WORD: // 49. 游戏向app发送爆词
                SudGIPMGState.MGCommonGameSendBurstWord mgCommonGameSendBurstWord = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameSendBurstWord.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSendBurstWord(handle, mgCommonGameSendBurstWord);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_PLAYER_RANKS: // 50. 游戏向app发送玩家实时排名（只支持怪物消消乐）
                SudGIPMGState.MGCommonGamePlayerRanks mgCommonGamePlayerRanks = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGamePlayerRanks.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerRanks(handle, mgCommonGamePlayerRanks);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_PLAYER_PAIR_SINGULAR: // 51. 游戏向app发送玩家即时变化的单双牌（只支持okey101）
                SudGIPMGState.MGCommonGamePlayerPairSingular mgCommonGamePlayerPairSingular = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGamePlayerPairSingular.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerPairSingular(handle, mgCommonGamePlayerPairSingular);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_PLAYER_MONOPOLY_CARDS: // 52. 游戏向app发送获取玩家持有的道具卡（只支持大富翁）
                SudGIPMGState.MGCommonGamePlayerMonopolyCards mgCommonGamePlayerMonopolyCards = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGamePlayerMonopolyCards.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerMonopolyCards(handle, mgCommonGamePlayerMonopolyCards);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_PLAYER_SCORES: // 53. 游戏向app发送玩家实时积分（只支持怪物消消乐）
                SudGIPMGState.MGCommonGamePlayerScores mgCommonGamePlayerScores = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGamePlayerScores.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerScores(handle, mgCommonGamePlayerScores);
                }
                break;
            case SudGIPMGState.MG_COMMON_DESTROY_GAME_SCENE: // 54. 游戏通知app销毁游戏（只支持部分概率类游戏）
                SudGIPMGState.MGCommonDestroyGameScene mgCommonDestroyGameScene = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonDestroyGameScene.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonDestroyGameScene(handle, mgCommonDestroyGameScene);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_BILLIARDS_HIT_STATE: // 55. 游戏通知app击球状态（只支持桌球）
                SudGIPMGState.MGCommonGameBilliardsHitState mgCommonGameBilliardsHitState = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameBilliardsHitState.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameBilliardsHitState(handle, mgCommonGameBilliardsHitState);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_PLAYER_PROPS_CARDS: // 56. 游戏向app发送获取玩家持有的指定点数道具卡（只支持飞行棋）
                SudGIPMGState.MGCommonGamePlayerPropsCards mgCommonGamePlayerPropsCards = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGamePlayerPropsCards.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerPropsCards(handle, mgCommonGamePlayerPropsCards);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_INFO_X: // 57. 游戏向app发送获游戏通用数据
                SudGIPMGState.MGCommonGameInfoX mgCommonGameInfoX = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameInfoX.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameInfoX(handle, mgCommonGameInfoX);
                }
                break;
            case SudGIPMGState.MG_COMMON_AI_MODEL_MESSAGE: // 通知app开启ai大模型
                SudGIPMGState.MGCommonAiModelMessage mgCommonAiModelMessage = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonAiModelMessage.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAiModelMessage(handle, mgCommonAiModelMessage);
                }
                break;
            case SudGIPMGState.MG_COMMON_AI_MESSAGE: // 通知app ai消息
                SudGIPMGState.MGCommonAiMessage mgCommonAiMessage = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonAiMessage.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAiMessage(handle, mgCommonAiMessage);
                }
                break;
            case SudGIPMGState.MG_COMMON_AI_LARGE_SCALE_MODEL_MSG: // 64. 通知app ai大模型消息
                SudGIPMGState.MGCommonAiLargeScaleModelMsg mgCommonAiLargeScaleModelMsg = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonAiLargeScaleModelMsg.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAiLargeScaleModelMsg(handle, mgCommonAiLargeScaleModelMsg);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_PLAYER_MIC_STATE: // 65. 通知app可以开始推送麦克说话状态
                SudGIPMGState.MGCommonGamePlayerMicState mgCommonGamePlayerMicState = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGamePlayerMicState.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerMicState(handle, mgCommonGamePlayerMicState);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_DISCO_ACTION: // 1. 元宇宙砂砂舞指令回调
                SudGIPMGState.MGCommonGameDiscoAction mgCommonGameDiscoAction = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameDiscoAction.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameDiscoAction(handle, mgCommonGameDiscoAction);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_DISCO_ACTION_END: // 2. 元宇宙砂砂舞指令动作结束通知
                SudGIPMGState.MGCommonGameDiscoActionEnd mgCommonGameDiscoActionEnd = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameDiscoActionEnd.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameDiscoActionEnd(handle, mgCommonGameDiscoActionEnd);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_CONFIG: // 1. 礼物配置文件(火箭)
                SudGIPMGState.MGCustomRocketConfig mgCustomRocketConfig = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketConfig.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketConfig(handle, mgCustomRocketConfig);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_MODEL_LIST: // 2. 拥有模型列表(火箭)
                SudGIPMGState.MGCustomRocketModelList mgCustomRocketModelList = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketModelList.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketModelList(handle, mgCustomRocketModelList);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_COMPONENT_LIST: // 3. 拥有组件列表(火箭)
                SudGIPMGState.MGCustomRocketComponentList mgCustomRocketComponentList = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketComponentList.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketComponentList(handle, mgCustomRocketComponentList);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_USER_INFO: // 4. 获取用户信息(火箭)
                SudGIPMGState.MGCustomRocketUserInfo mgCustomRocketUserInfo = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketUserInfo.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketUserInfo(handle, mgCustomRocketUserInfo);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_ORDER_RECORD_LIST: // 5. 订单记录列表(火箭)
                SudGIPMGState.MGCustomRocketOrderRecordList mgCustomRocketOrderRecordList = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketOrderRecordList.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketOrderRecordList(handle, mgCustomRocketOrderRecordList);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_ROOM_RECORD_LIST: // 6. 展馆内列表(火箭)
                SudGIPMGState.MGCustomRocketRoomRecordList mgCustomRocketRoomRecordList = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketRoomRecordList.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketRoomRecordList(handle, mgCustomRocketRoomRecordList);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_USER_RECORD_LIST: // 7. 展馆内玩家送出记录(火箭)
                SudGIPMGState.MGCustomRocketUserRecordList mgCustomRocketUserRecordList = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketUserRecordList.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketUserRecordList(handle, mgCustomRocketUserRecordList);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_SET_DEFAULT_MODEL: // 8. 设置默认模型(火箭)
                SudGIPMGState.MGCustomRocketSetDefaultModel mgCustomRocketSetDefaultSeat = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketSetDefaultModel.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketSetDefaultModel(handle, mgCustomRocketSetDefaultSeat);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_DYNAMIC_FIRE_PRICE: // 9. 动态计算一键发送价格(火箭)
                SudGIPMGState.MGCustomRocketDynamicFirePrice mgCustomRocketDynamicFirePrice = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketDynamicFirePrice.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketDynamicFirePrice(handle, mgCustomRocketDynamicFirePrice);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_FIRE_MODEL: // 10. 一键发送(火箭)
                SudGIPMGState.MGCustomRocketFireModel mGCustomRocketFireModel = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketFireModel.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketFireModel(handle, mGCustomRocketFireModel);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_CREATE_MODEL: // 11. 新组装模型(火箭)
                SudGIPMGState.MGCustomRocketCreateModel mgCustomRocketCreateModel = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketCreateModel.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketCreateModel(handle, mgCustomRocketCreateModel);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_REPLACE_COMPONENT: // 12. 模型更换组件(火箭)
                SudGIPMGState.MGCustomRocketReplaceComponent mgCustomRocketReplaceComponent = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketReplaceComponent.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketReplaceComponent(handle, mgCustomRocketReplaceComponent);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_BUY_COMPONENT: // 13. 购买组件(火箭)
                SudGIPMGState.MGCustomRocketBuyComponent mgCustomRocketBuyComponent = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketBuyComponent.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketBuyComponent(handle, mgCustomRocketBuyComponent);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_PLAY_EFFECT_START: // 14. 播放效果开始(火箭)
                SudGIPMGState.MGCustomRocketPlayEffectStart mgCustomRocketPlayEffectStart = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketPlayEffectStart.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketPlayEffectStart(handle, mgCustomRocketPlayEffectStart);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_PLAY_EFFECT_FINISH: // 15. 播放效果完成(火箭)
                SudGIPMGState.MGCustomRocketPlayEffectFinish mgCustomRocketPlayEffectFinish = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketPlayEffectFinish.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketPlayEffectFinish(handle, mgCustomRocketPlayEffectFinish);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_VERIFY_SIGN: // 16. 验证签名合规(火箭)
                SudGIPMGState.MGCustomRocketVerifySign mgCustomRocketVerifySign = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketVerifySign.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketVerifySign(handle, mgCustomRocketVerifySign);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_UPLOAD_MODEL_ICON: // 17. 上传icon(火箭)
                SudGIPMGState.MGCustomRocketUploadModelIcon mgCustomRocketUploadModelIcon = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketUploadModelIcon.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketUploadModelIcon(handle, mgCustomRocketUploadModelIcon);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_PREPARE_FINISH: // 18. 前期准备完成(火箭)
                SudGIPMGState.MGCustomRocketPrepareFinish mgCustomRocketPrepareFinish = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketPrepareFinish.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketPrepareFinish(handle, mgCustomRocketPrepareFinish);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_SHOW_GAME_SCENE: // 19. 火箭主界面已显示(火箭)
                SudGIPMGState.MGCustomRocketShowGameScene mgCustomRocketShowGameScene = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketShowGameScene.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketShowGameScene(handle, mgCustomRocketShowGameScene);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_HIDE_GAME_SCENE: // 20. 火箭主界面已隐藏(火箭)
                SudGIPMGState.MGCustomRocketHideGameScene mgCustomRocketHideGameScene = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketHideGameScene.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketHideGameScene(handle, mgCustomRocketHideGameScene);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_CLICK_LOCK_COMPONENT: // 21. 点击锁住组件(火箭)
                SudGIPMGState.MGCustomRocketClickLockComponent mgCustomRocketClickLockComponent = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketClickLockComponent.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketClickLockComponent(handle, mgCustomRocketClickLockComponent);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_FLY_CLICK: // 22. 火箭效果飞行点击(火箭)
                SudGIPMGState.MGCustomRocketFlyClick mgCustomRocketFlyClick = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketFlyClick.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketFlyClick(handle, mgCustomRocketFlyClick);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_FLY_END: // 23. 火箭效果飞行结束(火箭)
                SudGIPMGState.MGCustomRocketFlyEnd mgCustomRocketFlyEnd = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketFlyEnd.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketFlyEnd(handle, mgCustomRocketFlyEnd);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_SET_CLICK_RECT: // 24. 设置点击区域(火箭)
                SudGIPMGState.MGCustomRocketSetClickRect mgCustomRocketSetClickRect = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketSetClickRect.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketSetClickRect(handle, mgCustomRocketSetClickRect);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_ROCKET_SAVE_SIGN_COLOR: // 25. 颜色和签名自定义改到装配间的模式，保存颜色或签名 模型
                SudGIPMGState.MGCustomRocketSaveSignColor mgCustomRocketSaveSignColor = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomRocketSaveSignColor.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketSaveSignColor(handle, mgCustomRocketSaveSignColor);
                }
                break;
            case SudGIPMGState.MG_BASEBALL_DEFUALT_STATE: // 1. 设置界面默认状态(棒球)
                SudGIPMGState.MGBaseballDefaultState mgBaseballDefaultState = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGBaseballDefaultState.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballDefaultState(handle, mgBaseballDefaultState);
                }
                break;
            case SudGIPMGState.MG_BASEBALL_PREPARE_FINISH: // 2. 前期准备完成(棒球)
                SudGIPMGState.MGBaseballPrepareFinish mgBaseballPrepareFinish = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGBaseballPrepareFinish.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballPrepareFinish(handle, mgBaseballPrepareFinish);
                }
                break;
            case SudGIPMGState.MG_BASEBALL_SHOW_GAME_SCENE: // 3. 主界面已显示(棒球)
                SudGIPMGState.MGBaseballShowGameScene mgBaseballShowGameScene = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGBaseballShowGameScene.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballShowGameScene(handle, mgBaseballShowGameScene);
                }
                break;
            case SudGIPMGState.MG_BASEBALL_HIDE_GAME_SCENE: // 4. 主界面已隐藏(棒球)
                SudGIPMGState.MGBaseballHideGameScene mgBaseballHideGameScene = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGBaseballHideGameScene.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballHideGameScene(handle, mgBaseballHideGameScene);
                }
                break;
            case SudGIPMGState.MG_BASEBALL_RANKING: // 5. 查询排行榜数据(棒球)
                SudGIPMGState.MGBaseballRanking mgBaseballRanking = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGBaseballRanking.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballRanking(handle, mgBaseballRanking);
                }
                break;
            case SudGIPMGState.MG_BASEBALL_MY_RANKING: // 6. 查询我的排名(棒球)
                SudGIPMGState.MGBaseballMyRanking mgBaseballMyRanking = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGBaseballMyRanking.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballMyRanking(handle, mgBaseballMyRanking);
                }
                break;
            case SudGIPMGState.MG_BASEBALL_RANGE_INFO: // 7. 查询当前距离我的前后玩家数据(棒球)
                SudGIPMGState.MGBaseballRangeInfo mgBaseballRangeInfo = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGBaseballRangeInfo.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballRangeInfo(handle, mgBaseballRangeInfo);
                }
                break;
            case SudGIPMGState.MG_BASEBALL_SET_CLICK_RECT: // 8. 设置app提供给游戏可点击区域(棒球)
                SudGIPMGState.MGBaseballSetClickRect mgBaseballSetClickRect = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGBaseballSetClickRect.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballSetClickRect(handle, mgBaseballSetClickRect);
                }
                break;
            case SudGIPMGState.MG_BASEBALL_TEXT_CONFIG: // 9. 获取文本配置数据(棒球)
                SudGIPMGState.MGBaseballTextConfig mgBaseballTextConfig = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGBaseballTextConfig.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballTextConfig(handle, mgBaseballTextConfig);
                }
                break;
            case SudGIPMGState.MG_BASEBALL_SEND_DISTANCE: // 10. 球落地, 通知距离(棒球)
                SudGIPMGState.MGBaseballSendDistance mgBaseballSendDistance = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGBaseballSendDistance.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballSendDistance(handle, mgBaseballSendDistance);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_CR_ROOM_INIT_DATA: // 1. 请求房间数据
                SudGIPMGState.MGCustomCrRoomInitData mgCustomCrRoomInitData = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomCrRoomInitData.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomCrRoomInitData(handle, mgCustomCrRoomInitData);
                }
                break;
            case SudGIPMGState.MG_CUSTOM_CR_CLICK_SEAT: // 2. 点击主播位或老板位通知
                SudGIPMGState.MGCustomCrClickSeat mgCustomCrClickSeat = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCustomCrClickSeat.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomCrClickSeat(handle, mgCustomCrClickSeat);
                }
                break;
            case SudGIPMGState.MG_HAPPY_GOAT_CHAT: // 1. 文本/语音聊天结果
                SudGIPMGState.MGHappyGoatChat mgHappyGoatChat = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGHappyGoatChat.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGHappyGoatChat(handle, mgHappyGoatChat);
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
            case SudGIPMGState.MG_COMMON_PLAYER_IN: // 1.加入状态（已修改）
                SudGIPMGState.MGCommonPlayerIn mgCommonPlayerIn = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonPlayerIn.class);
                sudFSMMGCache.onPlayerMGCommonPlayerIn(userId, mgCommonPlayerIn);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerIn(handle, userId, mgCommonPlayerIn);
                }
                break;
            case SudGIPMGState.MG_COMMON_PLAYER_READY: // 2.准备状态（已修改）
                SudGIPMGState.MGCommonPlayerReady mgCommonPlayerReady = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonPlayerReady.class);
                sudFSMMGCache.onPlayerMGCommonPlayerReady(userId, mgCommonPlayerReady);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerReady(handle, userId, mgCommonPlayerReady);
                }
                break;
            case SudGIPMGState.MG_COMMON_PLAYER_CAPTAIN: // 3.队长状态（已修改）
                SudGIPMGState.MGCommonPlayerCaptain mgCommonPlayerCaptain = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonPlayerCaptain.class);
                sudFSMMGCache.onPlayerMGCommonPlayerCaptain(userId, mgCommonPlayerCaptain);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerCaptain(handle, userId, mgCommonPlayerCaptain);
                }
                break;
            case SudGIPMGState.MG_COMMON_PLAYER_PLAYING: // 4.游戏状态（已修改）
                SudGIPMGState.MGCommonPlayerPlaying mgCommonPlayerPlaying = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonPlayerPlaying.class);
                sudFSMMGCache.onPlayerMGCommonPlayerPlaying(userId, mgCommonPlayerPlaying);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerPlaying(handle, userId, mgCommonPlayerPlaying);
                }
                break;
            case SudGIPMGState.MG_COMMON_PLAYER_ONLINE: // 5.玩家在线状态
                SudGIPMGState.MGCommonPlayerOnline mgCommonPlayerOnline = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonPlayerOnline.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerOnline(handle, userId, mgCommonPlayerOnline);
                }
                break;
            case SudGIPMGState.MG_COMMON_PLAYER_CHANGE_SEAT: // 6.玩家换游戏位状态
                SudGIPMGState.MGCommonPlayerChangeSeat mgCommonPlayerChangeSeat = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonPlayerChangeSeat.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerChangeSeat(handle, userId, mgCommonPlayerChangeSeat);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_CLICK_GAME_PLAYER_ICON: // 7. 游戏通知app点击玩家头像
                SudGIPMGState.MGCommonSelfClickGamePlayerIcon mgCommonSelfClickGamePlayerIcon = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfClickGamePlayerIcon.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfClickGamePlayerIcon(handle, userId, mgCommonSelfClickGamePlayerIcon);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_DIE_STATUS: // 8. 游戏通知app玩家死亡状态（2022-04-24新增）
                SudGIPMGState.MGCommonSelfDieStatus mgCommonSelfDieStatus = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfDieStatus.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfDieStatus(handle, userId, mgCommonSelfDieStatus);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_TURN_STATUS: // 9. 游戏通知app轮到玩家出手状态（2022-04-24新增）
                SudGIPMGState.MGCommonSelfTurnStatus mgCommonSelfTurnStatus = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfTurnStatus.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfTurnStatus(handle, userId, mgCommonSelfTurnStatus);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_SELECT_STATUS: // 10. 游戏通知app玩家选择状态（2022-04-24新增）
                SudGIPMGState.MGCommonSelfSelectStatus mgCommonSelfSelectStatus = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfSelectStatus.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfSelectStatus(handle, userId, mgCommonSelfSelectStatus);
                }
                break;
            case SudGIPMGState.MG_COMMON_GAME_COUNTDOWN_TIME: // 11. 游戏通知app层当前游戏剩余时间（2022-05-23新增，目前UMO生效）
                SudGIPMGState.MGCommonGameCountdownTime mgCommonGameCountdownTime = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonGameCountdownTime.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonGameCountdownTime(handle, userId, mgCommonGameCountdownTime);
                }
                break;
            case SudGIPMGState.MG_COMMON_SELF_OB_STATUS: // 12. 游戏通知app层当前玩家死亡后变成ob视角（2022-08-23新增，目前狼人杀生效）
                SudGIPMGState.MGCommonSelfObStatus mgCommonSelfObStatus = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGCommonSelfObStatus.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfObStatus(handle, userId, mgCommonSelfObStatus);
                }
                break;
            case SudGIPMGState.MG_DG_SELECTING: // 1. 选词中状态（已修改）
                SudGIPMGState.MGDGSelecting mgdgSelecting = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGDGSelecting.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGSelecting(handle, userId, mgdgSelecting);
                }
                break;
            case SudGIPMGState.MG_DG_PAINTING: // 2. 作画中状态（已修改）
                SudGIPMGState.MGDGPainting mgdgPainting = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGDGPainting.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGPainting(handle, userId, mgdgPainting);
                }
                break;
            case SudGIPMGState.MG_DG_ERRORANSWER: // 3. 显示错误答案状态（已修改）
                SudGIPMGState.MGDGErroranswer mgdgErroranswer = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGDGErroranswer.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGErroranswer(handle, userId, mgdgErroranswer);
                }
                break;
            case SudGIPMGState.MG_DG_TOTALSCORE: // 4. 显示总积分状态（已修改）
                SudGIPMGState.MGDGTotalscore mgdgTotalscore = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGDGTotalscore.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGTotalscore(handle, userId, mgdgTotalscore);
                }
                break;
            case SudGIPMGState.MG_DG_SCORE: // 5. 本次获得积分状态（已修改）
                SudGIPMGState.MGDGScore mgdgScore = SudJsonUtils.fromJson(dataJson, SudGIPMGState.MGDGScore.class);
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
        sudFSMMGListener = null;
    }

    /**
     * 返回当前游戏的状态，数值参数{@link SudGIPMGState.MGCommonGameState}
     */
    public int getGameState() {
        return sudFSMMGCache.getGameState();
    }

    /** 获取缓存的状态 */
    public SudFSMMGCache getSudFSMMGCache() {
        return sudFSMMGCache;
    }

}
