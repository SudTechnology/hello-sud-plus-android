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
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneCommandManager.DiscoActionPayCommandListener;
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
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdDiscoActionPayModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdDiscoInfoReqModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.RoomCmdDiscoInfoRespModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftHelper;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.disco.model.DiscoInteractionModel;
import tech.sud.mgp.hello.ui.scenes.disco.viewmodel.DiscoActionHelper;

/**
 * ???????????? ??????????????????
 */
public class SceneDiscoManager extends BaseServiceManager {

    private SceneRoomServiceManager parentManager;
    private final DiscoActionHelper helper = new DiscoActionHelper(); // ????????????????????????
    private final List<DanceModel> danceList = new ArrayList<>(); // ???????????????????????????
    private final List<ContributionModel> contributionList = new ArrayList<>(); // ??????????????????
    private boolean initDiscoInfoCompleted; // ??????????????????????????????????????????
    private UserInfo initRecUserInfo; // ??????????????????????????????????????????????????????
    private CustomCountdownTimer djCountdownTimer;
    private final int djCountdownCycle = 60; // ?????????dj?????????
    private UserInfo selfUserInfo = RoomCmdModelUtils.getSendUser();
    private Context context = Utils.getApp();

    public static final int ROBOT_UP_MIC_COUNT = 6; // ????????????????????????

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
        parentManager.sceneEngineManager.setCommandListener(discoActionPayCommandListener);
    }

    /** ???????????????????????????1????????? */
    private void addPublicMsgContribution(UserInfo userInfo) {
        addContribution(userInfo, 1);
    }

    /** ?????????????????? */
    private void checkChatMsgHit(String msg) {
        if (context.getString(R.string.move).equals(msg)) { // ??????
            triggerMove();
        } else if (context.getString(R.string.god).equals(msg)) { // ??????
            triggerGod();
        } else if (context.getString(R.string.change_role).equals(msg)) { // ?????????
            triggerChangeRole();
        } else if (context.getString(R.string.go_to_work).equals(msg)) { // ??????
            triggerGoToWork();
        } else if (context.getString(R.string.get_off_work).equals(msg)) { // ??????
            triggerGetOffWork();
        } else if (context.getString(R.string.focus).equals(msg)) { // ??????
            triggerFocus();
        }
    }

    /** ?????????????????? */
    private void triggerMove() {
        callbackAction(helper.roleMove(null, null));
    }

    /** ?????????????????? */
    private void triggerGod() {
        callbackAction(helper.roleFly(3));
    }

    /** ????????????????????? */
    private void triggerChangeRole() {
        callbackAction(helper.changeRole(null));
    }

    /** ?????????????????? */
    private void triggerGoToWork() {
        int selfMicIndex = parentManager.sceneMicManager.findSelfMicIndex();
        if (selfMicIndex >= 0) {
            callbackAction(helper.joinAnchor(null, null));
            RoomRepository.discoSwitchAnchor(parentManager, parentManager.getRoomId(), 1, HSUserInfo.userId, new RxCallback<>());
        }
    }

    /** ?????????????????? */
    private void triggerGetOffWork() {
        callbackAction(helper.leaveAnchor(HSUserInfo.userId + ""));
        RoomRepository.discoSwitchAnchor(parentManager, parentManager.getRoomId(), 2, HSUserInfo.userId, new RxCallback<>());
    }

    /** ?????????????????? */
    private void triggerFocus() {
        if (parentManager.sceneMicManager.findSelfMicIndex() >= 0) {
            callbackAction(helper.roleFocus(4, false));
        }
    }

    /** ?????????????????????????????????????????? */
    private void callbackAction(String dataJson) {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, dataJson, null);
        }
    }

    /** ?????????????????? */
    public void onEnterRoomSuccess() {
        // ??????????????????????????????????????????
        String discoInfoReqCmd = RoomCmdModelUtils.buildCmdDiscoInfoReq();
        parentManager.sceneEngineManager.sendCommand(discoInfoReqCmd);

        // ???????????????dj
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

    /** ??????????????????dj */
    private void randomDJ() {
        // ????????????????????????????????????
        boolean isPermission = false;
        List<AudioRoomMicModel> micList = parentManager.sceneMicManager.getMicList();
        for (AudioRoomMicModel model : micList) {
            // ???????????????????????????????????????????????????????????????
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
        // ??????????????????
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

    /** ????????????????????? */
    public void onSendGift(GiftModel giftModel, int giftCount, UserInfo toUser) {
        if (toUser == null || giftModel == null) {
            return;
        }
        processGiftAction(giftModel.giftId, giftCount, toUser, giftModel.type, giftModel.giftName);
        addGiftContribution(giftModel, giftCount, selfUserInfo);
    }

    /** ?????????????????? */
    private void addGiftContribution(GiftModel giftModel, int giftCount, UserInfo userInfo) {
        addContribution(userInfo, (long) giftModel.giftPrice * giftCount);
    }

    /** ??????????????? */
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

    /** ?????????????????? */
    private void processGiftAction(long giftID, int giftCount, UserInfo toUser, int type, String giftName) {
        // ?????????????????????????????????
        if (type != 0) {
            return;
        }

        Application context = Utils.getApp();
        // ????????????????????????????????????????????????
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
     * ????????????????????????
     *
     * @param fromUser ?????????
     * @param toUser   ?????????
     * @param duration ???????????????
     */
    private void addDance(UserInfo fromUser, UserInfo toUser, int duration) {
        if (fromUser == null || toUser == null || fromUser.equals(toUser)) {
            return;
        }
        DanceModel model = findDancingModel(fromUser, toUser);
        if (model == null) {
            // ??????????????????????????????????????????????????????????????????
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
            if (model.beginTime > 0) { // ?????????????????????
                model.duration += duration;
                if ((HSUserInfo.userId + "").equals(fromUser.userID)) { // ????????????????????????????????????
                    callDanceWithAnchor(toUser, duration);
                }
                startCountdownTimer(model);
            } else { // ??????????????????????????????
                model.duration += duration;
                callbackUpdateDance(danceList.indexOf(model));
            }
        }

        if (model.beginTime == 0 && (HSUserInfo.userId + "").equals(fromUser.userID)) { // ??????????????????????????????
            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
                callback.onDanceWait();
            }
        }
    }

    /** ??????????????????????????????????????????????????????????????????-1 */
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

    /** ?????????????????????????????????????????? */
    private void callDanceWithAnchor(UserInfo toUser, int duration) {
        callbackAction(helper.danceWithAnchor(duration, false, toUser.userID));
        callbackAction(helper.roleFocus(5, false));
    }

    /** ?????????????????? */
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

    /** ??????????????? */
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

    /** ??????????????? */
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

    /** ???????????? */
    private void danceTop(UserInfo fromUser, UserInfo toUser) {
        if (fromUser == null || toUser == null) {
            return;
        }
        // ????????????????????????????????????????????????
        DanceModel danceWaitingModel = findDanceWaitingModel(fromUser, toUser);
        if (danceWaitingModel == null) { // ???????????????????????????????????????
            return;
        }
        // ????????????????????????????????????
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
        if (insertPosition < curPosition) { // ????????????????????????????????????????????????????????????
            danceList.remove(danceWaitingModel);
            danceList.add(insertPosition, danceWaitingModel);
            callbackDanceList();
        }
    }

    /** ?????????????????????????????????????????????????????? */
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

    /** ?????????????????????????????????????????????????????? */
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

    /** ???????????????????????? */
    private void callbackDanceList() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onDanceList(danceList);
        }
    }

    /** ????????????????????? */
    private void sortContributions() {
        Collections.sort(contributionList);
    }

    /** ??????????????????????????? */
    private void callbackContribution() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onDiscoContribution(contributionList);
        }
    }

    /** ??????????????????????????????????????? */
    private void callbackUpdateDance(int index) {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onUpdateDance(index);
        }
    }

    /** ???????????????????????????????????????????????? */
    private void checkDanceStart() {
        for (DanceModel model : danceList) {
            if (model.isCompleted) { // ????????????????????????
                continue;
            }
            if (model.beginTime > 0) { // ???????????????????????????????????????????????????????????????
                if (model.countdownTimer == null) {
                    startCountdownTimer(model);
                }
                continue;
            }
            // ????????????????????????????????????
            if (isAnchorDancing(model.fromUser) || isAnchorDancing(model.toUser)) {
                continue;
            }
            if ((HSUserInfo.userId + "").equals(model.fromUser.userID)) { // ????????????????????????????????????
                callDanceWithAnchor(model.toUser, model.duration);
            }
            startCountdownTimer(model);
        }
    }

    /** ???????????????????????????????????? */
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

    /** ?????????????????? */
    public List<DanceModel> getDanceList() {
        return danceList;
    }

    /** ?????????????????? */
    private final SceneCommandManager.SendGiftCommandListener sendGiftCommandListener = new SceneCommandManager.SendGiftCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdSendGiftModel model, String userID) {
            GiftModel giftModel = GiftHelper.getInstance().getGift(model.giftID);
            if (model.sendUser != null && giftModel != null) {
                // ???????????????
                addGiftContribution(giftModel, model.giftCount, model.sendUser);

                // ??????
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

    /** ???????????????????????? */
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

    /** ???????????????????????? */
    private final SceneCommandManager.DiscoInfoRespCommandListener discoInfoRespListener = new SceneCommandManager.DiscoInfoRespCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdDiscoInfoRespModel model, String userID) {
            if (initDiscoInfoCompleted) {
                // ????????????????????????????????????????????????
                return;
            }
            if (initRecUserInfo == null) {
                initRecUserInfo = model.sendUser;
            } else {
                // ???????????????????????????????????????????????????
                if (!initRecUserInfo.equals(model.sendUser)) {
                    return;
                }
            }

            if (model.isEnd) {
                initDiscoInfoCompleted = true;
            }

            // ?????????
            if (model.contribution != null) {
                for (ContributionModel contributionModel : model.contribution) {
                    if (!contributionList.contains(contributionModel)) {
                        contributionList.add(contributionModel);
                    }
                }
            }
            sortContributions();
            callbackContribution();

            // ????????????
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

    /** ???????????????????????? */
    private final SceneCommandManager.PublicMsgCommandListener publicMsgCommandListener = new SceneCommandManager.PublicMsgCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdChatTextModel command, String userID) {
            UserInfo userInfo = command.sendUser;
            if (userInfo == null) return;
            addPublicMsgContribution(userInfo);
        }
    };

    /** ???dj????????? */
    private final SceneCommandManager.DiscoBecomeDJCommandListener becomeDJCommandListener = new SceneCommandManager.DiscoBecomeDJCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdBecomeDJModel model, String userID) {
            if ((HSUserInfo.userId + "").equals(model.userID)) {
                callbackAction(helper.upDJ(djCountdownCycle));
            }
        }
    };

    /** ?????????????????? ?????? */
    private final SceneCommandManager.GameChangeCommandListener gameChangeCommandListener = new SceneCommandManager.GameChangeCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdChangeGameModel model, String userID) {
            if (model.gameID == 0) {
                closeDisco();
            }
        }
    };

    /** ?????????????????? ?????? */
    private final DiscoActionPayCommandListener discoActionPayCommandListener = new DiscoActionPayCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdDiscoActionPayModel model, String userID) {
            if (model.sendUser != null) {
                addContribution(model.sendUser, model.price);
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
        parentManager.sceneEngineManager.removeCommandListener(discoActionPayCommandListener);
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

    /** ??????????????? */
    private void closeDisco() {
        // ?????????????????????
        releaseDanceList();
        callbackDanceList();
    }

    /**
     * ????????????
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

    /** ????????????????????????????????? */
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
                String cmd = RoomCmdModelUtils.buildCmdDiscoActionPay(model.price);
                parentManager.sceneEngineManager.sendCommand(cmd);
            }
        });
    }

    /** ????????????????????????????????? */
    private void deductionSuccess(DiscoInteractionModel model, LifecycleOwner owner, long roomId, ActionListener listener) {
        switch (model.type) {
            case JOIN_ANCHOR:
                switchAnchor(owner, roomId, true, listener);
                break;
            case LEAVE_ANCHOR:
                switchAnchor(owner, roomId, false, listener);
                break;
            case UP_DJ:
                actionMessage(context.getString(R.string.up_dj));
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
                exeActionBig();
                exeActionFocus();
                callbackAction(helper.textPop(null, context.getString(R.string.disco_nick_name)));
                break;
            case POP_BIG_FOCUS_EFFECTS:
                exeActionBig();
                exeActionFocus();
                exeActionEffects();
                callbackAction(helper.textPop(null, context.getString(R.string.disco_nick_name)));
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
     * ???????????????
     *
     * @param owner  ??????????????????
     * @param isJoin true??????????????? false???????????????
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

    /** ????????????????????? */
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
