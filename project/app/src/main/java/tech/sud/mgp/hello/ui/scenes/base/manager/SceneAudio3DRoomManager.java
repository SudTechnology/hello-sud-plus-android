package tech.sud.mgp.hello.ui.scenes.base.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState.AppCustomCrSetSeats.CrSeatModel;
import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.req.Audio3DGetConfigReq;
import tech.sud.mgp.hello.service.room.req.Audio3DMicListReq;
import tech.sud.mgp.hello.service.room.req.Audio3DSwitchMicReq;
import tech.sud.mgp.hello.service.room.req.Audio3DUpdateMicrophoneStateReq;
import tech.sud.mgp.hello.ui.scenes.base.activity.RoomConfig;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.audio3d.Audio3DCmdConfigChangModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.audio3d.Audio3DCmdFaceNotifyModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.audio3d.Audio3DCmdMicChangModel;

/**
 * 3D语聊房
 */
public class SceneAudio3DRoomManager extends BaseServiceManager {

    private final SceneRoomServiceManager parentManager;
    private SudGIPAPPState.AppCustomCrSetRoomConfig mConfig; // 配置
    private List<CrSeatModel> mSeats; // 主播位数据

    public SceneAudio3DRoomManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.sceneEngineManager.setCommandListener(mAudio3DConfigChangeListener);
        parentManager.sceneEngineManager.setCommandListener(mAudio3DMicChangeListener);
        parentManager.sceneEngineManager.setCommandListener(mAudio3DFaceNotifyListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        parentManager.sceneEngineManager.removeCommandListener(mAudio3DConfigChangeListener);
        parentManager.sceneEngineManager.removeCommandListener(mAudio3DMicChangeListener);
        parentManager.sceneEngineManager.removeCommandListener(mAudio3DFaceNotifyListener);
    }

    public void audio3DInitData(SudGIPMGState.MGCustomCrRoomInitData model) {
        refreshConfig();
        refreshSeats();
    }

    private void initRoomConfig() {
        if (mConfig == null) {
            refreshConfig();
        } else {
            callbackConfig();
        }
    }

    private void refreshConfig() {
        Audio3DGetConfigReq req = new Audio3DGetConfigReq();
        req.roomId = parentManager.getRoomId();
        RoomRepository.audio3DGetConfig(parentManager, req, new RxCallback<SudGIPAPPState.AppCustomCrSetRoomConfig>() {
            @Override
            public void onSuccess(SudGIPAPPState.AppCustomCrSetRoomConfig appCustomCrSetRoomConfig) {
                super.onSuccess(appCustomCrSetRoomConfig);
                mConfig = appCustomCrSetRoomConfig;
                callbackConfig();
            }
        });
    }

    private void callbackConfig() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onAudio3DConfig(mConfig);
        }
    }

    private void initSeats() {
        if (mSeats == null) {
            refreshSeats();
        } else {
            callbackSeats();
        }
    }

    private void refreshSeats() {
        Audio3DMicListReq req = new Audio3DMicListReq();
        req.roomId = parentManager.getRoomId();
        RoomRepository.audio3DMicList(parentManager, req, new RxCallback<SudGIPAPPState.AppCustomCrSetSeats>() {
            @Override
            public void onSuccess(SudGIPAPPState.AppCustomCrSetSeats resp) {
                super.onSuccess(resp);
                if (resp != null) {
                    mSeats = resp.seats;
                }
                callbackSeats();
            }
        });
    }

    private void callbackSeats() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            SudGIPAPPState.AppCustomCrSetSeats appCustomCrSetSeats = new SudGIPAPPState.AppCustomCrSetSeats();
            appCustomCrSetSeats.seats = mSeats;
            callback.onAudio3DSeats(appCustomCrSetSeats);
        }
    }

    public void callbackPageData() {
        initRoomConfig();
        initSeats();
    }

    // 配置变动
    private final SceneCommandManager.Audio3DConfigChangeListener mAudio3DConfigChangeListener = new SceneCommandManager.Audio3DConfigChangeListener() {
        @Override
        public void onRecvCommand(Audio3DCmdConfigChangModel model, String userID) {
            if (model == null || model.content == null) {
                return;
            }
            mConfig = model.content;
            callbackConfig();
        }
    };

    // 麦位变动
    private final SceneCommandManager.Audio3DMicChangeListener mAudio3DMicChangeListener = new SceneCommandManager.Audio3DMicChangeListener() {
        @Override
        public void onRecvCommand(Audio3DCmdMicChangModel model, String userID) {
            if (model == null || model.content == null || model.content.seats == null) {
                return;
            }
            boolean preSelfIsInSeat = selfIsInSeat();
            // 更新本地缓存的数据
            List<CrSeatModel> list = model.content.seats;
            if (mSeats == null) {
                mSeats = new ArrayList<>();
            }
            for (CrSeatModel crSeatModel : list) {
                Iterator<CrSeatModel> iterator = mSeats.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().seatIndex == crSeatModel.seatIndex) {
                        iterator.remove();
                        break;
                    }
                }
                mSeats.add(crSeatModel);
            }
            Collections.sort(mSeats, new Comparator<CrSeatModel>() {
                @Override
                public int compare(CrSeatModel o1, CrSeatModel o2) {
                    return o1.seatIndex - o2.seatIndex;
                }
            });
            callbackSeats();

            // 上麦之后处理麦克风状态同步
            CrSeatModel selfSeatModel = findUserSeatModel(HSUserInfo.userId + "");
            if (!preSelfIsInSeat && selfSeatModel != null) {
                boolean publishingStream = parentManager.sceneStreamManager.isPublishingStream();
                if (publishingStream && selfSeatModel.micphoneState != 1) {
                    setAudio3DMicrophoneState(true);
                } else if (!publishingStream && selfSeatModel.micphoneState == 1) {
                    setAudio3DMicrophoneState(false);
                }
            }
        }
    };

    // 麦位变动
    private final SceneCommandManager.Audio3DFaceNotifyListener mAudio3DFaceNotifyListener = new SceneCommandManager.Audio3DFaceNotifyListener() {
        @Override
        public void onRecvCommand(Audio3DCmdFaceNotifyModel model, String userID) {
            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
                callback.onAudio3DFaceNotify(model);
            }
        }
    };

    public List<CrSeatModel> getSeats() {
        return mSeats;
    }

    public SudGIPAPPState.AppCustomCrSetRoomConfig getConfig() {
        return mConfig;
    }

    public void enterRoom(RoomConfig config, RoomInfoModel model) {
        audio3DInitData(null);
    }

    public void exitRoom() {
        // 自己下麦
        CrSeatModel userSeatModel = findUserSeatModel(HSUserInfo.userId + "");
        if (userSeatModel != null) {
            sendAudio3DSwitchMic(Long.parseLong(userSeatModel.userId), userSeatModel.seatIndex, false);
        }
    }

    private boolean selfIsInSeat() {
        return findUserSeatModel(HSUserInfo.userId + "") != null;
    }

    private void sendAudio3DSwitchMic(long userId, int micIndex, boolean isUpMic) {
        Audio3DSwitchMicReq req = new Audio3DSwitchMicReq();
        req.roomId = parentManager.getRoomId();
        req.userId = userId;
        req.micIndex = micIndex;
        req.handleType = isUpMic ? 0 : 1;
        RoomRepository.audio3DSwitchMic(null, req, new RxCallback<>());
    }

    private void setAudio3DMicrophoneState(boolean isOpened) {
        Audio3DUpdateMicrophoneStateReq req = new Audio3DUpdateMicrophoneStateReq();
        req.roomId = parentManager.getRoomId();
        req.userId = HSUserInfo.userId;
        // 麦克风状态  -1:禁麦  0:闭麦  1:开麦
        req.micphoneState = isOpened ? 1 : 0;
        RoomRepository.audio3DUpdateMicrophoneState(null, req, new RxCallback<>());
    }

    private CrSeatModel findUserSeatModel(String userId) {
        List<CrSeatModel> list = getSeats();
        if (list == null || list.size() == 0) {
            return null;
        }
        for (CrSeatModel crSeatModel : list) {
            if (userId.equals(crSeatModel.userId)) {
                return crSeatModel;
            }
        }
        return null;
    }

    public void sendFaceNotify(int type, int actionId, int seatIndex) {
        String command = RoomCmdModelUtils.buildCmdAudio3DFaceNotify(type, actionId, seatIndex);
        parentManager.sceneEngineManager.sendCommand(command, null);
    }

}
