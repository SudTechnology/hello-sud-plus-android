package tech.sud.mgp.hello.ui.scenes.common.gift.manager;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.MicUserInfoModel;

public class RoomGiftDialogManager extends GiftBaseManager {

    /**
     * 初始化 送礼面板麦位
     */
    public void setMicUsers(List<AudioRoomMicModel> mDatas, int selectedIndex) {
        GiftHelper.getInstance().inMic = true;
        GiftHelper.getInstance().inMics.clear();
        if (mDatas != null && mDatas.size() > 0) {
            if (selectedIndex < 0 || selectedIndex >= mDatas.size()) {
                //根据业务，设置默认选中指定的麦上用户
                selectedIndex = 0;
            }
            for (AudioRoomMicModel model : mDatas) {
                if (model.userId != 0) {
                    MicUserInfoModel user = new MicUserInfoModel();
                    user.userInfo = model;
                    user.indexMic = model.micIndex;
                    user.checked = false;
                    GiftHelper.getInstance().inMics.add(user);
                }
            }
            if (GiftHelper.getInstance().inMics.size() > 0) {
                //这里默认选中第一个
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
                if (model.userId != 0) {
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
        //切麦后排序
        if (GiftHelper.getInstance().inMics.size() > 1) {
            Collections.sort(GiftHelper.getInstance().inMics, new Comparator<MicUserInfoModel>() {
                @Override
                public int compare(MicUserInfoModel o1, MicUserInfoModel o2) {
                    if (o1.indexMic > o2.indexMic) {
                        return 1;
                    } else if (o1.indexMic < o2.indexMic) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
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
