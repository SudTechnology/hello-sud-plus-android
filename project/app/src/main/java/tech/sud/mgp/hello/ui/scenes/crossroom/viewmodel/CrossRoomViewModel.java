package tech.sud.mgp.hello.ui.scenes.crossroom.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.room.model.PkStatus;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.response.RoomPkAgreeResp;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;

/**
 * 跨房Pk业务处理
 */
public class CrossRoomViewModel extends BaseViewModel {

    public final MutableLiveData<RoomPkAgreeResp> againPkLiveData = new MutableLiveData<>(); // 再来一次pk返回成功

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

    /** 再来一轮pk */
    public void againPk(LifecycleOwner owner, RoomInfoModel model) {
        if (model.roomPkModel == null
                || model.roomPkModel.getPkRival() == null
                || model.roomPkModel.srcRoomInfo == null
                || model.roomPkModel.destRoomInfo == null
                || model.roomPkModel.pkStatus != PkStatus.PK_END) {
            return;
        }
        RoomRepository.roomPkAgree(owner, model.roomPkModel.srcRoomInfo.roomId,
                model.roomPkModel.destRoomInfo.roomId, new RxCallback<RoomPkAgreeResp>() {
                    @Override
                    public void onSuccess(RoomPkAgreeResp roomPkAgreeResp) {
                        super.onSuccess(roomPkAgreeResp);
                        if (roomPkAgreeResp == null) {
                            return;
                        }
                        if (model.roomPkModel == null
                                || model.roomPkModel.getPkRival() == null
                                || model.roomPkModel.srcRoomInfo == null
                                || model.roomPkModel.destRoomInfo == null
                                || model.roomPkModel.pkStatus != PkStatus.PK_END) {
                            return;
                        }
                        againPkLiveData.setValue(roomPkAgreeResp);
                    }
                });
    }

}
