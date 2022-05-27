package tech.sud.mgp.hello.ui.scenes.crossroom.viewmodel;

import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;

/**
 * 跨房Pk业务处理
 */
public class CrossRoomViewModel extends BaseViewModel {

    /**
     * 初始化设置游戏房间的Id
     *
     * @param model
     */
    public long getGameRoomId(RoomInfoModel model) {
        long srcRoomId = getSrcRoomId(model);
        if (srcRoomId != 0) {
            return srcRoomId;
        }
        return model.roomId;
    }

    /** 获取发起方的房间Id */
    private long getSrcRoomId(RoomInfoModel model) {
        if (model.roomPkModel != null && model.roomPkModel.srcRoomInfo != null) {
            return model.roomPkModel.srcRoomInfo.roomId;
        }
        return 0;
    }

}
