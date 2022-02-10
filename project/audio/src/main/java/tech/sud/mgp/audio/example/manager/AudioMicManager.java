package tech.sud.mgp.audio.example.manager;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tech.sud.mgp.audio.example.http.repository.AudioRepository;
import tech.sud.mgp.audio.example.http.response.RoomMicListResp;
import tech.sud.mgp.audio.example.http.response.RoomMicResp;
import tech.sud.mgp.audio.example.http.response.RoomMicSwitchResp;
import tech.sud.mgp.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.audio.example.model.AudioRoomMicModelConverter;
import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.audio.example.model.UserInfo;
import tech.sud.mgp.audio.example.model.command.DownMicCommand;
import tech.sud.mgp.audio.example.model.command.UpMicCommand;
import tech.sud.mgp.audio.example.service.AudioRoomServiceCallback;
import tech.sud.mgp.audio.example.utils.AudioRoomCommandUtils;
import tech.sud.mgp.audio.middle.MediaUser;
import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.http.use.repository.UserInfoRepository;
import tech.sud.mgp.common.http.use.resp.UserInfoResp;
import tech.sud.mgp.common.model.HSUserInfo;

/**
 * 房间麦位
 */
public class AudioMicManager extends BaseServiceManager {
    private final AudioRoomServiceManager parentManager;

    private final List<AudioRoomMicModel> micList = new ArrayList<>();

    public AudioMicManager(AudioRoomServiceManager audioRoomServiceManager) {
        super();
        this.parentManager = audioRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        for (int i = 0; i < 9; i++) {
            AudioRoomMicModel model = new AudioRoomMicModel();
            model.micIndex = i;
            micList.add(model);
        }
        parentManager.audioEngineManager.setCommandListener(upMicCommandListener);
        parentManager.audioEngineManager.setCommandListener(downMicCommandListener);
    }

    public void enterRoom(RoomInfoModel model) {
        notifyDataSetChange();
        refreshMicList();
    }

    /**
     * 从后端拉取麦位列表并且更新
     */
    private void refreshMicList() {
        AudioRepository.getRoomMicList(null, parentManager.getRoomId(), new RxCallback<RoomMicListResp>() {
            @Override
            public void onSuccess(RoomMicListResp roomMicListResp) {
                super.onSuccess(roomMicListResp);
                if (roomMicListResp == null) return;
                List<RoomMicResp> backList = roomMicListResp.roomMicList;
                if (backList == null || backList.size() == 0) {
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

    private void updateMicModel(AudioRoomMicModel model) {
        int micIndex = model.micIndex;
        if (micIndex >= 0 && micIndex < micList.size()) {
            AudioRoomMicModel audioRoomMicModel = micList.get(micIndex);
            audioRoomMicModel.userId = model.userId;
            audioRoomMicModel.nickName = model.nickName;
            audioRoomMicModel.avatar = model.avatar;
            audioRoomMicModel.roleType = model.roleType;
        }
    }

    public List<AudioRoomMicModel> getMicList() {
        return new ArrayList<>(micList);
    }

    /**
     * 上下麦
     *
     * @param micIndex 麦位索引
     * @param userId   用户id
     * @param operate  true上麦 false下麦
     */
    public void micLocationSwitch(int micIndex, long userId, boolean operate) {
        if (operate) {
            upMicLocation(micIndex, userId);
        } else {
            downMicLocation(micIndex, userId);
        }
    }

    public void upMicLocation(int micIndex, long userId) {
        // 查找当前自己已经在的麦位
        int selfMicIndex = findSelfMicIndex();
        if (selfMicIndex == micIndex) {
            return;
        }

        // 发送http告知后端
        AudioRepository.roomMicLocationSwitch(null, parentManager.getRoomId(), micIndex, true, new RxCallback<RoomMicSwitchResp>() {
            @Override
            public void onSuccess(RoomMicSwitchResp roomMicSwitchResp) {
                super.onSuccess(roomMicSwitchResp);
                // 把旧的麦位给下掉
                if (selfMicIndex > 0) {
                    downMicLocation(selfMicIndex, HSUserInfo.userId);
                }

                // 发送信令
                String command = AudioRoomCommandUtils.buildUpMicCommand(micIndex);
                parentManager.audioEngineManager.sendCommand(command, null);

                // 麦位列表
                addUser2MicList(micIndex, userId, roomMicSwitchResp.streamId);
            }
        });

    }

    public void downMicLocation(int micIndex, long userId) {
        // 发送http告知后端
        AudioRepository.roomMicLocationSwitch(null, parentManager.getRoomId(), micIndex, false, new RxCallback<>());

        // 发送信令
        String command = AudioRoomCommandUtils.buildDownMicCommand(micIndex);
        parentManager.audioEngineManager.sendCommand(command, null);

        // 麦位列表
        removeUser2MicList(micIndex, userId);

        // 关闭麦位风
        parentManager.setMicState(false);
    }

    /**
     * 添加一个用户到麦位列表当中
     *
     * @param micIndex 麦位索引
     * @param userId   用户id
     * @param streamId 流id
     */
    private void addUser2MicList(int micIndex, long userId, String streamId) {
        if (micIndex >= 0 && micIndex < micList.size()) {
            AudioRoomMicModel model = micList.get(micIndex);
            model.userId = userId;
            if (userId == HSUserInfo.userId) { // 如果是自己
                model.nickName = HSUserInfo.nickName;
                model.avatar = HSUserInfo.avatar;
                model.streamId = streamId;
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
        AudioRoomServiceCallback callback = parentManager.getCallback();
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
    private void wrapMicModel(AudioRoomServiceCallback callback, AudioRoomMicModel model) {
        // 回调页面，使用页面上的相关数据进行赋值
        callback.onWrapMicModel(model);
    }

    /**
     * 通知页面刷新整个麦位
     */
    public void notifyDataSetChange() {
        AudioRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            for (AudioRoomMicModel audioRoomMicModel : micList) {
                wrapMicModel(callback, audioRoomMicModel);
            }
            callback.setMicList(micList);
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
        AudioRoomServiceCallback callback = parentManager.getCallback();
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
        AudioRoomMicModel selfMicModel = findSelfMicModel();
        if (selfMicModel == null) {
            return -1;
        } else {
            return selfMicModel.micIndex;
        }
    }

    /**
     * 根据流id查找对应的麦位
     *
     * @return 如果为-1,则表示不在麦上
     */
    public int findMicIndexByStreamId(String streamId) {
        if (TextUtils.isEmpty(streamId)) {
            return -1;
        }
        for (AudioRoomMicModel audioRoomMicModel : micList) {
            if (streamId.equals(audioRoomMicModel.streamId)) {
                return audioRoomMicModel.micIndex;
            }
        }
        return -1;
    }

    /**
     * 查找自己所在的麦位
     *
     * @return 如果为null, 则表示不在麦上
     */
    public AudioRoomMicModel findSelfMicModel() {
        for (AudioRoomMicModel audioRoomMicModel : micList) {
            if (audioRoomMicModel.userId == HSUserInfo.userId) {
                return audioRoomMicModel;
            }
        }
        return null;
    }

    public void autoUpMic() {
        int selfMicIndex = findSelfMicIndex();
        if (selfMicIndex >= 0) return; // 在麦上就不再上麦了
        int emptyMicIndex = findEmptyMicIndex();
        if (emptyMicIndex >= 0) {
            micLocationSwitch(emptyMicIndex, HSUserInfo.userId, true);
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

    /**
     * 更新麦位的礼物icon展示效果
     */
    public void updateGiftIcon(Map<Long, Boolean> userState) {
        if (userState != null) {
            for (AudioRoomMicModel model : micList) {
                if (model.userId != 0) {
                    model.giftEnable = userState.get(model.userId) != null && userState.get(model.userId);
                } else {
                    model.giftEnable = false;
                }
            }
            notifyDataSetChange();
        }
    }

    private final AudioCommandManager.UpMicCommandListener upMicCommandListener = new AudioCommandManager.UpMicCommandListener() {
        @Override
        public void onRecvCommand(UpMicCommand command, MediaUser user, String roomId) {
            UserInfo sendUser = command.sendUser;
            if (sendUser == null) {
                return;
            }
            int micIndex = command.micIndex;
            if (micIndex >= 0 && micIndex < micList.size()) {
                AudioRoomMicModel model = micList.get(micIndex);
                model.userId = sendUser.userID;
                model.nickName = sendUser.name;
                model.avatar = sendUser.icon;
                notifyItemChange(micIndex, model);
            }
        }
    };

    private final AudioCommandManager.DownMicCommandListener downMicCommandListener = new AudioCommandManager.DownMicCommandListener() {
        @Override
        public void onRecvCommand(DownMicCommand command, MediaUser user, String roomId) {
            if (command.sendUser != null) {
                removeUser2MicList(command.micIndex, command.sendUser.userID);
            }
        }
    };

}
