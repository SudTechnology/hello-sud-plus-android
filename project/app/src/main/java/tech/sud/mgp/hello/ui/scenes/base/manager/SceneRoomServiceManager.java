package tech.sud.mgp.hello.ui.scenes.base.manager;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;
import com.trello.rxlifecycle4.LifecycleTransformer;
import com.trello.rxlifecycle4.RxLifecycle;

import java.util.List;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.lifecycle.CustomLifecycleEvent;
import tech.sud.mgp.hello.common.utils.lifecycle.CustomLifecycleProvider;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.RoomMicSwitchResp;
import tech.sud.mgp.hello.ui.common.constant.RequestKey;
import tech.sud.mgp.hello.ui.main.base.constant.SceneType;
import tech.sud.mgp.hello.ui.scenes.base.activity.RoomConfig;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomService;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChangeGameModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdEnterRoomModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdKickOutRoomModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.danmaku.RoomCmdDanmakuTeamChangeModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.ContributionModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.DanceModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.game.RoomCmdPropsCardGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.monopoly.RoomCmdMonopolyCardGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;

/**
 * 房间主要业务逻辑
 */
public class SceneRoomServiceManager extends BaseServiceManager implements CustomLifecycleProvider, LifecycleOwner {

    private final BehaviorSubject<CustomLifecycleEvent> lifecycleSubject = BehaviorSubject.create();
    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    private SceneRoomServiceCallback sceneRoomServiceCallback;
    private RoomInfoModel roomInfoModel;
    private boolean enterRoomCompleted = false; // 标识是否进房成功
    private Class<? extends Activity> roomActivityClass; // 当前展示的Activity
    private SceneRoomService sceneRoomService; // 服务

    public SceneRoomServiceManager(SceneRoomService sceneRoomService) {
        this.sceneRoomService = sceneRoomService;
    }

    public final SceneEngineManager sceneEngineManager = new SceneEngineManager(this);
    public final SceneChatManager sceneChatManager = new SceneChatManager(this);
    public final SceneMicManager sceneMicManager = new SceneMicManager(this);
    public final SceneStreamManager sceneStreamManager = new SceneStreamManager(this);
    public final SceneGiftManager sceneGiftManager = new SceneGiftManager(this);
    public final SceneGameManager sceneGameManager = new SceneGameManager(this);
    public final SceneOrderManager sceneOrderManager = new SceneOrderManager(this);
    public final SceneRoomPkManager sceneRoomPkManager = new SceneRoomPkManager(this);
    public final SceneQuizManager sceneQuizManager = new SceneQuizManager(this);
    public final SceneFloatingManager floatingManager = new SceneFloatingManager(this);
    public SceneDiscoManager sceneDiscoManager;
    public SceneLeagueManager sceneLeagueManager;
    public SceneCrossAppManager sceneCrossAppManager;
    public SceneAudio3DRoomManager sceneAudio3DRoomManager;

    @Override
    public void onCreate() {
        super.onCreate();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        lifecycleSubject.onNext(CustomLifecycleEvent.CREATE);
        sceneEngineManager.onCreate();
        sceneChatManager.onCreate();
        sceneMicManager.onCreate();
        sceneStreamManager.onCreate();
        sceneGiftManager.onCreate();
        sceneGameManager.onCreate();
        sceneOrderManager.onCreate();
        sceneRoomPkManager.onCreate();
        sceneQuizManager.onCreate();
        floatingManager.onCreate();
        setListener();
    }

    private void setListener() {
        sceneEngineManager.setCommandListener(new SceneCommandManager.GameChangeCommandListener() {
            @Override
            public void onRecvCommand(RoomCmdChangeGameModel command, String userID) {
                roomInfoModel.gameId = command.gameID;
                callbackOnGameChange(command.gameID);
            }
        });
        sceneEngineManager.setCommandListener(new SceneCommandManager.EnterRoomCommandListener() {
            @Override
            public void onRecvCommand(RoomCmdEnterRoomModel command, String userID) {
                if (command == null || command.sendUser == null || command.sendUser.name == null) {
                    return;
                }
                String msg = buildEnterRoomMsg(command.sendUser.name);
                sceneChatManager.addMsg(msg);
            }
        });
        sceneEngineManager.setCommandListener(new SceneCommandManager.KickOutRoomCommandListener() {
            @Override
            public void onRecvCommand(RoomCmdKickOutRoomModel model, String userID) {
                SceneRoomServiceCallback callback = getCallback();
                if (callback == null) {
                    if ((HSUserInfo.userId + "").equals(model.userID)) {
                        exitRoom();
                    }
                } else {
                    callback.onKickOutRoom(model.userID);
                }
            }
        });
        sceneEngineManager.setCommandListener(new SceneCommandManager.DanmakuTeamChangeListener() {
            @Override
            public void onRecvCommand(RoomCmdDanmakuTeamChangeModel model, String userID) {
                // 公屏处理
                if (model.content != null && !TextUtils.isEmpty(model.content.content)) {
                    sceneChatManager.addMsg(model.content.content);
                }

                SceneRoomServiceCallback callback = getCallback();
                if (callback != null) {
                    callback.onDanmakuMatch(model);
                }
            }
        });
        sceneEngineManager.setCommandListener(new SceneCommandManager.MonopolyCardGiftListener() {
            @Override
            public void onRecvCommand(RoomCmdMonopolyCardGiftModel model, String userID) {
                SceneRoomServiceCallback callback = getCallback();
                if (callback != null) {
                    callback.onMonopolyCardGiftNotify(model);
                }
            }
        });
        sceneEngineManager.setCommandListener(new SceneCommandManager.PropsCardGiftListener() {
            @Override
            public void onRecvCommand(RoomCmdPropsCardGiftModel model, String userID) {
                SceneRoomServiceCallback callback = getCallback();
                if (callback != null) {
                    callback.onGamePropsCardGift(model);
                }
            }
        });
    }

    /** 回调页面，游戏切换了 */
    public void callbackOnGameChange(long gameId) {
        SceneRoomServiceCallback callback = getCallback();
        if (callback == null) {
            // 没有回调对象，可能页面挂起了，直接修改数据
            if (roomInfoModel != null) {
                roomInfoModel.gameId = gameId;
            }
        } else {
            callback.onGameChange(gameId);
        }
    }

    private String buildEnterRoomMsg(String nickName) {
        return nickName + " " + Utils.getApp().getString(R.string.audio_enter_the_room);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sceneRoomService = null;
        roomActivityClass = null;
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        lifecycleSubject.onNext(CustomLifecycleEvent.DESTROY);
        sceneEngineManager.onDestroy();
        sceneChatManager.onDestroy();
        sceneMicManager.onDestroy();
        sceneStreamManager.onDestroy();
        sceneGiftManager.onDestroy();
        sceneGameManager.onDestroy();
        sceneOrderManager.onDestroy();
        sceneRoomPkManager.onDestroy();
        sceneQuizManager.onDestroy();
        floatingManager.onDestroy();
        if (sceneDiscoManager != null) {
            sceneDiscoManager.onDestroy();
        }
        if (sceneLeagueManager != null) {
            sceneLeagueManager.onDestroy();
        }
        if (sceneCrossAppManager != null) {
            sceneCrossAppManager.onDestroy();
        }
    }

    /** 设置回调 */
    public void setCallback(SceneRoomServiceCallback callback) {
        sceneRoomServiceCallback = callback;
    }

    /** 移除回调 */
    public void removeCallback(SceneRoomServiceCallback callback) {
        if (sceneRoomServiceCallback == callback) {
            sceneRoomServiceCallback = null;
        }
    }

    public SceneRoomServiceCallback getCallback() {
        return sceneRoomServiceCallback;
    }

    /** 获取当前房间id */
    public long getRoomId() {
        RoomInfoModel model = roomInfoModel;
        if (model == null || model.roomId == null) {
            return 0;
        } else {
            return model.roomId;
        }
    }

    /** 获取房间的角色 */
    public int getRoleType() {
        RoomInfoModel model = roomInfoModel;
        if (model == null) {
            return RoleType.NORMAL;
        } else {
            return model.roleType;
        }
    }

    public RoomInfoModel getRoomInfoModel() {
        return roomInfoModel;
    }

    /** 进入房间 */
    public void enterRoom(RoomConfig config, Class<? extends Activity> startClass, RoomInfoModel model) {
        initManager(model);
        roomActivityClass = startClass;
        enterRoomCompleted = false;
        roomInfoModel = model;
        sceneEngineManager.enterRoomCompletedListener = this::checkEnterRoomCompleted;
        sceneMicManager.enterRoomCompletedListener = this::checkEnterRoomCompleted;
        sceneEngineManager.enterRoom(config, model);
        sceneMicManager.enterRoom(config, model);
        sceneRoomPkManager.enterRoom(config, model);
        if (sceneCrossAppManager != null) {
            sceneCrossAppManager.enterRoom(config, model);
        }
        if (sceneAudio3DRoomManager != null) {
            sceneAudio3DRoomManager.enterRoom(config, model);
        }
    }

    /** 根据不同的场景类型，初始化相关的业务管理类 */
    private void initManager(RoomInfoModel model) {
        switch (model.sceneType) {
            case SceneType.DISCO:
                sceneDiscoManager = new SceneDiscoManager(this);
                sceneDiscoManager.onCreate();
                break;
            case SceneType.LEAGUE:
                sceneLeagueManager = new SceneLeagueManager(this);
                sceneLeagueManager.onCreate();
                break;
            case SceneType.CROSS_APP_MATCH:
                sceneCrossAppManager = new SceneCrossAppManager(this);
                sceneCrossAppManager.onCreate();
                break;
            case SceneType.AUDIO_3D:
            case SceneType.CUBE:
                sceneAudio3DRoomManager = new SceneAudio3DRoomManager(this);
                sceneAudio3DRoomManager.onCreate();
                break;
        }
    }

    public int getSceneType() {
        if (roomInfoModel == null) {
            return SceneType.UNDEFINED;
        } else {
            return roomInfoModel.sceneType;
        }
    }

    /** 打开房间的Activity页面 */
    public void startRoomActivity() {
        if (roomActivityClass == null) return;
        Class<? extends Activity> cls = roomActivityClass;
        if (ActivityUtils.isActivityExistsInStack(cls)) return;
        Activity topActivity = ActivityUtils.getTopActivity();
        if (topActivity == null) return;
        Intent intent = new Intent(topActivity, cls);
        intent.putExtra(RequestKey.KEY_IS_PENDING_INTENT, true);
        topActivity.startActivity(intent);
    }

    /** 回调页面数据 */
    public void callbackPageData() {
        sceneChatManager.callbackPageData();
        sceneMicManager.callbackPageData();
        sceneStreamManager.callbackPageData();
        sceneRoomPkManager.callbackPageData();
        sceneOrderManager.callbackPageData();
        if (sceneCrossAppManager != null) {
            sceneCrossAppManager.callbackPageData();
        }
        if (sceneAudio3DRoomManager != null) {
            sceneAudio3DRoomManager.callbackPageData();
        }
        SceneRoomServiceCallback callback = getCallback();
        if (callback != null) {
            callback.onRecoverCompleted();
        }
    }

    /** 检查进入房间是否已完成 */
    private void checkEnterRoomCompleted() {
        if (enterRoomCompleted) return;
        if (sceneEngineManager.isEnterRoomCompleted() && sceneMicManager.isEnterRoomCompleted()) {
            // 初始化完成了
            enterRoomCompleted = true;
            sceneChatManager.addMsg(buildEnterRoomMsg(HSUserInfo.nickName));
            SceneRoomServiceCallback callback = sceneRoomServiceCallback;
            if (callback != null) {
                callback.onEnterRoomSuccess();
            }
            if (sceneDiscoManager != null) {
                sceneDiscoManager.onEnterRoomSuccess();
            }
            if (sceneCrossAppManager != null) {
                sceneCrossAppManager.onEnterRoomSuccess();
            }
        }
    }

    /**
     * 设置麦克风开关
     *
     * @param isOpen true为开 false为关
     */
    public void setMicState(boolean isOpen) {
        if (isOpen) {
            String streamId;
            AudioRoomMicModel selfMicModel = sceneMicManager.findMicModel(HSUserInfo.userId);
            if (selfMicModel == null) {
                return;
            } else {
                streamId = selfMicModel.streamId;
            }
            sceneStreamManager.openMic(streamId);
        } else {
            sceneStreamManager.closeMic();
        }
    }

    /**
     * 游戏切换
     *
     * @param gameId 游戏id
     */
    public void switchGame(long gameId) {
        // 发送信令通知房间内其他人
        String command = RoomCmdModelUtils.buildGameChangeCommand(gameId);
        sceneEngineManager.sendCommand(command, null);
        if (sceneDiscoManager != null) {
            sceneDiscoManager.switchGame(gameId);
        }
    }

    /** 退出房间 */
    public void exitRoom() {
        long roomId = getRoomId();
        if (roomId > 0) {
            RoomRepository.exitRoom(null, roomId, new RxCallback<>());
        }
        if (sceneAudio3DRoomManager != null) {
            sceneAudio3DRoomManager.exitRoom();
        }
        sceneMicManager.exitRoom();
        SceneRoomService service = sceneRoomService;
        if (service != null) {
            service.stopSelf();
        }
    }

    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, CustomLifecycleEvent.DESTROY);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    /** 发送礼物 */
    public void sendGift(GiftModel giftModel, int giftCount, List<UserInfo> toUserList, boolean isAllSeat) {
        sceneGiftManager.sendGift(giftModel, giftCount, toUserList, isAllSeat);
        if (sceneDiscoManager != null) {
            sceneDiscoManager.onSendGift(giftModel, giftCount, toUserList);
        }
    }

    public List<ContributionModel> getDiscoContribution() {
        if (sceneDiscoManager != null) {
            return sceneDiscoManager.getDiscoContribution();
        }
        return null;
    }

    /** 将用户踢出房间 */
    public void kickOutRoom(long userId) {
        AudioRoomMicModel micModel = sceneMicManager.findMicModel(userId);
        if (micModel == null) { // 不在麦位上的处理
            // 发送信令，让其自己退出房间
            String cmd = RoomCmdModelUtils.buildKickOutRoomCommand(userId + "");
            sceneEngineManager.sendCommand(cmd);
        } else { // 在麦位上的处理
            kickOutRoom(micModel);
        }
    }

    /** 将用户踢出房间 */
    public void kickOutRoom(AudioRoomMicModel model) {
        if (!model.hasUser()) {
            return;
        }
        // 发送http告知后端
        RoomRepository.roomMicLocationSwitch(this, getRoomId(), model.micIndex, false, model.userId, new RxCallback<RoomMicSwitchResp>() {
            @Override
            public void onSuccess(RoomMicSwitchResp roomMicSwitchResp) {
                super.onSuccess(roomMicSwitchResp);
                if (!model.hasUser()) {
                    return;
                }
                // 如果是真人，发送信令让其退出房间
                String cmd = RoomCmdModelUtils.buildKickOutRoomCommand(model.userId + "");
                sceneEngineManager.sendCommand(cmd);

                // 麦位上的处理
                sceneMicManager.kickOutRoom(model);
            }
        });
    }

    /** 进入房间完成的回调,用于childManager */
    public interface EnterRoomCompletedListener {
        void onEnterRoomCompleted();
    }

    /** 获取跳舞集合 */
    public List<DanceModel> getDanceList() {
        if (sceneDiscoManager != null) {
            return sceneDiscoManager.getDanceList();
        }
        return null;
    }

}
