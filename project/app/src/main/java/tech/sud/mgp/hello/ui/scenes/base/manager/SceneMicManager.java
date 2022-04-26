package tech.sud.mgp.hello.ui.scenes.base.manager;

import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.service.main.repository.UserInfoRepository;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.response.RoomMicListResp;
import tech.sud.mgp.hello.service.room.response.RoomMicResp;
import tech.sud.mgp.hello.service.room.response.RoomMicSwitchResp;
import tech.sud.mgp.hello.ui.scenes.base.activity.RoomConfig;
import tech.sud.mgp.hello.ui.scenes.base.constant.OperateMicType;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModelConverter;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdDownMicModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdUpMicModel;

/**
 * 房间麦位
 */
public class SceneMicManager extends BaseServiceManager {

    private final SceneRoomServiceManager parentManager;
    private boolean enterRoomCompleted = false; // 标识是否进房成功
    private final List<AudioRoomMicModel> micList = new ArrayList<>();
    public SceneRoomServiceManager.EnterRoomCompletedListener enterRoomCompletedListener;

    public SceneMicManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.sceneEngineManager.setCommandListener(upMicCommandListener);
        parentManager.sceneEngineManager.setCommandListener(downMicCommandListener);
    }

    /** 初始化 */
    private void init(RoomConfig config) {
        micList.clear();
        for (int i = 0; i < config.micCount; i++) {
            AudioRoomMicModel model = new AudioRoomMicModel();
            model.micIndex = i;
            micList.add(model);
        }
    }

    /** 进入房间 */
    public void enterRoom(RoomConfig config, RoomInfoModel model) {
        init(config);
        enterRoomCompleted = false;
        notifyDataSetChange();
        refreshMicList();
    }

    /** 从后端拉取麦位列表并且更新 */
    private void refreshMicList() {
        RoomRepository.getRoomMicList(null, parentManager.getRoomId(), new RxCallback<RoomMicListResp>() {
            @Override
            public void onNext(BaseResponse<RoomMicListResp> t) {
                super.onNext(t);
                if (t.getRetCode() != RetCode.SUCCESS) {
                    enterRoomCompleted();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                enterRoomCompleted();
            }

            @Override
            public void onSuccess(RoomMicListResp roomMicListResp) {
                super.onSuccess(roomMicListResp);
                if (roomMicListResp == null) {
                    enterRoomCompleted();
                    return;
                }
                List<RoomMicResp> backList = roomMicListResp.roomMicList;
                if (backList == null || backList.size() == 0) {
                    enterRoomCompleted();
                    return;
                }
                List<AudioRoomMicModel> list = new ArrayList<>();
                List<Long> userIds = new ArrayList<>();
                for (int i = 0; i < backList.size(); i++) {
                    RoomMicResp roomMicResp = backList.get(i);
                    userIds.add(roomMicResp.userId);
                    list.add(AudioRoomMicModelConverter.conver(roomMicResp));
                }
                UserInfoRepository.getUserInfoList(null, userIds, new UserInfoRepository.UserInfoResultListener() {
                    @Override
                    public void userInfoList(List<UserInfoResp> userInfos) {
                        if (userInfos != null) {
                            for (UserInfoResp userInfo : userInfos) {
                                long userId = userInfo.userId;
                                for (AudioRoomMicModel audioRoomMicModel : list) {
                                    if (userId == audioRoomMicModel.userId) {
                                        audioRoomMicModel.nickName = userInfo.nickname;
                                        audioRoomMicModel.avatar = userInfo.avatar;
                                        break;
                                    }
                                }
                            }
                        }
                        updateMicList(list);
                        enterRoomCompleted();
                    }
                });
            }
        });
    }

    private void updateMicList(List<AudioRoomMicModel> list) {
        if (list != null) {
            for (AudioRoomMicModel audioRoomMicModel : list) {
                updateMicModel(audioRoomMicModel);
            }
        }
        notifyDataSetChange();
    }

    /** 更新某个麦位模型 */
    private void updateMicModel(AudioRoomMicModel model) {
        int micIndex = model.micIndex;
        if (micIndex >= 0 && micIndex < micList.size()) {
            AudioRoomMicModel audioRoomMicModel = micList.get(micIndex);
            audioRoomMicModel.userId = model.userId;
            audioRoomMicModel.nickName = model.nickName;
            audioRoomMicModel.avatar = model.avatar;
            audioRoomMicModel.roleType = model.roleType;
            audioRoomMicModel.streamId = model.streamId;
        }
    }

    public List<AudioRoomMicModel> getMicList() {
        return new ArrayList<>(micList);
    }

    /**
     * 上下麦
     *
     * @param micIndex 麦位索引
     * @param operate  true上麦 false下麦
     */
    public void micLocationSwitch(int micIndex, boolean operate, OperateMicType type) {
        if (operate) {
            upMicLocation(micIndex, type);
        } else {
            downMicLocation(micIndex, type);
        }
    }

    /**
     * 上麦处理
     *
     * @param micIndex 麦位索引
     * @param type
     */
    public void upMicLocation(int micIndex, OperateMicType type) {
        // 已经在那个麦位上面了，不再继续执行
        if (findSelfMicIndex() == micIndex) {
            return;
        }

        // 发送http告知后端
        RoomRepository.roomMicLocationSwitch(null, parentManager.getRoomId(), micIndex, true, new RxCallback<RoomMicSwitchResp>() {
            @Override
            public void onSuccess(RoomMicSwitchResp roomMicSwitchResp) {
                super.onSuccess(roomMicSwitchResp);
                int selfMicIndex = findSelfMicIndex();
                if (selfMicIndex == micIndex) {
                    return;
                }

                // 把旧的麦位给下掉
                if (selfMicIndex >= 0) {
                    removeUser2MicList(selfMicIndex, HSUserInfo.userId);
                }

                // 发送信令
                String command = RoomCmdModelUtils.buildUpMicCommand(micIndex, roomMicSwitchResp.streamId, parentManager.getRoleType());
                parentManager.sceneEngineManager.sendCommand(command, null);

                // 麦位列表
                addUser2MicList(micIndex, HSUserInfo.userId, roomMicSwitchResp.streamId, parentManager.getRoleType());

                // 回调给页面
                SceneRoomServiceCallback callback = parentManager.getCallback();
                if (callback != null) {
                    callback.onMicLocationSwitchCompleted(micIndex, true, type);
                }
            }
        });

    }

    /**
     * 下麦操作
     *
     * @param micIndex 麦位索引
     * @param type
     */
    public void downMicLocation(int micIndex, OperateMicType type) {
        // 发送http告知后端
        RoomRepository.roomMicLocationSwitch(null, parentManager.getRoomId(), micIndex, false, new RxCallback<>());

        // 发送信令
        String command = RoomCmdModelUtils.buildDownMicCommand(micIndex);
        parentManager.sceneEngineManager.sendCommand(command, null);

        // 麦位列表
        removeUser2MicList(micIndex, HSUserInfo.userId);

        // 关闭麦位风
        parentManager.setMicState(false);

        // 回调给页面
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.onMicLocationSwitchCompleted(micIndex, false, type);
        }
    }

    /**
     * 添加一个用户到麦位列表当中
     *
     * @param micIndex 麦位索引
     * @param userId   用户id
     * @param streamId 流id
     * @param roleType 房间角色
     */
    private void addUser2MicList(int micIndex, long userId, String streamId, int roleType) {
        if (micIndex >= 0 && micIndex < micList.size()) {
            AudioRoomMicModel model = micList.get(micIndex);
            model.userId = userId;
            if (userId == HSUserInfo.userId) { // 如果是自己
                model.nickName = HSUserInfo.nickName;
                model.avatar = HSUserInfo.avatar;
                model.streamId = streamId;
                model.roleType = roleType;
                notifyItemChange(micIndex, model);
            } else {
                List<Long> userIds = new ArrayList<>();
                userIds.add(userId);
                UserInfoRepository.getUserInfoList(null, userIds, new UserInfoRepository.UserInfoResultListener() {
                    @Override
                    public void userInfoList(List<UserInfoResp> userInfos) {
                        if (userInfos != null) {
                            for (UserInfoResp userInfo : userInfos) {
                                if (model.userId == userInfo.userId) {
                                    model.nickName = userInfo.nickname;
                                    model.avatar = userInfo.avatar;
                                    model.roleType = roleType;
                                    break;
                                }
                            }
                        }
                        notifyItemChange(micIndex, model);
                    }
                });
            }
        }
    }

    /**
     * 通知单条数据的变动
     *
     * @param micIndex 麦位索引
     * @param model    数据
     */
    private void notifyItemChange(int micIndex, AudioRoomMicModel model) {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            wrapMicModel(callback, model);
            callback.notifyMicItemChange(micIndex, model);
            callbackSelfMicIndex();
        }
    }

    /**
     * 对麦位模型进行数据赋值
     *
     * @param model
     */
    private void wrapMicModel(SceneRoomServiceCallback callback, AudioRoomMicModel model) {
        // 回调页面，使用页面上的相关数据进行赋值
        callback.onWrapMicModel(model);
    }

    /**
     * 通知页面刷新整个麦位
     */
    public void notifyDataSetChange() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            for (AudioRoomMicModel audioRoomMicModel : micList) {
                wrapMicModel(callback, audioRoomMicModel);
            }
            callback.onMicList(micList);
            callbackSelfMicIndex();
        }
    }

    /**
     * 从麦位列表当中删除一个用户
     *
     * @param micIndex 麦位索引
     */
    private void removeUser2MicList(int micIndex, long userId) {
        if (micIndex >= 0 && micIndex < micList.size()) {
            AudioRoomMicModel model = micList.get(micIndex);
            if (userId == model.userId) {
                model.clearUser();
                notifyItemChange(micIndex, model);
            }
        }
    }

    public void callbackSelfMicIndex() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.selfMicIndex(findSelfMicIndex());
        }
    }

    /**
     * 查找自己所在的麦位
     *
     * @return 如果为-1,则表示不在麦上
     */
    public int findSelfMicIndex() {
        return findMicIndex(HSUserInfo.userId);
    }

    /**
     * 查找该用户所在的麦位索引
     *
     * @return 如果为-1,则表示不在麦上
     */
    public int findMicIndex(long userId) {
        AudioRoomMicModel micModel = findMicModel(userId);
        if (micModel == null) {
            return -1;
        } else {
            return micModel.micIndex;
        }
    }

    /**
     * 查找该用户所在麦位模型
     *
     * @param userId 用户id
     * @return 如果为null, 则表示不在麦上
     */
    public AudioRoomMicModel findMicModel(long userId) {
        for (AudioRoomMicModel audioRoomMicModel : micList) {
            if (audioRoomMicModel.userId == userId) {
                return audioRoomMicModel;
            }
        }
        return null;
    }

    // 自动上麦
    public void autoUpMic(OperateMicType type) {
        int selfMicIndex = findSelfMicIndex();
        if (selfMicIndex >= 0) return; // 在麦上就不再上麦了
        int emptyMicIndex = findEmptyMicIndex();
        if (emptyMicIndex >= 0) {
            micLocationSwitch(emptyMicIndex, true, type);
        } else {
            ToastUtils.showShort(R.string.no_empty_seat);
        }
    }

    /**
     * 寻找无人的空麦位
     *
     * @return
     */
    private int findEmptyMicIndex() {
        for (AudioRoomMicModel model : micList) {
            if (model.userId == 0) {
                return model.micIndex;
            }
        }
        return -1;
    }

    /** 上麦信令监听 */
    private final SceneCommandManager.UpMicCommandListener upMicCommandListener = new SceneCommandManager.UpMicCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdUpMicModel command, String userID) {
            UserInfo sendUser = command.sendUser;
            if (sendUser == null) {
                return;
            }
            // 上麦前，先将该用户从麦位上移除
            long userId = 0;
            try {
                userId = Long.parseLong(sendUser.userID);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            removeUser2MicList(findMicIndex(userId), userId);

            int micIndex = command.micIndex;
            if (micIndex >= 0 && micIndex < micList.size()) {
                AudioRoomMicModel model = micList.get(micIndex);
                model.userId = userId;
                model.nickName = sendUser.name;
                model.avatar = sendUser.icon;
                model.roleType = command.roleType;
                model.streamId = command.streamID;
                notifyItemChange(micIndex, model);
            }
        }
    };

    /** 下麦信令监听 */
    private final SceneCommandManager.DownMicCommandListener downMicCommandListener = new SceneCommandManager.DownMicCommandListener() {
        @Override
        public void onRecvCommand(RoomCmdDownMicModel command, String userID) {
            if (command.sendUser != null) {
                long userId;
                try {
                    userId = Long.parseLong(command.sendUser.userID);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                removeUser2MicList(command.micIndex, userId);
            }
        }
    };

    /** 退出房间 */
    public void exitRoom() {
        // 如果自己在麦上 则下麦
        int selfMicIndex = findSelfMicIndex();
        if (selfMicIndex >= 0) {
            // 发送信令
            String command = RoomCmdModelUtils.buildDownMicCommand(selfMicIndex);
            parentManager.sceneEngineManager.sendCommand(command, null);
        }
    }

    public boolean isEnterRoomCompleted() {
        return enterRoomCompleted;
    }

    private void enterRoomCompleted() {
        if (enterRoomCompleted) return;
        enterRoomCompleted = true;
        SceneRoomServiceManager.EnterRoomCompletedListener listener = enterRoomCompletedListener;
        if (listener != null) {
            listener.onEnterRoomCompleted();
        }
    }

    /** 回调页面数据 */
    public void callbackPageData() {
        notifyDataSetChange();
    }

}
