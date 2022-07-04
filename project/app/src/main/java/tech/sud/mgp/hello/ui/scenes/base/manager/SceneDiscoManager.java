package tech.sud.mgp.hello.ui.scenes.base.manager;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.CustomCountdownTimer;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdSendGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.ContributionModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.DanceModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdDiscoInfoReqModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdDiscoInfoRespModel;
import tech.sud.mgp.hello.ui.scenes.disco.viewmodel.DiscoActionHelper;

/**
 * 蹦迪场景 内的业务逻辑
 */
public class SceneDiscoManager extends BaseServiceManager {

    private SceneRoomServiceManager parentManager;
    private final DiscoActionHelper helper = new DiscoActionHelper(); // 操作蹦迪动作助手
    private final List<DanceModel> danceList = new ArrayList<>(); // 跳舞列表，包含所有
    private final List<ContributionModel> contributionModels = new ArrayList<>(); // 房间贡献列表
    private boolean initDiscoInfoCompleted; // 是否已经初始化完成了蹦迪信息

    public SceneDiscoManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.sceneChatManager.addSendMsgListener(sendMsgListener);
        parentManager.sceneEngineManager.setCommandListener(sendGiftCommandListener);
        parentManager.sceneEngineManager.setCommandListener(discoInfoReqListener);
        parentManager.sceneEngineManager.setCommandListener(discoInfoRespListener);
    }

    /** 发送公屏监听 */
    private final SceneChatManager.SendMsgListener sendMsgListener = new SceneChatManager.SendMsgListener() {
        @Override
        public void onSendMsgCompleted(String msg) {
            checkChatMsgHit(msg);
        }
    };

    /** 检查是否命中 */
    private void checkChatMsgHit(String msg) {
        Application context = Utils.getApp();
        if (context.getString(R.string.move).equals(msg)) { // 移动
            triggerMove();
        } else if (context.getString(R.string.god).equals(msg)) { // 上天
            triggerGod();
        } else if (context.getString(R.string.change_role).equals(msg)) { // 换角色
            triggerChangeRole();
        } else if (context.getString(R.string.go_to_work).equals(msg)) { // 上班
            triggerGoToWork();
        } else if (context.getString(R.string.get_off_work).equals(msg)) { // 下班
            triggerGetOffWork();
        } else if (context.getString(R.string.focus).equals(msg)) { // 聚焦
            triggerFocus();
        }
    }

    /** 触发【移动】 */
    private void triggerMove() {
        callbackAction(helper.roleMove(null, null));
    }

    /** 触发【上天】 */
    private void triggerGod() {
        callbackAction(helper.roleFly(3));
    }

    /** 触发【换角色】 */
    private void triggerChangeRole() {
        callbackAction(helper.changeRole(null));
    }

    /** 触发【上班】 */
    private void triggerGoToWork() {
        int selfMicIndex = parentManager.sceneMicManager.findSelfMicIndex();
        if (selfMicIndex >= 0) {
            // TODO: 2022/7/1 如果不在舞池中，连续发送的时候，会报请先加入舞池
            callbackAction(helper.joinDancingFloor("#ff0000"));
            callbackAction(helper.joinAnchor(null));
        }
    }

    /** 触发【下班】 */
    private void triggerGetOffWork() {
        callbackAction(helper.leaveAnchor(HSUserInfo.userId + ""));
    }

    /** 触发【聚焦】 */
    private void triggerFocus() {
        callbackAction(helper.roleFocus(4, false));
    }

    /** 回调页面让其通知游戏蹦迪动作 */
    private void callbackAction(String dataJson) {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, dataJson, null);
        }
    }

    /** 进入房间成功 */
    public void onEnterRoomSuccess() {
        // 向其他房间的人，获取房间信息
        String discoInfoReqCmd = RoomCmdModelUtils.buildCmdDiscoInfoReq();
        parentManager.sceneEngineManager.sendCommand(discoInfoReqCmd);
    }

    /** 送出了礼物 */
    public void sendGift(long giftID, int giftCount, UserInfo toUser, int type, String giftName, String giftUrl, String animationUrl) {
        // 目前是内置礼物才有动效
        if (type != 0) {
            return;
        }

        Application context = Utils.getApp();
        // 根据送的礼物来决定给予其什么特效
        if (giftID == 1) {
            callbackAction(helper.textPop(3, context.getString(R.string.send_gift_title, giftCount + "", giftName)));
        } else if (giftID == 2) {
            callbackAction(helper.textPop(3, context.getString(R.string.send_gift_title, giftCount + "", giftName)));
            callbackAction(helper.roleFocus(3, null));
        } else if (giftID == 3) {
            callbackAction(helper.textPop(6, context.getString(R.string.send_gift_title, giftCount + "", giftName)));
            callbackAction(helper.roleBig(30, 2));
            callbackAction(helper.roleFocus(4, null));
        } else if (giftID == 4) {
            callbackAction(helper.textPop(9, context.getString(R.string.send_gift_title, giftCount + "", giftName)));
            callbackAction(helper.roleBig(60, 2));
            callbackAction(helper.roleFocus(5, true));
            callbackAction(helper.roleEffects(60 * 60 * 2, null));
        } else if (giftID == 5) {
            addDance(RoomCmdModelUtils.getSendUser(), toUser, 60);
        } else if (giftID == 6) {
            addDance(RoomCmdModelUtils.getSendUser(), toUser, 3 * 60);
        } else if (giftID == 7) {
            danceTop(RoomCmdModelUtils.getSendUser(), toUser);
        }
    }

    /** 增加一个跳舞请求 */
    private void addDance(UserInfo fromUser, UserInfo toUser, int duration) {
        if (fromUser == null || toUser == null) {
            return;
        }
        DanceModel model = findDancingModel(fromUser, toUser);
        if (model == null) {
            // 没有在队列中的相同数据，直接添加到队列中即可
            model = new DanceModel();
            model.fromUser = fromUser;
            model.toUser = toUser;
            model.duration = duration;
            int insetPosition = findFirstCompletedDanceModel();
            if (insetPosition == -1) {
                danceList.add(model);
            } else {
                danceList.add(insetPosition, model);
            }
            collatingDanceList();
            checkDanceStart();
        } else {
            if (model.beginTime > 0) { // 如果正在执行中
                model.duration += duration;
                if ((HSUserInfo.userId + "").equals(fromUser.userID)) { // 自己发起的，则自己去触发
                    callDanceWithAnchor(toUser, duration);
                }
                startCountdownTimer(model);
            } else { // 如果在排队等待执行中
                model.duration += duration;
                callbackUpdateDance(danceList.indexOf(model));
                if ((HSUserInfo.userId + "").equals(fromUser.userID)) { // 自己发起的，提示等待
                    SceneRoomServiceCallback callback = parentManager.getCallback();
                    if (callback != null) {
                        callback.onDanceWait();
                    }
                }
            }
        }
    }

    /** 查找第一个已经完成的跳舞数据，没找到，则返回-1 */
    private int findFirstCompletedDanceModel() {
        int firstPosition = -1;
        for (int i = danceList.size() - 1; i >= 0; i--) {
            DanceModel danceModel = danceList.get(i);
            if (danceModel.isCompleted) {
                firstPosition = i;
                break;
            }
        }
        return firstPosition;
    }

    /** 向游戏发送状态，与该主播跳舞 */
    private void callDanceWithAnchor(UserInfo toUser, int duration) {
        callbackAction(helper.danceWithAnchor(duration, false, toUser.userID));
        callbackAction(helper.roleFocus(3, false));
    }

    /** 整理跳舞数据 */
    private void collatingDanceList() {
        boolean isFirstCompleted = true;
        for (DanceModel model : danceList) {
            if (model.isCompleted) {
                if (isFirstCompleted) {
                    isFirstCompleted = false;
                    model.isShowCompletedTitle = true;
                } else {
                    model.isShowCompletedTitle = false;
                }
            }
        }
        callbackDanceList();
    }

    /** 开始倒计时 */
    private void startCountdownTimer(DanceModel model) {
        CustomCountdownTimer countdownTimer = model.countdownTimer;
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
        long curTimeSecond = System.currentTimeMillis() / 1000;
        if (model.beginTime == 0) {
            model.beginTime = curTimeSecond;
        }
        int duration = (int) (model.beginTime + model.duration - curTimeSecond);
        countdownTimer = new CustomCountdownTimer(duration) {
            int index = danceList.indexOf(model);

            @Override
            protected void onTick(int count) {
                model.countdown = count;
                DanceModel listDanceModel = danceList.get(index);
                if (listDanceModel != model) {
                    index = danceList.indexOf(model);
                }
                callbackUpdateDance(index);
            }

            @Override
            protected void onFinish() {
                danceCompleted(model);
            }
        };
        model.countdownTimer = countdownTimer;
        countdownTimer.start();
    }

    /** 跳舞结束了 */
    private void danceCompleted(DanceModel model) {
        model.countdownTimer = null;
        model.isCompleted = true;
        danceList.remove(model);
        int insetPosition = findFirstCompletedDanceModel();
        if (insetPosition == -1) {
            danceList.add(model);
        } else {
            danceList.add(insetPosition, model);
        }
        collatingDanceList();
        checkDanceStart();
    }

    /** 跳舞插队 */
    private void danceTop(UserInfo fromUser, UserInfo toUser) {
        if (fromUser == null || toUser == null) {
            return;
        }
        // 找到用户与该主播正在排队中的数据
        DanceModel danceWaitingModel = findDanceWaitingModel(fromUser, toUser);
        if (danceWaitingModel == null) { // 没有排队中的数据，不能插队
            return;
        }
        // 找到该主播排队中的第一位
        int insertPosition = -1;
        for (int i = 0; i < danceList.size(); i++) {
            DanceModel model = danceList.get(i);
            if (model.isCompleted) {
                continue;
            }
            if (model.beginTime == 0 && Objects.equals(model.toUser, toUser)) {
                insertPosition = i;
                break;
            }
        }
        if (insertPosition == -1) {
            return;
        }

        int curPosition = danceList.indexOf(danceWaitingModel);
        if (insertPosition < curPosition) { // 找到的该主播排队第一名，比现在的位置要大
            danceList.remove(danceWaitingModel);
            danceList.add(insertPosition, danceWaitingModel);
            callbackDanceList();
        }
    }

    /** 匹配用户与主播，找到未结束的跳舞数据 */
    private DanceModel findDancingModel(UserInfo fromUser, UserInfo toUser) {
        for (DanceModel model : danceList) {
            if (model.isCompleted) {
                continue;
            }
            if (model.fromUser != null && model.fromUser.equals(fromUser)
                    && model.toUser != null && model.toUser.equals(toUser)) {
                return model;
            }
        }
        return null;
    }

    /** 匹配用户与主播，找到排队中的跳舞数据 */
    private DanceModel findDanceWaitingModel(UserInfo fromUser, UserInfo toUser) {
        for (DanceModel model : danceList) {
            if (model.isCompleted) {
                continue;
            }
            if (model.fromUser != null && model.fromUser.equals(fromUser)
                    && model.toUser != null && model.toUser.equals(toUser)) {
                if (model.beginTime == 0) {
                    return model;
                }
            }
        }
        return null;
    }

    /** 回调整个跳舞队列 */
    private void callbackDanceList() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onDanceList(danceList);
        }
    }

    /** 回调蹦迪排行榜变化 */
    private void callbackContribution() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onDiscoContribution(contributionModels);
        }
    }

    /** 回调页面更新某一个跳舞数据 */
    private void callbackUpdateDance(int index) {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onUpdateDance(index);
        }
    }

    /** 检查跳舞队列，哪个可以执行开始了 */
    private void checkDanceStart() {
        for (DanceModel model : danceList) {
            if (model.isCompleted) { // 已结束的就不管了
                continue;
            }
            if (model.beginTime > 0) { // 如果已经开始的，检查一下是否有执行定时任务
                if (model.countdownTimer == null) {
                    startCountdownTimer(model);
                }
                continue;
            }
            // 查找该主播是否正在跳着舞
            if (isAnchorDancing(model.toUser)) {
                continue;
            }
            if ((HSUserInfo.userId + "").equals(model.fromUser.userID)) { // 自己发起的，则自己去触发
                callDanceWithAnchor(model.toUser, model.duration);
            }
            startCountdownTimer(model);
        }
    }

    /** 查找该主播是否正在跳着舞 */
    private boolean isAnchorDancing(UserInfo userInfo) {
        for (DanceModel model : danceList) {
            if (model.isCompleted) {
                continue;
            }
            if (model.beginTime > 0 && model.toUser.equals(userInfo)) {
                return true;
            }
        }
        return false;
    }

    /** 获取跳舞集合 */
    public List<DanceModel> getDanceList() {
        return danceList;
    }

    /** 发送礼物监听 */
    private final SceneCommandManager.SendGiftCommandListener sendGiftCommandListener = new SceneCommandManager.SendGiftCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdSendGiftModel model, String userID) {
            if (model.type == 0) {
                if (model.giftID == 5) {
                    addDance(model.sendUser, model.toUser, 60);
                } else if (model.giftID == 6) {
                    addDance(model.sendUser, model.toUser, 3 * 60);
                } else if (model.giftID == 7) {
                    danceTop(model.sendUser, model.toUser);
                }
            }
        }
    };

    /** 请求蹦迪信息通知 */
    private final SceneCommandManager.DiscoInfoReqCommandListener discoInfoReqListener = new SceneCommandManager.DiscoInfoReqCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdDiscoInfoReqModel model, String userID) {
            String cmd = RoomCmdModelUtils.buildCmdDiscoInfoResp(danceList, contributionModels);
            parentManager.sceneEngineManager.sendCommand(cmd);
        }
    };

    /** 响应蹦迪信息通知 */
    private final SceneCommandManager.DiscoInfoRespCommandListener discoInfoRespListener = new SceneCommandManager.DiscoInfoRespCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdDiscoInfoRespModel model, String userID) {
            if (initDiscoInfoCompleted) {
                return;
            }
            initDiscoInfoCompleted = true;
            // 跳舞队列
            releaseDanceList();
            danceList.clear();
            if (model.dancingMenu != null) {
                danceList.addAll(model.dancingMenu);
            }
            long curTimeSecond = System.currentTimeMillis() / 1000;
            for (DanceModel danceModel : danceList) {
                if (danceModel.beginTime > 0 && (danceModel.beginTime + danceModel.duration) < curTimeSecond) {
                    danceModel.isCompleted = true;
                }
            }
            collatingDanceList();
            checkDanceStart();

            // 排行榜
            contributionModels.clear();
            if (model.contribution != null) {
                contributionModels.addAll(model.contribution);
            }
            callbackContribution();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        parentManager.sceneChatManager.removeSendMsgListener(sendMsgListener);
        parentManager.sceneEngineManager.removeCommandListener(sendGiftCommandListener);
        parentManager.sceneEngineManager.removeCommandListener(discoInfoReqListener);
        parentManager.sceneEngineManager.removeCommandListener(discoInfoRespListener);
        releaseDanceList();
    }

    private void releaseDanceList() {
        for (DanceModel danceModel : danceList) {
            if (danceModel.countdownTimer != null) {
                danceModel.countdownTimer.cancel();
            }
        }
    }
}
