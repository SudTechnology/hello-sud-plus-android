package tech.sud.mgp.hello.ui.room.audio.gift.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tech.sud.mgp.hello.ui.room.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.room.audio.gift.model.MicUserInfoModel;
import tech.sud.mgp.hello.common.model.HSUserInfo;

public class RoomGiftDialogManager extends GiftBaseManager {

    /**
     * 初始化 送礼面板麦位
     */
    public void setMicUsers(List<AudioRoomMicModel> mDatas, int selectedIndex) {
        if (mDatas != null && mDatas.size() > 0) {
            GiftHelper.getInstance().inMic = true;
            GiftHelper.getInstance().inMics.clear();
            if (selectedIndex < 0 || selectedIndex >= mDatas.size()) {
                selectedIndex = 0;
            }
            for (AudioRoomMicModel model : mDatas) {
                if (model.userId != 0 && model.userId != HSUserInfo.userId) {
                    MicUserInfoModel user = new MicUserInfoModel();
                    user.userInfo = model;
                    user.indexMic = model.micIndex;
                    user.checked = false;
                    GiftHelper.getInstance().inMics.add(user);
                }
            }
            if (GiftHelper.getInstance().inMics.size() > 0) {
                GiftHelper.getInstance().inMics.get(0).checked = true;
            }
        }
    }

    /**
     * 实时更新送礼面板的麦位数据
     */
    public void updateMicUsers(List<AudioRoomMicModel> mDatas) {
        if (mDatas != null && mDatas.size() > 0) {
            Map<Long, Boolean> checkState = new HashMap<>();
            for (MicUserInfoModel model : GiftHelper.getInstance().inMics) {
                checkState.put(model.userInfo.userId, model.checked);
            }
            // 清空麦位列表
            GiftHelper.getInstance().inMics.clear();
            for (AudioRoomMicModel model : mDatas) {
                if (model.userId != 0 && model.userId != HSUserInfo.userId) {
                    MicUserInfoModel user = new MicUserInfoModel();
                    user.userInfo = model;
                    user.indexMic = model.micIndex;
                    user.checked = checkState.get(model.userId) != null && checkState.get(model.userId);
                    GiftHelper.getInstance().inMics.add(user);
                }
            }
        }
    }

    /**
     * 实时更新送礼面板的单个麦位数据
     */
    public void updateOneMicUsers(AudioRoomMicModel micModel) {
        MicUserInfoModel userInfoModel = findMicIndex(micModel);
        if (micModel.userId > 0) {
            if (userInfoModel == null) {
                MicUserInfoModel user = new MicUserInfoModel();
                user.userInfo = micModel;
                user.indexMic = micModel.micIndex;
                user.checked = micModel.giftEnable;
                GiftHelper.getInstance().inMics.add(user);
            } else {
                userInfoModel.userInfo = micModel;
                userInfoModel.indexMic = micModel.micIndex;
                userInfoModel.checked = micModel.giftEnable;
//                userInfoModel.checked = false;
            }
        } else {
            Iterator<MicUserInfoModel> iterator = GiftHelper.getInstance().inMics.listIterator();
            while (iterator.hasNext()) {
                MicUserInfoModel micUserInfoModel = iterator.next();
                if (micUserInfoModel.userInfo.micIndex == micModel.micIndex) {
                    iterator.remove();
                }
            }
        }
    }

    private MicUserInfoModel findMicIndex(AudioRoomMicModel micModel) {
        if (GiftHelper.getInstance().inMics.size() > 0 && GiftHelper.getInstance().inMic) {
            for (MicUserInfoModel infoModel : GiftHelper.getInstance().inMics) {
                if (micModel.micIndex == infoModel.userInfo.micIndex) {
                    return infoModel;
                }
            }
        }
        return null;
    }

}
