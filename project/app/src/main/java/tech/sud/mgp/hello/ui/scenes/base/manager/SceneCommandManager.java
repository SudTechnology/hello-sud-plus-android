package tech.sud.mgp.hello.ui.scenes.base.manager;

import com.blankj.utilcode.util.LogUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChangeGameModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChatMediaModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChatTextModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdDownMicModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdEnterRoomModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdKickOutRoomModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdSendGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdUpMicModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.audio3d.Audio3DCmdConfigChangModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.audio3d.Audio3DCmdFaceNotifyModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.audio3d.Audio3DCmdMicChangModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.crossapp.CrossAppCmdGameSwitchModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.crossapp.CrossAppCmdStatusChangeModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.crossapp.CrossAppCmdTeamChangeModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.crossapp.CrossAppCmdUsersChangeModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.danmaku.RoomCmdDanmakuTeamChangeModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdBecomeDJModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdDiscoActionPayModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdDiscoInfoReqModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdDiscoInfoRespModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.game.RoomCmdPropsCardGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.league.RoomCmdLeagueInfoRespModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.monopoly.RoomCmdMonopolyCardGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.order.RoomCmdOrderOperateModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.order.RoomCmdUserOrderModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKAnswerModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKChangeGameModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKFinishModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKOpenMatchModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKRemoveRivalModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKRivalExitModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKSendInviteModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKSettingsModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKSettleModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk.RoomCmdPKStartModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.quiz.QuizBetModel;

/**
 * 房间信令相关
 */
public class SceneCommandManager extends BaseServiceManager {

    private final List<ICommandListener> listenerList = new ArrayList<>();

    /**
     * 设置信令监听
     *
     * @param listener 设置具体某个监听器来监听具体的信令
     */
    public void setCommandListener(ICommandListener listener) {
        listenerList.add(listener);
    }

    /** 移除信令监听 */
    public void removeCommandListener(ICommandListener listener) {
        if (listener != null) {
            listenerList.remove(listener);
        }
    }

    /**
     * 接收到信令，进行解析分发
     *
     * @param userID  发送者
     * @param command 信令字符
     */
    public void onRecvCommand(String userID, String command) {
        LogUtils.d("onRecvCommand:" + command);
        int commandCmd = getCommandCmd(command);
        switch (commandCmd) {
            case RoomCmd.CMD_CHAT_TEXT_NOTIFY: // 公屏消息
                dispatchCommand(commandCmd, RoomCmdChatTextModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_SEND_GIFT_NOTIFY: // 发送礼物
                dispatchCommand(commandCmd, RoomCmdSendGiftModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_UP_MIC_NOTIFY: // 上麦位
                dispatchCommand(commandCmd, RoomCmdUpMicModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_DOWN_MIC_NOTIFY: // 下麦位
                dispatchCommand(commandCmd, RoomCmdDownMicModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_CHANGE_GAME_NOTIFY: // 游戏切换
                dispatchCommand(commandCmd, RoomCmdChangeGameModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ENTER_ROOM_NOTIFY: // 进入房间通知
                dispatchCommand(commandCmd, RoomCmdEnterRoomModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_KICK_OUT_ROOM: // 踢出房间
                dispatchCommand(commandCmd, RoomCmdKickOutRoomModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_CHAT_MEDIA_NOTIFY: // 公屏消息V2
                dispatchCommand(commandCmd, RoomCmdChatMediaModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_SEND_INVITE: // 发送跨房PK邀请
                dispatchCommand(commandCmd, RoomCmdPKSendInviteModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_ANSWER: // 跨房PK邀请应答
                dispatchCommand(commandCmd, RoomCmdPKAnswerModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_START: // 开始跨房PK
                dispatchCommand(commandCmd, RoomCmdPKStartModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_FINISH: // 结束跨房PK
                dispatchCommand(commandCmd, RoomCmdPKFinishModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_SETTINGS: // 跨房PK设置
                dispatchCommand(commandCmd, RoomCmdPKSettingsModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_OPEN_MATCH: // 开启匹配跨房PK
                dispatchCommand(commandCmd, RoomCmdPKOpenMatchModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_CHANGE_GAME: // 跨房PK，切换游戏
                dispatchCommand(commandCmd, RoomCmdPKChangeGameModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_REMOVE_RIVAL: // 跨房PK，移除对手
                dispatchCommand(commandCmd, RoomCmdPKRemoveRivalModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_SETTLE: // 跨房pk游戏结算消息通知
                dispatchCommand(commandCmd, RoomCmdPKSettleModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_PK_RIVAL_EXIT: // 跨房PK对手房间关闭消息
                dispatchCommand(commandCmd, RoomCmdPKRivalExitModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_USER_ORDER_NOTIFY: // 用户点单
                dispatchCommand(commandCmd, RoomCmdUserOrderModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ORDER_OPERATE_NOTIFY: // 主播同意或者拒绝用户点单
                dispatchCommand(commandCmd, RoomCmdOrderOperateModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_QUIZ_BET: // 竞猜下注通知
                dispatchCommand(commandCmd, QuizBetModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_DISCO_INFO_REQ: // 请求蹦迪信息
                dispatchCommand(commandCmd, RoomCmdDiscoInfoReqModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_DISCO_INFO_RESP: // 响应蹦迪信息
                dispatchCommand(commandCmd, RoomCmdDiscoInfoRespModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_DISCO_BECOME_DJ: // 上DJ台
                dispatchCommand(commandCmd, RoomCmdBecomeDJModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_LEAGUE_INFO_RESP: // 响应联赛信息
                dispatchCommand(commandCmd, RoomCmdLeagueInfoRespModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_DISCO_ACTION_PAY: // 蹦迪动作付费
                dispatchCommand(commandCmd, RoomCmdDiscoActionPayModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_GAME_EXTRA_MATCH_USERS_CHANGED_NOTIFY: // 跨域匹配人数变更通知
                dispatchCommand(commandCmd, CrossAppCmdUsersChangeModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_GAME_EXTRA_MATCH_STATUS_CHANGED_NOTIFY: // 跨域匹配状态变更通知
                dispatchCommand(commandCmd, CrossAppCmdStatusChangeModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_GAME_EXTRA_TEAM_CHANGED_NOTIFY: // 跨域匹配队伍变更通知
                dispatchCommand(commandCmd, CrossAppCmdTeamChangeModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_GAME_EXTRA_GAME_SWITCH_NOTIFY: // 跨域匹配游戏切换通知
                dispatchCommand(commandCmd, CrossAppCmdGameSwitchModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_GAME_BULLET_MATCH_NOTIFY: // 弹幕游戏pk匹配成功通知
                dispatchCommand(commandCmd, RoomCmdDanmakuTeamChangeModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_3D_CONFIG_CHANGE_NOTIFY: // 3D语聊房配置变更
                dispatchCommand(commandCmd, Audio3DCmdConfigChangModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_3D_MIC_STATE_CHANGE_NOTIFY: // 弹幕游戏pk匹配成功通知
                dispatchCommand(commandCmd, Audio3DCmdMicChangModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_ROOM_3D_SEND_FACE_NOTIFY: // 3D语聊房发送表情通知
                dispatchCommand(commandCmd, Audio3DCmdFaceNotifyModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_GAME_MONOPOLY_CARD_GIFT_NOTIFY: // 大富翁道具卡送礼通知
                dispatchCommand(commandCmd, RoomCmdMonopolyCardGiftModel.fromJson(command), userID);
                break;
            case RoomCmd.CMD_GAME_PROPS_CARD_GIFT_NOTIFY: // 游戏道具卡送礼通知
                dispatchCommand(commandCmd, RoomCmdPropsCardGiftModel.fromJson(command), userID);
                break;
        }
    }

    /**
     * 分发信令
     *
     * @param cmd        信令命令
     * @param model      信令内容
     * @param fromUserID 发送者
     */
    private void dispatchCommand(int cmd, RoomCmdBaseModel model, String fromUserID) {
        if (model == null) return;
        for (ICommandListener listener : listenerList) {
            switch (cmd) {
                case RoomCmd.CMD_CHAT_TEXT_NOTIFY: // 发送公屏
                    if (listener instanceof PublicMsgCommandListener) {
                        ((PublicMsgCommandListener) listener).onRecvCommand((RoomCmdChatTextModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_SEND_GIFT_NOTIFY: // 发送礼物
                    if (listener instanceof SendGiftCommandListener) {
                        ((SendGiftCommandListener) listener).onRecvCommand((RoomCmdSendGiftModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_UP_MIC_NOTIFY: // 上麦位
                    if (listener instanceof UpMicCommandListener) {
                        ((UpMicCommandListener) listener).onRecvCommand((RoomCmdUpMicModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_DOWN_MIC_NOTIFY: // 下麦位
                    if (listener instanceof DownMicCommandListener) {
                        ((DownMicCommandListener) listener).onRecvCommand((RoomCmdDownMicModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_CHANGE_GAME_NOTIFY: // 游戏切换
                    if (listener instanceof GameChangeCommandListener) {
                        ((GameChangeCommandListener) listener).onRecvCommand((RoomCmdChangeGameModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ENTER_ROOM_NOTIFY: // 进入房间通知
                    if (listener instanceof EnterRoomCommandListener) {
                        ((EnterRoomCommandListener) listener).onRecvCommand((RoomCmdEnterRoomModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_KICK_OUT_ROOM: // 踢出房间
                    if (listener instanceof KickOutRoomCommandListener) {
                        ((KickOutRoomCommandListener) listener).onRecvCommand((RoomCmdKickOutRoomModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_CHAT_MEDIA_NOTIFY: // 发送公屏V2
                    if (listener instanceof MediaMsgCommandListener) {
                        ((MediaMsgCommandListener) listener).onRecvCommand((RoomCmdChatMediaModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_SEND_INVITE: // 发送跨房PK邀请
                    if (listener instanceof PKSendInviteCommandListener) {
                        ((PKSendInviteCommandListener) listener).onRecvCommand((RoomCmdPKSendInviteModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_ANSWER: // 跨房PK邀请应答
                    if (listener instanceof PKAnswerCommandListener) {
                        ((PKAnswerCommandListener) listener).onRecvCommand((RoomCmdPKAnswerModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_START: // 开始跨房PK
                    if (listener instanceof PKStartCommandListener) {
                        ((PKStartCommandListener) listener).onRecvCommand((RoomCmdPKStartModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_FINISH: // 结束跨房PK
                    if (listener instanceof PKFinishCommandListener) {
                        ((PKFinishCommandListener) listener).onRecvCommand((RoomCmdPKFinishModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_SETTINGS: // 跨房PK设置
                    if (listener instanceof PKSettingsCommandListener) {
                        ((PKSettingsCommandListener) listener).onRecvCommand((RoomCmdPKSettingsModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_OPEN_MATCH: // 开启匹配跨房PK
                    if (listener instanceof PKOpenMatchCommandListener) {
                        ((PKOpenMatchCommandListener) listener).onRecvCommand((RoomCmdPKOpenMatchModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_CHANGE_GAME: // 跨房PK，切换游戏
                    if (listener instanceof PKChangeGameCommandListener) {
                        ((PKChangeGameCommandListener) listener).onRecvCommand((RoomCmdPKChangeGameModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_REMOVE_RIVAL: // 跨房PK，移除对手
                    if (listener instanceof PKRemoveRivalCommandListener) {
                        ((PKRemoveRivalCommandListener) listener).onRecvCommand((RoomCmdPKRemoveRivalModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_SETTLE: // 跨房pk游戏结算消息通知
                    if (listener instanceof PKSettleCommandListener) {
                        ((PKSettleCommandListener) listener).onRecvCommand((RoomCmdPKSettleModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_PK_RIVAL_EXIT: // 跨房PK对手房间关闭消息
                    if (listener instanceof PKRivalExitCommandListener) {
                        ((PKRivalExitCommandListener) listener).onRecvCommand((RoomCmdPKRivalExitModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_USER_ORDER_NOTIFY: // 用户点单
                    if (listener instanceof UserOrderCommandListener) {
                        ((UserOrderCommandListener) listener).onRecvCommand((RoomCmdUserOrderModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ORDER_OPERATE_NOTIFY: // 主播同意或者拒绝用户点单
                    if (listener instanceof OrderResultCommandListener) {
                        ((OrderResultCommandListener) listener).onRecvCommand((RoomCmdOrderOperateModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_QUIZ_BET: // 竞猜下注通知
                    if (listener instanceof QuizBetCommandListener) {
                        ((QuizBetCommandListener) listener).onRecvCommand((QuizBetModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_DISCO_INFO_REQ: // 请求蹦迪信息
                    if (listener instanceof DiscoInfoReqCommandListener) {
                        ((DiscoInfoReqCommandListener) listener).onRecvCommand((RoomCmdDiscoInfoReqModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_DISCO_INFO_RESP: // 响应蹦迪信息
                    if (listener instanceof DiscoInfoRespCommandListener) {
                        ((DiscoInfoRespCommandListener) listener).onRecvCommand((RoomCmdDiscoInfoRespModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_DISCO_BECOME_DJ: // 上DJ台
                    if (listener instanceof DiscoBecomeDJCommandListener) {
                        ((DiscoBecomeDJCommandListener) listener).onRecvCommand((RoomCmdBecomeDJModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_LEAGUE_INFO_RESP: // 响应联赛信息
                    if (listener instanceof LeagueInfoRespListener) {
                        ((LeagueInfoRespListener) listener).onRecvCommand((RoomCmdLeagueInfoRespModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_DISCO_ACTION_PAY: // 蹦迪动作付费
                    if (listener instanceof DiscoActionPayCommandListener) {
                        ((DiscoActionPayCommandListener) listener).onRecvCommand((RoomCmdDiscoActionPayModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_GAME_EXTRA_MATCH_USERS_CHANGED_NOTIFY: // 跨域匹配人数变更通知
                    if (listener instanceof CrossAppCmdUsersChangeListener) {
                        ((CrossAppCmdUsersChangeListener) listener).onRecvCommand((CrossAppCmdUsersChangeModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_GAME_EXTRA_MATCH_STATUS_CHANGED_NOTIFY: // 跨域匹配状态变更通知
                    if (listener instanceof CrossAppCmdStatusChangeListener) {
                        ((CrossAppCmdStatusChangeListener) listener).onRecvCommand((CrossAppCmdStatusChangeModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_GAME_EXTRA_TEAM_CHANGED_NOTIFY: // 跨域匹配队伍变更通知
                    if (listener instanceof CrossAppCmdTeamChangeListener) {
                        ((CrossAppCmdTeamChangeListener) listener).onRecvCommand((CrossAppCmdTeamChangeModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_GAME_EXTRA_GAME_SWITCH_NOTIFY: // 跨域匹配游戏切换通知
                    if (listener instanceof CrossAppCmdGameSwitchListener) {
                        ((CrossAppCmdGameSwitchListener) listener).onRecvCommand((CrossAppCmdGameSwitchModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_GAME_BULLET_MATCH_NOTIFY: // 弹幕游戏pk匹配成功通知
                    if (listener instanceof DanmakuTeamChangeListener) {
                        ((DanmakuTeamChangeListener) listener).onRecvCommand((RoomCmdDanmakuTeamChangeModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_3D_CONFIG_CHANGE_NOTIFY: // 3D语聊房配置变更
                    if (listener instanceof Audio3DConfigChangeListener) {
                        ((Audio3DConfigChangeListener) listener).onRecvCommand((Audio3DCmdConfigChangModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_3D_MIC_STATE_CHANGE_NOTIFY: // 3D语聊房麦位状态变更
                    if (listener instanceof Audio3DMicChangeListener) {
                        ((Audio3DMicChangeListener) listener).onRecvCommand((Audio3DCmdMicChangModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_ROOM_3D_SEND_FACE_NOTIFY: // 3D语聊房发送表情通知
                    if (listener instanceof Audio3DFaceNotifyListener) {
                        ((Audio3DFaceNotifyListener) listener).onRecvCommand((Audio3DCmdFaceNotifyModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_GAME_MONOPOLY_CARD_GIFT_NOTIFY: // 大富翁道具卡送礼通知
                    if (listener instanceof MonopolyCardGiftListener) {
                        ((MonopolyCardGiftListener) listener).onRecvCommand((RoomCmdMonopolyCardGiftModel) model, fromUserID);
                    }
                    break;
                case RoomCmd.CMD_GAME_PROPS_CARD_GIFT_NOTIFY: // 游戏道具卡送礼通知
                    if (listener instanceof PropsCardGiftListener) {
                        ((PropsCardGiftListener) listener).onRecvCommand((RoomCmdPropsCardGiftModel) model, fromUserID);
                    }
                    break;
            }
        }
    }

    private int getCommandCmd(String command) {
        try {
            JSONObject obj = new JSONObject(command);
            return obj.getInt("cmd");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    interface ICommandListener {
    }

    // region 基础信令监听
    interface PublicMsgCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdChatTextModel model, String userID);
    }

    interface SendGiftCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdSendGiftModel model, String userID);
    }

    interface UpMicCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdUpMicModel model, String userID);
    }

    interface DownMicCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdDownMicModel model, String userID);
    }

    interface GameChangeCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdChangeGameModel model, String userID);
    }

    interface EnterRoomCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdEnterRoomModel model, String userID);
    }

    interface KickOutRoomCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdKickOutRoomModel model, String userID);
    }

    interface MediaMsgCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdChatMediaModel model, String userID);
    }
    // endregion 基础信令监听

    // region 跨房pk信令监听
    interface PKSendInviteCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKSendInviteModel model, String userID);
    }

    interface PKAnswerCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKAnswerModel model, String userID);
    }

    interface PKStartCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKStartModel model, String userID);
    }

    interface PKFinishCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKFinishModel model, String userID);
    }

    interface PKSettingsCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKSettingsModel model, String userID);
    }

    interface PKOpenMatchCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKOpenMatchModel model, String userID);
    }

    interface PKChangeGameCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKChangeGameModel model, String userID);
    }

    interface PKRemoveRivalCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKRemoveRivalModel model, String userID);
    }

    interface PKSettleCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKSettleModel model, String userID);
    }

    interface PKRivalExitCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdPKRivalExitModel model, String userID);
    }
    // endregion 跨房pk信令监听

    // region 点单信令监听
    interface UserOrderCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdUserOrderModel model, String userID);
    }

    interface OrderResultCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdOrderOperateModel model, String userID);
    }
    // endregion 点单信令监听

    // region 竞猜信令监听
    interface QuizBetCommandListener extends ICommandListener {
        void onRecvCommand(QuizBetModel model, String userID);
    }
    // endregion 竞猜信令监听

    // region 蹦迪信令监听
    interface DiscoInfoReqCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdDiscoInfoReqModel model, String userID);
    }

    interface DiscoInfoRespCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdDiscoInfoRespModel model, String userID);
    }

    interface DiscoBecomeDJCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdBecomeDJModel model, String userID);
    }

    interface DiscoActionPayCommandListener extends ICommandListener {
        void onRecvCommand(RoomCmdDiscoActionPayModel model, String userID);
    }
    // endregion 蹦迪信令监听

    // region 联赛信令监听
    interface LeagueInfoRespListener extends ICommandListener {
        void onRecvCommand(RoomCmdLeagueInfoRespModel model, String userID);
    }
    // endregion 联赛信令监听

    // region 跨域信令监听
    interface CrossAppCmdUsersChangeListener extends ICommandListener {
        void onRecvCommand(CrossAppCmdUsersChangeModel model, String userID);
    }

    interface CrossAppCmdStatusChangeListener extends ICommandListener {
        void onRecvCommand(CrossAppCmdStatusChangeModel model, String userID);
    }

    interface CrossAppCmdTeamChangeListener extends ICommandListener {
        void onRecvCommand(CrossAppCmdTeamChangeModel model, String userID);
    }

    interface CrossAppCmdGameSwitchListener extends ICommandListener {
        void onRecvCommand(CrossAppCmdGameSwitchModel model, String userID);
    }
    // endregion 跨域信令监听

    // region 弹幕
    interface DanmakuTeamChangeListener extends ICommandListener {
        void onRecvCommand(RoomCmdDanmakuTeamChangeModel model, String userID);
    }
    // endregion 弹幕

    // region 3D语聊房
    interface Audio3DConfigChangeListener extends ICommandListener {
        void onRecvCommand(Audio3DCmdConfigChangModel model, String userID);
    }

    interface Audio3DMicChangeListener extends ICommandListener {
        void onRecvCommand(Audio3DCmdMicChangModel model, String userID);
    }

    interface Audio3DFaceNotifyListener extends ICommandListener {
        void onRecvCommand(Audio3DCmdFaceNotifyModel model, String userID);
    }
    // endregion 3D语聊房

    // region 大富翁
    interface MonopolyCardGiftListener extends ICommandListener {
        void onRecvCommand(RoomCmdMonopolyCardGiftModel model, String userID);
    }
    // endregion 大富翁

    interface PropsCardGiftListener extends ICommandListener {
        void onRecvCommand(RoomCmdPropsCardGiftModel model, String userID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listenerList.clear();
    }
}
