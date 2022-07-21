package tech.sud.mgp.hello.ui.scenes.base.manager;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.CustomCountdownTimer;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.SceneUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChangeGameModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChatTextModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdSendGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.ContributionModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.DanceModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdBecomeDJModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdDiscoInfoReqModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdDiscoInfoRespModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftHelper;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.disco.model.DiscoInteractionModel;
import tech.sud.mgp.hello.ui.scenes.disco.viewmodel.DiscoActionHelper;

/**
 * 蹦迪场景内的业务逻辑
 */
public class SceneDiscoManager extends BaseServiceManager {

    private SceneRoomServiceManager parentManager;
    private final DiscoActionHelper helper = new DiscoActionHelper(); // 操作蹦迪动作助手
    private final List<DanceModel> danceList = new ArrayList<>(); // 跳舞列表，包含所有
    private final List<ContributionModel> contributionList = new ArrayList<>(); // 房间贡献列表
    private boolean initDiscoInfoCompleted; // 是否已经初始化完成了蹦迪信息
    private UserInfo initRecUserInfo; // 初始化时，只接受该用户传递过来的数据
    private CustomCountdownTimer djCountdownTimer;
    private final int djCountdownCycle = 60; // 循环上dj台秒数
    private UserInfo selfUserInfo = RoomCmdModelUtils.getSendUser();
    private Context context = Utils.getApp();

    public static final int ROBOT_UP_MIC_COUNT = 6; // 机器人上几个麦位

    public SceneDiscoManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.sceneEngineManager.setCommandListener(sendGiftCommandListener);
        parentManager.sceneEngineManager.setCommandListener(discoInfoReqListener);
        parentManager.sceneEngineManager.setCommandListener(discoInfoRespListener);
        parentManager.sceneEngineManager.setCommandListener(publicMsgCommandListener);
        parentManager.sceneEngineManager.setCommandListener(becomeDJCommandListener);
        parentManager.sceneEngineManager.setCommandListener(gameChangeCommandListener);
    }

    /** 每发送一条公屏，加1贡献值 */
    private void addPublicMsgContribution(UserInfo userInfo) {
        addContribution(userInfo, 1);
    }

    /** 检查是否命中 */
    private void checkChatMsgHit(String msg) {
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
            callbackAction(helper.joinAnchor(null, null));
            RoomRepository.discoSwitchAnchor(parentManager, parentManager.getRoomId(), 1, HSUserInfo.userId, new RxCallback<>());
        }
    }

    /** 触发【下班】 */
    private void triggerGetOffWork() {
        callbackAction(helper.leaveAnchor(HSUserInfo.userId + ""));
        RoomRepository.discoSwitchAnchor(parentManager, parentManager.getRoomId(), 2, HSUserInfo.userId, new RxCallback<>());
    }

    /** 触发【聚焦】 */
    private void triggerFocus() {
        if (parentManager.sceneMicManager.findSelfMicIndex() >= 0) {
            callbackAction(helper.roleFocus(4, false));
        }
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

        // 倒计时成为dj
        startDJCountdown();
    }

    private void startDJCountdown() {
        cancelDJCountdown();
        djCountdownTimer = new CustomCountdownTimer(SceneUtils.getTotalCount(djCountdownCycle)) {
            @Override
            protected void onTick(int count) {
                callbackDjCountdowm(count);
            }

            @Override
            protected void onFinish() {
                startDJCountdown();
                randomDJ();
            }
        };
        djCountdownTimer.start();
    }

    /** 随机产生一个dj */
    private void randomDJ() {
        // 自己是麦位第一人，才执行
        boolean isPermission = false;
        List<AudioRoomMicModel> micList = parentManager.sceneMicManager.getMicList();
        for (AudioRoomMicModel model : micList) {
            // 前面六个是机器人，所以这里从第五位开始判断
            if (model.micIndex >= ROBOT_UP_MIC_COUNT && model.userId > 0) {
                if (model.userId == HSUserInfo.userId) {
                    isPermission = true;
                }
                break;
            }
        }
        if (!isPermission) {
            return;
        }

        int size = contributionList.size();
        if (size == 0) {
            return;
        }
        // 最多取前五位
        if (size > 5) {
            size = 5;
        }
        Random random = new Random();
        ContributionModel contributionModel = contributionList.get(random.nextInt(size));
        if (contributionModel.fromUser != null) {
            if ((HSUserInfo.userId + "").equals(contributionModel.fromUser.userID)) {
                callbackAction(helper.upDJ(djCountdownCycle));
            } else {
                String cmd = RoomCmdModelUtils.buildCmdBecomeDJ(contributionModel.fromUser.userID);
                parentManager.sceneEngineManager.sendCommand(cmd);
            }
        }
    }

    private void callbackDjCountdowm(int countdown) {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onDJCountdown(countdown);
        }
    }

    private void cancelDJCountdown() {
        if (djCountdownTimer != null) {
            djCountdownTimer.cancel();
            djCountdownTimer = null;
        }
    }

    /** 自己发送了礼物 */
    public void onSendGift(GiftModel giftModel, int giftCount, UserInfo toUser) {
        if (toUser == null || giftModel == null) {
            return;
        }
        processGiftAction(giftModel.giftId, giftCount, toUser, giftModel.type, giftModel.giftName);
        addGiftContribution(giftModel, giftCount, selfUserInfo);
    }

    /** 添加礼物贡献 */
    private void addGiftContribution(GiftModel giftModel, int giftCount, UserInfo userInfo) {
        addContribution(userInfo, (long) giftModel.giftPrice * giftCount);
    }

    /** 添加贡献值 */
    private void addContribution(UserInfo userInfo, long count) {
        ContributionModel contributionModel = findContributionUser(userInfo);
        if (contributionModel == null) {
            contributionModel = new ContributionModel();
            contributionModel.fromUser = userInfo;
            contributionList.add(contributionModel);
        }
        contributionModel.count += count;
        sortContributions();
        callbackContribution();
    }

    private ContributionModel findContributionUser(UserInfo fromUser) {
        for (ContributionModel model : contributionList) {
            if (model.fromUser != null && model.fromUser.equals(fromUser)) {
                return model;
            }
        }
        return null;
    }

    /** 处理送礼动效 */
    private void processGiftAction(long giftID, int giftCount, UserInfo toUser, int type, String giftName) {
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
            callbackAction(helper.roleFocus(3, false));
        } else if (giftID == 3) {
            callbackAction(helper.textPop(6, context.getString(R.string.send_gift_title, giftCount + "", giftName)));
            callbackAction(helper.roleBig(30, 2));
            callbackAction(helper.roleFocus(4, false));
        } else if (giftID == 4) {
            callbackAction(helper.textPop(9, context.getString(R.string.send_gift_title, giftCount + "", giftName)));
            callbackAction(helper.roleBig(60, 2));
            callbackAction(helper.roleFocus(5, false));
            callbackAction(helper.roleEffects(60 * 60 * 2, null));
        } else if (giftID == 5) {
            addDance(RoomCmdModelUtils.getSendUser(), toUser, 60 * giftCount);
        } else if (giftID == 6) {
            addDance(RoomCmdModelUtils.getSendUser(), toUser, 3 * 60 * giftCount);
        } else if (giftID == 7) {
            danceTop(RoomCmdModelUtils.getSendUser(), toUser);
        }
    }

    /**
     * 增加一个跳舞请求
     *
     * @param fromUser 邀请方
     * @param toUser   受邀方
     * @param duration 时长（秒）
     */
    private void addDance(UserInfo fromUser, UserInfo toUser, int duration) {
        if (fromUser == null || toUser == null || fromUser.equals(toUser)) {
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
            }
        }

        if (model.beginTime == 0 && (HSUserInfo.userId + "").equals(fromUser.userID)) { // 自己发起的，提示等待
            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
                callback.onDanceWait();
            }
        }
    }

    /** 查找第一个已经完成的跳舞数据，没找到，则返回-1 */
    private int findFirstCompletedDanceModel() {
        int firstPosition = -1;
        for (int i = 0; i < danceList.size(); i++) {
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
        callbackAction(helper.roleFocus(5, false));
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

    /** 贡献榜进行排序 */
    private void sortContributions() {
        Collections.sort(contributionList);
    }

    /** 回调蹦迪排行榜变化 */
    private void callbackContribution() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onDiscoContribution(contributionList);
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
            if (isAnchorDancing(model.fromUser) || isAnchorDancing(model.toUser)) {
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
            GiftModel giftModel = GiftHelper.getInstance().getGift(model.giftID);
            if (model.sendUser != null && giftModel != null) {
                // 添加贡献榜
                addGiftContribution(giftModel, model.giftCount, model.sendUser);

                // 跳舞
                if (model.type == 0) {
                    if (model.giftID == 5) {
                        addDance(model.sendUser, model.toUser, 60 * model.giftCount);
                    } else if (model.giftID == 6) {
                        addDance(model.sendUser, model.toUser, 3 * 60 * model.giftCount);
                    } else if (model.giftID == 7) {
                        danceTop(model.sendUser, model.toUser);
                    }
                }
            }
        }
    };

    /** 请求蹦迪信息通知 */
    private final SceneCommandManager.DiscoInfoReqCommandListener discoInfoReqListener = new SceneCommandManager.DiscoInfoReqCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdDiscoInfoReqModel model, String userID) {
            for (int i = 0; i < contributionList.size(); i++) {
                String cmd = RoomCmdModelUtils.buildCmdDiscoInfoResp(null, contributionList.subList(i, i + 1), false);
                parentManager.sceneEngineManager.sendCommand(cmd);
            }
            for (int i = 0; i < danceList.size(); i++) {
                boolean isEnd = (i == (danceList.size() - 1));
                String cmd = RoomCmdModelUtils.buildCmdDiscoInfoResp(danceList.subList(i, i + 1), null, isEnd);
                ThreadUtils.runOnUiThreadDelayed(new Runnable() {
                    @Override
                    public void run() {
                        parentManager.sceneEngineManager.sendCommand(cmd);
                    }
                }, 10L * i);
            }
        }
    };

    /** 响应蹦迪信息通知 */
    private final SceneCommandManager.DiscoInfoRespCommandListener discoInfoRespListener = new SceneCommandManager.DiscoInfoRespCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdDiscoInfoRespModel model, String userID) {
            if (initDiscoInfoCompleted) {
                // 已完成初始化，则不再处理新的数据
                return;
            }
            if (initRecUserInfo == null) {
                initRecUserInfo = model.sendUser;
            } else {
                // 只接受指定第一个用户发送过来的数据
                if (!initRecUserInfo.equals(model.sendUser)) {
                    return;
                }
            }

            if (model.isEnd) {
                initDiscoInfoCompleted = true;
            }

            // 排行榜
            if (model.contribution != null) {
                for (ContributionModel contributionModel : model.contribution) {
                    if (!contributionList.contains(contributionModel)) {
                        contributionList.add(contributionModel);
                    }
                }
            }
            sortContributions();
            callbackContribution();

            // 跳舞队列
            long curTimeSecond = System.currentTimeMillis() / 1000;
            if (model.dancingMenu != null) {
                for (DanceModel danceModel : model.dancingMenu) {
                    if (danceModel.beginTime > 0 && (danceModel.beginTime + danceModel.duration) < curTimeSecond) {
                        danceModel.isCompleted = true;
                    }
                    if (!danceList.contains(danceModel)) {
                        danceList.add(danceModel);
                    }
                }
            }
            collatingDanceList();
            checkDanceStart();
        }
    };

    /** 公屏消息信令通知 */
    private final SceneCommandManager.PublicMsgCommandListener publicMsgCommandListener = new SceneCommandManager.PublicMsgCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdChatTextModel command, String userID) {
            UserInfo userInfo = command.sendUser;
            if (userInfo == null) return;
            addPublicMsgContribution(userInfo);
        }
    };

    /** 上dj台通知 */
    private final SceneCommandManager.DiscoBecomeDJCommandListener becomeDJCommandListener = new SceneCommandManager.DiscoBecomeDJCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdBecomeDJModel model, String userID) {
            if ((HSUserInfo.userId + "").equals(model.userID)) {
                callbackAction(helper.upDJ(djCountdownCycle));
            }
        }
    };

    /** 游戏切换信令 通知 */
    private final SceneCommandManager.GameChangeCommandListener gameChangeCommandListener = new SceneCommandManager.GameChangeCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdChangeGameModel model, String userID) {
            if (model.gameID == 0) {
                closeDisco();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        parentManager.sceneEngineManager.removeCommandListener(sendGiftCommandListener);
        parentManager.sceneEngineManager.removeCommandListener(discoInfoReqListener);
        parentManager.sceneEngineManager.removeCommandListener(discoInfoRespListener);
        parentManager.sceneEngineManager.removeCommandListener(publicMsgCommandListener);
        parentManager.sceneEngineManager.removeCommandListener(becomeDJCommandListener);
        parentManager.sceneEngineManager.removeCommandListener(gameChangeCommandListener);
        releaseDanceList();
        cancelDJCountdown();
    }

    private void releaseDanceList() {
        for (DanceModel danceModel : danceList) {
            if (danceModel.countdownTimer != null) {
                danceModel.countdownTimer.cancel();
            }
        }
        danceList.clear();
    }

    public List<ContributionModel> getDiscoContribution() {
        return contributionList;
    }

    public void switchGame(long gameId) {
        if (gameId == 0) {
            closeDisco();
        }
    }

    /** 关闭了蹦迪 */
    private void closeDisco() {
        // 清除舞台节目单
        releaseDanceList();
        callbackDanceList();
    }

    /**
     * 执行动作
     *
     * @param model
     */
    public void exeDiscoAction(long roomId, DiscoInteractionModel model, ActionListener listener) {
        actionDeduction(parentManager, roomId, model, new CompletedListener() {
            @Override
            public void onCompleted() {
                deductionSuccess(model, parentManager, roomId, listener);
            }
        });
    }

    /** 执行动作之前，进行扣费 */
    private void actionDeduction(LifecycleOwner owner, long roomId, DiscoInteractionModel model, CompletedListener listener) {
        if (model.price == null || model.price == 0) {
            listener.onCompleted();
            return;
        }
        RoomRepository.deductionCoin(owner, model.price, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                listener.onCompleted();
                addContribution(selfUserInfo, model.price);
            }
        });
    }

    /** 扣费成功之后，执行动作 */
    private void deductionSuccess(DiscoInteractionModel model, LifecycleOwner owner, long roomId, ActionListener listener) {
        switch (model.type) {
            case JOIN_ANCHOR:
                switchAnchor(owner, roomId, true, listener);
                break;
            case LEAVE_ANCHOR:
                switchAnchor(owner, roomId, false, listener);
                break;
            case UP_DJ:
                callbackAction(helper.upDJ(null));
                break;
            case MOVE:
                actionMessage(context.getString(R.string.move));
                callbackAction(helper.roleMove(null, null));
                break;
            case GOD:
                actionMessage(context.getString(R.string.god));
                callbackAction(helper.roleFly(null));
                break;
            case BIG:
                exeActionBig();
                break;
            case CHANGE_ROLE:
                actionMessage(context.getString(R.string.change_role));
                callbackAction(helper.changeRole(null));
                break;
            case FOCUS:
                exeActionFocus();
                break;
            case TITLE:
                actionMessage(context.getString(R.string.title));
                callbackAction(helper.roleTitle(60, null, null));
                break;
            case EFFECTS:
                exeActionEffects();
                break;
            case POP_BIG_FOCUS:
                callbackAction(helper.textPop(null, context.getString(R.string.disco_nick_name)));
                exeActionBig();
                exeActionFocus();
                break;
            case POP_BIG_FOCUS_EFFECTS:
                callbackAction(helper.textPop(null, context.getString(R.string.disco_nick_name)));
                exeActionBig();
                exeActionFocus();
                exeActionEffects();
                break;
        }
    }

    private void actionMessage(String text) {
        parentManager.sceneChatManager.sendPublicMsg(text);
        showMsgTextPop(text);
        addPublicMsgContribution(selfUserInfo);
    }

    private void exeActionEffects() {
        actionMessage(context.getString(R.string.effect));
        callbackAction(helper.roleEffects(null, null));
    }

    private void exeActionFocus() {
        actionMessage(context.getString(R.string.feature));
        callbackAction(helper.roleFocus(null, null));
    }

    private void exeActionBig() {
        actionMessage(context.getString(R.string.largen));
        callbackAction(helper.roleBig(null, 2));
    }

    /**
     * 上下主播位
     *
     * @param owner  生命周期对象
     * @param isJoin true为上主播位 false为下主播位
     */
    private void switchAnchor(LifecycleOwner owner, long roomId, boolean isJoin, ActionListener listener) {
        if (isJoin) {
            callbackAction(helper.joinAnchor(null, HSUserInfo.userId + ""));
        } else {
            callbackAction(helper.leaveAnchor(HSUserInfo.userId + ""));
        }
        RoomRepository.discoSwitchAnchor(owner, roomId, isJoin ? 1 : 2, HSUserInfo.userId, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                listener.onAnchorChange(isJoin);
            }
        });
    }

    /** 发送了公屏消息 */
    public void sendPublicMsg(String msg) {
        checkChatMsgHit(msg);
        addPublicMsgContribution(selfUserInfo);
        showMsgTextPop(msg);
    }

    private void showMsgTextPop(String msg) {
        callbackAction(helper.textPop(3, msg));
    }

    public interface ActionListener {
        void onAnchorChange(boolean isAnchor);
    }

}
