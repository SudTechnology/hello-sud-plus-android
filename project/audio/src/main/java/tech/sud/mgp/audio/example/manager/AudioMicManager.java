package tech.sud.mgp.audio.example.manager;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.audio.example.http.repository.AudioRepository;
import tech.sud.mgp.audio.example.http.response.RoomMicListResp;
import tech.sud.mgp.audio.example.http.response.RoomMicResp;
import tech.sud.mgp.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.audio.example.model.AudioRoomMicModelConverter;
import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.audio.example.service.AudioRoomServiceCallback;
import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.http.use.repository.UserInfoRepository;
import tech.sud.mgp.common.http.use.resp.UserInfoResp;

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
    }

    public void enterRoom(RoomInfoModel model) {
        AudioRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.setMicList(micList);
        }
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
        AudioRoomServiceCallback callback = parentManager.getCallback();
        if (callback != null) {
            callback.setMicList(micList);
        }
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

    /**
     * 上下麦
     *
     * @param micIndex 麦位索引
     * @param userId   用户id
     * @param operate  true上麦 false下麦
     */
    public void micSwitch(int micIndex, long userId, boolean operate) {

    }

}
