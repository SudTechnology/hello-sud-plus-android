/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.SudMGPWrapper.decorator;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.SudMGPWrapper.utils.ISudFSMStateHandleUtils;
import tech.sud.mgp.core.ISudFSMStateHandle;

/**
 * {@link SudFSMMGDecorator} 回调定义
 */
public interface SudFSMMGListener {

    /**
     * 游戏日志
     * 最低版本：v1.1.30.xx
     */
    default void onGameLog(String str) {
    }

    /**
     * 游戏加载进度
     *
     * @param stage    阶段：start=1,loading=2,end=3
     * @param retCode  错误码：0成功
     * @param progress 进度：[0, 100]
     */
    default void onGameLoadingProgress(int stage, int retCode, int progress) {
    }

    /**
     * 游戏开始，需要实现
     * 最低版本：v1.1.30.xx
     */
    void onGameStarted();

    /**
     * 游戏销毁，需要实现
     * 最低版本：v1.1.30.xx
     */
    void onGameDestroyed();

    /**
     * Code过期，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param dataJson {"code":"value"}
     */
    void onExpireCode(ISudFSMStateHandle handle, String dataJson);

    /**
     * 获取游戏View信息，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle
     * @param dataJson {}
     */
    void onGetGameViewInfo(ISudFSMStateHandle handle, String dataJson);

    /**
     * 获取游戏Config，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle
     * @param dataJson {}
     *                 最低版本：v1.1.30.xx
     */
    void onGetGameCfg(ISudFSMStateHandle handle, String dataJson);

    // region 游戏回调APP 通用状态
    // 参考文档：https://github.com/SudTechnology/sud-mgp-doc/blob/main/Client/MG%20FSM/%E9%80%9A%E7%94%A8%E7%8A%B6%E6%80%81-%E7%8E%A9%E5%AE%B6.md

    /**
     * 1.游戏公屏消息
     * mg_common_public_message
     */
    default void onGameMGCommonPublicMessage(ISudFSMStateHandle handle, SudMGPMGState.MGCommonPublicMessage model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 2. 关键词状态
     * mg_common_key_word_to_hit
     */
    default void onGameMGCommonKeyWordToHit(ISudFSMStateHandle handle, SudMGPMGState.MGCommonKeyWordToHit model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 3. 游戏结算状态
     * mg_common_game_settle
     */
    default void onGameMGCommonGameSettle(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameSettle model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 4. 加入游戏按钮点击状态
     * mg_common_self_click_join_btn
     */
    default void onGameMGCommonSelfClickJoinBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickJoinBtn model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 5. 取消加入(退出)游戏按钮点击状态
     * mg_common_self_click_cancel_join_btn
     */
    default void onGameMGCommonSelfClickCancelJoinBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickCancelJoinBtn model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 6. 准备按钮点击状态
     * mg_common_self_click_ready_btn
     */
    default void onGameMGCommonSelfClickReadyBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickReadyBtn model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 7. 取消准备按钮点击状态
     * mg_common_self_click_cancel_ready_btn
     */
    default void onGameMGCommonSelfClickCancelReadyBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickCancelReadyBtn model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 8. 开始游戏按钮点击状态
     * mg_common_self_click_start_btn
     */
    default void onGameMGCommonSelfClickStartBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickStartBtn model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 9. 分享按钮点击状态
     * mg_common_self_click_share_btn
     */
    default void onGameMGCommonSelfClickShareBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickShareBtn model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 10. 游戏状态
     * mg_common_game_state
     */
    default void onGameMGCommonGameState(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameState model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 11. 结算界面关闭按钮点击状态（2021-12-27新增）
     * mg_common_self_click_game_settle_close_btn
     */
    default void onGameMGCommonSelfClickGameSettleCloseBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickGameSettleCloseBtn model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 12. 结算界面再来一局按钮点击状态（2021-12-27新增）
     * mg_common_self_click_game_settle_again_btn
     */
    default void onGameMGCommonSelfClickGameSettleAgainBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickGameSettleAgainBtn model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 13. 游戏上报游戏中的声音列表（2021-12-30新增，现在只支持碰碰我最强）
     * mg_common_game_sound_list
     */
    default void onGameMGCommonGameSoundList(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameSoundList model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 14. 游通知app层播放声音（2021-12-30新增，现在只支持碰碰我最强）
     * mg_common_game_sound
     */
    default void onGameMGCommonGameSound(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameSound model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 15. 游戏通知app层播放背景音乐状态（2022-01-07新增，现在只支持碰碰我最强）
     * mg_common_game_bg_music_state
     */
    default void onGameMGCommonGameBgMusicState(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameBgMusicState model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 16. 游戏通知app层播放音效的状态（2022-01-07新增，现在只支持碰碰我最强）
     * mg_common_game_sound_state
     */
    default void onGameMGCommonGameSoundState(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameSoundState model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 17. ASR状态(开启和关闭语音识别状态，v1.1.45.xx 版本新增)
     * mg_common_game_asr
     */
    default void onGameMGCommonGameASR(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameASR model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 18. 麦克风状态（2022-02-08新增）
     * mg_common_self_microphone
     */
    default void onGameMGCommonSelfMicrophone(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfMicrophone model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 19. 耳机（听筒，扬声器）状态（2022-02-08新增）
     * mg_common_self_headphone
     */
    default void onGameMGCommonSelfHeadphone(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfHeadphone model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 20. App通用状态操作结果错误码（2022-05-10新增）
     * mg_common_app_common_self_x_resp
     */
    default void onGameMGCommonAPPCommonSelfXResp(ISudFSMStateHandle handle, SudMGPMGState.MGCommonAPPCommonSelfXResp model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 21. 游戏通知app层添加陪玩机器人是否成功（2022-05-17新增）
     * mg_common_game_add_ai_players
     */
    default void onGameMGCommonGameAddAIPlayers(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameAddAIPlayers model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 22. 游戏通知app层添当前网络连接状态（2022-06-21新增）
     * mg_common_game_network_state
     */
    default void onGameMGCommonGameNetworkState(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameNetworkState model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 23. 游戏通知app获取积分
     * mg_common_game_score
     */
    default void onGameMGCommonGameGetScore(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameGetScore model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 24. 游戏通知app带入积分
     * mg_common_game_set_score
     */
    default void onGameMGCommonGameSetScore(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameSetScore model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }
    // endregion 游戏回调APP 通用状态

    // region 游戏回调APP 玩家状态

    /**
     * 1.加入状态（已修改）
     * mg_common_player_in
     */
    default void onPlayerMGCommonPlayerIn(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonPlayerIn model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 2.准备状态（已修改）
     * mg_common_player_ready
     */
    default void onPlayerMGCommonPlayerReady(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonPlayerReady model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 3.队长状态（已修改）
     * mg_common_player_captain
     */
    default void onPlayerMGCommonPlayerCaptain(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonPlayerCaptain model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 4.游戏状态（已修改）
     * mg_common_player_playing
     */
    default void onPlayerMGCommonPlayerPlaying(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonPlayerPlaying model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 5.玩家在线状态
     * mg_common_player_online
     */
    default void onPlayerMGCommonPlayerOnline(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonPlayerOnline model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 6.玩家换游戏位状态
     * mg_common_player_change_seat
     */
    default void onPlayerMGCommonPlayerChangeSeat(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonPlayerChangeSeat model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 7. 游戏通知app点击玩家头像（2022-02-09新增，现在只支持飞行棋ludo，仅用于游戏场景中的玩家头像）
     * mg_common_self_click_game_player_icon
     */
    default void onPlayerMGCommonSelfClickGamePlayerIcon(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonSelfClickGamePlayerIcon model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 8. 游戏通知app玩家死亡状态（2022-04-24新增）
     * mg_common_self_die_status
     */
    default void onPlayerMGCommonSelfDieStatus(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonSelfDieStatus model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 9. 游戏通知app轮到玩家出手状态（2022-04-24新增）
     * mg_common_self_turn_status
     */
    default void onPlayerMGCommonSelfTurnStatus(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonSelfTurnStatus model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 10. 游戏通知app玩家选择状态（2022-04-24新增）
     * mg_common_self_select_status
     */
    default void onPlayerMGCommonSelfSelectStatus(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonSelfSelectStatus model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 11. 游戏通知app层当前游戏剩余时间（2022-05-23新增，目前UMO生效）
     * mg_common_game_countdown_time
     */
    default void onPlayerMGCommonGameCountdownTime(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonGameCountdownTime model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    // endregion 游戏回调APP 玩家状态

    // region 游戏回调APP 玩家状态 你画我猜
    // 参考文档：https://github.com/SudTechnology/sud-mgp-doc/blob/main/Client/MG%20FSM/%E4%BD%A0%E7%94%BB%E6%88%91%E7%8C%9C.md

    /**
     * 1. 选词中状态（已修改）
     * mg_dg_selecting
     */
    default void onPlayerMGDGSelecting(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGDGSelecting model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 2. 作画中状态（已修改）
     * mg_dg_painting
     */
    default void onPlayerMGDGPainting(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGDGPainting model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 3. 显示错误答案状态（已修改）
     * mg_dg_erroranswer
     */
    default void onPlayerMGDGErroranswer(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGDGErroranswer model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 4. 显示总积分状态（已修改）
     * mg_dg_totalscore
     */
    default void onPlayerMGDGTotalscore(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGDGTotalscore model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 5. 本次获得积分状态（已修改）
     * mg_dg_score
     */
    default void onPlayerMGDGScore(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGDGScore model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    // endregion 游戏回调APP 玩家状态 你画我猜

    // region 游戏回调APP 玩家状态 元宇宙砂砂舞

    /**
     * 1. 元宇宙砂砂舞指令回调
     * mg_common_game_disco_action
     */
    default void onGameMGCommonGameDiscoAction(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameDiscoAction model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 2. 元宇宙砂砂舞指令动作结束通知
     * mg_common_game_disco_action_end
     */
    default void onGameMGCommonGameDiscoActionEnd(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameDiscoActionEnd model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }
    // endregion 游戏回调APP 玩家状态 元宇宙砂砂舞

}
