package tech.sud.mgp.hello.ui.scenes.base.manager;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;
import com.trello.rxlifecycle4.LifecycleTransformer;
import com.trello.rxlifecycle4.RxLifecycle;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.lifecycle.CustomLifecycleEvent;
import tech.sud.mgp.hello.common.utils.lifecycle.CustomLifecycleProvider;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.ui.common.constant.RequestKey;
import tech.sud.mgp.hello.ui.scenes.base.activity.RoomConfig;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChangeGameModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdEnterRoomModel;

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

    public final SceneEngineManager sceneEngineManager = new SceneEngineManager(this);
    public final SceneChatManager sceneChatManager = new SceneChatManager(this);
    public final SceneMicManager sceneMicManager = new SceneMicManager(this);
    public final SceneStreamManager sceneStreamManager = new SceneStreamManager(this);
    public final SceneGiftManager sceneGiftManager = new SceneGiftManager(this);
    public final SceneGameManager sceneGameManager = new SceneGameManager(this);
    public final SceneOrderManager sceneOrderManager = new SceneOrderManager(this);
    public final SceneRoomPkManager sceneRoomPkManager = new SceneRoomPkManager(this);
    public final SceneQuizManager sceneQuizManager = new SceneQuizManager(this);

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
        roomActivityClass = startClass;
        enterRoomCompleted = false;
        roomInfoModel = model;
        sceneEngineManager.enterRoomCompletedListener = this::checkEnterRoomCompleted;
        sceneMicManager.enterRoomCompletedListener = this::checkEnterRoomCompleted;
        sceneEngineManager.enterRoom(config, model);
        sceneMicManager.enterRoom(config, model);
        sceneRoomPkManager.enterRoom(config, model);
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
        SceneRoomServiceCallback callback = getCallback();
        if (callback != null) {
            callback.onRecoverCompleted();
        }
    }

    /** 检查进入房间是否已完成 */
    private void checkEnterRoomCompleted() {
        if (enterRoomCompleted) return;
        if (sceneEngineManager.isEnterRoomCompleted() && sceneMicManager.isEnterRoomCompleted()) {
            enterRoomCompleted = true;
            sceneChatManager.addMsg(buildEnterRoomMsg(HSUserInfo.nickName));
            SceneRoomServiceCallback callback = sceneRoomServiceCallback;
            if (callback != null) {
                callback.onEnterRoomSuccess();
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
    }

    /** 退出房间 */
    public void exitRoom() {
        long roomId = getRoomId();
        if (roomId > 0) {
            RoomRepository.exitRoom(null, roomId, new RxCallback<>());
        }
        sceneMicManager.exitRoom();
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

    /** 进入房间完成的回调,用于childManager */
    public interface EnterRoomCompletedListener {
        void onEnterRoomCompleted();
    }

}
