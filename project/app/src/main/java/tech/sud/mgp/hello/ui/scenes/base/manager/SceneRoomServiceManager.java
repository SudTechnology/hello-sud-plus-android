package tech.sud.mgp.hello.ui.scenes.base.manager;

import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.ui.scenes.base.activity.SceneConfig;
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
public class SceneRoomServiceManager extends BaseServiceManager {

    private SceneRoomServiceCallback sceneRoomServiceCallback;
    private RoomInfoModel roomInfoModel;
    private boolean enterRoomCompleted = false; // 标识是否进房成功

    public final SceneEngineManager sceneEngineManager = new SceneEngineManager(this);
    public final SceneChatManager sceneChatManager = new SceneChatManager(this);
    public final SceneMicManager sceneMicManager = new SceneMicManager(this);
    public final SceneStreamManager sceneStreamManager = new SceneStreamManager(this);
    public final SceneGiftManager sceneGiftManager = new SceneGiftManager(this);
    public final SceneGameManager sceneGameManager = new SceneGameManager(this);

    @Override
    public void onCreate() {
        super.onCreate();
        sceneEngineManager.onCreate();
        sceneChatManager.onCreate();
        sceneMicManager.onCreate();
        sceneStreamManager.onCreate();
        sceneGiftManager.onCreate();
        sceneGameManager.onCreate();
        setListener();
    }

    private void setListener() {
        sceneEngineManager.setCommandListener(new SceneCommandManager.GameChangeCommandListener() {
            @Override
            public void onRecvCommand(RoomCmdChangeGameModel command, String userID) {
                roomInfoModel.gameId = command.gameID;
                SceneRoomServiceCallback callback = getCallback();
                if (callback != null) {
                    callback.onGameChange(command.gameID);
                }
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

    private String buildEnterRoomMsg(String nickName) {
        return nickName + " " + Utils.getApp().getString(R.string.audio_enter_the_room);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sceneEngineManager.onDestroy();
        sceneChatManager.onDestroy();
        sceneMicManager.onDestroy();
        sceneStreamManager.onDestroy();
        sceneGiftManager.onDestroy();
        sceneGameManager.onDestroy();
    }

    public void setCallback(SceneRoomServiceCallback callback) {
        sceneRoomServiceCallback = callback;
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

    /** 进入房间 */
    public void enterRoom(SceneConfig config, RoomInfoModel model) {
        enterRoomCompleted = false;
        roomInfoModel = model;
        sceneEngineManager.enterRoomCompletedListener = this::checkEnterRoomCompleted;
        sceneMicManager.enterRoomCompletedListener = this::checkEnterRoomCompleted;
        sceneEngineManager.enterRoom(config, model);
        sceneMicManager.enterRoom(config, model);
    }

    /** 回调页面数据 */
    public void callbackPageData() {
        sceneChatManager.callbackPageData();
        sceneMicManager.callbackPageData();
        sceneStreamManager.callbackPageData();
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
     * @param gameId     游戏id
     * @param selfSwitch 标识是否是自己切换的
     */
    public void switchGame(long gameId, boolean selfSwitch) {
        if (selfSwitch) {
            // 发送http通知后台
            GameRepository.switchGame(null, getRoomId(), gameId, new RxCallback<>());

            // 发送信令通知房间内其他人
            String command = RoomCmdModelUtils.buildGameChangeCommand(gameId);
            sceneEngineManager.sendCommand(command, null);
        }
    }

    /** 退出房间 */
    public void exitRoom() {
        sceneMicManager.exitRoom();
    }

    /** 进入房间完成的回调,用于childManager */
    public interface EnterRoomCompletedListener {
        void onEnterRoomCompleted();
    }

}
