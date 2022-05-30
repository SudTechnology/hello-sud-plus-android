/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.hello.SudMGPWrapper.decorator;

import com.blankj.utilcode.util.LogUtils;

import tech.sud.mgp.core.ISudFSMMG;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.SudMGPWrapper.utils.HSJsonUtils;
import tech.sud.mgp.hello.SudMGPWrapper.utils.ISudFSMStateHandleUtils;

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
        LogUtils.d("onPlayerStateChange:" + "---:" + state + "---:" + dataJson);
        SudFSMMGListener listener = sudFSMMGListener;
        switch (state) {
            case SudMGPMGState.MG_COMMON_PUBLIC_MESSAGE: // 1. 公屏消息
                SudMGPMGState.MGCommonPublicMessage mgCommonPublicMessage = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonPublicMessage.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonPublicMessage(handle, mgCommonPublicMessage);
                }
                break;
            case SudMGPMGState.MG_COMMON_KEY_WORD_TO_HIT: // 2. 关键词状态
                SudMGPMGState.MGCommonKeyWordToHit mgCommonKeyWordToHit = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonKeyWordToHit.class);
                sudFSMMGCache.onGameMGCommonKeyWordToHit(mgCommonKeyWordToHit);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonKeyWordToHit(handle, mgCommonKeyWordToHit);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_SETTLE: // 3. 游戏结算状态
                SudMGPMGState.MGCommonGameSettle mgCommonGameSettle = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameSettle.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSettle(handle, mgCommonGameSettle);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_JOIN_BTN: // 4. 加入游戏按钮点击状态
                SudMGPMGState.MGCommonSelfClickJoinBtn mgCommonSelfClickJoinBtn = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickJoinBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickJoinBtn(handle, mgCommonSelfClickJoinBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_CANCEL_JOIN_BTN: // 5. 取消加入(退出)游戏按钮点击状态
                SudMGPMGState.MGCommonSelfClickCancelJoinBtn selfClickCancelJoinBtn = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickCancelJoinBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickCancelJoinBtn(handle, selfClickCancelJoinBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_READY_BTN: // 6. 准备按钮点击状态
                SudMGPMGState.MGCommonSelfClickReadyBtn mgCommonSelfClickReadyBtn = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickReadyBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickReadyBtn(handle, mgCommonSelfClickReadyBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_CANCEL_READY_BTN: // 7. 取消准备按钮点击状态
                SudMGPMGState.MGCommonSelfClickCancelReadyBtn mgCommonSelfClickCancelReadyBtn = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickCancelReadyBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickCancelReadyBtn(handle, mgCommonSelfClickCancelReadyBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_START_BTN: // 8. 开始游戏按钮点击状态
                SudMGPMGState.MGCommonSelfClickStartBtn mgCommonSelfClickStartBtn = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickStartBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickStartBtn(handle, mgCommonSelfClickStartBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_SHARE_BTN: // 9. 分享按钮点击状态
                SudMGPMGState.MGCommonSelfClickShareBtn mgCommonSelfClickShareBtn = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickShareBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickShareBtn(handle, mgCommonSelfClickShareBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_STATE: // 10. 游戏状态
                SudMGPMGState.MGCommonGameState mgCommonGameState = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameState.class);
                sudFSMMGCache.onGameMGCommonGameState(mgCommonGameState);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameState(handle, mgCommonGameState);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_GAME_SETTLE_CLOSE_BTN: // 11. 结算界面关闭按钮点击状态（2021-12-27新增）
                SudMGPMGState.MGCommonSelfClickGameSettleCloseBtn mgCommonSelfClickGameSettleCloseBtn = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickGameSettleCloseBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGameSettleCloseBtn(handle, mgCommonSelfClickGameSettleCloseBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_GAME_SETTLE_AGAIN_BTN: // 12. 结算界面再来一局按钮点击状态（2021-12-27新增）
                SudMGPMGState.MGCommonSelfClickGameSettleAgainBtn mgCommonSelfClickGameSettleAgainBtn = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickGameSettleAgainBtn.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGameSettleAgainBtn(handle, mgCommonSelfClickGameSettleAgainBtn);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_SOUND_LIST: // 13. 游戏上报游戏中的声音列表（2021-12-30新增，现在只支持碰碰我最强）
                SudMGPMGState.MGCommonGameSoundList mgCommonGameSoundList = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameSoundList.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSoundList(handle, mgCommonGameSoundList);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_SOUND: // 14. 游通知app层播放声音（2021-12-30新增，现在只支持碰碰我最强）
                SudMGPMGState.MGCommonGameSound mgCommonGameSound = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameSound.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSound(handle, mgCommonGameSound);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_BG_MUSIC_STATE: // 15. 游戏通知app层播放背景音乐状态（2022-01-07新增，现在只支持碰碰我最强）
                SudMGPMGState.MGCommonGameBgMusicState mgCommonGameBgMusicState = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameBgMusicState.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameBgMusicState(handle, mgCommonGameBgMusicState);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_SOUND_STATE: // 16. 游戏通知app层播放音效的状态（2022-01-07新增，现在只支持碰碰我最强）
                SudMGPMGState.MGCommonGameSoundState mgCommonGameSoundState = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameSoundState.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSoundState(handle, mgCommonGameSoundState);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_ASR: // 17. ASR状态(开启和关闭语音识别状态，v1.1.45.xx 版本新增)
                SudMGPMGState.MGCommonGameASR mgCommonGameASR = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameASR.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameASR(handle, mgCommonGameASR);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_MICROPHONE: // 18. 麦克风状态（2022-02-08新增）
                SudMGPMGState.MGCommonSelfMicrophone mgCommonSelfMicrophone = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfMicrophone.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfMicrophone(handle, mgCommonSelfMicrophone);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_HEADPHONE: // 19. 耳机（听筒，扬声器）状态（2022-02-08新增）
                SudMGPMGState.MGCommonSelfHeadphone mgCommonSelfHeadphone = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfHeadphone.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfHeadphone(handle, mgCommonSelfHeadphone);
                }
                break;
            case SudMGPMGState.MG_COMMON_APP_COMMON_SELF_X_RESP: // 20. App通用状态操作结果错误码（2022-05-10新增）
                SudMGPMGState.MGCommonAPPCommonSelfXResp mgCommonAPPCommonSelfXResp = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonAPPCommonSelfXResp.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAPPCommonSelfXResp(handle, mgCommonAPPCommonSelfXResp);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_ADD_AI_PLAYERS: // 21. 游戏通知app层添加陪玩机器人是否成功（2022-05-17新增）
                SudMGPMGState.MGCommonGameAddAIPlayers mgCommonGameAddAIPlayers = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameAddAIPlayers.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameAddAIPlayers(handle, mgCommonGameAddAIPlayers);
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
        LogUtils.d("onPlayerStateChange:" + userId + "---:" + state + "---:" + dataJson);
        SudFSMMGListener listener = sudFSMMGListener;
        switch (state) {
            case SudMGPMGState.MG_COMMON_PLAYER_IN: // 1.加入状态（已修改）
                SudMGPMGState.MGCommonPlayerIn mgCommonPlayerIn = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonPlayerIn.class);
                sudFSMMGCache.onPlayerMGCommonPlayerIn(userId, mgCommonPlayerIn);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerIn(handle, userId, mgCommonPlayerIn);
                }
                break;
            case SudMGPMGState.MG_COMMON_PLAYER_READY: // 2.准备状态（已修改）
                SudMGPMGState.MGCommonPlayerReady mgCommonPlayerReady = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonPlayerReady.class);
                sudFSMMGCache.onPlayerMGCommonPlayerReady(userId, mgCommonPlayerReady);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerReady(handle, userId, mgCommonPlayerReady);
                }
                break;
            case SudMGPMGState.MG_COMMON_PLAYER_CAPTAIN: // 3.队长状态（已修改）
                SudMGPMGState.MGCommonPlayerCaptain mgCommonPlayerCaptain = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonPlayerCaptain.class);
                sudFSMMGCache.onPlayerMGCommonPlayerCaptain(userId, mgCommonPlayerCaptain);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerCaptain(handle, userId, mgCommonPlayerCaptain);
                }
                break;
            case SudMGPMGState.MG_COMMON_PLAYER_PLAYING: // 4.游戏状态（已修改）
                SudMGPMGState.MGCommonPlayerPlaying mgCommonPlayerPlaying = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonPlayerPlaying.class);
                sudFSMMGCache.onPlayerMGCommonPlayerPlaying(userId, mgCommonPlayerPlaying);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerPlaying(handle, userId, mgCommonPlayerPlaying);
                }
                break;
            case SudMGPMGState.MG_COMMON_PLAYER_ONLINE: // 5.玩家在线状态
                SudMGPMGState.MGCommonPlayerOnline mgCommonPlayerOnline = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonPlayerOnline.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerOnline(handle, userId, mgCommonPlayerOnline);
                }
                break;
            case SudMGPMGState.MG_COMMON_PLAYER_CHANGE_SEAT: // 6.玩家换游戏位状态
                SudMGPMGState.MGCommonPlayerChangeSeat mgCommonPlayerChangeSeat = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonPlayerChangeSeat.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerChangeSeat(handle, userId, mgCommonPlayerChangeSeat);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_CLICK_GAME_PLAYER_ICON: // 7. 游戏通知app点击玩家头像（2022-02-09新增，现在只支持飞行棋ludo，仅用于游戏场景中的玩家头像）
                SudMGPMGState.MGCommonSelfClickGamePlayerIcon mgCommonSelfClickGamePlayerIcon = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfClickGamePlayerIcon.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfClickGamePlayerIcon(handle, userId, mgCommonSelfClickGamePlayerIcon);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_DIE_STATUS: // 8. 游戏通知app玩家死亡状态（2022-04-24新增）
                SudMGPMGState.MGCommonSelfDieStatus mgCommonSelfDieStatus = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfDieStatus.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfDieStatus(handle, userId, mgCommonSelfDieStatus);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_TURN_STATUS: // 9. 游戏通知app轮到玩家出手状态（2022-04-24新增）
                SudMGPMGState.MGCommonSelfTurnStatus mgCommonSelfTurnStatus = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfTurnStatus.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfTurnStatus(handle, userId, mgCommonSelfTurnStatus);
                }
                break;
            case SudMGPMGState.MG_COMMON_SELF_SELECT_STATUS: // 10. 游戏通知app玩家选择状态（2022-04-24新增）
                SudMGPMGState.MGCommonSelfSelectStatus mgCommonSelfSelectStatus = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonSelfSelectStatus.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfSelectStatus(handle, userId, mgCommonSelfSelectStatus);
                }
                break;
            case SudMGPMGState.MG_COMMON_GAME_COUNTDOWN_TIME: // 11. 游戏通知app层当前游戏剩余时间（2022-05-23新增，目前UMO生效）
                SudMGPMGState.MGCommonGameCountdownTime mgCommonGameCountdownTime = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGCommonGameCountdownTime.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonGameCountdownTime(handle, userId, mgCommonGameCountdownTime);
                }
                break;
            case SudMGPMGState.MG_DG_SELECTING: // 1. 选词中状态（已修改）
                SudMGPMGState.MGDGSelecting mgdgSelecting = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGDGSelecting.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGSelecting(handle, userId, mgdgSelecting);
                }
                break;
            case SudMGPMGState.MG_DG_PAINTING: // 2. 作画中状态（已修改）
                SudMGPMGState.MGDGPainting mgdgPainting = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGDGPainting.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGPainting(handle, userId, mgdgPainting);
                }
                break;
            case SudMGPMGState.MG_DG_ERRORANSWER: // 3. 显示错误答案状态（已修改）
                SudMGPMGState.MGDGErroranswer mgdgErroranswer = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGDGErroranswer.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGErroranswer(handle, userId, mgdgErroranswer);
                }
                break;
            case SudMGPMGState.MG_DG_TOTALSCORE: // 4. 显示总积分状态（已修改）
                SudMGPMGState.MGDGTotalscore mgdgTotalscore = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGDGTotalscore.class);
                if (listener == null) {
                    ISudFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGTotalscore(handle, userId, mgdgTotalscore);
                }
                break;
            case SudMGPMGState.MG_DG_SCORE: // 5. 本次获得积分状态（已修改）
                SudMGPMGState.MGDGScore mgdgScore = HSJsonUtils.fromJson(dataJson, SudMGPMGState.MGDGScore.class);
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

    // 返回该用户是否为游戏队长
    public boolean isCaptain(long userId) {
        return sudFSMMGCache.isCaptain(userId);
    }

    // 返回该玩家是否正在游戏中
    public boolean playerIsPlaying(long userId) {
        return sudFSMMGCache.playerIsPlaying(userId);
    }

    // 返回该玩家是否已准备
    public boolean playerIsReady(long userId) {
        return sudFSMMGCache.playerIsReady(userId);
    }

    // 返回该玩家是否已加入了游戏
    public boolean playerIsIn(long userId) {
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

}
