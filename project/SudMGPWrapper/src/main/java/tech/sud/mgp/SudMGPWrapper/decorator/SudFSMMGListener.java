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

    /**
     * 25. 创建订单
     * mg_common_game_create_order
     */
    default void onGameMGCommonGameCreateOrder(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameCreateOrder model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 26. 游戏通知app玩家角色(仅对狼人杀有效)
     * mg_common_player_role_id
     */
    default void onGameMGCommonPlayerRoleId(ISudFSMStateHandle handle, SudMGPMGState.MGCommonPlayerRoleId model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 27. 游戏通知app玩家被扔便便(你画我猜，你说我猜，友尽闯关有效)
     * mg_common_self_click_poop
     */
    default void onGameMGCommonSelfClickPoop(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickPoop model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 28. 游戏通知app玩家被点赞(你画我猜，你说我猜，友尽闯关有效)
     * mg_common_self_click_good
     */
    default void onGameMGCommonSelfClickGood(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickGood model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 29. 游戏通知app游戏FPS(仅对碰碰，多米诺骨牌，飞镖达人生效)
     * mg_common_game_fps
     */
    default void onGameMGCommonGameFps(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameFps model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 30. 游戏通知app游戏弹框
     * mg_common_alert
     */
    default void onGameMGCommonAlert(ISudFSMStateHandle handle, SudMGPMGState.MGCommonAlert model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 31. 游戏通知app最坑队友（只支持友尽闯关）
     * mg_common_worst_teammate
     */
    default void onGameMGCommonWorstTeammate(ISudFSMStateHandle handle, SudMGPMGState.MGCommonWorstTeammate model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 32. 游戏通知app因玩家逃跑导致游戏结束（只支持友尽闯关）
     * mg_common_game_over_tip
     */
    default void onGameMGCommonGameOverTip(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameOverTip model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 33. 游戏通知app玩家颜色（只支持友尽闯关）
     * mg_common_game_player_color
     */
    default void onGameMGCommonGamePlayerColor(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGamePlayerColor model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 34. 游戏通知app玩家头像的坐标（只支持ludo）
     * mg_common_game_player_icon_position
     */
    default void onGameMGCommonGamePlayerIconPosition(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGamePlayerIconPosition model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 35. 游戏通知app退出游戏（只支持teenpattipro 与 德州pro）
     * mg_common_self_click_exit_game_btn
     */
    default void onGameMGCommonSelfClickExitGameBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickExitGameBtn model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 36. 游戏通知app是否要开启带入积分（只支持teenpattipro 与 德州pro）
     * mg_common_game_is_app_chip
     */
    default void onGameMGCommonGameIsAppChip(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameIsAppChip model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 37. 游戏通知app当前游戏的设置信息（只支持德州pro，teenpatti pro）
     * mg_common_game_rule
     */
    default void onGameMGCommonGameRule(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameRule model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 38. 游戏通知app进行玩法设置（只支持德州pro，teenpatti pro）
     * mg_common_game_settings
     */
    default void onGameMGCommonGameSettings(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameSettings model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 39. 游戏通知app钱币不足（只支持德州pro，teenpatti pro）
     * mg_common_game_money_not_enough
     */
    default void onGameMGCommonGameMoneyNotEnough(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameMoneyNotEnough model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 40. 游戏通知app下发定制ui配置表（只支持ludo）
     * mg_common_game_ui_custom_config
     */
    default void onGameMGCommonGameUiCustomConfig(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameUiCustomConfig model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 41. 设置app提供给游戏可点击区域(赛车)
     * mg_common_set_click_rect
     */
    default void onGameMGCommonSetClickRect(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSetClickRect model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 42. 通知app提供对应uids列表玩家的数据(赛车)
     * mg_common_users_info
     */
    default void onGameMGCommonUsersInfo(ISudFSMStateHandle handle, SudMGPMGState.MGCommonUsersInfo model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 43. 通知app游戏前期准备完成(赛车)
     * mg_common_game_prepare_finish
     */
    default void onGameMGCommonGamePrepareFinish(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGamePrepareFinish model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 44. 通知app游戏主界面已显示(赛车)
     * mg_common_show_game_scene
     */
    default void onGameMGCommonShowGameScene(ISudFSMStateHandle handle, SudMGPMGState.MGCommonShowGameScene model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 45. 通知app游戏主界面已隐藏(赛车)
     * mg_common_hide_game_scene
     */
    default void onGameMGCommonHideGameScene(ISudFSMStateHandle handle, SudMGPMGState.MGCommonHideGameScene model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 46. 通知app点击了游戏的金币按钮(赛车)
     * mg_common_self_click_gold_btn
     */
    default void onGameMGCommonSelfClickGoldBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickGoldBtn model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 47. 通知app棋子到达终点(ludo)
     * mg_common_game_piece_arrive_end
     */
    default void onGameMGCommonGamePieceArriveEnd(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGamePieceArriveEnd model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 48. 通知app玩家是否托管
     * mg_common_game_player_managed_state
     */
    default void onGameMGCommonGamePlayerManagedState(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGamePlayerManagedState model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 49. 游戏向app发送爆词
     * mg_common_game_send_burst_word
     */
    default void onGameMGCommonGameSendBurstWord(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameSendBurstWord model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 50. 游戏向app发送玩家实时排名（只支持怪物消消乐）
     * mg_common_game_player_ranks
     */
    default void onGameMGCommonGamePlayerRanks(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGamePlayerRanks model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 51. 游戏向app发送玩家即时变化的单双牌（只支持okey101）
     * mg_common_game_player_pair_singular
     */
    default void onGameMGCommonGamePlayerPairSingular(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGamePlayerPairSingular model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 52. 游戏向app发送获取玩家持有的道具卡（只支持大富翁）
     * mg_common_game_player_monopoly_cards
     */
    default void onGameMGCommonGamePlayerMonopolyCards(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGamePlayerMonopolyCards model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 53. 游戏向app发送玩家实时积分（只支持怪物消消乐）
     * mg_common_game_player_scores
     */
    default void onGameMGCommonGamePlayerScores(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGamePlayerScores model) {
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
     * 7. 游戏通知app点击玩家头像
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

    /**
     * 12. 游戏通知app层当前玩家死亡后变成ob视角（2022-08-23新增，目前狼人杀生效）
     * mg_common_self_ob_status
     */
    default void onPlayerMGCommonSelfObStatus(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonSelfObStatus model) {
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

    // region 游戏回调APP 通用状态 元宇宙砂砂舞

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
    // endregion 游戏回调APP 通用状态 元宇宙砂砂舞

    // region 游戏回调APP 通用状态 定制火箭

    /**
     * 1. 礼物配置文件(火箭)
     * mg_custom_rocket_config
     */
    default void onGameMGCustomRocketConfig(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketConfig model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 2. 拥有模型列表(火箭)
     * mg_custom_rocket_model_list
     */
    default void onGameMGCustomRocketModelList(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketModelList model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 3. 拥有组件列表(火箭)
     * mg_custom_rocket_component_list
     */
    default void onGameMGCustomRocketComponentList(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketComponentList model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 4. 获取用户信息(火箭)
     * mg_custom_rocket_user_info
     */
    default void onGameMGCustomRocketUserInfo(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketUserInfo model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 5. 订单记录列表(火箭)
     * mg_custom_rocket_order_record_list
     */
    default void onGameMGCustomRocketOrderRecordList(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketOrderRecordList model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 6. 展馆内列表(火箭)
     * mg_custom_rocket_room_record_list
     */
    default void onGameMGCustomRocketRoomRecordList(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketRoomRecordList model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 7. 展馆内玩家送出记录(火箭)
     * mg_custom_rocket_user_record_list
     */
    default void onGameMGCustomRocketUserRecordList(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketUserRecordList model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 8. 设置默认模型(火箭)
     * mg_custom_rocket_set_default_model
     */
    default void onGameMGCustomRocketSetDefaultModel(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketSetDefaultModel model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 9. 动态计算一键发送价格(火箭)
     * mg_custom_rocket_dynamic_fire_price
     */
    default void onGameMGCustomRocketDynamicFirePrice(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketDynamicFirePrice model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 10. 一键发送(火箭)
     * mg_custom_rocket_fire_model
     */
    default void onGameMGCustomRocketFireModel(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketFireModel model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 11. 新组装模型(火箭)
     * mg_custom_rocket_create_model
     */
    default void onGameMGCustomRocketCreateModel(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketCreateModel model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 12. 模型更换组件(火箭)
     * mg_custom_rocket_replace_component
     */
    default void onGameMGCustomRocketReplaceComponent(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketReplaceComponent model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 13. 购买组件(火箭)
     * mg_custom_rocket_buy_component
     */
    default void onGameMGCustomRocketBuyComponent(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketBuyComponent model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 14. 播放效果开始(火箭)
     * mg_custom_rocket_play_effect_start
     */
    default void onGameMGCustomRocketPlayEffectStart(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketPlayEffectStart model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 15. 播放效果完成(火箭)
     * mg_custom_rocket_play_effect_finish
     */
    default void onGameMGCustomRocketPlayEffectFinish(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketPlayEffectFinish model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 16. 验证签名合规(火箭)
     * mg_custom_rocket_verify_sign
     */
    default void onGameMGCustomRocketVerifySign(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketVerifySign model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 17. 上传icon(火箭)
     * mg_custom_rocket_upload_model_icon
     */
    default void onGameMGCustomRocketUploadModelIcon(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketUploadModelIcon model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 18. 前期准备完成(火箭)
     * mg_custom_rocket_prepare_finish
     */
    default void onGameMGCustomRocketPrepareFinish(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketPrepareFinish model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 19. 火箭主界面已显示(火箭)
     * mg_custom_rocket_show_game_scene
     */
    default void onGameMGCustomRocketShowGameScene(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketShowGameScene model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 20. 火箭主界面已隐藏(火箭)
     * mg_custom_rocket_hide_game_scene
     */
    default void onGameMGCustomRocketHideGameScene(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketHideGameScene model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 21. 点击锁住组件(火箭)
     * mg_custom_rocket_click_lock_component
     */
    default void onGameMGCustomRocketClickLockComponent(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketClickLockComponent model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 22. 火箭效果飞行点击(火箭)
     * mg_custom_rocket_fly_click
     */
    default void onGameMGCustomRocketFlyClick(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketFlyClick model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 23. 火箭效果飞行结束(火箭)
     * mg_custom_rocket_fly_end
     */
    default void onGameMGCustomRocketFlyEnd(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketFlyEnd model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 24. 设置点击区域(火箭)
     * mg_custom_rocket_set_click_rect
     */
    default void onGameMGCustomRocketSetClickRect(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketSetClickRect model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 25. 颜色和签名自定义改到装配间的模式，保存颜色或签名
     * mg_custom_rocket_save_sign_color
     */
    default void onGameMGCustomRocketSaveSignColor(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketSaveSignColor model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }
    // endregion 游戏回调APP 通用状态 定制火箭

    // region 游戏回调APP 通用状态 棒球

    /**
     * 1. 查询排行榜数据(棒球)
     * mg_baseball_ranking
     */
    default void onGameMGBaseballRanking(ISudFSMStateHandle handle, SudMGPMGState.MGBaseballRanking model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 2. 查询我的排名(棒球)
     * mg_baseball_my_ranking
     */
    default void onGameMGBaseballMyRanking(ISudFSMStateHandle handle, SudMGPMGState.MGBaseballMyRanking model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 3. 查询当前距离我的前后玩家数据(棒球)
     * mg_baseball_range_info
     */
    default void onGameMGBaseballRangeInfo(ISudFSMStateHandle handle, SudMGPMGState.MGBaseballRangeInfo model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 4. 设置app提供给游戏可点击区域(棒球)
     * mg_baseball_set_click_rect
     */
    default void onGameMGBaseballSetClickRect(ISudFSMStateHandle handle, SudMGPMGState.MGBaseballSetClickRect model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 5. 前期准备完成(棒球)
     * mg_baseball_prepare_finish
     */
    default void onGameMGBaseballPrepareFinish(ISudFSMStateHandle handle, SudMGPMGState.MGBaseballPrepareFinish model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 6. 主界面已显示(棒球)
     * mg_baseball_show_game_scene
     */
    default void onGameMGBaseballShowGameScene(ISudFSMStateHandle handle, SudMGPMGState.MGBaseballShowGameScene model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 7. 主界面已隐藏(棒球)
     * mg_baseball_hide_game_scene
     */
    default void onGameMGBaseballHideGameScene(ISudFSMStateHandle handle, SudMGPMGState.MGBaseballHideGameScene model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 8. 获取文本配置数据(棒球)
     * mg_baseball_text_config
     */
    default void onGameMGBaseballTextConfig(ISudFSMStateHandle handle, SudMGPMGState.MGBaseballTextConfig model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }
    // endregion 游戏回调APP 通用状态 棒球

    // region 游戏回调APP 3D语聊房

    /**
     * 1. 请求房间数据
     * mg_custom_cr_room_init_data
     */
    default void onGameMGCustomCrRoomInitData(ISudFSMStateHandle handle, SudMGPMGState.MGCustomCrRoomInitData model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 2. 点击主播位或老板位通知
     * mg_custom_cr_click_seat
     */
    default void onGameMGCustomCrClickSeat(ISudFSMStateHandle handle, SudMGPMGState.MGCustomCrClickSeat model) {
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }
    // endregion 游戏回调APP 3D语聊房

    /**
     * 游戏状态变化
     * 透传游戏向App发送的游戏通用状态消息
     * **********使用此方法可先看下此方法的使用逻辑*************
     *
     * @param handle   回调操作
     * @param state    状态命令
     * @param dataJson 状态值
     * @return 返回true，表示由此方法接管该状态处理，此时需注意调用：ISudFSMStateHandleUtils.handleSuccess(handle);
     */
    default boolean onGameStateChange(ISudFSMStateHandle handle, String state, String dataJson) {
        return false;
    }

    /**
     * 游戏玩家状态变化
     * 透传游戏向App发送的玩家状态变化
     * **********使用此方法可先看下此方法的使用逻辑*************
     *
     * @param handle   回调操作
     * @param userId   用户Id
     * @param state    状态命令
     * @param dataJson 状态值
     * @return 返回true，表示由此方法接管该状态处理，此时需注意调用：ISudFSMStateHandleUtils.handleSuccess(handle);
     */
    default boolean onPlayerStateChange(ISudFSMStateHandle handle, String userId, String state, String dataJson) {
        return false;
    }

}
